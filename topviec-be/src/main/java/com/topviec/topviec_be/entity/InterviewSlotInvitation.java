package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_slot_invitations",
        uniqueConstraints = @UniqueConstraint(columnNames = { "application_id", "round_id", "batch_number" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSlotInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "round_id", nullable = false)
    private Long roundId;

    @Column(name = "batch_number", nullable = false)
    private Integer batchNumber;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    // ── Relations ─────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", insertable = false, updatable = false)
    private InterviewRound round;
}
