package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateOnlineCvDTO {

    @NotBlank(message = "Ten CV khong duoc de trong")
    @Size(max = 100, message = "Ten CV toi da 100 ky tu")
    private String title;

    @NotNull(message = "templateId khong duoc de trong")
    private Long templateId;

    private Boolean isDefault;

    @Valid
    private CvOnlineExtraDataDTO extraData;
}
