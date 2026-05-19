package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.topviec.topviec_be.enums.candidateProfile.JobSeekingStatus;
import com.topviec.topviec_be.enums.candidateProfile.PreferredWorkType;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResCandidateProfileDTO {

    private Long id;
    private Long userId;

    // Thông tin cá nhân
    private String fullName;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneDisplay;
    private String bio;

    // Liên kết
    private String linkedinUrl;
    private String githubUrl;
    private String personalWebsite;

    // Lương
    private Double expectedSalaryMin;
    private Double expectedSalaryMax;
    private Boolean salaryNegotiable;

    // Trạng thái & hình thức
    private JobSeekingStatus jobSeekingStatus;
    private PreferredWorkType preferredWorkType;
    private String preferredJobTitle;
    private Integer preferredLocationId;

    // Hồ sơ
    private Integer profileCompletionPct;
    private Boolean isCvPublic;
    private Boolean hidePhone;
    private Boolean hideEmail;
    private Boolean hideDateOfBirth;
    private Boolean hideExpectedSalary;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
