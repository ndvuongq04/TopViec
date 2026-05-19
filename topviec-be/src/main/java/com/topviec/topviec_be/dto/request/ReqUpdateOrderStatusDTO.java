package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.services.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqUpdateOrderStatusDTO {
    @NotNull(message = "Trạng thái không được để trống")
    private OrderStatus status;
}
