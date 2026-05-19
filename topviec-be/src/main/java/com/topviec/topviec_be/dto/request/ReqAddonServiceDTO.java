package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqAddonServiceDTO {

    @NotNull(message = "ID dịch vụ không được để trống")
    private Long serviceId;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Mã không được để trống")
    private String code;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer quantity;

    private Integer durationDays;

    @NotNull(message = "Giá không được để trống")
    private BigDecimal price;

    private String description;

    private Boolean isActive;
}
