package com.dsa.mentor.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminDashboardResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalProblems;
    private Long totalSubmissions;
    private Long totalMockInterviews;
    private Double systemAccuracy;
    private Long apiUsageCalls;
}
