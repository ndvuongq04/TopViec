package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Lịch sử từng lần cộng điểm vi phạm của NTD.
 * Tổng điểm hiện tại được duy trì tại {@link EmployerViolationScore} để truy vấn nhanh.
 * Bảng này phục vụ mục đích audit và tính điều kiện reset 6 tháng không tái phạm nhóm B.
 */
@Entity
@Table(name = "violation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** FK → users.id — NTD bị cộng điểm vi phạm */
    @Column(name = "employer_id", nullable = false)
    private Long employerId;

    /** FK → job_postings.id */
    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    /**
     * Loại vi phạm.
     * Giá trị hợp lệ: {@code fraudulent} | {@code spam} | {@code wrong_info}
     * | {@code inappropriate} | {@code payment_issue} | {@code other}
     */
    @Column(name = "violation_type", nullable = false, length = 20)
    private String violationType;

    /** Số điểm cộng vào tổng điểm vi phạm của NTD */
    @Column(name = "points", nullable = false)
    private Integer points;

    /**
     * Nguồn phát hiện vi phạm.
     * Giá trị hợp lệ: {@code admin} | {@code system} | {@code complaint}
     */
    @Column(name = "source", nullable = false, length = 20)
    private String source;

    /** FK → complaints.id. NULL nếu vi phạm do system tự phát hiện (trùng lặp) */
    @Column(name = "complaint_id")
    private Long complaintId;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    /** FK → admin_users.id — admin tạo log. NULL nếu do system tự phát hiện */
    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
