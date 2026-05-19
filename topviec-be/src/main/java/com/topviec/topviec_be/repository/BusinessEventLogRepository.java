package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.BusinessEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BusinessEventLogRepository extends JpaRepository<BusinessEventLog, Long> {

    @Query("""
            SELECT b FROM BusinessEventLog b
            WHERE (:userIds IS NULL OR b.userId IN :userIds)
            AND (:action IS NULL OR b.action = :action)
            AND (:category IS NULL OR b.category = :category)
            AND (:status IS NULL OR b.status = :status)
            AND (:keyword IS NULL
                 OR LOWER(b.action) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(b.targetEntity) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:startDate IS NULL OR b.createdAt >= :startDate)
            AND (:endDate IS NULL OR b.createdAt <= :endDate)
            ORDER BY b.createdAt DESC
            """)
    Page<BusinessEventLog> findByFilters(
            @Param("userIds") List<Long> userIds,
            @Param("action") String action,
            @Param("category") String category,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /** Đếm tổng business event log all-time của danh sách userId */
    @Query("SELECT COUNT(b) FROM BusinessEventLog b WHERE b.userId IN :userIds")
    long countByUserIdIn(@Param("userIds") List<Long> userIds);

    // ═══════ STATISTICS — Admin Dashboard ═══════

    /** Đếm tổng business event log của danh sách admin trong khoảng thời gian */
    @Query("""
            SELECT COUNT(b) FROM BusinessEventLog b
            WHERE b.userId IN :userIds
            AND b.createdAt >= :startDate
            AND b.createdAt < :endDate
            """)
    long countByUserIdsAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /** Đếm business event log FAILURE của admin trong khoảng thời gian */
    @Query("""
            SELECT COUNT(b) FROM BusinessEventLog b
            WHERE b.userId IN :userIds
            AND b.status = :status
            AND b.createdAt >= :startDate
            AND b.createdAt < :endDate
            """)
    long countByUserIdsAndStatusAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /** Lấy danh sách userId distinct có business event log trong khoảng thời gian */
    @Query("""
            SELECT DISTINCT b.userId FROM BusinessEventLog b
            WHERE b.userId IN :userIds
            AND b.createdAt >= :startDate
            AND b.createdAt < :endDate
            """)
    List<Long> findDistinctUserIdsByUserIdsAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /** Đếm business event log của danh sách user thuộc các category cụ thể */
    @Query("""
            SELECT COUNT(b) FROM BusinessEventLog b
            WHERE b.userId IN :userIds
            AND b.category IN :categories
            """)
    long countByUserIdsAndCategories(
            @Param("userIds") List<Long> userIds,
            @Param("categories") List<String> categories);
}
