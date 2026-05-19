package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response danh sách khiếu nại mà NTD bị báo cáo.
 * Không chứa thông tin định danh của người báo cáo (UV).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResEmployerComplaintSummaryDTO {

    private Long id;
    private String reportCode;

    private JobPostInfo jobPost;

    /** Giá trị hợp lệ: {@code wrong_info} | {@code spam} | {@code payment_issue} | ... */
    private String complaintType;
    /** Giá trị hợp lệ: {@code A} (nhẹ) | {@code B} (nặng) */
    private String violationGroup;
    /** Giá trị hợp lệ: {@code urgent} | {@code important} | {@code normal} */
    private String priority;
    /** Giá trị hợp lệ: {@code pending} | {@code processing} | {@code waiting_employer} | {@code resolved} | {@code rejected} | {@code auto_closed} */
    private String status;

    /** Deadline NTD phải sửa tin (chỉ có với nhóm A ở trạng thái waiting_employer) */
    private LocalDateTime employerDeadline;
    private Long remainingHours;

    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JobPostInfo {
        private Long id;
        private String title;
        private String status;
    }
}
