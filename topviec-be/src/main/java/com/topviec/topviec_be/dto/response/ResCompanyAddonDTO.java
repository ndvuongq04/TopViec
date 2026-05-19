package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResCompanyAddonDTO {
    private Long id;
    private Long addonServiceId;
    private List<Long> companyAddonIds;
    private String addonName;
    private String addonCode;
    private Integer addonQuantity;
    private Long serviceId;
    private String serviceCode;
    private String serviceName;
    private ServiceCategory serviceCategory;
    private String serviceCategoryName;
    private SubscriptionStatus status;
    private Integer quantityTotal;
    private Integer quantityRemaining;
    private LocalDateTime startedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
}
