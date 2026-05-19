package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqUploadCvDTO {

    @NotBlank(message = "Tên CV không được để trống")
    @Size(max = 100, message = "Tên CV tối đa 100 ký tự")
    private String title;

    private boolean isDefault = false;
}