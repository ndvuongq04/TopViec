package com.topviec.topviec_be.util;

import com.topviec.topviec_be.enums.cvs.FileUploadType;
import com.topviec.topviec_be.exception.AppException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024L;

    private static final Set<String> ALLOWED_CV_MIME_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    private static final Set<String> ALLOWED_CV_EXTENSIONS = Set.of("pdf", "doc", "docx");
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private static final Map<FileUploadType, Long> SIZE_LIMIT = Map.of(
            FileUploadType.CV, MAX_FILE_SIZE,
            FileUploadType.AVATAR, MAX_IMAGE_SIZE,
            FileUploadType.CV_TEMPLATE_THUMBNAIL, MAX_IMAGE_SIZE,
            FileUploadType.COMPANY_LOGO, MAX_IMAGE_SIZE,
            FileUploadType.COMPANY_COVER, MAX_IMAGE_SIZE,
            FileUploadType.BUSINESS_LICENSE, MAX_FILE_SIZE);

    private static final Map<FileUploadType, Set<String>> MIME_LIMIT = Map.of(
            FileUploadType.CV, ALLOWED_CV_MIME_TYPES,
            FileUploadType.AVATAR, ALLOWED_IMAGE_MIME_TYPES,
            FileUploadType.CV_TEMPLATE_THUMBNAIL, ALLOWED_IMAGE_MIME_TYPES,
            FileUploadType.COMPANY_LOGO, ALLOWED_IMAGE_MIME_TYPES,
            FileUploadType.COMPANY_COVER, ALLOWED_IMAGE_MIME_TYPES,
            FileUploadType.BUSINESS_LICENSE, ALLOWED_CV_MIME_TYPES);

    private static final Map<FileUploadType, Set<String>> EXT_LIMIT = Map.of(
            FileUploadType.CV, ALLOWED_CV_EXTENSIONS,
            FileUploadType.AVATAR, ALLOWED_IMAGE_EXTENSIONS,
            FileUploadType.CV_TEMPLATE_THUMBNAIL, ALLOWED_IMAGE_EXTENSIONS,
            FileUploadType.COMPANY_LOGO, ALLOWED_IMAGE_EXTENSIONS,
            FileUploadType.COMPANY_COVER, ALLOWED_IMAGE_EXTENSIONS,
            FileUploadType.BUSINESS_LICENSE, ALLOWED_CV_EXTENSIONS);

    public void validate(MultipartFile file, FileUploadType type) {
        if (file == null || file.isEmpty()) {
            throw AppException.badRequest(resolveEmptyMessage(type));
        }

        long maxSize = SIZE_LIMIT.get(type);
        if (file.getSize() > maxSize) {
            throw AppException.badRequest(resolveSizeMessage(type, maxSize));
        }

        String contentType = file.getContentType();
        Set<String> allowedMimes = MIME_LIMIT.get(type);
        if (contentType == null || !allowedMimes.contains(contentType)) {
            throw AppException.badRequest(resolveFormatMessage(type));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw AppException.badRequest("Ten file khong hop le");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!EXT_LIMIT.get(type).contains(extension)) {
            throw AppException.badRequest(resolveFormatMessage(type));
        }
    }

    private String resolveEmptyMessage(FileUploadType type) {
        return switch (type) {
            case CV -> "Vui long chon file CV";
            case AVATAR -> "Vui long chon anh dai dien";
            case CV_TEMPLATE_THUMBNAIL -> "Vui long chon thumbnail template";
            case COMPANY_LOGO -> "Vui long chon logo cong ty";
            case COMPANY_COVER -> "Vui long chon anh bia cong ty";
            case BUSINESS_LICENSE -> "Vui long chon giay phep kinh doanh";
        };
    }

    private String resolveSizeMessage(FileUploadType type, long maxSize) {
        String sizeLabel = maxSize == MAX_FILE_SIZE ? "5MB" : "2MB";
        return switch (type) {
            case CV -> "File CV qua lon, vui long chon file nho hon " + sizeLabel;
            case AVATAR -> "Anh dai dien qua lon, vui long chon anh nho hon " + sizeLabel;
            case CV_TEMPLATE_THUMBNAIL -> "Thumbnail template qua lon, vui long chon anh nho hon " + sizeLabel;
            case COMPANY_LOGO -> "Logo cong ty qua lon, vui long chon file nho hon " + sizeLabel;
            case COMPANY_COVER -> "Anh bia qua lon, vui long chon anh nho hon " + sizeLabel;
            case BUSINESS_LICENSE -> "Giay phep kinh doanh qua lon, vui long chon file nho hon " + sizeLabel;
        };
    }

    private String resolveFormatMessage(FileUploadType type) {
        return switch (type) {
            case CV -> "CV chi chap nhan dinh dang PDF, DOC hoac DOCX";
            case AVATAR -> "Anh dai dien chi chap nhan JPG, PNG hoac WEBP";
            case CV_TEMPLATE_THUMBNAIL -> "Thumbnail template chi chap nhan JPG, PNG hoac WEBP";
            case COMPANY_LOGO -> "Logo cong ty chi chap nhan JPG, PNG hoac WEBP";
            case COMPANY_COVER -> "Anh bia chi chap nhan JPG, PNG hoac WEBP";
            case BUSINESS_LICENSE -> "Giay phep kinh doanh chi chap nhan PDF, DOC hoac DOCX";
        };
    }
}
