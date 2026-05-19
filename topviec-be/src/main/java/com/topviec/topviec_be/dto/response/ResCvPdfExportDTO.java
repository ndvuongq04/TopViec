package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResCvPdfExportDTO {

    private Long cvId;
    private String pdfUrl;
    private Boolean pdfDirty;
    private LocalDateTime generatedAt;
}
