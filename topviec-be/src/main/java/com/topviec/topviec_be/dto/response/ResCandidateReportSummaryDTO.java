package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response danh sách lịch sử khiếu nại của UV (candidate).
 * Chỉ chứa thông tin liên quan đến UV, không bao gồm thông tin nội bộ Admin.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResCandidateReportSummaryDTO {

    private Long id;
    private String reportCode;

    private JobPostInfo jobPost;
    private CompanyInfo company;

    /** Lý do khiếu nại: wrong_info | spam | inappropriate | fraudulent | payment_issue | other */
    private String complaintType;

    /** Trạng thái: pending | processing | waiting_employer | resolved | rejected | auto_closed */
    private String status;

    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobPostInfo {
        private Long id;
        private String title;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfo {
        private Long id;
        private String name;
        private String logoUrl;
    }
}
