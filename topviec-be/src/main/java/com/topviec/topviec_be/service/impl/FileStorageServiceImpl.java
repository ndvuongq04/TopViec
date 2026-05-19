package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.config.FileStorageConfig;
import com.topviec.topviec_be.enums.cvs.FileUploadType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final String FILES_SEGMENT = "/files/";

    private final FileStorageConfig fileStorageConfig;

    @Override
    public String uploadFile(MultipartFile file, Long ownerId, FileUploadType type) {
        try {
            String fileUrl = storeBytes(file.getBytes(), file.getOriginalFilename(), ownerId, type);

            log.info("Upload {} thanh cong - ownerId: {}", type, ownerId);
            return fileUrl;
        } catch (IOException e) {
            log.error("Upload {} that bai - ownerId: {}", type, ownerId, e);
            throw AppException.badRequest("Khong the luu file len he thong");
        }
    }

    @Override
    public String uploadBytes(byte[] content, String originalFilename, Long ownerId, FileUploadType type) {
        try {
            String fileUrl = storeBytes(content, originalFilename, ownerId, type);
            log.info("Upload generated {} thanh cong - ownerId: {}", type, ownerId);
            return fileUrl;
        } catch (IOException e) {
            log.error("Upload generated {} that bai - ownerId: {}", type, ownerId, e);
            throw AppException.badRequest("Khong the luu file len he thong");
        }
    }

    @Override
    public void deleteFile(String fileUrl, FileUploadType type) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            String relativePath = extractRelativePath(fileUrl);
            String normalizedRelativePath = relativePath.replace("\\", "/");
            if (!normalizedRelativePath.startsWith(type.getSubDir() + "/")) {
                log.debug("Bo qua xoa {} khong thuoc local storage type nay - url: {}", type, fileUrl);
                return;
            }

            Path filePath = fileStorageConfig.getUploadPath().resolve(relativePath).normalize();
            if (!filePath.startsWith(fileStorageConfig.getUploadPath())) {
                throw AppException.badRequest("Duong dan file khong hop le");
            }

            Files.deleteIfExists(filePath);
            log.info("Xoa {} thanh cong - path: {}", type, filePath);
        } catch (IOException e) {
            log.warn("Khong the xoa {} - url: {}", type, fileUrl, e);
        }
    }

    @Override
    public Resource loadFile(String fileUrl) {
        try {
            Path filePath = resolveFilePath(fileUrl);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw AppException.notFound("Khong tim thay file");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw AppException.badRequest("Duong dan file khong hop le");
        }
    }

    private Path resolveFilePath(String fileUrl) {
        String path = extractRelativePath(fileUrl);
        return fileStorageConfig.getUploadPath().resolve(path).normalize();
    }

    private String extractRelativePath(String fileUrl) {
        String normalized = fileUrl;
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            normalized = URI.create(fileUrl).getPath();
        }

        int index = normalized.indexOf(FILES_SEGMENT);
        if (index >= 0) {
            return normalized.substring(index + FILES_SEGMENT.length());
        }

        return normalized.startsWith("/") ? normalized.substring(1) : normalized;
    }

    private String buildFileUrl(String relativePath) {
        return fileStorageConfig.getBaseUrl().replaceAll("/+$", "") + "/" + relativePath.replace("\\", "/");
    }

    private String storeBytes(byte[] content, String originalFilename, Long ownerId, FileUploadType type) throws IOException {
        String extension = extractExtension(originalFilename);
        String fileName = UUID.randomUUID() + "." + extension;
        Path targetDir = fileStorageConfig.getUploadPath()
                .resolve(type.getSubDir())
                .resolve(type.resolveOwnerDir(ownerId))
                .normalize();

        if (!targetDir.startsWith(fileStorageConfig.getUploadPath())) {
            throw AppException.badRequest("Duong dan luu file khong hop le");
        }

        Files.createDirectories(targetDir);

        Path targetFile = targetDir.resolve(fileName).normalize();
        Files.write(targetFile, content);

        String relativePath = type.getSubDir() + "/" + type.resolveOwnerDir(ownerId) + "/" + fileName;
        return buildFileUrl(relativePath);
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw AppException.badRequest("Ten file khong hop le");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }
}
