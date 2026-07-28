package com.dsa.mentor.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Profile Details
    private String college;
    private Integer graduationYear;
    private String targetCompany;
    private String targetRole;
    
    @Builder.Default
    private String currentDsaLevel = "BEGINNER";
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String profilePhoto;

    // Stats
    @Builder.Default
    private Integer totalProblemsSolved = 0;
    
    @Builder.Default
    private Double accuracy = 0.0;
    
    @Builder.Default
    private Integer codingStreak = 0;

    // Skill gaps (comma-separated tags)
    private String topicStrengths;
    private String topicWeaknesses;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
