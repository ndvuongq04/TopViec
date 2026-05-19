package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResReportSummaryDTO {

    private Long id;
    private String reportCode;
    private Long reporterUserId;
    private String reporterName;
    private Long jobPostId;
    private String jobPostTitle;
    private Long companyId;
    private String companyName;
    private String complaintType;
    private String violationGroup;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processingDeadline;
    private Long remainingProcessingHours;
    private Long totalAllowedProcessingHours;
    private String assignedAdminName;
}
