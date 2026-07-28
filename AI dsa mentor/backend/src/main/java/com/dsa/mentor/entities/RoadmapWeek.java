package com.dsa.mentor.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roadmap_weeks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    private Integer weekNumber;
    private String topicTitle;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String conceptSummary;

    private String practiceProblems; // comma-separated problem IDs or titles

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String revisionTasks;

    private String mockTests;
}
