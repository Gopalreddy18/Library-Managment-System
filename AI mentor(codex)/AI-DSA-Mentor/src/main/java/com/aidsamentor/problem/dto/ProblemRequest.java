package com.aidsamentor.problem.dto;

import com.aidsamentor.problem.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProblemRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 180, message = "Title must not exceed 180 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotEmpty(message = "At least one example is required")
        List<@NotBlank(message = "Example must not be blank") String> examples,

        @NotBlank(message = "Constraints are required")
        String constraints,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @NotEmpty(message = "At least one tag is required")
        List<@NotBlank(message = "Tag must not be blank") String> tags,

        @NotBlank(message = "Expected complexity is required")
        @Size(max = 120, message = "Expected complexity must not exceed 120 characters")
        String expectedComplexity
) {
}
