package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.ServiceCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ResAddonServiceDTO {
    private Long id;
    private Long serviceId;
    private String serviceCode;
    private String serviceName;
    private ServiceCategory serviceCategory;
    private String serviceCategoryName;
    private String name;
    private String code;
    private Integer quantity;
    private Integer durationDays;
    private BigDecimal price;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
