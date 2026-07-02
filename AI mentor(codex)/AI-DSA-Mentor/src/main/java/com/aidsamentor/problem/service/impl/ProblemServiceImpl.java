package com.aidsamentor.problem.service.impl;

import com.aidsamentor.exception.BadRequestException;
import com.aidsamentor.exception.ResourceNotFoundException;
import com.aidsamentor.problem.dto.ProblemRequest;
import com.aidsamentor.problem.dto.ProblemResponse;
import com.aidsamentor.problem.entity.Difficulty;
import com.aidsamentor.problem.entity.Problem;
import com.aidsamentor.problem.repository.ProblemRepository;
import com.aidsamentor.problem.service.ProblemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public ProblemResponse createProblem(ProblemRequest request) {
        if (problemRepository.existsByTitleIgnoreCase(request.title())) {
            throw new BadRequestException("Problem title already exists");
        }

        Problem problem = Problem.builder()
                .title(request.title())
                .description(request.description())
                .examples(List.copyOf(request.examples()))
                .constraints(request.constraints())
                .difficulty(request.difficulty())
                .tags(normalizeTags(request.tags()))
                .expectedComplexity(request.expectedComplexity())
                .build();

        return toResponse(problemRepository.save(problem));
    }

    @Override
    @Transactional
    public ProblemResponse updateProblem(Long id, ProblemRequest request) {
        Problem problem = findProblemById(id);
        if (problemRepository.existsByTitleIgnoreCaseAndIdNot(request.title(), id)) {
            throw new BadRequestException("Problem title already exists");
        }

        problem.setTitle(request.title());
        problem.setDescription(request.description());
        problem.setExamples(List.copyOf(request.examples()));
        problem.setConstraints(request.constraints());
        problem.setDifficulty(request.difficulty());
        problem.setTags(normalizeTags(request.tags()));
        problem.setExpectedComplexity(request.expectedComplexity());

        return toResponse(problemRepository.save(problem));
    }

    @Override
    @Transactional
    public void deleteProblem(Long id) {
        Problem problem = findProblemById(id);
        problemRepository.delete(problem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemResponse> getAllProblems() {
        return problemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemResponse> searchByTag(String tag) {
        return problemRepository.findByTagIgnoreCase(tag).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemResponse> searchByDifficulty(Difficulty difficulty) {
        return problemRepository.findByDifficulty(difficulty).stream()
                .map(this::toResponse)
                .toList();
    }

    private Problem findProblemById(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
    }

    private List<String> normalizeTags(List<String> tags) {
        return tags.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }

    private ProblemResponse toResponse(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getDescription(),
                List.copyOf(problem.getExamples()),
                problem.getConstraints(),
                problem.getDifficulty(),
                List.copyOf(problem.getTags()),
                problem.getExpectedComplexity(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
}
