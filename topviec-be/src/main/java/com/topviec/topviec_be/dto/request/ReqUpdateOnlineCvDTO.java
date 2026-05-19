package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateOnlineCvDTO {

    @NotBlank(message = "Ten CV khong duoc de trong")
    @Size(max = 100, message = "Ten CV toi da 100 ky tu")
    private String title;

    @Valid
    private CvOnlineExtraDataDTO extraData;
}
