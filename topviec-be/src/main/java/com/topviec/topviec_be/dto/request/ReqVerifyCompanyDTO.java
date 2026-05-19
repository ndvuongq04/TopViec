package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqVerifyCompanyDTO {

    @NotNull(message = "Quyết định duyệt không được để trống")
    private Boolean approved;

    // Bắt buộc điền khi approved = false
    private String rejectionReason;
}