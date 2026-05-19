package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResSuperAdminDashboardDTO {
    private long totalActiveUsers;
    private long totalActiveCompanies;
    private long totalPublishedJobs;
    private BigDecimal monthlyRevenue;
    private List<DailyUserGrowth> userGrowth;
    private List<MonthlyRevenue> revenueByMonth;
    private List<RecentAdminActivity> recentActivities;
}
