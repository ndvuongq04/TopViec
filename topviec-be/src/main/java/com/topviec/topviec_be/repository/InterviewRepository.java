package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

        Optional<Interview> findByApplicationIdAndRoundIdAndDeletedAtIsNull(Long applicationId, Long roundId);

        Optional<Interview> findByIdAndDeletedAtIsNull(Long id);

        List<Interview> findByApplicationIdAndDeletedAtIsNullOrderByRoundId(Long applicationId);

        /**
         * Lấy danh sách lịch PV theo job post, lọc theo round và status.
         */
        @Query("""
                        SELECT i FROM Interview i
                        JOIN i.application a
                        WHERE a.jobPostId = :jobPostId
                        AND i.deletedAt IS NULL
                        AND a.deletedAt IS NULL
                        AND (:roundId IS NULL OR i.roundId = :roundId)
                        AND (:status IS NULL OR i.status = :status)
                        ORDER BY i.scheduledAt ASC
                        """)
        List<Interview> findByJobPostId(
                        @Param("jobPostId") Long jobPostId,
                        @Param("roundId") Long roundId,
                        @Param("status") String status);



        boolean existsByApplicationIdAndRoundId(Long applicationId, Long roundId);

        boolean existsByApplicationIdAndRoundIdAndDeletedAtIsNull(Long applicationId, Long roundId);

        /**
         * Kiểm tra UV đã có lịch THẬT (isDefault = false) cho vòng này chưa.
         * Dùng để phân biệt lịch placeholder tạo bởi startInterviewing.
         */
        boolean existsByApplicationIdAndRoundIdAndIsDefaultFalseAndDeletedAtIsNull(Long applicationId, Long roundId);

        /**
         * UV chưa có lịch PV thật (isDefault = true)
         * VÀ chưa được gửi slot (không có InterviewSlotInvitation) trong vòng này.
         */
        @Query("""
                        SELECT i FROM Interview i
                        JOIN i.application a
                        WHERE i.roundId = :roundId
                        AND i.isDefault = true
                        AND i.deletedAt IS NULL
                        AND a.deletedAt IS NULL
                        AND NOT EXISTS (
                            SELECT 1 FROM InterviewSlotInvitation inv
                            WHERE inv.applicationId = i.applicationId
                            AND inv.roundId = :roundId
                        )
                        ORDER BY i.createdAt ASC
                        """)
        List<Interview> findPendingCandidatesByRoundId(@Param("roundId") Long roundId);

        @Query("""
                        SELECT COUNT(i) FROM Interview i
                        JOIN i.application a
                        JOIN a.jobPosting jp
                        WHERE jp.companyId = :companyId
                        AND i.deletedAt IS NULL
                        AND jp.deletedAt IS NULL
                        """)
        long countSchedulesByCompanyId(@Param("companyId") Long companyId);

        @Query("""
                        SELECT COUNT(i) FROM Interview i
                        JOIN i.application a
                        JOIN a.jobPosting jp
                        WHERE jp.companyId = :companyId
                        AND i.isDefault = true
                        AND i.deletedAt IS NULL
                        AND jp.deletedAt IS NULL
                        """)
        long countPendingNewSchedulesByCompanyId(@Param("companyId") Long companyId);

        @Query("""
                        SELECT COUNT(i) FROM Interview i
                        JOIN i.application a
                        JOIN a.jobPosting jp
                        WHERE jp.companyId = :companyId
                        AND i.isDefault = false
                        AND i.confirmedByCandidate = false
                        AND i.status = 'scheduled'
                        AND i.deletedAt IS NULL
                        AND jp.deletedAt IS NULL
                        """)
        long countUnconfirmedSchedulesByCompanyId(@Param("companyId") Long companyId);

        @Query("""
                        SELECT COUNT(i) FROM Interview i
                        JOIN i.application a
                        JOIN a.jobPosting jp
                        WHERE jp.companyId = :companyId
                        AND i.status = 'scheduled'
                        AND i.scheduledAt < CURRENT_TIMESTAMP
                        AND i.deletedAt IS NULL
                        AND jp.deletedAt IS NULL
                        """)
        long countOverdueSchedulesByCompanyId(@Param("companyId") Long companyId);

        @Query("""
                        SELECT COUNT(i) FROM Interview i
                        JOIN i.application a
                        JOIN a.jobPosting jp
                        WHERE jp.companyId = :companyId
                        AND i.status = 'scheduled'
                        AND i.scheduledAt >= :from
                        AND i.scheduledAt < :to
                        AND i.deletedAt IS NULL
                        AND a.deletedAt IS NULL
                        AND jp.deletedAt IS NULL
                        """)
        long countUpcomingByCompany(
                        @Param("companyId") Long companyId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("""
                        SELECT COUNT(i) FROM Interview i
                        JOIN i.application a
                        WHERE a.jobPostId IN :jobPostIds
                        AND i.status = 'scheduled'
                        AND i.scheduledAt >= :from
                        AND i.scheduledAt < :to
                        AND i.deletedAt IS NULL
                        AND a.deletedAt IS NULL
                        """)
        long countUpcomingByJobPostIds(
                        @Param("jobPostIds") List<Long> jobPostIds,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);
}
