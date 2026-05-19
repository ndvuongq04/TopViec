package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LogQueryService {

    // ═══════ AUDIT LOG ═══════

    /** Danh sách audit log — có filter + keyword search + role filter */
    ResultPaginationDTO getAuditLogs(
            List<Long> userIds,
            String action, String category, String severity, String status,
            String keyword, String userRole,
            LocalDate startDate, LocalDate endDate,
            Long companyId,
            Pageable pageable);

    /** Chi tiết 1 audit log */
    ResAuditLogDetailDTO getAuditLogDetail(Long id);

    /** Chi tiết 1 audit log — resolve member role nếu có companyId */
    ResAuditLogDetailDTO getAuditLogDetail(Long id, Long companyId);

    // ═══════ BUSINESS EVENT LOG ═══════

    /** Danh sách business event log — có filter + keyword search + role filter */
    ResultPaginationDTO getBusinessEventLogs(
            List<Long> userIds,
            String action, String category, String status,
            String keyword, String userRole,
            LocalDate startDate, LocalDate endDate,
            Long companyId,
            Pageable pageable);

    /** Chi tiết 1 business event log */
    ResBusinessEventLogDetailDTO getBusinessEventLogDetail(Long id);

    /** Chi tiết 1 business event log — resolve member role nếu có companyId */
    ResBusinessEventLogDetailDTO getBusinessEventLogDetail(Long id, Long companyId);

    // ═══════ STATISTICS ═══════

    /** Thống kê log hôm nay cho Admin Dashboard — chỉ lấy log của admin, không lấy employer */
    ResAdminLogStatisticsDTO getAdminLogStatistics();

    /** Thống kê log cho Employer Dashboard */
    ResEmployerLogStatisticsDTO getEmployerLogStatistics();
}
