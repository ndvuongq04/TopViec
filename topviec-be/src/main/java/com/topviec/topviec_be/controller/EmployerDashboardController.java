package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.dashboard.ResManagerDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResOwnerDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResRecruiterDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResViewerDashboardDTO;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.EmployerDashboardService;
import com.topviec.topviec_be.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/employer/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerDashboardController {

    private final EmployerDashboardService dashboardService;
    private final CompanyService companyService;
    private final CompanyMemberRepository memberRepository;

    @GetMapping("/owner")
    public ResponseEntity<ResOwnerDashboardDTO> getOwnerDashboard() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);
        checkRole(companyId, userId, MemberRole.OWNER, MemberRole.MANAGER);
        return ResponseEntity.ok(dashboardService.getOwnerDashboard(companyId));
    }

    @GetMapping("/manager")
    public ResponseEntity<ResManagerDashboardDTO> getManagerDashboard() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);
        checkRole(companyId, userId, MemberRole.OWNER, MemberRole.MANAGER);
        return ResponseEntity.ok(dashboardService.getManagerDashboard(companyId));
    }

    @GetMapping("/recruiter")
    public ResponseEntity<ResRecruiterDashboardDTO> getRecruiterDashboard() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);
        checkRole(companyId, userId, MemberRole.OWNER, MemberRole.MANAGER, MemberRole.RECRUITER);
        return ResponseEntity.ok(dashboardService.getRecruiterDashboard(companyId, userId));
    }

    @GetMapping("/viewer")
    public ResponseEntity<ResViewerDashboardDTO> getViewerDashboard() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);
        return ResponseEntity.ok(dashboardService.getViewerDashboard(companyId));
    }

    private void checkRole(Long companyId, Long userId, MemberRole... allowedRoles) {
        CompanyMember member = memberRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> AppException.forbidden("User is not a company member."));
        if (!"active".equalsIgnoreCase(member.getStatus())) {
            throw AppException.forbidden("Company member is not active.");
        }

        MemberRole role = member.getMemberRole();
        boolean allowed = Arrays.stream(allowedRoles).anyMatch(allowedRole -> allowedRole == role);
        if (!allowed) {
            throw AppException.forbidden("You do not have permission to access this dashboard.");
        }
    }
}
