package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Audit Log — truy vết bảo mật: "Ai đã làm gì, khi nào?"
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user_id", columnList = "user_id"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_category", columnList = "category"),
        @Index(name = "idx_audit_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ai thực hiện hành động */
    @Column(name = "user_id")
    private Long userId;

    /** Tên action (enum name) */
    @Column(name = "action", nullable = false, length = 80)
    private String action;

    /** Category phân nhóm */
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    /** Mức độ nghiêm trọng */
    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    /** Loại entity bị tác động (COMPANY, USER, JOB_POSTING...) */
    @Column(name = "target_entity", length = 50)
    private String targetEntity;

    /** ID của entity bị tác động */
    @Column(name = "target_id")
    private Long targetId;

    /** Mô tả chi tiết */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Địa chỉ IP client */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** Thông tin trình duyệt */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /** SUCCESS hoặc FAILURE */
    @Column(name = "status", nullable = false, length = 10)
    private String status;

    /** Thời gian thực thi (ms) */
    @Column(name = "duration_ms")
    private Long durationMs;

    /** Thông báo lỗi nếu FAILURE */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
