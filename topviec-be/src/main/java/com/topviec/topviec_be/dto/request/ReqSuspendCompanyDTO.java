package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSuspendCompanyDTO {

    @NotBlank(message = "Lý do suspend không được để trống")
    private String suspendedReason;
}
