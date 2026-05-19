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
public class ResAdminOrderStatisticsDTO {

    /** Tổng số đơn hàng trong hệ thống */
    private long totalOrders;

    /** Tổng đơn hàng đã thanh toán (status = PAID) */
    private long paidOrders;

    /** Tổng đơn hàng đang chờ xử lý (status = PENDING) */
    private long pendingOrders;

    /** Tổng giá trị tất cả đơn hàng đã thanh toán */
    private BigDecimal totalRevenue;
}
