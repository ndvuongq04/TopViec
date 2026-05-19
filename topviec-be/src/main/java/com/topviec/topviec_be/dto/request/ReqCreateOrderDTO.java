package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.services.OrderType;
import com.topviec.topviec_be.enums.services.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReqCreateOrderDTO {

    @NotNull(message = "Loai order khong duoc de trong")
    private OrderType type;

    // Legacy single-item fields, still supported for backward compatibility.
    private Long packageId;

    @Min(value = 1, message = "So luong toi thieu la 1")
    private Integer quantity;

    @Valid
    private List<Item> items;

    @NotNull(message = "Phuong thuc thanh toan khong duoc de trong")
    private PaymentMethod paymentMethod;

    private Boolean payNow;

    @Data
    public static class Item {
        @NotNull(message = "ID goi khong duoc de trong")
        private Long packageId;

        @NotNull(message = "So luong khong duoc de trong")
        @Min(value = 1, message = "So luong toi thieu la 1")
        private Integer quantity;
    }
}
