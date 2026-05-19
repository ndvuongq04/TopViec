package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResOverdueApplicationDTO {

    private Long applicationId;
    private Long candidateUserId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private Integer reminderCount;
    private LocalDateTime firstReminderAt;
    private LocalDateTime reminderDeadline;
    private String currentRoundName;
    private Integer currentRoundNumber;
}
