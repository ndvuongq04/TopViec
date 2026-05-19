package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.BillingCycle;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResServicePackageDTO {
    private Long id;
    private String name;
    private String code;
    private BillingCycle billingCycle;
    private BigDecimal price;
    private String description;
    private Boolean isActive;
    private Integer sortOrder;
    private List<ResServicePackageDetailDTO> details;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
