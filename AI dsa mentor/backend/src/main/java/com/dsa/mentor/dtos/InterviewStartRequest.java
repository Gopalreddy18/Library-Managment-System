package com.dsa.mentor.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewStartRequest {
    @NotBlank
    private String mode; // DSA, JAVA, SYSTEM_DESIGN, HR
}
