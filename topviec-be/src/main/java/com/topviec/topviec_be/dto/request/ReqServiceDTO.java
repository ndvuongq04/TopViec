package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.services.ServiceCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqServiceDTO {

    @NotBlank(message = "Mã dịch vụ không được để trống")
    private String code;

    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    private ServiceCategory category;

    private String unit;

    private String description;

    private Boolean isActive;
}
