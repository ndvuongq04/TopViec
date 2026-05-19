package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqAddActionDTO {

    @NotBlank(message = "Tên action không được để trống")
    private String name;

    @NotBlank(message = "Mã action không được để trống")
    private String code;
}
