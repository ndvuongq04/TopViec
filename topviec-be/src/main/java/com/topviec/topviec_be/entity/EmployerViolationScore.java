package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tổng điểm vi phạm hiện tại của từng NTD.
 * Được cập nhật sau mỗi lần thêm {@link ViolationLog}, tránh tính SUM mỗi lần truy vấn.
 *
 * <p>Ngưỡng điểm và hành động:
 * <ul>
 *   <li>0–19: Bình thường — hoạt động đầy đủ</li>
 *   <li>20–49: Hạn chế — tối đa 3 tin/tuần, tin mới cần Admin duyệt trước khi hiển thị</li>
 *   <li>≥ 50: Khóa tài khoản 30 ngày, toàn bộ tin hiện tại bị ẩn</li>
 * </ul>
 *
 * <p>Cơ chế giảm điểm:
 * <ul>
 *   <li>Không tái phạm nhóm B trong 6 tháng → Admin reset về 0 (kiểm tra qua last_group_b_violation_at)</li>
 *   <li>NTD chủ động liên hệ và khắc phục → Admin giảm điểm thủ công</li>
 * </ul>
 */
@Entity
@Table(name = "employer_violation_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerViolationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** FK → users.id — mỗi NTD chỉ có 1 bản ghi (UNIQUE) */
    @Column(name = "employer_id", nullable = false, unique = true)
    private Long employerId;

    @Column(name = "total_score", nullable = false)
    @Builder.Default
    private Integer totalScore = 0;

    /** Thời điểm vi phạm nhóm B gần nhất — dùng để kiểm tra điều kiện reset 6 tháng */
    @Column(name = "last_group_b_violation_at")
    private LocalDateTime lastGroupBViolationAt;

    /** Thời điểm Admin reset điểm về 0 gần nhất */
    @Column(name = "last_reset_at")
    private LocalDateTime lastResetAt;

    /** FK → admin_users.id — admin thực hiện reset */
    @Column(name = "reset_by")
    private Long resetBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
