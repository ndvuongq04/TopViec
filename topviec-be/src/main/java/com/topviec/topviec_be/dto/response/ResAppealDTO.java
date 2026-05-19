package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResAppealDTO {

    private Long id;
    private Long employerId;

    /** Thông tin báo cáo bị kháng cáo */
    private ComplaintInfo complaint;

    private String content;

    /**
     * Trạng thái kháng cáo.
     * Giá trị hợp lệ: {@code pending} | {@code approved} | {@code rejected}
     */
    private String status;

    /** Ghi chú Admin khi xử lý kháng cáo. NULL nếu chưa xử lý */
    private String adminNote;

    /** Admin đã xử lý kháng cáo */
    private AdminInfo reviewedByAdmin;

    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComplaintInfo {
        private Long id;
        private String reportCode;
        /** Giá trị hợp lệ: {@code fraudulent} | {@code payment_issue} | ... */
        private String complaintType;
        /** Giá trị hợp lệ: {@code A} | {@code B} */
        private String violationGroup;
        private String status;
        private Long jobPostId;
        private String jobPostTitle;
        private String companyName;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdminInfo {
        private Long adminUserId;
        private String fullName;
    }
}
