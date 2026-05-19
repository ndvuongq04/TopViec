package com.topviec.topviec_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqIndustryDTO {

    private Long parentId;
    private String name;
    private String slug;
    private String icon;
    private Integer sortOrder;
    private Boolean isActive;
}