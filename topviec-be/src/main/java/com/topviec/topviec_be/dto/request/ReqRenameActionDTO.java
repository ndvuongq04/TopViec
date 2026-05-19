package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqRenameActionDTO {

    @NotBlank(message = "Tên action không được để trống")
    private String name;
}
