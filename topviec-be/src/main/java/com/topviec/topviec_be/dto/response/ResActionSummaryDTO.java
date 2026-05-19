package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResActionSummaryDTO {
    private String code;
    private String name;
}
