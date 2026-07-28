package com.dsa.mentor.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String sourceCode;

    @Column(nullable = false)
    private String language; // java, python, cpp, javascript

    private LocalDateTime submissionTime;

    private Long runtimeMs;

    private Long memoryKb;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionResult result;

    @PrePersist
    protected void onCreate() {
        submissionTime = LocalDateTime.now();
    }
}
