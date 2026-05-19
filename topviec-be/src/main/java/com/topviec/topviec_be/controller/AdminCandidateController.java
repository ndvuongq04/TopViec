package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;
import com.topviec.topviec_be.dto.response.ResAdminCandidateDetailDTO;
import com.topviec.topviec_be.dto.response.ResAdminCandidateStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.service.AdminCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/candidates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
        + AdminRoleConstants.SUPER_ADMIN + "', '"
        + AdminRoleConstants.CONTENT_MODERATOR + "', '"
        + AdminRoleConstants.SUPPORT_ADMIN + "')")
public class AdminCandidateController {

    private final AdminCandidateService adminCandidateService;

    /**
     * GET /admin/candidates?keyword=nguyen&status=active&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getCandidates(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(adminCandidateService.getCandidates(status, keyword, pageable));
    }

    /**
     * GET /admin/candidates/{id}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ResAdminCandidateDetailDTO> getCandidateDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(adminCandidateService.getCandidateDetail(userId));
    }

    /**
     * GET /admin/candidates/{userId}/cvs
     */
    @GetMapping("/{userId}/cvs")
    public ResponseEntity<ResultPaginationDTO> getCandidateCvs(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminCandidateService.getCandidateCvs(userId, pageable));
    }

    /**
     * GET /admin/candidates/{userId}/applications
     */
    @GetMapping("/{userId}/applications")
    public ResponseEntity<ResultPaginationDTO> getCandidateApplications(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminCandidateService.getCandidateApplications(userId, pageable));
    }

    /**
     * GET /admin/candidates/{userId}/followed-companies
     */
    @GetMapping("/{userId}/followed-companies")
    public ResponseEntity<ResultPaginationDTO> getCandidateFollowedCompanies(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "followedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminCandidateService.getCandidateFollowedCompanies(userId, pageable));
    }

    /**
     * GET /admin/candidates/{userId}/saved-jobs
     */
    @GetMapping("/{userId}/saved-jobs")
    public ResponseEntity<ResultPaginationDTO> getCandidateSavedJobs(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminCandidateService.getCandidateSavedJobs(userId, pageable));
    }

    /**
     * GET /admin/candidates/{userId}/statistics
     */
    @GetMapping("/{userId}/statistics")
    public ResponseEntity<ResAdminCandidateStatisticsDTO> getCandidateStatistics(
            @PathVariable Long userId) {
        return ResponseEntity.ok(adminCandidateService.getCandidateStatistics(userId));
    }

    /**
     * PATCH /admin/candidates/{userId}/toggle-status
     * Nếu status hiện tại là active → chuyển thành locked_perm
     * Nếu status hiện tại là locked_perm → chuyển thành active
     */
    @PatchMapping("/{userId}/toggle-status")
    @LogAction(LogActionType.TOGGLE_CANDIDATE_STATUS)
    public ResponseEntity<String> toggleCandidateStatus(@PathVariable Long userId) {
        String newStatus = adminCandidateService.toggleCandidateStatus(userId);
        return ResponseEntity.ok(newStatus);
    }
}
