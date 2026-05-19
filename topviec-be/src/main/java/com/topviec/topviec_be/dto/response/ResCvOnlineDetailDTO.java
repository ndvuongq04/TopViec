package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.enums.cvs.CvParseStatus;
import com.topviec.topviec_be.enums.cvs.CvType;
import com.topviec.topviec_be.enums.cvs.CvVisibility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResCvOnlineDetailDTO {

    private Long id;
    private String title;
    private CvType cvType;
    private Long templateId;
    private ResCvTemplateDetailDTO template;
    private CvOnlineExtraDataDTO extraData;
    private String pdfUrl;
    private Boolean pdfDirty;
    private Boolean isDefault;
    private CvVisibility visibility;
    private CvParseStatus parseStatus;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
