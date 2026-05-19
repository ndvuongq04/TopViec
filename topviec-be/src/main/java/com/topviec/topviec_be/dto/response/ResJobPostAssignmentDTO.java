package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResJobPostAssignmentDTO {

    private Long id;
    private Long jobPostId;
    private String jobPostTitle;
    private String jobPostStatus;
    private Long userId;
    private String userEmail;
    private Long assignedBy;
    private String assignedByEmail;
    private LocalDateTime assignedAt;
    private LocalDateTime revokedAt;
    private Long revokedBy;
    private String revokedByEmail;
    private String note;
}
