package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResInterviewHistoryDTO {

    private Long applicationId;
    private String candidateName;
    private String currentStatus;
    private String cvUrl;
    private List<RoundHistory> rounds;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoundHistory {
        private Integer roundNumber;
        private String roundName;
        private Boolean isFinal;

        // Lịch PV (nếu có)
        private Long scheduleId;
        private LocalDateTime scheduledAt;
        private String interviewType;
        private String scheduleStatus;

        // Kết quả (nếu có)
        private String result;
        private Integer rating;
        private String note;
        private LocalDateTime evaluatedAt;
    }
}
