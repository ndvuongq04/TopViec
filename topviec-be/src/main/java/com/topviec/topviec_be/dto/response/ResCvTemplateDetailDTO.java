package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResCvTemplateDetailDTO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private String htmlContent;
    private String cssContent;
    private String versionTag;
    private Boolean isActive;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
