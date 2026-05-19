package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResJobPostingStatisticsDTO {

    /** Tổng số lượt xem */
    private int viewCount;

    /** Tổng số UV đã nộp CV (đơn ứng tuyển) */
    private long applicationCount;

    /** Số lần sửa tin (vd: "0/1" hoặc "1/1") */
    private String editStatus;

    /** Tổng số ngày còn lại của tin này (tính đến deadline/expiredAt) */
    private long remainingDays;
}
