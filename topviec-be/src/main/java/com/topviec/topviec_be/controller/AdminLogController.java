package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.*;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.service.LogQueryService;
import com.topviec.topviec_be.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Admin Log Controller — xem log hoạt động hệ thống.
 *
 * Phân quyền:
 *   - SUPER_ADMIN : xem tất cả log, có thể lọc theo userId bất kỳ.
 *   - Admin phụ   : chỉ xem log của chính mình — userId param bị bỏ qua.
 *
 * Filter: userId, action, category, severity, status, keyword, userRole, date range
 */
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLogController {

    private final LogQueryService logQueryService;
    private final AdminUserRepository adminUserRepository;

    // ═══════════════════════════════════════════════
    // STATISTICS — Dashboard KPI
    // ═══════════════════════════════════════════════

    /**
     * Thống kê log cho Admin Dashboard — chỉ lấy log của admin.
     *
     * Trả về:
     *   1. totalLogs        — tổng log (audit + business) all-time
     *   2. criticalLogs     — số log severity HIGH/CRITICAL
     *   3. systemErrors     — số log FAILURE
     *   4. activeAdmins     — số admin đang active trong hệ thống
     */
    @GetMapping("/statistics")
    public ResponseEntity<ResAdminLogStatisticsDTO> getLogStatistics() {
        ResAdminLogStatisticsDTO statistics = logQueryService.getAdminLogStatistics();
        return ResponseEntity.ok(statistics);
    }

    // ═══════════════════════════════════════════════
    // AUDIT LOG
    // ═══════════════════════════════════════════════

    /**
     * Danh sách audit log — phân trang + filter + keyword search + role filter.
     * SUPER_ADMIN: có thể lọc userId tùy ý hoặc lấy tất cả admin logs.
     * Admin phụ: tự động chỉ xem log của chính mình.
     */
    @GetMapping("/audit")
    public ResponseEntity<ResultPaginationDTO> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String userRole,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {

        List<Long> userIds = resolveAllowedUserIds(userId);

        ResultPaginationDTO result = logQueryService.getAuditLogs(
                userIds, action, category, severity, status,
                keyword, userRole,
                startDate, endDate, null, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * Chi tiết 1 audit log — bao gồm IP, user agent, error message.
     * SUPER_ADMIN: xem bất kỳ log nào của admin.
     * Admin phụ: chỉ xem log của chính mình.
     */
    @GetMapping("/audit/{id}")
    public ResponseEntity<ResAuditLogDetailDTO> getAuditLogDetail(@PathVariable Long id) {
        ResAuditLogDetailDTO detail = logQueryService.getAuditLogDetail(id);
        validateLogAccess(detail.getUserId());
        return ResponseEntity.ok(detail);
    }

    // ═══════════════════════════════════════════════
    // BUSINESS EVENT LOG
    // ═══════════════════════════════════════════════

    /**
     * Danh sách business event log — phân trang + filter + keyword search + role filter.
     */
    @GetMapping("/business")
    public ResponseEntity<ResultPaginationDTO> getBusinessEventLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String userRole,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {

        List<Long> userIds = resolveAllowedUserIds(userId);

        ResultPaginationDTO result = logQueryService.getBusinessEventLogs(
                userIds, action, category, status,
                keyword, userRole,
                startDate, endDate, null, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * Chi tiết 1 business event log — bao gồm metadata JSON.
     */
    @GetMapping("/business/{id}")
    public ResponseEntity<ResBusinessEventLogDetailDTO> getBusinessEventLogDetail(@PathVariable Long id) {
        ResBusinessEventLogDetailDTO detail = logQueryService.getBusinessEventLogDetail(id);
        validateLogAccess(detail.getUserId());
        return ResponseEntity.ok(detail);
    }

    // ═══════════════════════════════════════════════
    // HELPERS — Quyền truy cập log
    // ═══════════════════════════════════════════════

    /**
     * Xác định danh sách userId được phép xem log:
     *   - SUPER_ADMIN → lấy tất cả admin IDs nếu không truyền userId cụ thể.
     *   - Admin phụ   → luôn là List.of(currentUserId), bỏ qua requestedUserId.
     */
    private List<Long> resolveAllowedUserIds(Long requestedUserId) {
        if (isSuperAdmin()) {
            if (requestedUserId != null) {
                return List.of(requestedUserId);
            }
            // Trả về tất cả Admin IDs thay vì null để tránh lấy log của Employer/Candidate
            return adminUserRepository.findAllActiveAdminUserIds();
        }
        // Admin phụ: chỉ xem log chính mình
        return List.of(SecurityUtil.getCurrentUserId());
    }

    /**
     * Kiểm tra quyền xem chi tiết 1 log:
     *   - SUPER_ADMIN → pass hết.
     *   - Admin phụ   → chỉ được xem log của chính mình.
     */
    private void validateLogAccess(Long logUserId) {
        if (isSuperAdmin()) return;
        if (logUserId == null) return; // system log — cho phép

        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!currentUserId.equals(logUserId)) {
            throw AppException.forbidden("Bạn chỉ được xem log của chính mình");
        }
    }

    /**
     * Đọc claim adminRole từ JWT để kiểm tra có phải SUPER_ADMIN không.
     */
    private boolean isSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        Object principal = auth.getPrincipal();
        if (!(principal instanceof Jwt jwt)) return false;
        return AdminRoleConstants.SUPER_ADMIN.equalsIgnoreCase(jwt.getClaimAsString("adminRole"));
    }
}
