package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.JobPostAddon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPostAddonRepository extends JpaRepository<JobPostAddon, Long> {

    /** Đếm xem 1 tin tuyển dụng có đang kích hoạt dịch vụ nào đó không */
    @Query("SELECT COUNT(jpa) FROM JobPostAddon jpa " +
           "JOIN jpa.addonService asv " +
           "JOIN asv.service s " +
           "WHERE jpa.jobPostingId = :jobPostingId " +
           "AND s.code = :serviceCode " +
           "AND jpa.status = 'ACTIVE' " +
           "AND jpa.expiredAt > :now")
    long countActiveAddonForJob(@Param("jobPostingId") Long jobPostingId, 
                                @Param("serviceCode") String serviceCode, 
                                @Param("now") LocalDateTime now);

    /** Đếm tổng số tin đang sử dụng dịch vụ (dùng kiểm tra slot cho HOT) */
    @Query("SELECT COUNT(jpa) FROM JobPostAddon jpa " +
           "JOIN jpa.addonService asv " +
           "JOIN asv.service s " +
           "JOIN jpa.jobPosting j " +
           "WHERE s.code = :serviceCode " +
           "AND jpa.status = 'ACTIVE' " +
           "AND jpa.expiredAt > :now " +
           "AND j.deletedAt IS NULL " +
           "AND j.status = 'published'")
    long countActiveGlobalAddons(@Param("serviceCode") String serviceCode, 
                                 @Param("now") LocalDateTime now);

    /** Lấy danh sách tin đang sử dụng dịch vụ (dùng lấy tin HOT cho trang chủ) */
    @Query("SELECT jpa.jobPosting FROM JobPostAddon jpa " +
           "JOIN jpa.addonService asv " +
           "JOIN asv.service s " +
           "WHERE s.code = :serviceCode " +
           "AND jpa.status = 'ACTIVE' " +
           "AND jpa.expiredAt > :now " +
           "AND jpa.jobPosting.deletedAt IS NULL " +
           "AND jpa.jobPosting.status = 'published' " +
           "ORDER BY jpa.startedAt DESC")
    List<com.topviec.topviec_be.entity.JobPosting> findActiveGlobalAddonsPosts(
            @Param("serviceCode") String serviceCode, 
            @Param("now") LocalDateTime now, 
            Pageable pageable);

    /** Tìm các lần kích hoạt dịch vụ đã hết hạn (dùng cho scheduler) */
    @Query("SELECT jpa FROM JobPostAddon jpa " +
           "JOIN jpa.addonService asv " +
           "JOIN asv.service s " +
           "WHERE s.code = :serviceCode " +
           "AND jpa.status = 'ACTIVE' " +
           "AND jpa.expiredAt <= :now")
    List<JobPostAddon> findExpiredAddons(@Param("serviceCode") String serviceCode, 
                                         @Param("now") LocalDateTime now);
}
