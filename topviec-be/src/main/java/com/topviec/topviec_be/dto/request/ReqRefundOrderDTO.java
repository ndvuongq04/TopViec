package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqRefundOrderDTO {
    @NotBlank(message = "Ly do hoan tien khong duoc de trong")
    private String reason;
}
