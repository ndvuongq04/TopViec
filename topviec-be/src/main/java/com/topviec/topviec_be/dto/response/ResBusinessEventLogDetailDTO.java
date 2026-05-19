package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO chi tiết Business Event Log — bao gồm metadata JSON.
 */
@Data
@Builder
public class ResBusinessEventLogDetailDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userRole;
    private String action;
    private String category;
    private String targetEntity;
    private Long targetId;
    private String targetName;
    private Map<String, Object> metadata;
    private Boolean hasChanges;
    private String status;
    private Long durationMs;
    private LocalDateTime createdAt;
}
