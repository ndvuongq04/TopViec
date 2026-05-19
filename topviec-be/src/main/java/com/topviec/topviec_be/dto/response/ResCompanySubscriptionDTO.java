package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.BillingCycle;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResCompanySubscriptionDTO {
    private Long id;
    private Long servicePackageId;
    private String packageName;
    private String packageCode;
    private BillingCycle billingCycle;
    private SubscriptionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private List<ResSubscriptionUsageDTO> usages;

    @Data
    @Builder
    public static class ResSubscriptionUsageDTO {
        private Long id;
        private String featureCode;
        private String featureName;
        private Integer quantityTotal;
        private Integer quantityRemaining;
        private LocalDateTime resetAt;
    }
}
