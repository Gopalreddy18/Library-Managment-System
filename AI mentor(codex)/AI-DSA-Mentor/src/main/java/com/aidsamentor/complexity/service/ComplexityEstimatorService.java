package com.aidsamentor.complexity.service;

import com.aidsamentor.complexity.dto.ComplexityEstimateResponse;

public interface ComplexityEstimatorService {

    ComplexityEstimateResponse estimate(String sourceCode);
}
