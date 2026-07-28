package com.dsa.mentor.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewRespondRequest {
    @NotBlank
    private String response; // User message or code snippet
}
