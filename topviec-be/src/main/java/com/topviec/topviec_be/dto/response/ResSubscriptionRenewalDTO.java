package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.BillingCycle;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResSubscriptionRenewalDTO {

    private Long renewalLogId;
    private Long orderId;
    private String orderCode;
    private BigDecimal totalAmount;

    /** Thông tin subscription sau khi gia hạn */
    private SubscriptionInfo subscription;

    @Data
    @Builder
    public static class SubscriptionInfo {
        private Long id;
        private String packageName;
        private String packageCode;
        private BillingCycle billingCycle;
        private SubscriptionStatus status;
        private LocalDateTime oldExpiredAt;
        private LocalDateTime newExpiredAt;
        private List<UsageInfo> usages;
    }

    @Data
    @Builder
    public static class UsageInfo {
        private String featureCode;
        private String featureName;
        private Integer quantityTotal;
        private Integer quantityRemaining;
    }
}
