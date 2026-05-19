package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCreateInterviewScheduleDTO {

    @NotNull(message = "applicationId không được trống")
    private Long applicationId;

    @NotNull(message = "Thời gian phỏng vấn không được trống")
    private LocalDateTime scheduledAt;

    private Integer durationMinutes;

    @NotBlank(message = "Loại phỏng vấn không được trống")
    private String interviewType;

    private String location;

    private String meetingLink;

    private String interviewerNote;
}
