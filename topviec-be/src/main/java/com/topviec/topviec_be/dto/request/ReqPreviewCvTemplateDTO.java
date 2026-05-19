package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPreviewCvTemplateDTO {

    private Long templateId;

    @NotBlank(message = "HTML content khong duoc de trong")
    private String htmlContent;

    @NotBlank(message = "CSS content khong duoc de trong")
    private String cssContent;
}
