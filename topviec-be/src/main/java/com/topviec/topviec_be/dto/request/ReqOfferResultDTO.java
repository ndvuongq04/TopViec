package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.interview.OfferResult;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqOfferResultDTO {

    @NotNull(message = "Kết quả offer không được trống")
    private OfferResult result;
}
