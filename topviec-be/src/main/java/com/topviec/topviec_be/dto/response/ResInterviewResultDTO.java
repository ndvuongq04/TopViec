package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResInterviewResultDTO {

    private Long id;
    private Long interviewId;
    private String result;
    private Integer rating;
    private String note;
    private Boolean notifyCandidate;
    private Long evaluatedBy;
    private LocalDateTime evaluatedAt;
}
