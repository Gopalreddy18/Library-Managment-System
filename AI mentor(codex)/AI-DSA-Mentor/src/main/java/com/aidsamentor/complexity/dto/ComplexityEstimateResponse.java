package com.aidsamentor.complexity.dto;

public record ComplexityEstimateResponse(
        String timeComplexity,
        String spaceComplexity,
        double confidence,
        String explanation
) {
}
