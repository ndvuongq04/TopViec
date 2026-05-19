package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminServiceStatisticsDTO {

    /** Tổng số gói dịch vụ trong hệ thống */
    private long totalServicePackages;

    /** Doanh thu trung bình trên mỗi đơn hàng đã thanh toán */
    private BigDecimal averageRevenue;

    /**
     * Tỉ lệ chuyển đổi (%)
     * = (Số công ty có ít nhất 1 đơn hàng đã thanh toán / Tổng số công ty) × 100
     */
    private double conversionRate;
}
