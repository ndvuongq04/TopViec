package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqApplyAddonDTO;
import com.topviec.topviec_be.dto.request.ReqRenewSubscriptionDTO;
import com.topviec.topviec_be.dto.response.ResCompanyAddonDTO;
import com.topviec.topviec_be.dto.response.ResCompanyBrandingDTO;
import com.topviec.topviec_be.dto.response.ResCompanySubscriptionDTO;
import com.topviec.topviec_be.dto.response.ResJobPostAddonDTO;
import com.topviec.topviec_be.dto.response.ResSubscriptionRenewalDTO;
import com.topviec.topviec_be.service.EmployerServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employer/services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerServiceManagementController {

    private final EmployerServiceManagementService employerServiceManagementService;

    /**
     * Lấy thông tin gói dịch vụ hiện tại NTD đang dùng và hạn mức còn lại
     */
    @GetMapping("/subscription")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:view_history')")
    public ResponseEntity<ResCompanySubscriptionDTO> getMySubscription(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(employerServiceManagementService.getMySubscription(extractUserId(jwt)));
    }

    /**
     * Gia hạn gói subscription hiện tại (cùng gói, nối thời gian, cộng dồn quota)
     */
    @PostMapping("/subscription/renew")
    @LogAction(LogActionType.RENEW_SUBSCRIPTION)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:purchase')")
    public ResponseEntity<ResSubscriptionRenewalDTO> renewSubscription(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqRenewSubscriptionDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employerServiceManagementService.renewSubscription(extractUserId(jwt), request));
    }

    /**
     * Lấy danh sách các dịch vụ lẻ mà NTD đã mua và số lượng còn lại
     */
    @GetMapping("/addons")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:view_history')")
    public ResponseEntity<List<ResCompanyAddonDTO>> getMyAddons(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(employerServiceManagementService.getMyAddons(extractUserId(jwt)));
    }

    /**
     * Áp dụng dịch vụ lẻ cho một tin tuyển dụng
     */
    @PostMapping("/job-posts/{jobPostingId}/apply-addon")
    @LogAction(LogActionType.APPLY_JOB_POST_ADDON)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:purchase')")
    public ResponseEntity<ResJobPostAddonDTO> applyAddonToJobPost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long jobPostingId,
            @Valid @RequestBody ReqApplyAddonDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employerServiceManagementService.applyAddonToJobPost(
                        extractUserId(jwt), jobPostingId, request));
    }

    /**
     * Áp dụng dịch vụ BRANDING cho công ty (Banner trang chủ, Top Employer, ...).
     * Service code được tự động xác định từ companyAddonId.
     */
    @PostMapping("/company/apply-branding")
    @LogAction(LogActionType.APPLY_COMPANY_BRANDING)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:purchase')")
    public ResponseEntity<ResCompanyBrandingDTO> applyBrandingToCompany(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqApplyAddonDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employerServiceManagementService.applyBrandingToCompany(extractUserId(jwt), request));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
