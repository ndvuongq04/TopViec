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
public class ReqForceScheduleDTO {

    @NotNull(message = "Thời gian phỏng vấn không được trống")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "Loại phỏng vấn không được trống")
    private String interviewType;

    private String location;

    private String meetingLink;
}
