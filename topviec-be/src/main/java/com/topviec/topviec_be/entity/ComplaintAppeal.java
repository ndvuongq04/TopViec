package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.enums.complaints.AppealStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Kháng cáo của NTD sau khi bị xử lý vi phạm nhóm B.
 *
 * <p>NTD có thể nộp kháng cáo sau khi bị khóa tài khoản.
 * Admin xem xét và quyết định mở khóa sớm nếu lý do chính đáng.
 *
 * <p>Vòng đời trạng thái:
 * <pre>
 *   [pending] → (admin duyệt) → [approved] → mở khóa tài khoản sớm
 *   [pending] → (admin từ chối) → [rejected]
 * </pre>
 */
@Entity
@Table(name = "complaint_appeals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintAppeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** FK → complaints.id — khiếu nại bị kháng cáo */
    @Column(name = "complaint_id", nullable = false)
    private Long complaintId;

    /** FK → users.id — NTD nộp kháng cáo */
    @Column(name = "employer_id", nullable = false)
    private Long employerId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Trạng thái kháng cáo.
     * Giá trị hợp lệ: {@code pending} | {@code approved} | {@code rejected}
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = AppealStatus.PENDING.getValue();

    /** Ghi chú kết quả xử lý kháng cáo của Admin */
    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    /** FK → admin_users.id — admin xử lý kháng cáo */
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

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
