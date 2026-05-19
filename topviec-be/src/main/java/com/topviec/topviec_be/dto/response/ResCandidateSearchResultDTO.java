package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResCandidateSearchResultDTO {

    private Long candidateUserId;
    private String fullName;
    private String avatarUrl;

    // Vị trí & hình thức làm việc mong muốn
    private String preferredJobTitle;
    private String preferredWorkType;

    // Địa chỉ mong muốn
    private Integer preferredLocationId;
    private String preferredLocationName;

    // Mức lương mong muốn
    private Double expectedSalaryMin;
    private Double expectedSalaryMax;
    private Boolean salaryNegotiable;

    private String jobSeekingStatus;

    // true nếu UV đã có trong talent pool của công ty hiện tại
    private boolean alreadyInPool;
}
