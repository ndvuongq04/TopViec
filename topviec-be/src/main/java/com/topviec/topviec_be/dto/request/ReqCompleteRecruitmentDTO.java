package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCompleteRecruitmentDTO {

    @NotEmpty(message = "Danh sách ứng viên trúng tuyển không được trống")
    private List<Long> applicationIds;
}
