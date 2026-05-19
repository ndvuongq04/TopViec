package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqConfirmReportDTO;
import com.topviec.topviec_be.dto.request.ReqProcessReportDTO;
import com.topviec.topviec_be.dto.response.ResAdminReportStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResAppealDTO;
import com.topviec.topviec_be.dto.response.ResReportDetailDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.service.AppealService;
import com.topviec.topviec_be.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;
    private final AppealService appealService;

    @GetMapping
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResultPaginationDTO> getReports(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String complaintType,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
                reportService.getReports(search, status, group, complaintType, fromDate, toDate, pageable));
    }

    @GetMapping("/job-posts/{jobPostId}")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResultPaginationDTO> getReportsByJobPost(
            @PathVariable Long jobPostId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String complaintType,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
                reportService.getReportsByJobPost(jobPostId, status, group, complaintType, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResReportDetailDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getDetail(id));
    }

    @GetMapping("/{id}/appeal")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResAppealDTO> getAppealByComplaint(@PathVariable Long id) {
        return appealService.getByComplaint(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping("/{id}/confirm")
    @LogAction(LogActionType.CONFIRM_REPORT)
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResReportDetailDTO> confirm(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody ReqConfirmReportDTO request) {

        return ResponseEntity.ok(reportService.confirm(extractUserId(jwt), id, request));
    }

    @PatchMapping("/{id}/process")
    @LogAction(LogActionType.PROCESS_REPORT)
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResReportDetailDTO> process(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody ReqProcessReportDTO request) {

        return ResponseEntity.ok(reportService.process(extractUserId(jwt), id, request));
    }

    @GetMapping("/statistics")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResAdminReportStatisticsDTO> getReportStatistics() {
        return ResponseEntity.ok(reportService.getReportStatistics());
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
