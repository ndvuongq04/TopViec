package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqExtendDeadlineDTO {

    @NotNull(message = "Số ngày gia hạn không được trống")
    @Min(value = 1, message = "Phải gia hạn ít nhất 1 ngày")
    private Integer extendDays;
}
