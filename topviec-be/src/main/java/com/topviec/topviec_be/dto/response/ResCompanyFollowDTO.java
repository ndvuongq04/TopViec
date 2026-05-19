package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResCompanyFollowDTO {
    private Long id;
    private Long userId;
    private Long companyId;
    private ResCompanyDTO company;
    private LocalDateTime followedAt;
}
