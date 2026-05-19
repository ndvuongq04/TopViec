package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Business Event Log — phân tích nghiệp vụ: "Hệ thống hoạt động ra sao?"
 */
@Entity
@Table(name = "business_event_logs", indexes = {
        @Index(name = "idx_biz_user_id", columnList = "user_id"),
        @Index(name = "idx_biz_action", columnList = "action"),
        @Index(name = "idx_biz_category", columnList = "category"),
        @Index(name = "idx_biz_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ai thực hiện (nullable — system/scheduler jobs) */
    @Column(name = "user_id")
    private Long userId;

    /** Tên action (enum name) */
    @Column(name = "action", nullable = false, length = 80)
    private String action;

    /** Category phân nhóm */
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    /** Loại entity chính bị tác động */
    @Column(name = "target_entity", length = 50)
    private String targetEntity;

    /** ID entity bị tác động */
    @Column(name = "target_id")
    private Long targetId;

    /**
     * Dữ liệu bổ sung linh hoạt (JSON).
     * VD: {"oldStatus": "PENDING", "newStatus": "APPROVED", "jobTitle": "Backend Dev"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "json")
    private Map<String, Object> metadata;

    /** SUCCESS hoặc FAILURE */
    @Column(name = "status", nullable = false, length = 10)
    private String status;

    /** Thời gian thực thi (ms) */
    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
