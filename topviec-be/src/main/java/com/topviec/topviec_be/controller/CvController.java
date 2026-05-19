package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.dto.request.ReqChangeOnlineCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqCreateOnlineCvDTO;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqRenameCvDTO;
import com.topviec.topviec_be.dto.request.ReqShareCvDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateOnlineCvDTO;
import com.topviec.topviec_be.dto.request.ReqUploadCvDTO;
import com.topviec.topviec_be.dto.request.ReqCreateShareTokenDTO;
import com.topviec.topviec_be.dto.response.ResCvDTO;
import com.topviec.topviec_be.dto.response.ResCvOnlineEditorPayloadDTO;
import com.topviec.topviec_be.dto.response.ResCvOnlineDetailDTO;
import com.topviec.topviec_be.dto.response.ResCvPdfExportDTO;
import com.topviec.topviec_be.dto.response.ResShareTokenDTO;
import com.topviec.topviec_be.service.CvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cvs")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;

    /**
     * POST /api/v1/cvs/upload
     * Upload CV từ máy tính (PDF/DOCX)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @LogAction(LogActionType.UPLOAD_CV)
    public ResponseEntity<ResCvDTO> uploadCv(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "isDefault", defaultValue = "false") boolean isDefault,
            @AuthenticationPrincipal Jwt jwt) {
        ReqUploadCvDTO request = ReqUploadCvDTO.builder()
                .title(title)
                .isDefault(isDefault)
                .build();

        ResCvDTO data = cvService.uploadCv(extractUserId(jwt), file, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    @PostMapping("/online")
    public ResponseEntity<ResCvOnlineDetailDTO> createOnlineCv(
            @Valid @RequestBody ReqCreateOnlineCvDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        ResCvOnlineDetailDTO data = cvService.createOnlineCv(extractUserId(jwt), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    /**
     * GET /api/v1/cvs
     * Lấy danh sách CV của user đang đăng nhập
     */
    @GetMapping
    public ResponseEntity<List<ResCvDTO>> getMyCvs(
            @AuthenticationPrincipal Jwt jwt) {
        List<ResCvDTO> data = cvService.getMyCvs(extractUserId(jwt));

        return ResponseEntity.ok(data);
    }

    /**
     * GET /api/v1/cvs/{id}
     * Lấy chi tiết CV theo ID của user đang đăng nhập
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResCvDTO> getCvById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        ResCvDTO data = cvService.getCvById(extractUserId(jwt), id);

        return ResponseEntity.ok(data);
    }

    @GetMapping("/online/prefill")
    public ResponseEntity<CvOnlineExtraDataDTO> getOnlineCvPrefill(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.getOnlineCvPrefill(extractUserId(jwt)));
    }

    /**
     * GET /api/v1/cvs/online/editor/template/{templateId}
     * Lấy payload editor cho CV online mới từ template.
     * `extraData` hỗ trợ cả các section mở rộng như projects, hobbies, awards, customSections.
     */
    @GetMapping("/online/editor/template/{templateId}")
    public ResponseEntity<ResCvOnlineEditorPayloadDTO> getOnlineCvEditorPayloadByTemplate(
            @PathVariable Long templateId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.getOnlineCvEditorPayloadByTemplate(extractUserId(jwt), templateId));
    }

    /**
     * GET /api/v1/cvs/online/editor/{id}
     * Lấy payload editor cho CV online đã lưu.
     * `extraData` hỗ trợ cả các section mở rộng như projects, hobbies, awards, customSections.
     */
    @GetMapping("/online/editor/{id}")
    public ResponseEntity<ResCvOnlineEditorPayloadDTO> getOnlineCvEditorPayloadById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.getOnlineCvEditorPayloadById(extractUserId(jwt), id));
    }

    @GetMapping("/online/{id}")
    public ResponseEntity<ResCvOnlineDetailDTO> getOnlineCvById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.getOnlineCvById(extractUserId(jwt), id));
    }

    @PutMapping("/online/{id}")
    public ResponseEntity<ResCvOnlineDetailDTO> updateOnlineCv(
            @PathVariable Long id,
            @Valid @RequestBody ReqUpdateOnlineCvDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.updateOnlineCv(extractUserId(jwt), id, request));
    }

    @PatchMapping("/online/{id}/template")
    public ResponseEntity<ResCvOnlineDetailDTO> changeOnlineCvTemplate(
            @PathVariable Long id,
            @Valid @RequestBody ReqChangeOnlineCvTemplateDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.changeOnlineCvTemplate(extractUserId(jwt), id, request));
    }

    @PatchMapping("/online/{id}/sections/personal-info")
    public ResponseEntity<ResCvOnlineDetailDTO> updateOnlineCvPersonalInfo(
            @PathVariable Long id,
            @Valid @RequestBody CvOnlineExtraDataDTO.PersonalInfo request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.updateOnlineCvPersonalInfo(extractUserId(jwt), id, request));
    }

    @PatchMapping("/online/{id}/sections/experiences")
    public ResponseEntity<ResCvOnlineDetailDTO> updateOnlineCvExperiences(
            @PathVariable Long id,
            @Valid @RequestBody List<CvOnlineExtraDataDTO.ExperienceItem> request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.updateOnlineCvExperiences(extractUserId(jwt), id, request));
    }

    @PatchMapping("/online/{id}/sections/educations")
    public ResponseEntity<ResCvOnlineDetailDTO> updateOnlineCvEducations(
            @PathVariable Long id,
            @Valid @RequestBody List<CvOnlineExtraDataDTO.EducationItem> request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.updateOnlineCvEducations(extractUserId(jwt), id, request));
    }

    @PatchMapping("/online/{id}/sections/skills")
    public ResponseEntity<ResCvOnlineDetailDTO> updateOnlineCvSkills(
            @PathVariable Long id,
            @Valid @RequestBody List<CvOnlineExtraDataDTO.SkillItem> request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.updateOnlineCvSkills(extractUserId(jwt), id, request));
    }

    @PatchMapping("/online/{id}/sections/certifications")
    public ResponseEntity<ResCvOnlineDetailDTO> updateOnlineCvCertifications(
            @PathVariable Long id,
            @Valid @RequestBody List<CvOnlineExtraDataDTO.CertificationItem> request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.updateOnlineCvCertifications(extractUserId(jwt), id, request));
    }

    @PatchMapping("/online/{id}/sections/languages")
    public ResponseEntity<ResCvOnlineDetailDTO> updateOnlineCvLanguages(
            @PathVariable Long id,
            @Valid @RequestBody List<CvOnlineExtraDataDTO.LanguageItem> request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.updateOnlineCvLanguages(extractUserId(jwt), id, request));
    }

    /**
     * POST /api/v1/cvs/{id}/export-pdf
     * Render CV online sang PDF và cập nhật `pdf_url` mới nhất.
     */
    @PostMapping("/{id}/export-pdf")
    public ResponseEntity<ResCvPdfExportDTO> exportOnlineCvPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cvService.exportOnlineCvPdf(extractUserId(jwt), id));
    }

    /**
     * GET /api/v1/cvs/{id}/download-pdf
     * Tải file PDF của CV online. Nếu chưa có PDF thì BE sẽ render trước khi trả file.
     */
    @GetMapping("/{id}/download-pdf")
    public ResponseEntity<Resource> downloadOnlineCvPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Resource resource = cvService.downloadOnlineCvPdf(extractUserId(jwt), id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv-online-" + id + ".pdf\"")
                .body(resource);
    }

    /**
     * PATCH /api/v1/cvs/:id/rename
     * Đổi tên CV
     */
    @PatchMapping("/{id}/rename")
    public ResponseEntity<ResCvDTO> renameCv(
            @PathVariable Long id,
            @Valid @RequestBody ReqRenameCvDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        ResCvDTO data = cvService.renameCv(extractUserId(jwt), id, request.getTitle());
        return ResponseEntity.ok(data);
    }

    /**
     * PATCH /api/v1/cvs/:id/default
     * Đặt CV làm mặc định
     */
    @PatchMapping("/{id}/default")
    public ResponseEntity<ResCvDTO> setDefaultCv(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        ResCvDTO data = cvService.setDefaultCv(extractUserId(jwt), id);
        return ResponseEntity.ok(data);
    }

    /**
     * POST /api/v1/cvs/:id/duplicate
     * Sao chép CV
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ResCvDTO> duplicateCv(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        ResCvDTO data = cvService.duplicateCv(extractUserId(jwt), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    /**
     * DELETE /api/v1/cvs/:id
     * Xóa CV (Soft delete)
     */
    @DeleteMapping("/{id}")
    @LogAction(LogActionType.DELETE_CV)
    public ResponseEntity<Void> deleteCv(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        cvService.deleteCv(extractUserId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/cvs/:id/download
     * Tải CV (Redirect ra Cloudinary URL)
     */

    /**
     * PATCH /api/v1/cvs/:id/share
     * update visibility
     */
    @PatchMapping("/{id}/share")
    @LogAction(LogActionType.SHARE_CV)
    public ResponseEntity<ResCvDTO> shareCv(
            @PathVariable Long id,
            @Valid @RequestBody ReqShareCvDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        ResCvDTO data = cvService.shareCv(extractUserId(jwt), id, request);
        return ResponseEntity.ok(data);
    }

    /**
     * POST /api/v1/cvs/:id/share-token
     * Tạo token chia sẻ CV với TTL
     */
    @PostMapping("/{id}/share-token")
    public ResponseEntity<ResShareTokenDTO> createShareToken(
            @PathVariable Long id,
            @Valid @RequestBody ReqCreateShareTokenDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        ResShareTokenDTO data = cvService.createShareToken(extractUserId(jwt), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    /**
     * GET /api/v1/cvs/public/:shareToken
     * Xem CV công khai qua token
     */
    @GetMapping("/public/{shareToken}")
    @LogAction(LogActionType.VIEW_PUBLIC_CV)
    public ResponseEntity<ResCvDTO> getPublicCv(@PathVariable String shareToken) {
        ResCvDTO data = cvService.getPublicCv(shareToken);
        return ResponseEntity.ok(data);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Lấy userId từ JWT subject
     * JwtService.generateAccessToken() đặt userId vào subject
     */
    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
