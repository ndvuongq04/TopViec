package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResEmployerInterviewStatisticsDTO {

    /** Tổng số lịch phỏng vấn của công ty */
    private long totalSchedules;

    /** Tổng số UV đã nộp CV nhưng chưa có lịch mới (isDefault = true) */
    private long pendingNewSchedules;

    /** Tổng số UV chưa xác nhận lịch PV (confirmedByCandidate = false) */
    private long unconfirmedSchedules;

    /** Tổng số lịch quá hạn (chưa xác nhận/lên lịch nhưng đã qua deadline hoặc application bị overdue) */
    private long overdueSchedules;
}
