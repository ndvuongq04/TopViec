package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResReportDetailDTO {

    private Long id;
    private String reportCode;
    private String complaintType;
    private String violationGroup;
    private String priority;
    private String status;
    private String description;
    private String resolutionNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime emailSentAt;
    private LocalDateTime employerDeadline;
    private LocalDateTime employerRespondedAt;
    private Long remainingProcessingHours;
    private Long totalAllowedProcessingHours;
    private ReporterInfo reporter;
    private JobInfo jobPosting;
    private AssignedAdminInfo assignedAdmin;
    private List<EvidenceInfo> evidences;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReporterInfo {
        private Long userId;
        private String fullName;
        private String email;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobInfo {
        private Long id;
        private String title;
        private String status;
        private CompanyInfo company;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfo {
        private Long id;
        private String name;
        private String logoUrl;
        private String status;
        private Integer violationScore;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignedAdminInfo {
        private Long adminUserId;
        private String fullName;
        private String adminRole;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceInfo {
        private Long id;
        private String fileUrl;
        private String fileType;
        private LocalDateTime createdAt;
    }
}
