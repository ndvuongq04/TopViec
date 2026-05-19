package com.topviec.topviec_be.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionableOrder {
    private Long orderId;
    private String orderCode;
    private String companyName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
