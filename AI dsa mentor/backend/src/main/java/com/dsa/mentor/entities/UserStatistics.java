package com.dsa.mentor.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private Integer solvedCount = 0;

    @Builder.Default
    private Double successRate = 0.0;

    @Builder.Default
    private Double averageDifficulty = 0.0; // 1 = Easy, 2 = Medium, 3 = Hard

    private String improvementTrend; // e.g. "steady upward", "needs more consistency"

    private String weakTopics;
    private String strongTopics;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String dailyActivityData; // JSON representing { "2026-06-28": 3, "2026-06-29": 5 }
}
