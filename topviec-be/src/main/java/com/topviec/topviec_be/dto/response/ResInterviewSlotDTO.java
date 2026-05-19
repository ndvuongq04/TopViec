package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResInterviewSlotDTO {

    private Long id;
    private Long roundId;
    private LocalDateTime slotDeadline;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String interviewType;
    private String location;
    private String meetingLink;
    private String interviewerName;
    private Integer maxCandidates;
    private Integer registeredCount;
    private LocalDateTime createdAt;
}
