package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response chi tiết một khiếu nại từ góc nhìn của NTD.
 * Không chứa danh tính người báo cáo, không chứa bằng chứng, không chứa ghi chú nội bộ Admin.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResEmployerComplaintDetailDTO {

    private Long id;
    private String reportCode;

    private JobPostInfo jobPost;

    private String complaintType;
    private String violationGroup;
    private String priority;
    private String status;

    /** Mô tả khiếu nại do UV nhập (hiển thị để NTD biết cần sửa gì) */
    private String description;

    // ── Thông tin xử lý nhóm A ────────────────────────────────────────────────

    /** Thời điểm hệ thống gửi email nhắc NTD sửa tin */
    private LocalDateTime emailSentAt;

    /** Deadline NTD phải sửa tin = emailSentAt + 48h */
    private LocalDateTime employerDeadline;

    /** Thời gian còn lại (giờ) trước deadline. 0 nếu đã hết hạn hoặc đã xử lý xong */
    private Long remainingHours;

    /** Thời điểm NTD bấm xác nhận đã sửa */
    private LocalDateTime employerRespondedAt;

    // ── Kết quả xử lý (hiển thị khi resolved / rejected) ─────────────────────

    /** Ghi chú kết quả xử lý của Admin (public — không chứa thông tin nội bộ) */
    private String resolutionNote;

    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
