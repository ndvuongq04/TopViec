package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.InterviewSlotInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSlotInvitationRepository extends JpaRepository<InterviewSlotInvitation, Long> {

    List<InterviewSlotInvitation> findByApplicationIdAndRoundIdOrderByBatchNumberAsc(Long applicationId, Long roundId);

    /** Lấy batch number lớn nhất hiện tại của 1 round để tính batch tiếp theo. */
    @Query("SELECT COALESCE(MAX(i.batchNumber), 0) FROM InterviewSlotInvitation i WHERE i.roundId = :roundId")
    int findMaxBatchNumber(@Param("roundId") Long roundId);

    /** Lấy tất cả invitation của 1 round (để map batchNumber → deadline). */
    List<InterviewSlotInvitation> findByRoundId(Long roundId);

    /** Lấy invitation mới nhất (batch lớn nhất) của 1 UV trong 1 round. */
    Optional<InterviewSlotInvitation> findTopByApplicationIdAndRoundIdOrderByBatchNumberDesc(Long applicationId, Long roundId);

    /**
     * Lấy các invitation sắp hết hạn (deadline nằm trong khoảng [now, now + 1 ngày])
     * mà UV vẫn chưa chọn slot (application vẫn ở trạng thái SCHEDULE_PENDING).
     * Dùng cho cron job nhắc nhở tự động.
     */
    @Query("""
            SELECT inv FROM InterviewSlotInvitation inv
            JOIN inv.application a
            WHERE a.status = 'schedule_pending'
            AND a.deletedAt IS NULL
            AND inv.deadline >= :now
            AND inv.deadline < :oneDayLater
            AND inv.id = (
                SELECT MAX(inv2.id) FROM InterviewSlotInvitation inv2
                WHERE inv2.applicationId = inv.applicationId
                AND inv2.roundId = inv.roundId
            )
            """)
    List<InterviewSlotInvitation> findApproachingDeadlineInvitations(
            @Param("now") LocalDateTime now,
            @Param("oneDayLater") LocalDateTime oneDayLater);
}
