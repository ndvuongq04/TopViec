package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResSubscriptionHistoryDTO {
    private Long subscriptionId;
    private Long companyId;
    private Long orderId;
    private Long servicePackageId;
    private String packageName;
    private String packageCode;
    private String status;
    private String billingCycle;
    private LocalDateTime startedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime purchasedAt; // Mapping from createdAt
    private java.math.BigDecimal packagePrice;
}
