package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqApplyAddonDTO {

    @NotNull(message = "ID dịch vụ lẻ không được để trống")
    private Long companyAddonId;
}
