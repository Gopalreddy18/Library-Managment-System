package com.aidsamentor.problem.dto;

import com.aidsamentor.problem.entity.Difficulty;
import java.time.Instant;
import java.util.List;

public record ProblemResponse(
        Long id,
        String title,
        String description,
        List<String> examples,
        String constraints,
        Difficulty difficulty,
        List<String> tags,
        String expectedComplexity,
        Instant createdAt,
        Instant updatedAt
) {
}
