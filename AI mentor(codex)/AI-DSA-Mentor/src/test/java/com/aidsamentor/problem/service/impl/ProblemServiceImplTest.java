package com.aidsamentor.problem.service.impl;

import com.aidsamentor.exception.BadRequestException;
import com.aidsamentor.exception.ResourceNotFoundException;
import com.aidsamentor.problem.dto.ProblemRequest;
import com.aidsamentor.problem.dto.ProblemResponse;
import com.aidsamentor.problem.entity.Difficulty;
import com.aidsamentor.problem.entity.Problem;
import com.aidsamentor.problem.repository.ProblemRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProblemServiceImplTest {

    private ProblemRepository problemRepository;
    private ProblemServiceImpl problemService;

    @BeforeEach
    void setUp() {
        problemRepository = mock(ProblemRepository.class);
        problemService = new ProblemServiceImpl(problemRepository);
    }

    @Test
    void createProblemSavesNormalizedTags() {
        ProblemRequest request = request("Two Sum", Difficulty.EASY, List.of("Array", "Hashing", "array"));
        when(problemRepository.existsByTitleIgnoreCase("Two Sum")).thenReturn(false);
        when(problemRepository.save(any(Problem.class))).thenAnswer(invocation -> {
            Problem problem = invocation.getArgument(0);
            problem.setId(1L);
            return problem;
        });

        ProblemResponse response = problemService.createProblem(request);

        ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);
        verify(problemRepository).save(problemCaptor.capture());
        assertThat(problemCaptor.getValue().getTags()).containsExactly("array", "hashing");
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.difficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void createProblemRejectsDuplicateTitle() {
        ProblemRequest request = request("Two Sum", Difficulty.EASY, List.of("array"));
        when(problemRepository.existsByTitleIgnoreCase("Two Sum")).thenReturn(true);

        assertThatThrownBy(() -> problemService.createProblem(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Problem title already exists");
    }

    @Test
    void updateProblemUpdatesExistingProblem() {
        Problem existingProblem = problem(10L, "Old Title", Difficulty.EASY, List.of("array"));
        ProblemRequest request = request("Updated Title", Difficulty.MEDIUM, List.of("DP", "Array"));
        when(problemRepository.findById(10L)).thenReturn(Optional.of(existingProblem));
        when(problemRepository.existsByTitleIgnoreCaseAndIdNot("Updated Title", 10L)).thenReturn(false);
        when(problemRepository.save(any(Problem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProblemResponse response = problemService.updateProblem(10L, request);

        assertThat(response.title()).isEqualTo("Updated Title");
        assertThat(response.difficulty()).isEqualTo(Difficulty.MEDIUM);
        assertThat(response.tags()).containsExactly("dp", "array");
    }

    @Test
    void deleteProblemThrowsWhenProblemDoesNotExist() {
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> problemService.deleteProblem(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Problem not found");
    }

    @Test
    void searchByDifficultyMapsResults() {
        when(problemRepository.findByDifficulty(Difficulty.HARD))
                .thenReturn(List.of(problem(1L, "Median of Two Sorted Arrays", Difficulty.HARD, List.of("binary-search"))));

        List<ProblemResponse> responses = problemService.searchByDifficulty(Difficulty.HARD);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().title()).isEqualTo("Median of Two Sorted Arrays");
    }

    private ProblemRequest request(String title, Difficulty difficulty, List<String> tags) {
        return new ProblemRequest(
                title,
                "Find the answer efficiently.",
                List.of("Input: nums = [2,7,11,15], target = 9 Output: [0,1]"),
                "1 <= n <= 10^5",
                difficulty,
                tags,
                "Time O(n), Space O(n)"
        );
    }

    private Problem problem(Long id, String title, Difficulty difficulty, List<String> tags) {
        return Problem.builder()
                .id(id)
                .title(title)
                .description("Find the answer efficiently.")
                .examples(List.of("Example"))
                .constraints("1 <= n <= 10^5")
                .difficulty(difficulty)
                .tags(tags)
                .expectedComplexity("Time O(n)")
                .build();
    }
}
