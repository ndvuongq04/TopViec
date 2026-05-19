package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.services.AddonPackageGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqAddonPackageDTO {

    @NotNull(message = "Nhóm Addon không được để trống")
    private AddonPackageGroup groupCode;

    @NotBlank(message = "Tên gói không được để trống")
    private String name;

    @NotBlank(message = "Mã gói không được để trống")
    private String code;

    @NotNull(message = "Giá không được để trống")
    private BigDecimal price;

    private Integer durationDays;

    private String description;

    private Boolean isActive;
}
