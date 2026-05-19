package com.topviec.topviec_be.dto.request;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateInterviewRoundDTO {

    private String roundName;

    private String description;

    private Integer expectedDuration;

    private Boolean isFinal;

    @Valid
    private List<ReqCreateInterviewRoundDTO.InterviewerDTO> interviewers;
}
