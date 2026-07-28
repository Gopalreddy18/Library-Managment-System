package com.dsa.mentor.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoadmapRequest {
    @NotBlank
    private String targetCompany;

    @NotBlank
    private String currentSkillLevel; // BEGINNER, INTERMEDIATE, ADVANCED

    private String weakTopics; // Comma separated tags e.g. "Graphs,Dynamic Programming"
}
