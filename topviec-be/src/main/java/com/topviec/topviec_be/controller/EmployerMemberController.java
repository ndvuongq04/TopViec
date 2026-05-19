package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqAddMemberDTO;
import com.topviec.topviec_be.dto.request.ReqBatchMemberPermissionDTO;
import com.topviec.topviec_be.dto.request.ReqToggleActionDTO;
import com.topviec.topviec_be.dto.request.ReqUpdatePermissionDTO;
import com.topviec.topviec_be.dto.response.ResCompanyMemberDTO;
import com.topviec.topviec_be.dto.response.ResEmployerMemberStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResMemberPermissionDetailDTO;
import com.topviec.topviec_be.dto.response.ResPermissionChangeLogDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;

import java.util.List;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.service.CompanyMemberService;
import com.topviec.topviec_be.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller dành cho EMPLOYER quản lý thành viên công ty.
 * Base URL: /api/v1/employer/member
 */
@RestController
@RequestMapping("/employer/member")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerMemberController {

    private final CompanyMemberService companyMemberService;
    private final CompanyService companyService;

    /**
     * POST /employer/member
     * Thêm thành viên mới vào công ty (TH1: email chưa có tài khoản).
     *
     * companyId được lấy tự động từ công ty của người dùng hiện tại.
     */
    @PostMapping
    @LogAction(LogActionType.ADD_MEMBER)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:add')")
    public ResponseEntity<ResCompanyMemberDTO> addMember(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqAddMemberDTO request) {

        Long inviterUserId = extractUserId(jwt);

        // Lấy companyId từ công ty của inviter
        Long companyId = companyService.getMyCompany(inviterUserId).getId();

        ResCompanyMemberDTO result = companyMemberService.addMember(inviterUserId, companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/{targetUserId}/permission")
    @LogAction(LogActionType.UPDATE_MEMBER_PERMISSION)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:permission')")
    public ResponseEntity<ResCompanyMemberDTO> updateMemberPermission(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long targetUserId,
            @Valid @RequestBody ReqUpdatePermissionDTO req) {

        Long inviterId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(inviterId).getId();

        ResCompanyMemberDTO updatedMember = companyMemberService.updateMemberPermission(inviterId, companyId,
                targetUserId, req);
        return ResponseEntity.ok(updatedMember);
    }

    @GetMapping
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:view_activity')")
    public ResponseEntity<ResultPaginationDTO> getMembers(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Long userId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(userId).getId();

        ResultPaginationDTO members = companyMemberService.getMembers(companyId, status, role, keyword, pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/me/permissions")
    public ResponseEntity<ResMemberPermissionDetailDTO> getMyPermissions(
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(userId).getId();

        List<ResMemberPermissionDetailDTO> result =
                companyMemberService.getBatchMemberPermissions(companyId, List.of(userId));

        if (result.isEmpty()) {
            throw AppException.notFound("Không tìm thấy thông tin quyền hạn của bạn trong công ty");
        }

        return ResponseEntity.ok(result.get(0));
    }

    @PostMapping("/permissions/batch")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:view_activity')")
    public ResponseEntity<List<ResMemberPermissionDetailDTO>> getBatchMemberPermissions(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqBatchMemberPermissionDTO request) {

        Long userId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(userId).getId();

        List<ResMemberPermissionDetailDTO> result =
                companyMemberService.getBatchMemberPermissions(companyId, request.getUserIds());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{targetUserId}/permissions/{actionCode}")
    @LogAction(LogActionType.TOGGLE_MEMBER_ACTION_PERMISSION)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:permission')")
    public ResponseEntity<ResMemberPermissionDetailDTO> toggleMemberActionPermission(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long targetUserId,
            @PathVariable String actionCode,
            @Valid @RequestBody ReqToggleActionDTO request) {

        Long inviterUserId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(inviterUserId).getId();

        ResMemberPermissionDetailDTO result = companyMemberService.toggleMemberActionPermission(
                inviterUserId, companyId, targetUserId, actionCode, request.getEnabled());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{targetUserId}/permissions/history")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:view_activity')")
    public ResponseEntity<List<ResPermissionChangeLogDTO>> getMemberPermissionHistory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long targetUserId) {

        Long userId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(userId).getId();

        return ResponseEntity.ok(companyMemberService.getMemberPermissionHistory(companyId, targetUserId));
    }

    @GetMapping("/permissions/history")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:view_activity')")
    public ResponseEntity<ResultPaginationDTO> getCompanyPermissionHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Long userId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(userId).getId();

        return ResponseEntity.ok(companyMemberService.getCompanyPermissionHistory(companyId, fromDate, toDate, pageable));
    }

    @DeleteMapping("/{targetUserId}")
    @LogAction(LogActionType.REMOVE_MEMBER)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:delete')")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long targetUserId) {
        Long inviterId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(inviterId).getId();

        companyMemberService.removeMember(inviterId, companyId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /employer/member/statistics
     * Thống kê thành viên của công ty (tổng, active, pending, locked)
     */
    @GetMapping("/statistics")
    public ResponseEntity<ResEmployerMemberStatisticsDTO> getMemberStatistics(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        Long companyId = companyService.getMyCompany(userId).getId();

        return ResponseEntity.ok(companyMemberService.getMemberStatistics(companyId));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
