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
public class ResContentModeratorDashboardDTO {
    private long pendingApprovalJobs;
    private long pendingVerifyCompanies;
    private long rejectedJobsThisMonth;
    private List<DailyModerationStat> moderationStats;
    private List<OldestPendingJob> oldestPendingJobs;
}
