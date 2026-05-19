package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response trả về khi Admin xem điểm vi phạm của một NTD.
 * Bao gồm thông tin tổng hợp và lịch sử từng lần vi phạm.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResViolationScoreDTO {

    private Long employerId;
    private String employerEmail;

    private CompanyInfo company;
    private ScoreInfo score;

    /** Lịch sử vi phạm sắp xếp mới nhất trước */
    private List<ViolationLogInfo> history;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyInfo {
        private Long id;
        private String name;
        private String logoUrl;
        /** Giá trị hợp lệ: {@code pending} | {@code active} | {@code suspended} | {@code deleted} */
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreInfo {
        private Integer totalScore;

        /**
         * Mức độ vi phạm hiện tại dựa trên tổng điểm.
         * Giá trị: {@code normal} (0–19) | {@code limited} (20–49) | {@code suspended} (≥50)
         */
        private String scoreLevel;

        /** Thời điểm vi phạm nhóm B gần nhất — dùng để kiểm tra điều kiện reset 6 tháng */
        private LocalDateTime lastGroupBViolationAt;

        /** Thời điểm Admin reset điểm về 0 gần nhất */
        private LocalDateTime lastResetAt;

        /** Tên Admin đã thực hiện reset gần nhất */
        private String resetByAdminName;

        /**
         * Admin có thể reset điểm về 0 không.
         * true nếu chưa từng vi phạm nhóm B hoặc vi phạm nhóm B gần nhất đã qua 6 tháng.
         */
        private Boolean canResetScore;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ViolationLogInfo {
        private Long id;
        /** Khớp với {@code complaint_type}: fraudulent | spam | wrong_info | ... */
        private String violationType;
        private Integer points;
        /** Nguồn phát hiện: {@code admin} | {@code system} | {@code complaint} */
        private String source;
        /** ID báo cáo liên quan. NULL nếu vi phạm do system phát hiện */
        private Long complaintId;
        private String note;
        /** Tên Admin tạo log. NULL nếu do system */
        private String createdByAdminName;
        private LocalDateTime createdAt;
    }
}
