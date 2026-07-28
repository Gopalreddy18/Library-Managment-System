package com.dsa.mentor.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String logicCorrectnessFeedback;

    private String timeComplexity;
    private String spaceComplexity;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String optimizationSuggestions;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String bestPracticesFeedback;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String rawAiReview;
}
