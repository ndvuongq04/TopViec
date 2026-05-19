package com.topviec.topviec_be.dto.request;

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
public class ReqCreateAppealDTO {

    @NotNull(message = "ID báo cáo không được để trống")
    private Long complaintId;

    @NotBlank(message = "Nội dung kháng cáo không được để trống")
    @Size(max = 2000, message = "Nội dung kháng cáo không được vượt quá 2000 ký tự")
    private String content;
}
