package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqSubmitAppealDTO {

    @NotBlank(message = "Nội dung kháng cáo không được để trống")
    @Size(max = 2000, message = "Nội dung kháng cáo không được vượt quá 2000 ký tự")
    private String content;
}
