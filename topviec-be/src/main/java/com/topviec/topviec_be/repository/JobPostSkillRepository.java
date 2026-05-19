package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.JobPostSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostSkillRepository extends JpaRepository<JobPostSkill, Long> {

    // Dùng derived query — đơn giản, không cần @Query
    List<JobPostSkill> findByJobPostId(Long jobPostId);

    @Modifying
    @Query("DELETE FROM JobPostSkill j WHERE j.jobPostId = :jobPostId")
    void deleteByJobPostId(@Param("jobPostId") Long jobPostId);
}