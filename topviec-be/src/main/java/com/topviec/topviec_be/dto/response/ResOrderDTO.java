package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.OrderStatus;
import com.topviec.topviec_be.enums.services.OrderType;
import com.topviec.topviec_be.enums.services.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResOrderDTO {
    private Long id;
    private String orderCode;
    private OrderType type;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private String paymentTransactionId;
    private String vnpayTransactionNo;
    private String vnpayResponseCode;
    private LocalDateTime paidAt;
    private String paymentUrl;
    private Boolean refundEligible;
    private String refundReason;
    private LocalDateTime refundRequestedAt;
    private LocalDateTime refundApprovedAt;
    private String note;
    private LocalDateTime createdAt;
    private List<ResOrderItemDTO> items;
    private CompanyInfo company;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfo {
        private String name;
        private String logoUrl;
        private String email;
        private String phone;
    }
}
