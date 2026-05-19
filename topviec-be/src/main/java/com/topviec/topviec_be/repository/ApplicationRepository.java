package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

        boolean existsByJobPostIdAndCandidateUserIdAndDeletedAtIsNull(Long jobPostId, Long candidateUserId);

        Optional<Application> findByJobPostIdAndCandidateUserIdAndDeletedAtIsNull(Long jobPostId, Long candidateUserId);

        List<Application> findByJobPostIdAndDeletedAtIsNull(Long jobPostId);

        @Query("""
                        SELECT a FROM Application a
                        LEFT JOIN FETCH a.jobPosting
                        WHERE a.id = :id
                        AND a.candidateUserId = :candidateUserId
                        AND a.deletedAt IS NULL
                        """)
        Optional<Application> findByIdAndCandidateUserId(
                        @Param("id") Long id,
                        @Param("candidateUserId") Long candidateUserId);

        /**
         * Lấy danh sách đơn của UV — hỗ trợ lọc theo status
         * - status null → lấy tất cả
         */
        @Query("""
                        SELECT a FROM Application a
                        LEFT JOIN FETCH a.jobPosting
                        WHERE a.candidateUserId = :candidateUserId
                        AND a.deletedAt IS NULL
                        AND (:status IS NULL OR a.status = :status)
                        ORDER BY a.createdAt DESC
                        """)
        Page<Application> findByCandidate(
                        @Param("candidateUserId") Long candidateUserId,
                        @Param("status") String status,
                        Pageable pageable);

        /**
         * Lấy danh sách đơn theo job post — dành cho recruiter
         * - status null → lấy tất cả
         * - search null → không lọc theo tên/email
         */
        @Query("""
                        SELECT a FROM Application a
                        JOIN a.candidate u
                        WHERE a.jobPostId = :jobPostId
                        AND a.deletedAt IS NULL
                        AND (:status IS NULL OR a.status = :status)
                        AND (:search IS NULL
                            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR EXISTS (
                                SELECT cp FROM CandidateProfile cp
                                WHERE cp.userId = a.candidateUserId
                                AND cp.deletedAt IS NULL
                                AND LOWER(cp.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                            ))
                        ORDER BY a.createdAt DESC
                        """)
        Page<Application> findByJobPost(
                        @Param("jobPostId") Long jobPostId,
                        @Param("status") String status,
                        @Param("search") String search,
                        Pageable pageable);

        // ── Interview support ─────────────────────────────────────────────────────

        /**
         * Lấy các đơn của UV có ít nhất 1 lịch PV (không phân trang).
         * Dùng EXISTS để tránh duplicate khi 1 đơn có nhiều vòng PV.
         */
        @Query("""
                        SELECT a FROM Application a
                        LEFT JOIN FETCH a.jobPosting
                        WHERE a.candidateUserId = :candidateUserId
                        AND a.deletedAt IS NULL
                        AND EXISTS (
                            SELECT 1 FROM Interview i
                            WHERE i.applicationId = a.id
                            AND i.deletedAt IS NULL
                        )
                        ORDER BY a.updatedAt DESC
                        """)
        List<Application> findWithInterviewsByCandidate(@Param("candidateUserId") Long candidateUserId);

        List<Application> findByJobPostIdAndStatusAndDeletedAtIsNull(Long jobPostId, String status);

        /**
         * Bulk update status — tránh load toàn bộ entity về rồi save từng cái.
         * Dùng cho startInterviewing (CV_PASSED → INTERVIEWING)
         * và completeRecruitment (remaining → REJECTED).
         */
        @Modifying
        @Query("""
                        UPDATE Application a
                        SET a.status = :newStatus, a.updatedAt = CURRENT_TIMESTAMP
                        WHERE a.jobPostId = :jobPostId
                        AND a.status = :currentStatus
                        AND a.deletedAt IS NULL
                        """)
        int bulkUpdateStatus(
                        @Param("jobPostId") Long jobPostId,
                        @Param("currentStatus") String currentStatus,
                        @Param("newStatus") String newStatus);

        /**
         * Bulk reject tất cả CV chưa terminal (dùng cho completeRecruitment).
         */
        @Modifying
        @Query("""
                        UPDATE Application a
                        SET a.status = 'rejected', a.rejectedAt = CURRENT_TIMESTAMP, a.updatedAt = CURRENT_TIMESTAMP
                        WHERE a.jobPostId = :jobPostId
                        AND a.status NOT IN ('hired', 'rejected', 'withdrawn', 'expired')
                        AND a.deletedAt IS NULL
                        AND a.id NOT IN :excludeIds
                        """)
        int bulkRejectExcluding(
                        @Param("jobPostId") Long jobPostId,
                        @Param("excludeIds") List<Long> excludeIds);

        /**
         * Bulk chuyển SCHEDULE_PENDING → OVERDUE cho các application có tất cả
         * invitation đã qua deadline (không còn invitation nào deadline >= now).
         *
         * Nhận :now từ Java để tránh timezone mismatch với CURRENT_TIMESTAMP của DB.
         */
        @Modifying
        @Query("""
                        UPDATE Application a
                        SET a.status = 'overdue', a.updatedAt = :now
                        WHERE a.status = 'schedule_pending'
                        AND a.deletedAt IS NULL
                        AND EXISTS (
                            SELECT 1 FROM InterviewSlotInvitation inv
                            WHERE inv.applicationId = a.id
                            AND inv.deadline < :now
                        )
                        AND NOT EXISTS (
                            SELECT 1 FROM InterviewSlotInvitation inv2
                            WHERE inv2.applicationId = a.id
                            AND inv2.deadline >= :now
                        )
                        """)
        int bulkMarkOverdue(@Param("now") LocalDateTime now);

        /** Đếm số hồ sơ (chưa xóa) theo job post id. */
        long countByJobPostIdAndDeletedAtIsNull(Long jobPostId);

        /** Đếm số hồ sơ (chưa xóa) theo candidate user id. */
        long countByCandidateUserIdAndDeletedAtIsNull(Long candidateUserId);

        /**
         * Batch đếm số hồ sơ theo nhiều job — tránh N+1 khi hiển thị danh sách.
         * Trả về List<Object[]> gồm [jobPostId, count].
         */
        @Query("""
                        SELECT a.jobPostId, COUNT(a)
                        FROM Application a
                        WHERE a.jobPostId IN :jobPostIds
                        AND a.deletedAt IS NULL
                        GROUP BY a.jobPostId
                        """)
        List<Object[]> countByJobPostIds(@Param("jobPostIds") List<Long> jobPostIds);

        /**
         * Batch đếm số offer thành công (status = 'hired') theo nhiều job.
         * Trả về List<Object[]> gồm [jobPostId, count].
         */
        @Query("""
                        SELECT a.jobPostId, COUNT(a)
                        FROM Application a
                        WHERE a.jobPostId IN :jobPostIds
                        AND a.status = 'hired'
                        AND a.deletedAt IS NULL
                        GROUP BY a.jobPostId
                        """)
        List<Object[]> countHiredByJobPostIds(@Param("jobPostIds") List<Long> jobPostIds);

        /**
         * Đếm tổng số đơn ứng tuyển (CV) mà công ty đã nhận — qua tất cả tin tuyển dụng.
         */
        @Query("""
                        SELECT COUNT(a) FROM Application a
                        JOIN a.jobPosting j
                        WHERE j.companyId = :companyId
                        AND a.deletedAt IS NULL
                        AND j.deletedAt IS NULL
                        """)
        long countByCompanyId(@Param("companyId") Long companyId);

        @Query("""
                        SELECT COUNT(a) FROM Application a
                        JOIN a.jobPosting j
                        WHERE j.companyId = :companyId
                        AND a.status IN :statuses
                        AND a.createdAt >= :startDate
                        AND a.createdAt < :endDate
                        AND a.deletedAt IS NULL
                        AND j.deletedAt IS NULL
                        """)
        long countByCompanyAndStatusesAndDateRange(
                        @Param("companyId") Long companyId,
                        @Param("statuses") List<String> statuses,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                        SELECT COUNT(a) FROM Application a
                        JOIN a.jobPosting j
                        WHERE j.companyId = :companyId
                        AND a.status IN :statuses
                        AND a.deletedAt IS NULL
                        AND j.deletedAt IS NULL
                        """)
        long countByCompanyAndStatuses(
                        @Param("companyId") Long companyId,
                        @Param("statuses") List<String> statuses);

        @Query("""
                        SELECT a.status, COUNT(a)
                        FROM Application a
                        JOIN a.jobPosting j
                        WHERE j.companyId = :companyId
                        AND a.deletedAt IS NULL
                        AND j.deletedAt IS NULL
                        GROUP BY a.status
                        """)
        List<Object[]> countByCompanyGroupByStatus(@Param("companyId") Long companyId);

        @Query("""
                        SELECT a FROM Application a
                        JOIN FETCH a.jobPosting j
                        LEFT JOIN FETCH a.candidate
                        WHERE j.companyId = :companyId
                        AND a.status IN :statuses
                        AND a.deletedAt IS NULL
                        AND j.deletedAt IS NULL
                        ORDER BY a.createdAt ASC
                        """)
        List<Application> findPendingByCompany(
                        @Param("companyId") Long companyId,
                        @Param("statuses") List<String> statuses,
                        Pageable pageable);

        @Query(value = """
                        SELECT FLOOR(TIMESTAMPDIFF(DAY, :startDate, a.created_at) / 7) AS week_index, COUNT(*)
                        FROM applications a
                        JOIN job_postings j ON a.job_post_id = j.id
                        WHERE j.company_id = :companyId
                        AND a.created_at >= :startDate
                        AND a.created_at < :endDate
                        AND a.deleted_at IS NULL
                        AND j.deleted_at IS NULL
                        GROUP BY week_index
                        ORDER BY week_index
                        """, nativeQuery = true)
        List<Object[]> countWeeklyByCompany(
                        @Param("companyId") Long companyId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                        SELECT COUNT(a) FROM Application a
                        WHERE a.jobPostId IN :jobPostIds
                        AND a.status IN :statuses
                        AND a.deletedAt IS NULL
                        """)
        long countByJobPostIdsAndStatuses(
                        @Param("jobPostIds") List<Long> jobPostIds,
                        @Param("statuses") List<String> statuses);

        @Query("""
                        SELECT a.jobPostId, COUNT(a)
                        FROM Application a
                        WHERE a.jobPostId IN :jobPostIds
                        AND a.deletedAt IS NULL
                        GROUP BY a.jobPostId
                        """)
        List<Object[]> countByJobPostIdsGroupByJob(@Param("jobPostIds") List<Long> jobPostIds);

        @Query("""
                        SELECT a FROM Application a
                        JOIN FETCH a.jobPosting j
                        LEFT JOIN FETCH a.candidate
                        WHERE a.jobPostId IN :jobPostIds
                        AND a.status IN :statuses
                        AND a.deletedAt IS NULL
                        AND j.deletedAt IS NULL
                        ORDER BY a.createdAt ASC
                        """)
        List<Application> findPendingByJobPostIds(
                        @Param("jobPostIds") List<Long> jobPostIds,
                        @Param("statuses") List<String> statuses,
                        Pageable pageable);

        @Query("""
                        SELECT COUNT(a) FROM Application a
                        JOIN a.jobPosting j
                        WHERE j.companyId = :companyId
                        AND a.createdAt >= :startDate
                        AND a.createdAt < :endDate
                        AND a.deletedAt IS NULL
                        AND j.deletedAt IS NULL
                        """)
        long countByCompanyAndDateRange(
                        @Param("companyId") Long companyId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
