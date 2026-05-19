package com.topviec.topviec_be.config;

import com.topviec.topviec_be.enums.cvs.FileUploadType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Configuration
public class FileStorageConfig {

    @Value("${app.storage.upload-dir}")
    private String uploadDir;

    @Value("${app.storage.base-url}")
    private String baseUrl;

    @PostConstruct
    public void init() throws IOException {
        Path rootPath = getUploadPath();
        Files.createDirectories(rootPath);

        for (FileUploadType type : FileUploadType.values()) {
            Files.createDirectories(rootPath.resolve(type.getSubDir()));
        }
    }

    public Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}
