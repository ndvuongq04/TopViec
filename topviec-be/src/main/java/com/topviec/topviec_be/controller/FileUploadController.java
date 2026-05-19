package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResFileUploadDTO;
import com.topviec.topviec_be.enums.cvs.FileUploadType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.FileStorageService;
import com.topviec.topviec_be.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final FileValidator fileValidator;
    private final CompanyService companyService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResFileUploadDTO> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") FileUploadType type,
            @AuthenticationPrincipal Jwt jwt) {

        fileValidator.validate(file, type);

        Long userId = Long.parseLong(jwt.getSubject());
        validateRole(jwt, type);

        Long ownerId = switch (type) {
            case CV, AVATAR, CV_TEMPLATE_THUMBNAIL -> userId;
            case COMPANY_LOGO, COMPANY_COVER, BUSINESS_LICENSE -> companyService.getCompanyIdByUserId(userId);
        };

        String fileUrl = fileStorageService.uploadFile(file, ownerId, type);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResFileUploadDTO.builder()
                        .fileUrl(fileUrl)
                        .type(type)
                        .build());
    }

    private void validateRole(Jwt jwt, FileUploadType type) {
        String role = jwt.getClaimAsString("role");
        boolean isCandidateType = type == FileUploadType.CV || type == FileUploadType.AVATAR;
        boolean isAdminType = type == FileUploadType.CV_TEMPLATE_THUMBNAIL;

        if (isCandidateType && !"CANDIDATE".equalsIgnoreCase(role)) {
            throw AppException.forbidden("Ban khong co quyen upload loai file nay");
        }

        if (isAdminType && !"ADMIN".equalsIgnoreCase(role)) {
            throw AppException.forbidden("Ban khong co quyen upload loai file nay");
        }

        if (!isCandidateType && !isAdminType && !"EMPLOYER".equalsIgnoreCase(role)) {
            throw AppException.forbidden("Ban khong co quyen upload loai file nay");
        }
    }
}
