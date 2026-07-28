package com.dsa.mentor.services;

import com.dsa.mentor.entities.*;
import com.dsa.mentor.repositories.ProblemRepository;
import com.dsa.mentor.repositories.RecommendationRepository;
import com.dsa.mentor.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    public List<Recommendation> getRecommendationsForUser(User user) {
        List<Recommendation> recs = recommendationRepository.findByUserOrderByCreatedAtDesc(user);
        if (recs.isEmpty()) {
            return generateRecommendations(user);
        }
        return recs;
    }

    @Transactional
    public List<Recommendation> generateRecommendations(User user) {
        // Clear previous recommendations
        recommendationRepository.deleteByUser(user);

        List<Recommendation> recommendations = new ArrayList<>();
        List<Problem> allProblems = problemRepository.findAll();
        
        // Find which problems the user already solved
        List<Submission> submissions = submissionRepository.findByUserOrderBySubmissionTimeDesc(user);
        List<Long> solvedProblemIds = submissions.stream()
                .filter(s -> s.getResult() == SubmissionResult.ACCEPTED)
                .map(s -> s.getProblem().getId())
                .collect(Collectors.toList());

        // 1. Weak Topics Recommendation
        String weak = user.getTopicWeaknesses();
        if (weak != null && !weak.isBlank()) {
            String[] weakTags = weak.split(",");
            for (String tag : weakTags) {
                tag = tag.trim();
                List<Problem> tagProblems = problemRepository.findByTag(tag);
                for (Problem problem : tagProblems) {
                    if (!solvedProblemIds.contains(problem.getId())) {
                        recommendations.add(Recommendation.builder()
                            .user(user)
                            .type(RecommendationType.TOPIC_REVISION)
                            .targetId(problem.getId())
                            .targetName(problem.getTitle())
                            .reasoning("Recommended because you need practice in your weak area: " + tag)
                            .build());
                        break; // recommend one problem per weak tag
                    }
                }
            }
        }

        // 2. Next Problem recommendation (EASY or MEDIUM problem user hasn't solved yet)
        Difficulty targetDifficulty = Difficulty.EASY;
        if ("INTERMEDIATE".equalsIgnoreCase(user.getCurrentDsaLevel())) {
            targetDifficulty = Difficulty.MEDIUM;
        } else if ("ADVANCED".equalsIgnoreCase(user.getCurrentDsaLevel())) {
            targetDifficulty = Difficulty.HARD;
        }

        final Difficulty searchDiff = targetDifficulty;
        List<Problem> difficultyProblems = allProblems.stream()
                .filter(p -> p.getDifficulty() == searchDiff)
                .filter(p -> !solvedProblemIds.contains(p.getId()))
                .toList();

        if (!difficultyProblems.isEmpty()) {
            Problem targetProblem = difficultyProblems.getFirst();
            recommendations.add(Recommendation.builder()
                .user(user)
                .type(RecommendationType.NEXT_PROBLEM)
                .targetId(targetProblem.getId())
                .targetName(targetProblem.getTitle())
                .reasoning("Suggested based on your current skill level (" + user.getCurrentDsaLevel() + ")")
                .build());
        }

        // 3. Target Company Recommendation
        String targetCompany = user.getTargetCompany();
        if (targetCompany != null && !targetCompany.isBlank()) {
            // Find problems matching tags or simple difficulty that fits target company
            List<Problem> companyProblems = allProblems.stream()
                    .filter(p -> !solvedProblemIds.contains(p.getId()))
                    .filter(p -> p.getDifficulty() == Difficulty.MEDIUM || p.getDifficulty() == Difficulty.HARD)
                    .toList();
            if (!companyProblems.isEmpty()) {
                Problem targetProblem = companyProblems.get(Math.min(1, companyProblems.size() - 1));
                recommendations.add(Recommendation.builder()
                    .user(user)
                    .type(RecommendationType.INTERVIEW_PREP)
                    .targetId(targetProblem.getId())
                    .targetName(targetProblem.getTitle())
                    .reasoning("Commonly asked in interviews at " + targetCompany)
                    .build());
            }
        }

        // Fallback if no specific recommendations could be generated
        if (recommendations.isEmpty() && !allProblems.isEmpty()) {
            Problem p = allProblems.getFirst();
            recommendations.add(Recommendation.builder()
                .user(user)
                .type(RecommendationType.NEXT_PROBLEM)
                .targetId(p.getId())
                .targetName(p.getTitle())
                .reasoning("Start practicing with this introductory problem.")
                .build());
        }

        return recommendationRepository.saveAll(recommendations);
    }
}
