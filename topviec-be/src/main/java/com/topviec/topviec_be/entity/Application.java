package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.annotation.Trackable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = { "job_post_id",
        "candidate_user_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_post_id", nullable = false)
    private Long jobPostId;

    @Column(name = "candidate_user_id", nullable = false)
    private Long candidateUserId;

    @Column(name = "cv_id", nullable = false)
    private Long cvId;

    // ── Relations ─────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", insertable = false, updatable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_user_id", insertable = false, updatable = false)
    private User candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", insertable = false, updatable = false)
    private Cvs cv;

    // ─────────────────────────────────────────────────────────────────────────

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cv_snapshot", nullable = false, columnDefinition = "json")
    private Object cvSnapshot;

    @Trackable(label = "Trạng thái đơn")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "apply_method", nullable = false, length = 20)
    private String applyMethod;

    @Trackable(label = "Đánh giá của NTD")
    @Column(name = "recruiter_rating")
    private Integer recruiterRating;

    @Trackable(label = "Ghi chú NTD")
    @Column(name = "recruiter_note", columnDefinition = "TEXT")
    private String recruiterNote;

    @Trackable(label = "Tags")
    @Column(name = "recruiter_tags", columnDefinition = "TEXT")
    private String recruiterTags;

    @Column(name = "withdrawal_reason", columnDefinition = "TEXT")
    private String withdrawalReason;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "hired_at")
    private LocalDateTime hiredAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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