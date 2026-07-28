package com.dsa.mentor.services;

import com.dsa.mentor.ai.GeminiService;
import com.dsa.mentor.entities.Roadmap;
import com.dsa.mentor.entities.RoadmapWeek;
import com.dsa.mentor.entities.User;
import com.dsa.mentor.repositories.RoadmapRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoadmapService {
    private static final Logger logger = LoggerFactory.getLogger(RoadmapService.class);

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private GeminiService geminiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Roadmap> getLatestRoadmap(User user) {
        return roadmapRepository.findFirstByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Roadmap generateRoadmap(User user, String targetCompany, String currentSkillLevel, String weakTopics) {
        // Create parent roadmap
        Roadmap roadmap = Roadmap.builder()
            .user(user)
            .targetCompany(targetCompany)
            .currentSkillLevel(currentSkillLevel)
            .weakTopics(weakTopics)
            .weeks(new ArrayList<>())
            .build();

        // 1. Build prompt for Gemini
        String prompt = String.format("""
            You are an AI DSA Learning Planner. Generate a customized 4-week learning roadmap for a student.
            
            [Student Profile]
            Current Skill Level: %s
            Target Company: %s
            Weak Topics: %s
            
            Provide a syllabus for exactly 4 weeks. Your response MUST be a JSON array of objects representing the weeks.
            Each object in the array must contain the following keys exactly:
            - weekNumber: integer (1 to 4)
            - topicTitle: string
            - conceptSummary: string (summary of what to study)
            - practiceProblems: string (comma-separated list of recommended standard LeetCode problem titles, e.g. "Two Sum, Reverse Linked List")
            - revisionTasks: string (revision tasks to complete)
            - mockTests: string (mock test description)
            
            Return ONLY raw, valid JSON. Do not wrap in markdown or include code blocks.
            """,
            currentSkillLevel,
            targetCompany,
            weakTopics != null ? weakTopics : "None specified"
        );

        // 2. Call Gemini
        String aiOutput = geminiService.generateContent(prompt);
        logger.info("AI Roadmap generation triggered for user: {}", user.getId());

        try {
            // Clean markdown blocks if present
            String cleanedJson = aiOutput.replaceAll("```json", "").replaceAll("```", "").trim();
            List<RoadmapWeekDto> weekDtos = objectMapper.readValue(cleanedJson, new TypeReference<List<RoadmapWeekDto>>() {});

            for (RoadmapWeekDto dto : weekDtos) {
                RoadmapWeek week = RoadmapWeek.builder()
                    .roadmap(roadmap)
                    .weekNumber(dto.getWeekNumber())
                    .topicTitle(dto.getTopicTitle())
                    .conceptSummary(dto.getConceptSummary())
                    .practiceProblems(dto.getPracticeProblems())
                    .revisionTasks(dto.getRevisionTasks())
                    .mockTests(dto.getMockTests())
                    .build();
                roadmap.getWeeks().add(week);
            }

        } catch (Exception e) {
            logger.error("Failed to parse AI Roadmap JSON. Generating fallback plan. Error: {}", e.getMessage());
            // Fallback: 4 weeks static roadmap
            generateFallbackWeeks(roadmap);
        }

        return roadmapRepository.save(roadmap);
    }

    private void generateFallbackWeeks(Roadmap roadmap) {
        String level = roadmap.getCurrentSkillLevel().toUpperCase();
        List<RoadmapWeek> weeks = new ArrayList<>();

        if (level.contains("BEGINNER")) {
            weeks.add(createWeek(roadmap, 1, "Arrays & String Basics", 
                "Learn core array manipulations, subarray logic, sliding window basics, and string traversals.", 
                "Two Sum, Valid Anagram, Reverse String", "Review basic operations on Arrays and Strings.", "Array/String Quiz"));
            weeks.add(createWeek(roadmap, 2, "Hashing & HashMaps", 
                "Master searching in O(1) time complexity using maps, key-value lookup strategies.", 
                "Group Anagrams, Longest Consecutive Sequence", "Review lookup times and hash collisions.", "Hashing Mock"));
            weeks.add(createWeek(roadmap, 3, "Linked Lists & Pointers", 
                "Understand pointers, node insertions, removals, and loop detection.", 
                "Reverse Linked List, Merge Two Sorted Lists, Linked List Cycle", "Draw pointers on paper to dry run node switching.", "Linked List Test"));
            weeks.add(createWeek(roadmap, 4, "Stacks & Queues", 
                "Learn LIFO and FIFO protocols, parsing expressions, matching brackets.", 
                "Valid Parentheses, Implement Queue using Stacks", "Review monotonic stacks and queue traversals.", "Stack/Queue Challenge"));
        } else {
            // Intermediate / Advanced Fallback
            weeks.add(createWeek(roadmap, 1, "Recursion & Trees", 
                "Understand DFS/BFS traversals on Binary Search Trees, depth calculation, tree balance checks.", 
                "Invert Binary Tree, Maximum Depth of Binary Tree, Balanced Binary Tree", "Practice drawing recursive trees for execution dry-runs.", "Tree Mock Test"));
            weeks.add(createWeek(roadmap, 2, "Dynamic Programming (DP)", 
                "Master memoization vs tabulation, solving subproblems, fibonacci patterns, knapsack foundations.", 
                "Climbing Stairs, House Robber, Coin Change", "Practice writing state transition formulas for DP.", "DP Midterm Challenge"));
            weeks.add(createWeek(roadmap, 3, "Graphs & Traversals", 
                "Master BFS, DFS on adjacency lists, cycle detection, short-path algorithms.", 
                "Number of Islands, Clone Graph, Course Schedule", "Contrast queue implementation in BFS vs stack traversal in DFS.", "Graph Coding Interview"));
            weeks.add(createWeek(roadmap, 4, "Advanced Binary Search & Sliding Window", 
                "Learn searching in rotated arrays, boundary conditions, dynamic size windows.", 
                "Search in Rotated Sorted Array, Longest Substring Without Repeating Characters", "Review edge boundary parameters in binary search logic.", "DSA Final Assessment"));
        }

        roadmap.setWeeks(weeks);
    }

    private RoadmapWeek createWeek(Roadmap roadmap, int number, String title, String summary, String problems, String tasks, String mock) {
        return RoadmapWeek.builder()
            .roadmap(roadmap)
            .weekNumber(number)
            .topicTitle(title)
            .conceptSummary(summary)
            .practiceProblems(problems)
            .revisionTasks(tasks)
            .mockTests(mock)
            .build();
    }

    // Static nested DTO helper class for deserializing
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    public static class RoadmapWeekDto {
        private int weekNumber;
        private String topicTitle;
        private String conceptSummary;
        private String practiceProblems;
        private String revisionTasks;
        private String mockTests;
    }
}
