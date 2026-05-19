package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.JobRenewalLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRenewalLogRepository extends JpaRepository<JobRenewalLog, Long> {

    // Dùng @Query — query đơn giản, cố định, không cần Specification
    Page<JobRenewalLog> findByJobPostIdOrderByCreatedAtDesc(Long jobPostId, Pageable pageable);

    /**
     * Đếm số lần gia hạn miễn phí (orderId IS NULL) của 1 tin trong subscription.
     */
    @Query("""
            SELECT COUNT(r) FROM JobRenewalLog r
            WHERE r.jobPostId = :jobPostId
            AND r.orderId IS NULL
            """)
    int countFreeRenewalsByJobPostId(@Param("jobPostId") Long jobPostId);
}