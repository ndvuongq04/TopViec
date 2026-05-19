package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.TalentPool;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TalentPoolRepository extends JpaRepository<TalentPool, Long> {

    boolean existsByCompanyIdAndCandidateUserId(Long companyId, Long candidateUserId);

    Optional<TalentPool> findByCompanyIdAndCandidateUserId(Long companyId, Long candidateUserId);

    @Query("""
            SELECT tp.candidateUserId FROM TalentPool tp
            WHERE tp.companyId = :companyId
            AND tp.deletedAt IS NULL
            AND tp.candidateUserId IN :candidateUserIds
            """)
    List<Long> findExistingCandidateUserIds(
            @Param("companyId") Long companyId,
            @Param("candidateUserIds") List<Long> candidateUserIds);

    @Query(value = """
            SELECT tp FROM TalentPool tp
            WHERE tp.companyId = :companyId
            AND tp.deletedAt IS NULL
            AND (:source IS NULL OR tp.source = :source)
            AND (
                :search IS NULL
                OR EXISTS (
                    SELECT cp FROM CandidateProfile cp
                    WHERE cp.userId = tp.candidateUserId
                    AND LOWER(cp.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                OR EXISTS (
                    SELECT u FROM User u
                    WHERE u.id = tp.candidateUserId
                    AND LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            )
            ORDER BY tp.createdAt DESC
            """, countQuery = """
            SELECT COUNT(tp) FROM TalentPool tp
            WHERE tp.companyId = :companyId
            AND tp.deletedAt IS NULL
            AND (:source IS NULL OR tp.source = :source)
            AND (
                :search IS NULL
                OR EXISTS (
                    SELECT cp FROM CandidateProfile cp
                    WHERE cp.userId = tp.candidateUserId
                    AND LOWER(cp.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                OR EXISTS (
                    SELECT u FROM User u
                    WHERE u.id = tp.candidateUserId
                    AND LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            )
            """)
    Page<TalentPool> findByCompanyWithFilter(
            @Param("companyId") Long companyId,
            @Param("source") String source,
            @Param("search") String search,
            Pageable pageable);
}
