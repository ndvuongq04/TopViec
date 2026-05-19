package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResTalentPoolCandidateDTO {

    // Talent pool entry
    private Long talentPoolId;
    private String source;
    private String note;
    private LocalDateTime addedAt;

    // Thông tin ứng viên
    private Long candidateUserId;
    private String candidateName;
    private String candidateEmail;
    private String candidateAvatarUrl;

    // Thông tin mong muốn
    private String preferredJobTitle;
    private String preferredWorkType;
    private Integer preferredLocationId;
    private String preferredLocationName;
    private Double expectedSalaryMin;
    private Double expectedSalaryMax;
    private Boolean salaryNegotiable;
    private String jobSeekingStatus;
}
