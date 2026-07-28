package com.dsa.mentor.dtos;

import com.dsa.mentor.entities.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StudentDashboardResponse {
    private String name;
    private String email;
    private Integer streak;
    private Integer solvedCount;
    private Double accuracy;
    private String currentLevel;
    private String targetCompany;
    private String targetRole;
    private String strengths;
    private String weaknesses;
    private UserStatistics statistics;
    private Roadmap currentRoadmap;
    private List<Recommendation> recommendations;
    private List<Submission> recentSubmissions;
}
