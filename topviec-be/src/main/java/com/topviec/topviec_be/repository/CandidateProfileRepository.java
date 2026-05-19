package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CandidateProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.topviec.topviec_be.enums.users.UserStatus;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {

        Optional<CandidateProfile> findByUserId(Long userId);

        boolean existsByUserId(Long userId);

        List<CandidateProfile> findByUserIdIn(List<Long> userIds);

        @Modifying
        @Transactional
        @Query("UPDATE CandidateProfile c SET c.profileCompletionPct = :pct WHERE c.userId = :userId")
        void updateProfileCompletionPct(@Param("userId") Long userId, @Param("pct") int pct);

        @Query(value = """
                        SELECT cp FROM CandidateProfile cp
                        WHERE cp.deletedAt IS NULL
                        AND cp.cvPublic = true
                        AND (:locationId IS NULL OR cp.preferredLocationId = :locationId)
                        ORDER BY cp.updatedAt DESC
                        """, countQuery = """
                        SELECT COUNT(cp) FROM CandidateProfile cp
                        WHERE cp.deletedAt IS NULL
                        AND cp.cvPublic = true
                        AND (:locationId IS NULL OR cp.preferredLocationId = :locationId)
                        """)
        Page<CandidateProfile> searchCandidatesByLocation(
                        @Param("locationId") Integer locationId,
                        Pageable pageable);

        @Query(value = """
                        SELECT cp, u FROM CandidateProfile cp
                        JOIN User u ON cp.userId = u.id
                        WHERE u.userType = 'CANDIDATE'
                        AND (:status IS NULL OR u.status = :status)
                        AND (:keyword IS NULL OR (
                             LOWER(cp.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                             LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                             LOWER(cp.phoneDisplay) LIKE LOWER(CONCAT('%', :keyword, '%'))
                             OR (:userId IS NOT NULL AND u.id = :userId)
                        ))
                        """, countQuery = """
                        SELECT COUNT(cp) FROM CandidateProfile cp
                        JOIN User u ON cp.userId = u.id
                        WHERE u.userType = 'CANDIDATE'
                        AND (:status IS NULL OR u.status = :status)
                        AND (:keyword IS NULL OR (
                             LOWER(cp.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                             LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                             LOWER(cp.phoneDisplay) LIKE LOWER(CONCAT('%', :keyword, '%'))
                             OR (:userId IS NOT NULL AND u.id = :userId)
                        ))
                        """)
        Page<Object[]> searchAdminCandidates(
                        @Param("status") UserStatus status,
                        @Param("keyword") String keyword,
                        @Param("userId") Long userId,
                        Pageable pageable);
}