package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqAdjustViolationScoreDTO {

    @NotNull(message = "Số điểm giảm không được để trống")
    @Min(value = 1, message = "Số điểm giảm phải lớn hơn 0")
    private Integer pointsToDecrease;

    @NotBlank(message = "Lý do giảm điểm không được để trống")
    @Size(max = 500, message = "Lý do không được vượt quá 500 ký tự")
    private String note;
}
