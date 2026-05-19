package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CompanyMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {

    // ── Kiểm tra thành viên ─────────────────────────────────────────
    boolean existsByCompanyIdAndUserIdAndDeletedAtIsNull(Long companyId, Long userId);

    // ── Truy vấn thành viên ─────────────────────────────────────────
    @Query("""
            SELECT cm FROM CompanyMember cm
            WHERE cm.userId = :userId AND cm.status = 'pending' AND cm.deletedAt IS NULL
            """)
    List<CompanyMember> findPendingMembersByUserId(@Param("userId") Long userId);

    Optional<CompanyMember> findFirstByUserIdAndStatusAndDeletedAtIsNull(Long userId, String status);

    @Query("""
            SELECT cm FROM CompanyMember cm
            WHERE cm.companyId = :companyId AND cm.deletedAt IS NULL
            """)
    List<CompanyMember> findAllActiveByCompanyId(@Param("companyId") Long companyId);

    Page<CompanyMember> findAllByCompanyIdAndDeletedAtIsNull(Long companyId, Pageable pageable);

    @Query("""
            SELECT cm FROM CompanyMember cm
            JOIN User u ON cm.userId = u.id
            WHERE cm.companyId = :companyId
            AND (:status IS NULL OR cm.status = :status)
            AND (:role IS NULL OR cm.roleDefault.roleName = :role)
            AND (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND cm.deletedAt IS NULL
            """)
    Page<CompanyMember> findByFilters(
            @Param("companyId") Long companyId,
            @Param("status") String status,
            @Param("role") com.topviec.topviec_be.enums.companyMember.MemberRole role,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            SELECT cm FROM CompanyMember cm
            JOIN FETCH cm.roleDefault
            WHERE cm.companyId = :companyId AND cm.userId = :userId AND cm.deletedAt IS NULL
            """)
    Optional<CompanyMember> findByCompanyIdAndUserId(
            @Param("companyId") Long companyId,
            @Param("userId") Long userId);

    @Query("""
            SELECT cm FROM CompanyMember cm
            WHERE cm.companyId = :companyId AND cm.userId IN :userIds AND cm.deletedAt IS NULL
            """)
    List<CompanyMember> findByCompanyIdAndUserIds(
            @Param("companyId") Long companyId,
            @Param("userIds") List<Long> userIds);

    /** Tìm thành viên OWNER của công ty */
    @Query("""
            SELECT cm FROM CompanyMember cm
            JOIN cm.roleDefault rd
            WHERE cm.companyId = :companyId
            AND rd.roleName = com.topviec.topviec_be.enums.companyMember.MemberRole.OWNER
            AND cm.status = 'active'
            AND cm.deletedAt IS NULL
            """)
    Optional<CompanyMember> findOwnerByCompanyId(@Param("companyId") Long companyId);

    // ── Thống kê thành viên ─────────────────────────────────────────

    long countByCompanyIdAndDeletedAtIsNull(Long companyId);

    long countByCompanyIdAndStatusAndDeletedAtIsNull(Long companyId, String status);
}
