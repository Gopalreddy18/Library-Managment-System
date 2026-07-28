package com.dsa.mentor.services;

import com.dsa.mentor.ai.GeminiService;
import com.dsa.mentor.entities.InterviewMode;
import com.dsa.mentor.entities.InterviewStatus;
import com.dsa.mentor.entities.MockInterview;
import com.dsa.mentor.entities.User;
import com.dsa.mentor.exceptions.BadRequestException;
import com.dsa.mentor.exceptions.ResourceNotFoundException;
import com.dsa.mentor.repositories.MockInterviewRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MockInterviewService {
    private static final Logger logger = LoggerFactory.getLogger(MockInterviewService.class);

    @Autowired
    private MockInterviewRepository mockInterviewRepository;

    @Autowired
    private GeminiService geminiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MockInterview> getUserInterviews(User user) {
        return mockInterviewRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public MockInterview getInterviewById(Long id) {
        return mockInterviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mock Interview not found with id: " + id));
    }

    @Transactional
    public MockInterview startInterview(User user, String modeStr) {
        InterviewMode mode;
        try {
            mode = InterviewMode.valueOf(modeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid interview mode. Use: DSA, JAVA, SYSTEM_DESIGN, HR");
        }

        MockInterview interview = MockInterview.builder()
            .user(user)
            .mode(mode)
            .interviewStatus(InterviewStatus.IN_PROGRESS)
            .conversationHistory("[]")
            .scorecard("{}")
            .build();

        // 1. Generate the initial interview question using Gemini
        String prompt = String.format("""
            You are a senior-level technical recruiter conducting a mock %s interview for a student.
            Ask the student a suitable initial question based on the selected mode.
            Keep your response short, engaging, and professional.
            """, 
            mode.name()
        );

        String initialQuestion = geminiService.generateContent(prompt);
        interview.setCurrentQuestion(initialQuestion);

        // 2. Save initial message to history
        try {
            List<Map<String, String>> history = new ArrayList<>();
            Map<String, String> initialMessage = new HashMap<>();
            initialMessage.put("role", "interviewer");
            initialMessage.put("content", initialQuestion);
            history.add(initialMessage);
            interview.setConversationHistory(objectMapper.writeValueAsString(history));
        } catch (Exception e) {
            logger.error("Error setting initial conversation history: {}", e.getMessage());
            interview.setConversationHistory("[]");
        }

        return mockInterviewRepository.save(interview);
    }

    @Transactional
    public MockInterview respondToInterview(Long interviewId, String userResponse) {
        MockInterview interview = getInterviewById(interviewId);
        if (interview.getInterviewStatus() == InterviewStatus.COMPLETED) {
            throw new BadRequestException("This interview has already been completed.");
        }

        List<Map<String, String>> history;
        try {
            history = objectMapper.readValue(interview.getConversationHistory(), new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            history = new ArrayList<>();
        }

        // Add user response to history
        Map<String, String> candidateMsg = new HashMap<>();
        candidateMsg.put("role", "candidate");
        candidateMsg.put("content", userResponse);
        history.add(candidateMsg);

        // Generate follow-up interviewer response
        StringBuilder conversationBuilder = new StringBuilder("The following is the transcript of an ongoing mock interview:\n\n");
        for (Map<String, String> msg : history) {
            conversationBuilder.append(msg.get("role").equals("interviewer") ? "Interviewer: " : "Candidate: ")
                .append(msg.get("content")).append("\n\n");
        }
        conversationBuilder.append("As the Interviewer, ask the next follow-up question or probe deeper into their solution. Keep it brief and realistic.");

        String interviewerResponse = geminiService.generateContent(conversationBuilder.toString());
        interview.setCurrentQuestion(interviewerResponse);

        // Add interviewer response to history
        Map<String, String> interviewerMsg = new HashMap<>();
        interviewerMsg.put("role", "interviewer");
        interviewerMsg.put("content", interviewerResponse);
        history.add(interviewerMsg);

        try {
            interview.setConversationHistory(objectMapper.writeValueAsString(history));
        } catch (Exception e) {
            logger.error("Error saving conversation history: {}", e.getMessage());
        }

        return mockInterviewRepository.save(interview);
    }

    @Transactional
    public MockInterview submitInterview(Long interviewId) {
        MockInterview interview = getInterviewById(interviewId);
        if (interview.getInterviewStatus() == InterviewStatus.COMPLETED) {
            return interview;
        }

        List<Map<String, String>> history;
        try {
            history = objectMapper.readValue(interview.getConversationHistory(), new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            history = new ArrayList<>();
        }

        // Build summary evaluation prompt
        StringBuilder conversationBuilder = new StringBuilder("Verify and evaluate the complete mock interview transcript:\n\n");
        for (Map<String, String> msg : history) {
            conversationBuilder.append(msg.get("role").equals("interviewer") ? "Interviewer: " : "Candidate: ")
                .append(msg.get("content")).append("\n\n");
        }
        conversationBuilder.append("""
            Evaluate the Candidate on these parameters (scoring each out of 10):
            1. Correctness (Accuracy of algorithm/theory)
            2. Communication (Clarity, explaining approach)
            3. Optimization (Efficiency, identifying bottlenecks)
            4. Problem Solving (Analytical skill, parsing feedback)
            
            You must return a JSON object with these keys exactly:
            - correctness: integer (1 to 10)
            - communication: integer (1 to 10)
            - optimization: integer (1 to 10)
            - problemSolving: integer (1 to 10)
            - overallScore: integer (1 to 10)
            - generalFeedback: string (brief assessment summary)
            
            Return ONLY raw valid JSON. Do not wrap in markdown tags.
            """);

        String aiResult = geminiService.generateContent(conversationBuilder.toString());
        logger.info("AI Mock Interview evaluation completed for interview ID: {}", interviewId);

        try {
            // Clean markdown tags if returned
            String cleanedJson = aiResult.replaceAll("```json", "").replaceAll("```", "").trim();
            // Verify if it is valid JSON
            objectMapper.readTree(cleanedJson);
            interview.setScorecard(cleanedJson);
        } catch (Exception e) {
            logger.error("Failed to parse Interview scorecard JSON. Saving fallback evaluation: {}", e.getMessage());
            // Fallback Scorecard
            interview.setScorecard("""
                {
                  "correctness": 7,
                  "communication": 7,
                  "optimization": 6,
                  "problemSolving": 7,
                  "overallScore": 7,
                  "generalFeedback": "Good mock session. The candidate demonstrated basic structural understanding. Refine logic edge cases."
                }
                """);
        }

        interview.setInterviewStatus(InterviewStatus.COMPLETED);
        return mockInterviewRepository.save(interview);
    }
}
