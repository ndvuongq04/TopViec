package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.BillingCycle;
import com.topviec.topviec_be.enums.services.OrderItemType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ResOrderItemDTO {
    private Long id;
    private OrderItemType itemType;
    private Long servicePackageId;
    private Long addonServiceId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BillingCycle billingCycle;
    private Integer durationDays;
    private String packageName;
    private List<ResServicePackageDetailDTO> details;
}
