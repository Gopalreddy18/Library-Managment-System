package com.aidsamentor.problem.service;

import com.aidsamentor.problem.dto.ProblemRequest;
import com.aidsamentor.problem.dto.ProblemResponse;
import com.aidsamentor.problem.entity.Difficulty;
import java.util.List;

public interface ProblemService {

    ProblemResponse createProblem(ProblemRequest request);

    ProblemResponse updateProblem(Long id, ProblemRequest request);

    void deleteProblem(Long id);

    List<ProblemResponse> getAllProblems();

    List<ProblemResponse> searchByTag(String tag);

    List<ProblemResponse> searchByDifficulty(Difficulty difficulty);
}
