package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.annotation.Trackable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews", uniqueConstraints = @UniqueConstraint(columnNames = { "application_id", "round_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "round_id", nullable = false)
    private Long roundId;

    @Column(name = "slot_id")
    private Long slotId;

    @Trackable(label = "Thời gian phỏng vấn")
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Trackable(label = "Thời lượng (phút)")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Trackable(label = "Hình thức phỏng vấn")
    @Column(name = "interview_type", length = 20)
    private String interviewType;

    @Trackable(label = "Địa điểm")
    @Column(name = "location", columnDefinition = "TEXT")
    private String location;

    @Trackable(label = "Link họp online")
    @Column(name = "meeting_link", columnDefinition = "TEXT")
    private String meetingLink;

    @Trackable(label = "Trạng thái")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "confirmed_by_candidate", nullable = false)
    private Boolean confirmedByCandidate;

    /**
     * true  = lịch placeholder tạo tự động (startInterviewing / pass vòng tiếp theo)
     * false = lịch thật sự do NTT đặt hoặc UV chọn slot
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    // Lưu Redis
    // @Column(name = "reminder_count", nullable = false)
    // private Integer reminderCount;

    // @Column(name = "reminder_deadline")
    // private LocalDateTime reminderDeadline;

    // @Column(name = "last_reminded_at")
    // private LocalDateTime lastRemindedAt;

    @Column(name = "interviewer_note", columnDefinition = "TEXT")
    private String interviewerNote;

    @Column(name = "scheduled_by", nullable = false)
    private Long scheduledBy;

    // ── Relations ─────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", insertable = false, updatable = false)
    private InterviewRound round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", insertable = false, updatable = false)
    private InterviewSlot slot;

    // ── Audit ─────────────────────────────────────────────────────────────────

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.confirmedByCandidate == null)
            this.confirmedByCandidate = false;
        if (this.isDefault == null)
            this.isDefault = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
