package com.topviec.topviec_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResViolationReasonDTO {

    private String code;
    private String name;
    private String group;
    private Boolean requiresEvidence;
    private String priority;
}
