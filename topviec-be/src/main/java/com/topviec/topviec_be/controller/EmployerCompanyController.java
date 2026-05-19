package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqCreateCompanyDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCompanyDTO;
import com.topviec.topviec_be.dto.response.ResCompanyDTO;
import com.topviec.topviec_be.dto.response.ResEmployerJobStatisticsDTO;
import com.topviec.topviec_be.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Controller dành cho EMPLOYER quản lý hồ sơ công ty của mình.
 * Base URL: /api/v1/employer/company
 */
@RestController
@RequestMapping("/employer/company")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerCompanyController {

    private final CompanyService companyService;

    /**
     * GET /employer/company/me
     * Employer xem hồ sơ công ty của chính mình.
     */
    @GetMapping("/me")
    public ResponseEntity<ResCompanyDTO> getMyCompany(
            @AuthenticationPrincipal Jwt jwt) {

        ResCompanyDTO data = companyService.getMyCompany(extractUserId(jwt));
        return ResponseEntity.ok(data);
    }

    /**
     * Patch /employer/company
     * Employer cập nhật hồ sơ công ty.
     * Nếu hồ sơ đang bị rejected → tự động chuyển về pending để admin duyệt lại.
     */
    @PatchMapping
    @LogAction(LogActionType.UPDATE_COMPANY_PROFILE)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'company:edit')")
    public ResponseEntity<ResCompanyDTO> updateMyCompany(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqUpdateCompanyDTO request) {

        ResCompanyDTO data = companyService.updateMyCompany(extractUserId(jwt), request);
        return ResponseEntity.ok(data);
    }

    /**
     * GET /employer/company/job-statistics
     * Employer xem thống kê tin tuyển dụng của công ty mình.
     */
    @GetMapping("/job-statistics")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'job:view_all')")
    public ResponseEntity<ResEmployerJobStatisticsDTO> getJobStatistics(
            @AuthenticationPrincipal Jwt jwt) {
        
        ResEmployerJobStatisticsDTO data = companyService.getEmployerJobStatistics(extractUserId(jwt));
        return ResponseEntity.ok(data);
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
