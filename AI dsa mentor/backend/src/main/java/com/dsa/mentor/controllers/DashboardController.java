package com.dsa.mentor.controllers;

import com.dsa.mentor.dtos.AdminDashboardResponse;
import com.dsa.mentor.dtos.StudentDashboardResponse;
import com.dsa.mentor.entities.*;
import com.dsa.mentor.repositories.*;
import com.dsa.mentor.security.services.UserDetailsImpl;
import com.dsa.mentor.services.RecommendationService;
import com.dsa.mentor.services.RoadmapService;
import com.dsa.mentor.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private MockInterviewRepository mockInterviewRepository;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RoadmapService roadmapService;

    @GetMapping
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        UserStatistics stats = userService.getOrCreateUserStatistics(user);
        Optional<Roadmap> currentRoadmap = roadmapService.getLatestRoadmap(user);
        List<Recommendation> recommendations = recommendationService.getRecommendationsForUser(user);
        List<Submission> recentSubmissions = submissionRepository.findByUserOrderBySubmissionTimeDesc(user);
        
        // Limit to top 5 submissions
        List<Submission> topSubmissions = recentSubmissions.stream().limit(5).toList();

        StudentDashboardResponse response = StudentDashboardResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .streak(user.getCodingStreak())
                .solvedCount(user.getTotalProblemsSolved())
                .accuracy(user.getAccuracy())
                .currentLevel(user.getCurrentDsaLevel())
                .targetCompany(user.getTargetCompany())
                .targetRole(user.getTargetRole())
                .strengths(user.getTopicStrengths())
                .weaknesses(user.getTopicWeaknesses())
                .statistics(stats)
                .currentRoadmap(currentRoadmap.orElse(null))
                .recommendations(recommendations)
                .recentSubmissions(topSubmissions)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        long totalUsers = userRepository.count();
        long totalProblems = problemRepository.count();
        long totalSubmissions = submissionRepository.count();
        long totalMockInterviews = mockInterviewRepository.count();

        // Calculate overall average submission success rate
        double systemAccuracy = 0.0;
        if (totalSubmissions > 0) {
            long acceptedSubmissions = submissionRepository.findAll().stream()
                    .filter(s -> s.getResult() == SubmissionResult.ACCEPTED)
                    .count();
            systemAccuracy = ((double) acceptedSubmissions / totalSubmissions) * 100;
            systemAccuracy = Math.round(systemAccuracy * 100.0) / 100.0;
        }

        AdminDashboardResponse response = AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(totalUsers) // simple representation
                .totalProblems(totalProblems)
                .totalSubmissions(totalSubmissions)
                .totalMockInterviews(totalMockInterviews)
                .systemAccuracy(systemAccuracy)
                .apiUsageCalls(totalSubmissions * 2 + totalMockInterviews) // dynamic mock representation
                .build();

        return ResponseEntity.ok(response);
    }
}
