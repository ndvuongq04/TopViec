package com.topviec.topviec_be.service;

import com.topviec.topviec_be.enums.cvs.FileUploadType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file, Long ownerId, FileUploadType type);

    String uploadBytes(byte[] content, String originalFilename, Long ownerId, FileUploadType type);

    void deleteFile(String fileUrl, FileUploadType type);

    Resource loadFile(String fileUrl);
}
