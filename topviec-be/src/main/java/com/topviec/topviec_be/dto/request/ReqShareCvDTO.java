package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.cvs.CvVisibility;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqShareCvDTO {
    private CvVisibility visibility;
}
