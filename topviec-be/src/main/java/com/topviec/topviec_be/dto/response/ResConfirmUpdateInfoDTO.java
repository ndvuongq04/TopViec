package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResConfirmUpdateInfoDTO {

    private Long scheduleId;

    // Thông tin công ty & vị trí
    private String companyName;
    private String jobTitle;

    // Thông tin vòng PV
    private Integer roundNumber;
    private String roundName;

    // Thông tin lịch mới
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String interviewType;
    private String location;
    private String meetingLink;

    // Trạng thái xác nhận
    private String status;
    private Boolean confirmedByCandidate;
}
