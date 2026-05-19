package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.JobPostAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostAssignmentRepository extends JpaRepository<JobPostAssignment, Long> {

    // ── Tìm phân công đang active (chưa thu hồi) ──────────────────────

    /** Tìm phân công đang active của tin tuyển dụng (1 tin chỉ có 1 NTD được phân công). */
    Optional<JobPostAssignment> findByJobPostIdAndRevokedAtIsNull(Long jobPostId);

    /** Tìm tất cả phân công đang active của một recruiter. */
    List<JobPostAssignment> findByUserIdAndRevokedAtIsNull(Long userId);

    /** Tìm phân công đang active theo tin và recruiter. */
    Optional<JobPostAssignment> findByJobPostIdAndUserIdAndRevokedAtIsNull(Long jobPostId, Long userId);

    // ── Kiểm tra ────────────────────────────────────────────────────────

    /** Kiểm tra tin đã có người được phân công hay chưa. */
    boolean existsByJobPostIdAndRevokedAtIsNull(Long jobPostId);

    // ── Đếm số tin đang quản lý của NTD ─────────────────────────────────

    /** Đếm số tin đang được phân công cho một recruiter. */
    long countByUserIdAndRevokedAtIsNull(Long userId);

    /** Đếm số tin đang quản lý cho danh sách recruiter (trong công ty). */
    @Query("""
            SELECT a.userId, COUNT(a) FROM JobPostAssignment a
            JOIN JobPosting jp ON a.jobPostId = jp.id
            WHERE a.userId IN :userIds AND a.revokedAt IS NULL
            AND jp.companyId = :companyId AND jp.deletedAt IS NULL
            GROUP BY a.userId
            """)
    List<Object[]> countActiveByUserIds(@Param("userIds") List<Long> userIds,
            @Param("companyId") Long companyId);

    // ── Phân trang ──────────────────────────────────────────────────────

    /** Lấy danh sách tin đang được phân công cho recruiter (phân trang). */
    @Query("""
            SELECT a FROM JobPostAssignment a
            WHERE a.userId = :userId AND a.revokedAt IS NULL
            ORDER BY a.assignedAt DESC
            """)
    Page<JobPostAssignment> findActiveByUserId(@Param("userId") Long userId, Pageable pageable);

    /** Lấy danh sách tất cả phân công (bao gồm đã thu hồi) của tin tuyển dụng (phân trang). */
    Page<JobPostAssignment> findByJobPostId(Long jobPostId, Pageable pageable);

    // ── Lấy danh sách tin đang phân công trong công ty ───────────────────

    /** Lấy danh sách phân công đang active theo danh sách job_post_ids (trong công ty). */
    @Query("""
            SELECT a FROM JobPostAssignment a
            WHERE a.jobPostId IN :jobPostIds AND a.revokedAt IS NULL
            """)
    List<JobPostAssignment> findActiveByJobPostIds(@Param("jobPostIds") List<Long> jobPostIds);

    /** Lấy danh sách phân công theo companyId qua join với job_postings. */
    @Query("""
            SELECT a FROM JobPostAssignment a
            JOIN JobPosting jp ON a.jobPostId = jp.id
            WHERE jp.companyId = :companyId AND a.revokedAt IS NULL
            AND jp.deletedAt IS NULL
            ORDER BY a.assignedAt DESC
            """)
    Page<JobPostAssignment> findActiveByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    /** Lấy danh sách jobPostId đang được phân công (active) trong công ty. */
    @Query("""
            SELECT a.jobPostId FROM JobPostAssignment a
            JOIN JobPosting jp ON a.jobPostId = jp.id
            WHERE jp.companyId = :companyId AND a.revokedAt IS NULL
            AND jp.deletedAt IS NULL
            """)
    List<Long> findAssignedJobPostIdsByCompanyId(@Param("companyId") Long companyId);

    @Query("""
            SELECT a.jobPostId FROM JobPostAssignment a
            JOIN JobPosting jp ON a.jobPostId = jp.id
            WHERE a.userId = :userId
            AND jp.companyId = :companyId
            AND a.revokedAt IS NULL
            AND jp.status IN ('published', 'interviewing')
            AND jp.deletedAt IS NULL
            ORDER BY jp.createdAt DESC
            """)
    List<Long> findActiveAssignedJobPostIds(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId);
}
