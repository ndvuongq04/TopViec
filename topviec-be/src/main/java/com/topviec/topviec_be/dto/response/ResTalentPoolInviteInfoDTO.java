package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResTalentPoolInviteInfoDTO {
    private Long applicationId;
    private Long jobPostId;
    private String jobTitle;
    private String companyName;
    private String companyLogoUrl;
}
