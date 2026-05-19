package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    Optional<SavedJob> findByUserIdAndJobPostId(Long userId, Long jobPostId);

    boolean existsByUserIdAndJobPostId(Long userId, Long jobPostId);

    Page<SavedJob> findByUserId(Long userId, Pageable pageable);

    // Lấy tin sắp hết hạn trong 3 ngày (dùng cho notification)
    List<SavedJob> findByJobPostIdIn(List<Long> jobPostIds);

    void deleteByJobPostId(Long jobPostId);

    long countByUserId(Long userId);
}