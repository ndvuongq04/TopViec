package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.EmployerViolationScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerViolationScoreRepository extends JpaRepository<EmployerViolationScore, Long> {

    Optional<EmployerViolationScore> findByEmployerId(Long employerId);

    List<EmployerViolationScore> findByTotalScoreGreaterThan(Integer totalScore);

    long countByTotalScoreGreaterThanEqual(Integer totalScore);
}
