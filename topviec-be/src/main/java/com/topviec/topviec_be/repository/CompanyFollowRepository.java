package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CompanyFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyFollowRepository extends JpaRepository<CompanyFollow, Long> {

    // ── Candidate — check followed / unfollow ─────────────────────
    
    Optional<CompanyFollow> findByUserIdAndCompanyId(Long userId, Long companyId);
    
    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);
    
    void deleteByUserIdAndCompanyId(Long userId, Long companyId);

    // ── Candidate — getFollowedCompanies ─────────────────────
    
    /**
     * Lấy danh sách công ty mà ứng viên đang theo dõi.
     * JOIN FETCH với entity Company để tối ưu query.
     */
    @Query(value = """
            SELECT cf FROM CompanyFollow cf
            WHERE cf.userId = :userId
            ORDER BY cf.followedAt DESC
            """,
           countQuery = """
            SELECT count(cf) FROM CompanyFollow cf
            WHERE cf.userId = :userId
            """)
    Page<CompanyFollow> findAllByUserId(
            @Param("userId") Long userId,
            Pageable pageable);

    long countByCompanyId(Long companyId);

    long countByUserId(Long userId);
}
