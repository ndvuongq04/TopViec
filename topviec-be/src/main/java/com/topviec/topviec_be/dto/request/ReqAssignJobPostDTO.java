package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAssignJobPostDTO {

    @NotNull(message = "Vui lòng chọn tin tuyển dụng")
    private Long jobPostId;

    @NotNull(message = "Vui lòng chọn nhà tuyển dụng")
    private Long userId;

    private String note;
}
