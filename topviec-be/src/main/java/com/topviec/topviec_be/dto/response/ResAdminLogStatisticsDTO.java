package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO thống kê log cho Admin Dashboard.
 *
 * 1. totalLogs          — tổng số log (audit + business) của tất cả admin
 * 2. criticalLogs       — số log có severity = HIGH hoặc CRITICAL
 * 3. systemErrors       — số log có status = FAILURE
 * 4. activeAdmins       — số admin đang active trong hệ thống
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminLogStatisticsDTO {

    /** Tổng số log (audit + business) all-time của tất cả admin */
    private long totalLogs;

    /** Số log có mức độ nghiêm trọng (severity = HIGH hoặc CRITICAL) */
    private long criticalLogs;

    /** Số log có status = FAILURE (lỗi hệ thống) */
    private long systemErrors;

    /** Số admin đang active trong hệ thống (isActive = true) */
    private long activeAdmins;
}
