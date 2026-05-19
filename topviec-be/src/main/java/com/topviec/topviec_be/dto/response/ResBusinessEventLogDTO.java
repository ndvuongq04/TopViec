package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO danh sách Business Event Log — thông tin cơ bản.
 */
@Data
@Builder
public class ResBusinessEventLogDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userRole;
    private String action;
    private String category;
    private String targetEntity;
    private Long targetId;
    private String targetName;
    private Boolean hasChanges;
    private String status;
    private Long durationMs;
    private LocalDateTime createdAt;
}
