package com.topviec.topviec_be.dto.request;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateInterviewScheduleDTO {

    private LocalDateTime scheduledAt;

    private String interviewType;

    private String location;

    private String meetingLink;

    private String interviewerNote;
}
