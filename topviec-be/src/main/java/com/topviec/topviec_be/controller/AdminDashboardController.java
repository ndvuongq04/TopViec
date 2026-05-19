package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.dashboard.ResContentModeratorDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResFinanceAdminDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResSuperAdminDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResSupportAdminDashboardDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/super-admin")
    @PreAuthorize("@adminSecurity.hasRole(authentication, '" + AdminRoleConstants.SUPER_ADMIN + "')")
    public ResponseEntity<ResSuperAdminDashboardDTO> getSuperAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getSuperAdminDashboard());
    }

    @GetMapping("/content-moderator")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResContentModeratorDashboardDTO> getContentModeratorDashboard() {
        return ResponseEntity.ok(dashboardService.getContentModeratorDashboard());
    }

    @GetMapping("/support-admin")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResSupportAdminDashboardDTO> getSupportAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getSupportAdminDashboard());
    }

    @GetMapping("/finance-admin")
    @PreAuthorize("@adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.FINANCE_ADMIN + "')")
    public ResponseEntity<ResFinanceAdminDashboardDTO> getFinanceAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getFinanceAdminDashboard());
    }
}
