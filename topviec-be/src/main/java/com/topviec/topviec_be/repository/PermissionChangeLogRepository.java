package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.PermissionChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PermissionChangeLogRepository extends JpaRepository<PermissionChangeLog, Long> {

    List<PermissionChangeLog> findByCompanyIdAndTargetUserIdOrderByCreatedAtDesc(Long companyId, Long targetUserId);

    Page<PermissionChangeLog> findByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);

    @Query("SELECT p FROM PermissionChangeLog p WHERE p.companyId = :companyId " +
           "AND (:from IS NULL OR p.createdAt >= :from) " +
           "AND (:to IS NULL OR p.createdAt <= :to) " +
           "ORDER BY p.createdAt DESC")
    Page<PermissionChangeLog> findByCompanyIdAndDateRange(
            @Param("companyId") Long companyId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
