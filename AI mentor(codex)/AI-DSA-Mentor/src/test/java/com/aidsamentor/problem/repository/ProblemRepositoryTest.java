package com.aidsamentor.problem.repository;

import com.aidsamentor.problem.entity.Difficulty;
import com.aidsamentor.problem.entity.Problem;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Test
    void findByTagIgnoreCaseReturnsMatchingProblems() {
        problemRepository.save(problem("Two Sum", Difficulty.EASY, List.of("array", "hashing")));
        problemRepository.save(problem("Valid Parentheses", Difficulty.EASY, List.of("stack", "string")));

        List<Problem> results = problemRepository.findByTagIgnoreCase("ARRAY");

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getTitle()).isEqualTo("Two Sum");
    }

    @Test
    void findByDifficultyReturnsMatchingProblems() {
        problemRepository.save(problem("Two Sum", Difficulty.EASY, List.of("array")));
        problemRepository.save(problem("Word Ladder", Difficulty.HARD, List.of("bfs")));

        List<Problem> results = problemRepository.findByDifficulty(Difficulty.HARD);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getTitle()).isEqualTo("Word Ladder");
    }

    private Problem problem(String title, Difficulty difficulty, List<String> tags) {
        return Problem.builder()
                .title(title)
                .description("Problem description")
                .examples(List.of("Example 1"))
                .constraints("1 <= n <= 10^5")
                .difficulty(difficulty)
                .tags(tags)
                .expectedComplexity("Time O(n)")
                .build();
    }
}
