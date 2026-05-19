package com.topviec.topviec_be.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResInterviewReadinessDTO {

    private Boolean isJobClosed;
    private Boolean hasRounds;
    private Boolean hasCvPassed;
    private Boolean ready;
}
