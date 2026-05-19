package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.JobPosting;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long>,
        JpaSpecificationExecutor<JobPosting> {

    Optional<JobPosting> findByIdAndDeletedAtIsNull(Long id);

    /** Dùng cho restore: tìm tin kể cả đã bị xóa mềm. */
    Optional<JobPosting> findById(Long id);

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    boolean existsBySlugAndIdNotAndDeletedAtIsNull(String slug, Long id);

    @Modifying
    @Query("UPDATE JobPosting j SET j.viewCount = j.viewCount + 1 WHERE j.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT j.companyId, COUNT(j) FROM JobPosting j WHERE j.companyId IN :companyIds AND j.status IN ('published', 'interviewing') AND j.deletedAt IS NULL GROUP BY j.companyId")
    List<Object[]> countActiveByCompanyIds(@Param("companyIds") List<Long> companyIds);

    @Query("SELECT COUNT(j) FROM JobPosting j WHERE j.companyId = :companyId AND j.status IN ('published', 'interviewing') AND j.deletedAt IS NULL")
    long countActiveByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT j.id FROM JobPosting j WHERE j.companyId = :companyId AND j.deletedAt IS NULL")
    List<Long> findIdsByCompanyId(@Param("companyId") Long companyId);

    /** Đếm tổng số tin tuyển dụng (chưa xóa mềm) theo công ty. */
    long countByCompanyIdAndDeletedAtIsNull(Long companyId);

    long countByCompanyIdAndStatusAndDeletedAtIsNull(Long companyId, String status);

    @Query("""
            SELECT COUNT(j) FROM JobPosting j
            WHERE j.companyId = :companyId
            AND j.status IN ('published', 'interviewing')
            AND j.deletedAt IS NULL
            AND j.expiredAt > :now
            AND j.expiredAt <= :sevenDaysLater
            """)
    long countExpiringByCompanyId(
            @Param("companyId") Long companyId,
            @Param("now") LocalDateTime now,
            @Param("sevenDaysLater") LocalDateTime sevenDaysLater);

    @Query("""
            SELECT COUNT(j) FROM JobPosting j
            WHERE j.status IN ('published', 'interviewing')
            AND j.deletedAt IS NULL
            """)
    long countActiveJobs();

    long countByStatusAndDeletedAtIsNull(String status);

    @Query("""
            SELECT COUNT(j) FROM JobPosting j
            WHERE j.status = 'rejected'
            AND j.updatedAt >= :startDate
            AND j.updatedAt < :endDate
            AND j.deletedAt IS NULL
            """)
    long countRejectedInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT FUNCTION('DATE', j.updatedAt), j.status, COUNT(j)
            FROM JobPosting j
            WHERE j.status IN ('published', 'rejected')
            AND j.updatedAt >= :startDate
            AND j.updatedAt < :endDate
            AND j.deletedAt IS NULL
            GROUP BY FUNCTION('DATE', j.updatedAt), j.status
            ORDER BY FUNCTION('DATE', j.updatedAt)
            """)
    List<Object[]> countModerationByDateAndStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT j FROM JobPosting j
            WHERE j.status = 'pending_approval'
            AND j.deletedAt IS NULL
            ORDER BY j.createdAt ASC
            """)
    List<JobPosting> findOldestPendingJobs(Pageable pageable);

    @Query("""
            SELECT j FROM JobPosting j
            WHERE j.companyId = :companyId
            AND j.deletedAt IS NULL
            ORDER BY j.createdAt DESC
            """)
    List<JobPosting> findRecentByCompany(@Param("companyId") Long companyId, Pageable pageable);

    @Query("""
            SELECT j.id FROM JobPosting j
            WHERE j.companyId = :companyId
            AND j.status IN ('published', 'interviewing')
            AND j.deletedAt IS NULL
            ORDER BY j.createdAt DESC
            """)
    List<Long> findActiveIdsByCompanyId(@Param("companyId") Long companyId);
}
