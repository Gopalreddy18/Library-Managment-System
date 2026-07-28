package com.dsa.mentor.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${app.ai.gemini.apiKey:}")
    private String apiKey;

    @Value("${app.ai.gemini.apiUrl:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.startsWith("${")) {
            logger.warn("Gemini API key is not configured. Falling back to Mock AI responses.");
            return getMockResponse(prompt);
        }

        String url = apiUrl + "?key=" + apiKey;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Construct payload: { "contents": [ { "parts": [ { "text": "prompt" } ] } ] }
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", List.of(content));

            // Optional settings to force structured response or temperature
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.3);
            payload.put("generationConfig", generationConfig);

            String requestJson = objectMapper.writeValueAsString(payload);
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidateText = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                if (!candidateText.isMissingNode()) {
                    return candidateText.asText();
                }
            }
            logger.error("Failed to extract content from Gemini response. Body: {}", response.getBody());
            return getMockResponse(prompt);
        } catch (Exception e) {
            logger.error("Error communicating with Gemini API: {}. Falling back to mock.", e.getMessage(), e);
            return getMockResponse(prompt);
        }
    }

    private String getMockResponse(String prompt) {
        String lower = prompt.toLowerCase();
        if (lower.contains("code analysis") || lower.contains("logic correctness")) {
            return """
                {
                  "logicCorrectness": "The solution appears to be correct and passes all basic checks.",
                  "timeComplexity": "O(N)",
                  "spaceComplexity": "O(1)",
                  "optimizationSuggestions": "No major bottleneck. You could reuse variable references to avoid GC overhead.",
                  "bestPractices": "Follow standard naming guidelines; use CamelCase for Java classes and snake_case for Python scripts.",
                  "editorial": "Standard approach is sorting or utilizing a HashMap to map elements to index lookups in O(N)."
                }
                """;
        } else if (lower.contains("roadmap")) {
            return """
                [
                  {
                    "weekNumber": 1,
                    "topicTitle": "Arrays and Two Pointers",
                    "conceptSummary": "Master linear structures, sliding window strategies, and bilateral pointer movements.",
                    "practiceProblems": "Two Sum, Container With Most Water, 3Sum",
                    "revisionTasks": "Review complexity constraints of sub-array counts.",
                    "mockTests": "Easy-level Array Challenge"
                  },
                  {
                    "weekNumber": 2,
                    "topicTitle": "Hashing and Subarrays",
                    "conceptSummary": "Leverage HashMap for sub-sequence tracking, target value calculations in O(1) average time.",
                    "practiceProblems": "Longest Consecutive Sequence, Subarray Sum Equals K",
                    "revisionTasks": "Practice collision mitigation mechanisms in HashMap.",
                    "mockTests": "Medium-level Hashing Mock"
                  }
                ]
                """;
        } else if (lower.contains("mock interview") && lower.contains("scorecard")) {
            return """
                {
                  "correctness": 8,
                  "communication": 9,
                  "optimization": 7,
                  "problemSolving": 8,
                  "overallScore": 8,
                  "generalFeedback": "Excellent communication skills. Explains the approach before jumping into coding. Needs minor refinement in identifying boundary memory constraints."
                }
                """;
        } else if (lower.contains("mock interview")) {
            return "Interviewer: Let's discuss a coding problem. Can you implement a function to check if a binary tree is balanced? Explain your approach before writing the code.";
        }
        return "I am your DSA Mentor. Let's focus on mastering your data structures and algorithms.";
    }
}
