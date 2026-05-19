package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.*;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.LogQueryService;
import com.topviec.topviec_be.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Employer Log Controller — NTD (OWNER / MANAGER) xem log hoạt động team.
 *
 * Quyền:
 *   - OWNER / MANAGER: xem log của tất cả member trong công ty + chính mình
 *   - RECRUITER / VIEWER: chỉ xem log của chính mình
 *
 * Filter: memberId, action, category, severity, status, keyword, date range
 */
@RestController
@RequestMapping("/employer/logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerLogController {

    private final LogQueryService logQueryService;
    private final CompanyService companyService;
    private final CompanyMemberRepository companyMemberRepository;

    // ═══════════════════════════════════════════════
    // STATISTICS — Dashboard KPI
    // ═══════════════════════════════════════════════

    /**
     * Thống kê log cho Employer Dashboard.
     *
     * Trả về:
     *   1. totalActivity       — thể hiện quy mô tương tác chung của công ty
     *   2. candidateProcessing  — tập trung vào các hành động thực tế trên hồ sơ ứng viên
     *   3. dataUpdates         — giám sát các thay đổi về tin đăng và thông tin công ty
     *   4. activeMembers       — các employer đang hoạt động trong công ty
     */
    @GetMapping("/statistics")
    public ResponseEntity<ResEmployerLogStatisticsDTO> getLogStatistics() {
        ResEmployerLogStatisticsDTO statistics = logQueryService.getEmployerLogStatistics();
        return ResponseEntity.ok(statistics);
    }

    // ═══════════════════════════════════════════════
    // AUDIT LOG
    // ═══════════════════════════════════════════════

    /**
     * Danh sách audit log của công ty.
     *
     * @param memberId nếu OWNER/MANAGER muốn lọc theo member cụ thể (userId)
     */
    @GetMapping("/audit")
    public ResponseEntity<ResultPaginationDTO> getAuditLogs(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);
        List<Long> userIds = resolveAllowedUserIds(currentUserId, companyId, memberId);

        ResultPaginationDTO result = logQueryService.getAuditLogs(
                userIds, action, category, severity, status,
                keyword, null,
                startDate, endDate, companyId, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * Chi tiết 1 audit log — kiểm tra thuộc phạm vi công ty.
     */
    @GetMapping("/audit/{id}")
    public ResponseEntity<ResAuditLogDetailDTO> getAuditLogDetail(@PathVariable Long id) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        ResAuditLogDetailDTO detail = logQueryService.getAuditLogDetail(id, companyId);

        // Kiểm tra log thuộc phạm vi cho phép
        validateLogAccess(detail.getUserId(), currentUserId, companyId);

        return ResponseEntity.ok(detail);
    }

    // ═══════════════════════════════════════════════
    // BUSINESS EVENT LOG
    // ═══════════════════════════════════════════════

    @GetMapping("/business")
    public ResponseEntity<ResultPaginationDTO> getBusinessEventLogs(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);
        List<Long> userIds = resolveAllowedUserIds(currentUserId, companyId, memberId);

        ResultPaginationDTO result = logQueryService.getBusinessEventLogs(
                userIds, action, category, status,
                keyword, null,
                startDate, endDate, companyId, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/business/{id}")
    public ResponseEntity<ResBusinessEventLogDetailDTO> getBusinessEventLogDetail(@PathVariable Long id) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        ResBusinessEventLogDetailDTO detail = logQueryService.getBusinessEventLogDetail(id, companyId);

        validateLogAccess(detail.getUserId(), currentUserId, companyId);

        return ResponseEntity.ok(detail);
    }

    // ═══════════════════════════════════════════════
    // HELPERS — Quyền truy cập log
    // ═══════════════════════════════════════════════

    /**
     * Xác định danh sách userId được phép xem log:
     *
     * - OWNER / MANAGER → tất cả member active của company
     * - RECRUITER / VIEWER → chỉ chính mình
     *
     * Nếu memberId được truyền vào → kiểm tra member đó thuộc company.
     */
    private List<Long> resolveAllowedUserIds(Long currentUserId, Long companyId, Long requestedMemberId) {
        CompanyMember currentMember = companyMemberRepository
                .findByCompanyIdAndUserId(companyId, currentUserId)
                .orElseThrow(() -> AppException.forbidden("Bạn không phải thành viên công ty"));

        MemberRole role = currentMember.getMemberRole();
        boolean isOwnerOrManager = role == MemberRole.OWNER || role == MemberRole.MANAGER;

        if (!isOwnerOrManager) {
            // RECRUITER / VIEWER chỉ xem log chính mình
            return List.of(currentUserId);
        }

        // OWNER / MANAGER
        if (requestedMemberId != null) {
            // Kiểm tra memberId thuộc company
            boolean belongs = companyMemberRepository
                    .existsByCompanyIdAndUserIdAndDeletedAtIsNull(companyId, requestedMemberId);
            if (!belongs) {
                throw AppException.forbidden("Member không thuộc công ty của bạn");
            }
            return List.of(requestedMemberId);
        }

        // Lấy tất cả userId thuộc company
        List<CompanyMember> members = companyMemberRepository.findAllActiveByCompanyId(companyId);
        return members.stream()
                .map(CompanyMember::getUserId)
                .toList();
    }

    /**
     * Kiểm tra userId của log có thuộc phạm vi cho phép không.
     * Dùng cho API xem chi tiết.
     */
    private void validateLogAccess(Long logUserId, Long currentUserId, Long companyId) {
        if (logUserId == null) return; // system log — cho phép

        CompanyMember currentMember = companyMemberRepository
                .findByCompanyIdAndUserId(companyId, currentUserId)
                .orElseThrow(() -> AppException.forbidden("Bạn không phải thành viên công ty"));

        MemberRole role = currentMember.getMemberRole();
        boolean isOwnerOrManager = role == MemberRole.OWNER || role == MemberRole.MANAGER;

        if (!isOwnerOrManager && !currentUserId.equals(logUserId)) {
            throw AppException.forbidden("Bạn chỉ được xem log của chính mình");
        }

        if (isOwnerOrManager) {
            boolean belongs = companyMemberRepository
                    .existsByCompanyIdAndUserIdAndDeletedAtIsNull(companyId, logUserId);
            if (!belongs && !currentUserId.equals(logUserId)) {
                throw AppException.forbidden("Log này không thuộc phạm vi công ty của bạn");
            }
        }
    }
}
