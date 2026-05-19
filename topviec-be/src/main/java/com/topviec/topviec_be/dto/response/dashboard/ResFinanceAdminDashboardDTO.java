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
public class ResFinanceAdminDashboardDTO {
    private BigDecimal monthlyRevenue;
    private long pendingOrders;
    private long refundRequests;
    private List<MonthlyRevenue> revenueByMonth;
    private List<ActionableOrder> actionableOrders;
}
