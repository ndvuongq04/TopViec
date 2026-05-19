package com.topviec.topviec_be.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCreateInterviewRoundDTO {

    @NotBlank(message = "Tên vòng phỏng vấn không được trống")
    private String roundName;

    private String description;

    private Integer expectedDuration;

    private Boolean isFinal;

    @Valid
    private List<InterviewerDTO> interviewers;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InterviewerDTO {
        @NotBlank(message = "Tên người phỏng vấn không được trống")
        private String name;
        private String email;
        private String phone;
    }
}
