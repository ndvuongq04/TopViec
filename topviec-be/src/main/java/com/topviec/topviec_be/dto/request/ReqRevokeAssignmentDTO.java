package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRevokeAssignmentDTO {

    @NotNull(message = "Vui lòng chọn tin tuyển dụng cần thu hồi phân công")
    private Long jobPostId;

    private String note;
}
