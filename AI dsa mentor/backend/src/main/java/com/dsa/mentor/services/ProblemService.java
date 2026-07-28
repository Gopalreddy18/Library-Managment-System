package com.dsa.mentor.services;

import com.dsa.mentor.entities.Difficulty;
import com.dsa.mentor.entities.Problem;
import com.dsa.mentor.exceptions.ResourceNotFoundException;
import com.dsa.mentor.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    public Problem getProblemById(Long id) {
        return problemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + id));
    }

    public List<Problem> getProblemsByDifficulty(Difficulty difficulty) {
        return problemRepository.findByDifficulty(difficulty);
    }

    public List<Problem> getProblemsByTag(String tag) {
        return problemRepository.findByTag(tag);
    }

    public Problem createProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    public Problem updateProblem(Long id, Problem problemDetails) {
        Problem problem = getProblemById(id);
        problem.setTitle(problemDetails.getTitle());
        problem.setDescription(problemDetails.getDescription());
        problem.setDifficulty(problemDetails.getDifficulty());
        problem.setTags(problemDetails.getTags());
        problem.setConstraints(problemDetails.getConstraints());
        problem.setExamples(problemDetails.getExamples());
        problem.setTestCases(problemDetails.getTestCases());
        problem.setHiddenTestCases(problemDetails.getHiddenTestCases());
        problem.setEditorial(problemDetails.getEditorial());
        problem.setExpectedComplexity(problemDetails.getExpectedComplexity());
        
        return problemRepository.save(problem);
    }

    public void deleteProblem(Long id) {
        Problem problem = getProblemById(id);
        problemRepository.delete(problem);
    }
}
