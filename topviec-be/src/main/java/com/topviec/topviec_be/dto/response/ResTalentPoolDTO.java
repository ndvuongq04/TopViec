package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResTalentPoolDTO {

    private Long id;
    private Long companyId;
    private Long candidateUserId;
    private String candidateName;
    private String candidateAvatarUrl;
    private Long addedBy;
    private String source;
    private String note;
    private LocalDateTime createdAt;
}
