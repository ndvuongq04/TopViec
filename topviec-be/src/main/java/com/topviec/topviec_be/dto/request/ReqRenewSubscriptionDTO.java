package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.services.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqRenewSubscriptionDTO {

    @NotNull(message = "paymentMethod không được để trống")
    private PaymentMethod paymentMethod;
}
