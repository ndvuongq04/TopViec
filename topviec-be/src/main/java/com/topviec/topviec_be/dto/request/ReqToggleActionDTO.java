package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqToggleActionDTO {

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean enabled;
}
