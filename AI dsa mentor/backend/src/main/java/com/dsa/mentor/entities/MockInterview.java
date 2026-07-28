package com.dsa.mentor.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewMode mode;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String currentQuestion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus interviewStatus;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String conversationHistory; // JSON array of interview dialogs

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String scorecard; // JSON string containing rating fields

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
