package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.services.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReqServicePackageDTO {

    @NotBlank(message = "Tên gói không được để trống")
    private String name;

    @NotBlank(message = "Mã gói không được để trống")
    private String code;

    @NotNull(message = "Chu kỳ thanh toán không được để trống")
    private BillingCycle billingCycle;

    @NotNull(message = "Giá không được để trống")
    private BigDecimal price;

    private String description;

    private Boolean isActive;

    private Integer sortOrder;

    private List<DetailItem> details;

    @Data
    public static class DetailItem {
        @NotNull(message = "ID dịch vụ không được để trống")
        private Long serviceId;

        @NotNull(message = "Số lượng không được để trống")
        private Integer quantity;
    }
}
