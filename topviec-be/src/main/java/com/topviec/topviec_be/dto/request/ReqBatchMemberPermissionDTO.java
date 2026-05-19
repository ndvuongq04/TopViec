package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ReqBatchMemberPermissionDTO {

    @NotEmpty(message = "Danh sách userId không được để trống")
    @Size(min = 1, max = 5, message = "Chỉ được truy vấn từ 1 đến 5 thành viên cùng lúc")
    private List<Long> userIds;
}
