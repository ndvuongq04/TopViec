package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_id", nullable = false, unique = true)
    private Long interviewId;

    @Column(name = "result", nullable = false, length = 20)
    private String result;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "notify_candidate", nullable = false)
    private Boolean notifyCandidate;

    @Column(name = "evaluated_by", nullable = false)
    private Long evaluatedBy;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relations ─────────────────────────────────────────────────────────────

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", insertable = false, updatable = false)
    private Interview interview;

    @PrePersist
    protected void onCreate() {
        if (this.evaluatedAt == null)
            this.evaluatedAt = LocalDateTime.now();
        if (this.notifyCandidate == null)
            this.notifyCandidate = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
