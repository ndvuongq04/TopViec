package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRenameCvDTO {
    @NotBlank(message = "Tên CV không được để trống")
    private String title;
}
