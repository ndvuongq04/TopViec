package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.CompanyFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller dành cho Ứng viên (Candidate) quản lý việc theo dõi công ty.
 * Base URL: /api/v1/candidate/companies
 */
@RestController
@RequestMapping("/candidate/companies")
@RequiredArgsConstructor
public class CandidateCompanyFollowController {

    private final CompanyFollowService companyFollowService;

    /**
     * POST /candidate/companies/{companyId}/follow
     * Thêm công ty vào danh sách theo dõi
     */
    @PostMapping("/{companyId}/follow")
    @LogAction(LogActionType.FOLLOW_COMPANY)
    public ResponseEntity<Map<String, String>> followCompany(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long companyId) {

        companyFollowService.followCompany(extractUserId(jwt), companyId);
        return ResponseEntity.ok(Map.of("message", "Theo dõi công ty thành công"));
    }

    /**
     * DELETE /candidate/companies/{companyId}/follow
     * Bỏ theo dõi công ty
     */
    @DeleteMapping("/{companyId}/follow")
    public ResponseEntity<Map<String, String>> unfollowCompany(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long companyId) {

        companyFollowService.unfollowCompany(extractUserId(jwt), companyId);
        return ResponseEntity.ok(Map.of("message", "Bỏ theo dõi công ty thành công"));
    }

    /**
     * GET /candidate/companies/{companyId}/follow/status
     * Kiểm tra ứng viên đã theo dõi công ty này chưa
     */
    @GetMapping("/{companyId}/follow/status")
    public ResponseEntity<Map<String, Boolean>> checkFollowStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long companyId) {

        boolean isFollowing = companyFollowService.checkFollowStatus(extractUserId(jwt), companyId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    /**
     * GET /candidate/companies/followed
     * Lấy danh sách các công ty mà ứng viên đang theo dõi
     */
    @GetMapping("/followed")
    public ResponseEntity<ResultPaginationDTO> getFollowedCompanies(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {

        ResultPaginationDTO data = companyFollowService.getFollowedCompanies(extractUserId(jwt), pageable);
        return ResponseEntity.ok(data);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}