package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.AddonPackageGroup;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ResAddonPackageDTO {
    private Long id;
    private AddonPackageGroup groupCode;
    private String groupName;
    private String name;
    private String code;
    private BigDecimal price;
    private Integer durationDays;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
