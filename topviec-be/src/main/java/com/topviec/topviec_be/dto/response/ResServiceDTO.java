package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.ServiceCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResServiceDTO {
    private Long id;
    private String code;
    private String name;
    private ServiceCategory category;
    private String categoryName;
    private String unit;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
