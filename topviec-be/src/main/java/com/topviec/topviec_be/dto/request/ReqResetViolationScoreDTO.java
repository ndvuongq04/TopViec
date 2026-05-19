package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqResetViolationScoreDTO {

    @NotBlank(message = "Lý do reset không được để trống")
    @Size(max = 500, message = "Lý do không được vượt quá 500 ký tự")
    private String note;
}
