package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.JobPostEditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostEditLogRepository extends JpaRepository<JobPostEditLog, Long> {

    // Dùng @Query vì cần lọc theo job + sắp xếp — query đơn giản, cố định
    // Dùng khi admin muốn xem lịch sử chỉnh sửa của 1 tin
    Page<JobPostEditLog> findByJobPostIdOrderByCreatedAtDesc(Long jobPostId, Pageable pageable);
}