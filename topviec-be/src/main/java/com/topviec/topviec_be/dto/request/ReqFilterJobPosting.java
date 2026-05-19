package com.topviec.topviec_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqFilterJobPosting {

    private String keyword;
    private Long companyId;
    private Long industryId;
    private Long levelId;
    private String workType;
    private String status;
    private Long provinceId;
    private Boolean isFeatured;
    private Boolean isUrgent;

    private Long salaryMin;
    private Long salaryMax;

    private Integer experienceYearsMin;
    private Integer experienceYearsMax;

    private int page = 0;
    private int size = 10;

    private String sortBy = "createdAt";
    private String sortDir = "desc";
}