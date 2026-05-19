package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findBySlug(String slug);

    Optional<Company> findByUserId(Long userId);

    Optional<Company> findByCreatedBy(Long userId);

    boolean existsBySlug(String slug);

    boolean existsByTaxCode(String taxCode);

    boolean existsByCreatedBy(Long userId);

    long countByStatusAndDeletedAtIsNull(String status);

    long countByVerificationStatusAndDeletedAtIsNull(String verificationStatus);

    // ── Admin — getAllCompanies ─────────────────────
    @Query("""
            SELECT c FROM Company c
            WHERE (:status IS NULL OR c.status = :status)
            AND (:verificationStatus IS NULL OR c.verificationStatus = :verificationStatus)
            AND (:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Company> findAllWithFilter(
            @Param("status") String status,
            @Param("verificationStatus") String verificationStatus,
            @Param("keyword") String keyword,
            Pageable pageable);

    // ── Public — UV lấy danh sách công ty active ─────────────────────────────
    @Query("""
            SELECT c FROM Company c
            WHERE c.status = 'active'
            AND (:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:provinceId IS NULL OR c.provinceId = :provinceId)
            AND (:industryId IS NULL OR c.industryId = :industryId)
            AND (:isBanner IS NULL OR c.isBanner = :isBanner)
            AND (:isTopEmployer IS NULL OR c.isTopEmployer = :isTopEmployer)
            AND (:isBrandVerified IS NULL OR c.isBrandVerified = :isBrandVerified)
            """)
    Page<Company> findPublicCompanies(
            @Param("keyword") String keyword,
            @Param("provinceId") Integer provinceId,
            @Param("industryId") Long industryId,
            @Param("isBanner") Boolean isBanner,
            @Param("isTopEmployer") Boolean isTopEmployer,
            @Param("isBrandVerified") Boolean isBrandVerified,
            Pageable pageable);
}
