package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO chi tiết Audit Log — đầy đủ tất cả field.
 */
@Data
@Builder
public class ResAuditLogDetailDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userRole;
    private String action;
    private String category;
    private String severity;
    private String targetEntity;
    private Long targetId;
    private String targetName;
    private String description;
    private String ipAddress;
    private String userAgent;
    private String status;
    private Long durationMs;
    private String errorMessage;
    private LocalDateTime createdAt;
}
