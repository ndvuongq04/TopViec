package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.JobPostLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostLocationRepository extends JpaRepository<JobPostLocation, Long> {

    List<JobPostLocation> findByJobPostId(Long jobPostId);

    @Query("SELECT l FROM JobPostLocation l LEFT JOIN FETCH l.province WHERE l.jobPostId IN :jobPostIds")
    List<JobPostLocation> findByJobPostIdInWithProvince(@Param("jobPostIds") List<Long> jobPostIds);

    @Query("SELECT l FROM JobPostLocation l LEFT JOIN FETCH l.province WHERE l.jobPostId = :jobPostId")
    List<JobPostLocation> findByJobPostIdWithProvince(@Param("jobPostId") Long jobPostId);

    @Modifying
    @Query("DELETE FROM JobPostLocation j WHERE j.jobPostId = :jobPostId")
    void deleteByJobPostId(@Param("jobPostId") Long jobPostId);
}