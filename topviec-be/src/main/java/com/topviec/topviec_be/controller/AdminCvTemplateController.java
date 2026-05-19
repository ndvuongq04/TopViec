package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.dto.request.ReqCreateCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqPreviewCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCvTemplateContentDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCvTemplateDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplateDetailDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplatePreviewDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.adminUsers.AdminRoleConstants;
import com.topviec.topviec_be.service.CvTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/cv-templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCvTemplateController {

    private final CvTemplateService cvTemplateService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResultPaginationDTO> getTemplates(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(cvTemplateService.getAdminTemplates(keyword, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResCvTemplateDetailDTO> getTemplateDetail(@PathVariable Long id) {
        return ResponseEntity.ok(cvTemplateService.getAdminTemplateDetail(id));
    }

    /**
     * GET /api/v1/admin/cv-templates/sample-data
     * Trả sample data chuẩn để FE preview template nhanh.
     */
    @GetMapping("/sample-data")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<CvOnlineExtraDataDTO> getSampleData() {
        return ResponseEntity.ok(cvTemplateService.getSampleData());
    }

    /**
     * POST /api/v1/admin/cv-templates/preview
     * Preview template với sample data, đồng thời trả lỗi placeholder và cảnh báo CSS nếu có.
     */
    @PostMapping("/preview")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "', '"
            + AdminRoleConstants.SUPPORT_ADMIN + "')")
    public ResponseEntity<ResCvTemplatePreviewDTO> previewTemplate(
            @Valid @RequestBody ReqPreviewCvTemplateDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvTemplateService.previewTemplate(extractUserId(jwt), request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResCvTemplateDetailDTO> createTemplate(
            @Valid @ModelAttribute ReqCreateCvTemplateDTO request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cvTemplateService.createTemplate(extractUserId(jwt), request, thumbnail));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResCvTemplateDetailDTO> updateTemplateMetadata(
            @PathVariable Long id,
            @Valid @RequestBody ReqUpdateCvTemplateDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvTemplateService.updateTemplateMetadata(extractUserId(jwt), id, request));
    }

    @PutMapping("/{id}/content")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResCvTemplateDetailDTO> updateTemplateContent(
            @PathVariable Long id,
            @Valid @RequestBody ReqUpdateCvTemplateContentDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvTemplateService.updateTemplateContent(extractUserId(jwt), id, request));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResCvTemplateDetailDTO> activateTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvTemplateService.activateTemplate(extractUserId(jwt), id));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResCvTemplateDetailDTO> deactivateTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvTemplateService.deactivateTemplate(extractUserId(jwt), id));
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasRole('ADMIN') and @adminSecurity.hasAnyRole(authentication, '"
            + AdminRoleConstants.SUPER_ADMIN + "', '"
            + AdminRoleConstants.CONTENT_MODERATOR + "')")
    public ResponseEntity<ResCvTemplateDetailDTO> setDefaultTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvTemplateService.setDefaultTemplate(extractUserId(jwt), id));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
