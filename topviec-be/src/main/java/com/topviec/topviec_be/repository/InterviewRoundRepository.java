package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRound, Long> {

    List<InterviewRound> findByJobPostIdAndDeletedAtIsNullOrderByRoundNumberAsc(Long jobPostId);

    @Query("SELECT COUNT(r) FROM InterviewRound r WHERE r.jobPostId = :jobPostId AND r.deletedAt IS NULL")
    long countByJobPostIdActive(@Param("jobPostId") Long jobPostId);

    @Query("SELECT r.jobPostId, COUNT(r) FROM InterviewRound r WHERE r.jobPostId IN :jobPostIds AND r.deletedAt IS NULL GROUP BY r.jobPostId")
    List<Object[]> countByJobPostIdsActive(@Param("jobPostIds") List<Long> jobPostIds);

    boolean existsByJobPostIdAndIsFinalTrueAndDeletedAtIsNull(Long jobPostId);

    Optional<InterviewRound> findByJobPostIdAndRoundNumberAndDeletedAtIsNull(Long jobPostId, Integer roundNumber);

    Optional<InterviewRound> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT COALESCE(MAX(r.roundNumber), 0) FROM InterviewRound r WHERE r.jobPostId = :jobPostId AND r.deletedAt IS NULL")
    int findMaxRoundNumber(@Param("jobPostId") Long jobPostId);

    /**
     * Tìm vòng tiếp theo sau round_number hiện tại.
     */
    @Query("""
            SELECT r FROM InterviewRound r
            WHERE r.jobPostId = :jobPostId
            AND r.roundNumber > :currentRoundNumber
            AND r.deletedAt IS NULL
            ORDER BY r.roundNumber ASC
            LIMIT 1
            """)
    Optional<InterviewRound> findNextRound(
            @Param("jobPostId") Long jobPostId,
            @Param("currentRoundNumber") Integer currentRoundNumber);
}
