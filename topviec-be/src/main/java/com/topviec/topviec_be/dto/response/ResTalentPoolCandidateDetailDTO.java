package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.cvs.CvType;
import com.topviec.topviec_be.enums.cvs.CvVisibility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResTalentPoolCandidateDetailDTO {

    // === TALENT POOL ENTRY ===
    private Long talentPoolId;
    private String source;
    private String note;
    private LocalDateTime addedAt;
    private Long addedBy;
    private String addedByName;

    // === THÔNG TIN CƠ BẢN (luôn hiển thị) ===
    private Long candidateUserId;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String gender;
    private String linkedinUrl;
    private String githubUrl;
    private String personalWebsite;
    private Integer profileCompletionPct;
    private String jobSeekingStatus;

    // === THÔNG TIN CÓ THỂ ẨN ===
    private String phone;
    private Boolean phoneHidden;

    private String email;
    private Boolean emailHidden;

    private LocalDate dateOfBirth;
    private Boolean dateOfBirthHidden;

    // === MONG MUỐN CÔNG VIỆC ===
    private String preferredJobTitle;
    private String preferredWorkType;
    private Integer preferredLocationId;
    private String preferredLocationName;

    private Double expectedSalaryMin;
    private Double expectedSalaryMax;
    private Boolean salaryNegotiable;
    private Boolean salaryHidden;

    // === CV MẶC ĐỊNH ===
    private DefaultCvDTO defaultCv;

    @Getter
    @Setter
    @Builder
    public static class DefaultCvDTO {
        private Long cvId;
        private String title;
        private CvType cvType;
        private String fileUrl;
        private String pdfUrl;
        private Boolean pdfDirty;
        private CvVisibility visibility;
        private Integer viewCount;
        private LocalDateTime createdAt;
    }
}
