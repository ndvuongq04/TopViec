package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqCreateCandidateProfileDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCandidateProfileDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCandidateProfileVisibilityDTO;
import com.topviec.topviec_be.dto.response.ResCandidateProfileDTO;
import com.topviec.topviec_be.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate/profile")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    /**
     * POST /api/v1/candidate/profile
     * Tạo hồ sơ cá nhân lần đầu.
     */
    @PostMapping
    @LogAction(LogActionType.CREATE_CANDIDATE_PROFILE)
    public ResponseEntity<ResCandidateProfileDTO> createProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqCreateCandidateProfileDTO request) {

        ResCandidateProfileDTO data = candidateProfileService.createProfile(extractUserId(jwt), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    /**
     * GET /api/v1/candidate/profile/me
     * Lấy hồ sơ của ứng viên đang đăng nhập.
     */
    @GetMapping("/me")
    public ResponseEntity<ResCandidateProfileDTO> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        ResCandidateProfileDTO data = candidateProfileService.getMyProfile(extractUserId(jwt));
        return ResponseEntity.ok(data);
    }

    /**
     * PUT /api/v1/candidate/profile
     * Cập nhật hồ sơ cá nhân.
     */
    @PutMapping
    @LogAction(LogActionType.UPDATE_CANDIDATE_PROFILE)
    public ResponseEntity<ResCandidateProfileDTO> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqUpdateCandidateProfileDTO request) {

        ResCandidateProfileDTO data = candidateProfileService.updateProfile(extractUserId(jwt), request);
        return ResponseEntity.ok(data);
    }

    /**
     * PATCH /api/v1/candidate/profile/visibility
     * Cập nhật trạng thái ẩn/hiện các thông tin nhạy cảm trong hồ sơ ứng viên.
     */
    @PatchMapping("/visibility")
    @LogAction(LogActionType.UPDATE_CANDIDATE_VISIBILITY)
    public ResponseEntity<ResCandidateProfileDTO> updateVisibility(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqUpdateCandidateProfileVisibilityDTO request) {

        ResCandidateProfileDTO data = candidateProfileService.updateVisibility(extractUserId(jwt), request);
        return ResponseEntity.ok(data);
    }

    /**
     * DELETE /api/v1/candidate/profile
     * Xóa mềm hồ sơ cá nhân.
     */
    @DeleteMapping
    @LogAction(LogActionType.DELETE_CANDIDATE_PROFILE)
    public ResponseEntity<Void> deleteProfile(
            @AuthenticationPrincipal Jwt jwt) {

        candidateProfileService.deleteProfile(extractUserId(jwt));
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Lấy userId từ JWT subject.
     * JwtService.generateAccessToken() đặt userId vào subject:
     * .subject(String.valueOf(userId))
     */
    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
