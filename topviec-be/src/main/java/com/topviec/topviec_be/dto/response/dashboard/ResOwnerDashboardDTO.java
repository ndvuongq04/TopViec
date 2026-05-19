package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResOwnerDashboardDTO {
    private long activeJobs;
    private long newApplicationsThisMonth;
    private long activeMembers;
    private long activeSubscriptions;
    private List<WeeklyApplicationStat> weeklyApplications;
    private List<RecentJobSummary> recentJobs;
}
