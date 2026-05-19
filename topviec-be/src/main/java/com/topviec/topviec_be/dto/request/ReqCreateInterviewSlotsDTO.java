package com.topviec.topviec_be.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCreateInterviewSlotsDTO {

    @NotEmpty(message = "Danh sách ứng viên không được trống")
    private List<Long> applicationIds;

    @NotNull(message = "Hạn chót chọn lịch không được trống")
    private LocalDateTime deadline;

    @Valid
    @NotEmpty(message = "Cần ít nhất 1 slot thời gian")
    @Size(min = 2, message = "Cần ít nhất 2 slot thời gian")
    private List<SlotDTO> slots;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SlotDTO {
        @NotNull(message = "Giờ bắt đầu không được trống")
        private LocalDateTime startTime;

        @NotNull(message = "Giờ kết thúc không được trống")
        private LocalDateTime endTime;

        @NotNull(message = "Số ứng viên tối đa không được trống")
        @Min(value = 1, message = "Số ứng viên tối đa phải ít nhất là 1")
        private Integer maxCandidates;

        @NotBlank(message = "Loại phỏng vấn không được trống")
        private String interviewType;

        private String location;

        private String meetingLink;

        private String interviewerName;
    }
}
