package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.enums.complaints.ComplaintPriority;
import com.topviec.topviec_be.enums.complaints.ComplaintStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ khiếu nại tin tuyển dụng từ ứng viên.
 *
 * <p>Vòng đời trạng thái:
 * <pre>
 *   [pending] → (admin tiếp nhận) → [processing]
 *   [processing] → (gửi email NTD - nhóm A) → [waiting_employer]
 *   [waiting_employer] → (NTD tự sửa) → [auto_closed]
 *   [waiting_employer] → (NTD không sửa sau 72h) → ẩn tin, giữ nguyên trạng thái
 *   [processing] → (admin xử lý xong - nhóm B) → [resolved]
 *   [processing] → (admin từ chối) → [rejected]
 * </pre>
 *
 * <p>Giới hạn báo cáo:
 * <ul>
 *   <li>Mỗi UV tối đa 3 lần/ngày</li>
 *   <li>Mỗi tin chỉ được báo cáo 1 lần/UV</li>
 *   <li>Tài khoản dưới 7 ngày không được báo cáo</li>
 * </ul>
 *
 * <p>Mapping nhóm vi phạm:
 * <ul>
 *   <li>Nhóm A (nhẹ): {@code wrong_info}, {@code spam}, {@code inappropriate}</li>
 *   <li>Nhóm B (nặng, cộng điểm + bắt buộc bằng chứng): {@code fraudulent}, {@code payment_issue}</li>
 *   <li>{@code other}: Admin tự phân nhóm khi xử lý</li>
 * </ul>
 */
@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** FK → job_postings.id */
    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    /** FK → users.id — UV thực hiện báo cáo, bắt buộc đăng nhập */
    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

    /**
     * Loại vi phạm.
     * Giá trị hợp lệ: {@code fraudulent} | {@code spam} | {@code wrong_info}
     * | {@code inappropriate} | {@code payment_issue} | {@code other}
     */
    @Column(name = "complaint_type", nullable = false, length = 20)
    private String complaintType;

    /**
     * Nhóm vi phạm — quyết định luồng xử lý và có cộng điểm hay không.
     * Giá trị hợp lệ: {@code A} (nhẹ, chỉ cảnh báo) | {@code B} (nặng, cộng điểm + bằng chứng)
     */
    @Column(name = "violation_group", nullable = false, length = 2)
    private String violationGroup;

    /** Mô tả chi tiết khiếu nại, tối đa 500 ký tự */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Mức độ ưu tiên xử lý.
     * Giá trị hợp lệ: {@code urgent} | {@code important} | {@code normal}
     */
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private String priority = ComplaintPriority.NORMAL.getValue();

    /**
     * Trạng thái xử lý.
     * Giá trị hợp lệ: {@code pending} | {@code processing} | {@code waiting_employer}
     * | {@code resolved} | {@code rejected} | {@code auto_closed}
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = ComplaintStatus.PENDING.getValue();

    /** FK → admin_users.id — admin được phân công xử lý. NULL nếu chưa phân công */
    @Column(name = "assigned_to")
    private Long assignedTo;

    /** Thời điểm hệ thống gửi email nhắc NTD sửa tin (nhóm A). NULL nếu chưa gửi */
    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    /** Deadline NTD phải sửa tin = email_sent_at + 48h (nhóm A) */
    @Column(name = "employer_deadline")
    private LocalDateTime employerDeadline;

    /** Thời điểm NTD xác nhận đã sửa → trigger chuyển sang auto_closed (nhóm A) */
    @Column(name = "employer_responded_at")
    private LocalDateTime employerRespondedAt;

    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** FK → users.id hoặc admin_users.id — người thực hiện cập nhật gần nhất */
    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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
