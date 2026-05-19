package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResAdminCandidateDetailDTO {
    private Long id; // User ID
    private String fullName;
    private String email;
    private String phoneDisplay;
    private String status; // UserStatus
    private String avatarUrl;
    private String jobSeekingStatus;
    private LocalDate dateOfBirth;
    private String gender;
    private String bio;
    private String linkedinUrl;
    private String githubUrl;
    private String personalWebsite;
    private Double expectedSalaryMin;
    private Double expectedSalaryMax;
    private Boolean salaryNegotiable;
    private String preferredJobTitle;
    private String preferredWorkType;
    private Integer preferredLocationId;
    private String preferredLocationName;
    private Integer profileCompletionPct;
    private Boolean isCvPublic;
    private LocalDateTime createdAt;
}
