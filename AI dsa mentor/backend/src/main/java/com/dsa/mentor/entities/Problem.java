package com.dsa.mentor.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @NotBlank
    @Column(nullable = false)
    private String tags; // e.g. "Arrays,HashMap"

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String constraints;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String examples; // JSON string representing examples

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String testCases; // JSON string representing sample test cases

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String hiddenTestCases; // JSON string representing hidden test cases

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String editorial;

    private String expectedComplexity; // e.g. "O(N) time, O(1) space"
}
