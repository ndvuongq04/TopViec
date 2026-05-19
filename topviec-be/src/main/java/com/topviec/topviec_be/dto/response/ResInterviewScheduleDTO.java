package com.topviec.topviec_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResInterviewScheduleDTO {

    private Long id;
    private Long applicationId;
    private Long roundId;
    private Integer roundNumber;
    private String roundName;

    // Thông tin UV
    private Long candidateUserId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;

    // Thông tin buổi PV
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String interviewType;
    private String location;
    private String meetingLink;
    private String status;
    private Boolean confirmedByCandidate;
    private Boolean isDefault;
    private String interviewerNote;

    // Slot UV đã chọn (null nếu NTT đặt thủ công hoặc chưa chọn)
    private Long slotId;
    private LocalDateTime slotStartTime;
    private LocalDateTime slotEndTime;
    private String slotInterviewerName;

    // Danh sách slot đã gửi cho UV này (theo từng batch)
    private List<SentSlotDTO> sentSlots;

    // Trạng thái đơn ứng tuyển (dùng để FE kiểm tra ứng viên đã OFFERED chưa)
    private String applicationStatus;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SentSlotDTO {
        private Long id;
        private Integer batchNumber;
        private LocalDateTime deadline;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String interviewType;
        private String location;
        private String meetingLink;
        private String interviewerName;
        private Integer maxCandidates;
        private Integer registeredCount;
    }
}
