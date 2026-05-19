package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqChangeOnlineCvTemplateDTO {

    @NotNull(message = "templateId khong duoc de trong")
    private Long templateId;
}
