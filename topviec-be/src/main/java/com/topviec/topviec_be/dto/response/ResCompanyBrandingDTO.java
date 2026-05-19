package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.BrandingAddonStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResCompanyBrandingDTO {
    private Long id;
    private Long companyId;
    private Long companyAddonId;
    private Long addonServiceId;
    private String addonName;
    private String serviceCode;
    private BrandingAddonStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
}
