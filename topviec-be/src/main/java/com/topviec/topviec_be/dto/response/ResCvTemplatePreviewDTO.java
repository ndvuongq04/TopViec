package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class ResCvTemplatePreviewDTO {

    private Long templateId;
    private String versionTag;
    private String renderedHtml;
    private String renderedXhtml;
    private CvOnlineExtraDataDTO sampleData;
    private boolean valid;
    private Set<String> rootPlaceholders;
    private Set<String> sections;
    private List<String> placeholderErrors;
    private List<String> cssWarnings;
}
