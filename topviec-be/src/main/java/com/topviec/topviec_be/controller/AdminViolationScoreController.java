package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqAdjustViolationScoreDTO;
import com.topviec.topviec_be.dto.request.ReqResetViolationScoreDTO;
import com.topviec.topviec_be.dto.request.ReqUnsuspendDTO;
import com.topviec.topviec_be.dto.response.ResAppealDTO;
import com.topviec.topviec_be.dto.response.ResViolationScoreDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.service.AppealService;
import com.topviec.topviec_be.service.ViolationScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin quản lý điểm vi phạm của NTD.
 * Base URL: /admin/employers/{employerId}/violation-score
 */
@RestController
@RequestMapping("/admin/employers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminViolationScoreController {

    private final ViolationScoreService violationScoreService;
    private final AppealService appealService;

    /**
     * GET /admin/employers/{employerId}/violation-score
     * Xem tổng điểm vi phạm hiện tại và lịch sử vi phạm của NTD.
     */
    @GetMapping("/{employerId}/violation-score")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResViolationScoreDTO> getScore(@PathVariable Long employerId) {
        return ResponseEntity.ok(violationScoreService.getScore(employerId));
    }

    /**
     * POST /admin/employers/{employerId}/violation-score/reset
     * Reset điểm về 0.
     * Điều kiện: NTD không tái phạm nhóm B trong vòng 6 tháng gần nhất.
     */
    @PostMapping("/{employerId}/violation-score/reset")
    @LogAction(LogActionType.RESET_VIOLATION_SCORE)
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResViolationScoreDTO> resetScore(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long employerId,
            @Valid @RequestBody ReqResetViolationScoreDTO request) {

        return ResponseEntity.ok(violationScoreService.resetScore(extractUserId(jwt), employerId, request));
    }

    /**
     * PATCH /admin/employers/{employerId}/violation-score/adjust
     * Giảm điểm vi phạm thủ công khi NTD chủ động khắc phục hậu quả.
     */
    @PatchMapping("/{employerId}/violation-score/adjust")
    @LogAction(LogActionType.ADJUST_VIOLATION_SCORE)
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResViolationScoreDTO> adjustScore(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long employerId,
            @Valid @RequestBody ReqAdjustViolationScoreDTO request) {

        return ResponseEntity.ok(violationScoreService.adjustScore(extractUserId(jwt), employerId, request));
    }

    /**
     * GET /admin/employers/{employerId}/appeals
     * Xem toàn bộ danh sách kháng cáo của một NTD.
     */
    @GetMapping("/{employerId}/appeals")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<List<ResAppealDTO>> getAppeals(@PathVariable Long employerId) {
        return ResponseEntity.ok(appealService.getByEmployer(employerId));
    }

    /**
     * POST /admin/employers/{companyId}/unsuspend
     * Admin duyệt kháng cáo của NTD: gỡ toàn bộ điểm vi phạm từ complaint liên quan
     * và mở khóa tài khoản sớm nếu đang bị suspend.
     */
    @PostMapping("/{companyId}/unsuspend")
    @LogAction(LogActionType.UNSUSPEND_COMPANY)
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResAppealDTO> unsuspend(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long companyId,
            @Valid @RequestBody ReqUnsuspendDTO request) {

        return ResponseEntity.ok(appealService.unsuspend(extractUserId(jwt), companyId, request));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
