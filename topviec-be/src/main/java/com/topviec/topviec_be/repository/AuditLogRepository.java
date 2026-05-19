package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Tìm kiếm audit log với filter đa điều kiện.
     * Dùng cho cả Admin và Employer.
     *
     * @param userIds    danh sách userId cần lọc (null = all)
     * @param action     tên action (null = all)
     * @param category   category (null = all)
     * @param severity   severity (null = all)
     * @param status     SUCCESS/FAILURE (null = all)
     * @param keyword    tìm kiếm trong action, targetEntity, description (null = all)
     * @param startDate  từ ngày (null = all)
     * @param endDate    đến ngày (null = all)
     */
    @Query("""
            SELECT a FROM AuditLog a
            WHERE (:userIds IS NULL OR a.userId IN :userIds)
            AND (:action IS NULL OR a.action = :action)
            AND (:category IS NULL OR a.category = :category)
            AND (:severity IS NULL OR a.severity = :severity)
            AND (:status IS NULL OR a.status = :status)
            AND (:keyword IS NULL
                 OR LOWER(a.action) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(a.targetEntity) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:startDate IS NULL OR a.createdAt >= :startDate)
            AND (:endDate IS NULL OR a.createdAt <= :endDate)
            ORDER BY a.createdAt DESC
            """)
    Page<AuditLog> findByFilters(
            @Param("userIds") List<Long> userIds,
            @Param("action") String action,
            @Param("category") String category,
            @Param("severity") String severity,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /** Đếm log theo userId — dùng cho thống kê */
    long countByUserId(Long userId);

    /** Đếm tổng audit log all-time của danh sách userId */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId IN :userIds")
    long countByUserIdIn(@Param("userIds") List<Long> userIds);

    // ═══════ STATISTICS — Admin Dashboard ═══════

    /** Đếm tổng audit log của danh sách admin trong khoảng thời gian */
    @Query("""
            SELECT COUNT(a) FROM AuditLog a
            WHERE a.userId IN :userIds
            AND a.createdAt >= :startDate
            AND a.createdAt < :endDate
            """)
    long countByUserIdsAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /** Đếm audit log có severity = HIGH hoặc CRITICAL của admin trong khoảng thời gian */
    @Query("""
            SELECT COUNT(a) FROM AuditLog a
            WHERE a.userId IN :userIds
            AND a.severity IN :severities
            AND a.createdAt >= :startDate
            AND a.createdAt < :endDate
            """)
    long countByUserIdsAndSeveritiesAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("severities") List<String> severities,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /** Đếm audit log FAILURE của admin trong khoảng thời gian */
    @Query("""
            SELECT COUNT(a) FROM AuditLog a
            WHERE a.userId IN :userIds
            AND a.status = :status
            AND a.createdAt >= :startDate
            AND a.createdAt < :endDate
            """)
    long countByUserIdsAndStatusAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /** Lấy danh sách userId distinct có audit log trong khoảng thời gian */
    @Query("""
            SELECT DISTINCT a.userId FROM AuditLog a
            WHERE a.userId IN :userIds
            AND a.createdAt >= :startDate
            AND a.createdAt < :endDate
            """)
    List<Long> findDistinctUserIdsByUserIdsAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /** Đếm audit log của danh sách user thuộc các category cụ thể */
    @Query("""
            SELECT COUNT(a) FROM AuditLog a
            WHERE a.userId IN :userIds
            AND a.category IN :categories
            """)
    long countByUserIdsAndCategories(
            @Param("userIds") List<Long> userIds,
            @Param("categories") List<String> categories);

    @Query("""
            SELECT a FROM AuditLog a
            WHERE a.userId IN :adminUserIds
            ORDER BY a.createdAt DESC
            """)
    List<AuditLog> findRecentByAdminUserIds(
            @Param("adminUserIds") List<Long> adminUserIds,
            Pageable pageable);
}
