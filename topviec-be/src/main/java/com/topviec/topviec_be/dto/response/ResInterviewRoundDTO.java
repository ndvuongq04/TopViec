package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResInterviewRoundDTO {

    private Long id;
    private Long jobPostId;
    private Integer roundNumber;
    private String roundName;
    private String description;
    private Integer expectedDuration;
    private Boolean isFinal;
    private List<InterviewerInfo> interviewers;
    private Long candidateCount; // số UV đang ở vòng này
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InterviewerInfo {
        private Long id;
        private String name;
        private String email;
        private String phone;
    }
}
