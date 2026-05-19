package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqAdminUpdateCompanyDTO;
import com.topviec.topviec_be.dto.request.ReqRegisterEmployerDTO;
import com.topviec.topviec_be.dto.response.ResAdminCompanyStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResCompanyDTO;
import com.topviec.topviec_be.dto.response.ResCompanyPlanDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.service.AuthService;
import com.topviec.topviec_be.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/companies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCompanyController {

        private final CompanyService companyService;
        private final AuthService authService;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "', '"
                        + AdminRoleConstants.SUPPORT_ADMIN + "')")
        public ResponseEntity<String> createEmployerCompany(
                        @AuthenticationPrincipal Jwt jwt,
                        @Valid @RequestBody ReqRegisterEmployerDTO request) {

                authService.registerEmployer(request);
                return ResponseEntity.status(HttpStatus.CREATED).body("Tạo tài khoản employer thành công");
        }

        @GetMapping
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "', '"
                        + AdminRoleConstants.CONTENT_MODERATOR + "', '"
                        + AdminRoleConstants.SUPPORT_ADMIN + "', '"
                        + AdminRoleConstants.FINANCE_ADMIN + "')")
        public ResponseEntity<ResultPaginationDTO> getAllCompanies(
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String verificationStatus,
                        @RequestParam(required = false) String keyword,
                        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

                return ResponseEntity.ok(
                                companyService.getAllCompanies(status, verificationStatus, keyword, pageable));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "', '"
                        + AdminRoleConstants.CONTENT_MODERATOR + "', '"
                        + AdminRoleConstants.SUPPORT_ADMIN + "', '"
                        + AdminRoleConstants.FINANCE_ADMIN + "')")
        public ResponseEntity<ResCompanyDTO> getById(@PathVariable Long id) {
                return ResponseEntity.ok(companyService.adminGetById(id));
        }

        /**
         * PATCH /admin/companies/{id}
         *
         * Chỉ update status: { "action": "verify", "approved": true }
         * { "action": "verify", "approved": false, "rejectionReason": "..." }
         * { "action": "suspend", "suspendedReason": "..." }
         * { "action": "unsuspend" }
         * Chỉ update info: { "name": "ABC Corp", "website": "..." }
         * Kết hợp cả 2: { "action": "unsuspend", "name": "ABC Corp mới" }
         */
        @PatchMapping("/{id}")
        @LogAction(LogActionType.ADMIN_UPDATE_COMPANY)
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "', '"
                        + AdminRoleConstants.CONTENT_MODERATOR + "')")
        public ResponseEntity<ResCompanyDTO> updateCompany(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long id,
                        @Valid @RequestBody ReqAdminUpdateCompanyDTO request) {

                return ResponseEntity.ok(
                                companyService.adminUpdateCompany(id, extractUserId(jwt), request));
        }

        @DeleteMapping("/{id}")
        @LogAction(LogActionType.DELETE_COMPANY)
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "')")
        public ResponseEntity<Void> deleteCompany(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long id) {

                companyService.deleteCompany(id, extractUserId(jwt));
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}/statistics")
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "', '"
                        + AdminRoleConstants.CONTENT_MODERATOR + "', '"
                        + AdminRoleConstants.SUPPORT_ADMIN + "', '"
                        + AdminRoleConstants.FINANCE_ADMIN + "')")
        public ResponseEntity<ResAdminCompanyStatisticsDTO> getCompanyStatistics(
                        @PathVariable Long id) {
                return ResponseEntity.ok(companyService.getCompanyStatistics(id));
        }

        @GetMapping("/{id}/plan")
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "', '"
                        + AdminRoleConstants.CONTENT_MODERATOR + "', '"
                        + AdminRoleConstants.SUPPORT_ADMIN + "', '"
                        + AdminRoleConstants.FINANCE_ADMIN + "')")
        public ResponseEntity<ResCompanyPlanDTO> getCompanyPlan(
                        @PathVariable Long id) {
                return ResponseEntity.ok(companyService.getCompanyPlan(id));
        }

        @GetMapping("/{id}/subscriptions")
        @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
                        + AdminRoleConstants.SUPER_ADMIN + "', '"
                        + AdminRoleConstants.CONTENT_MODERATOR + "', '"
                        + AdminRoleConstants.SUPPORT_ADMIN + "', '"
                        + AdminRoleConstants.FINANCE_ADMIN + "')")
        public ResponseEntity<ResultPaginationDTO> getSubscriptionHistory(
                        @PathVariable Long id,
                        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
                return ResponseEntity.ok(companyService.getSubscriptionHistory(id, pageable));
        }

        private Long extractUserId(Jwt jwt) {
                return Long.parseLong(jwt.getSubject());
        }
}