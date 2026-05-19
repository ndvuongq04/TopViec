package com.topviec.topviec_be.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResIndustryDTO {

    private Long id;
    private Long parentId;
    private String name;
    private String slug;
    private String icon;
    private Integer sortOrder;
    private Boolean isActive;
}