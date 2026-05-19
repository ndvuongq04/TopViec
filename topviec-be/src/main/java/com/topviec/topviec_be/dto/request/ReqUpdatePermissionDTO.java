package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ReqUpdatePermissionDTO {

    @NotNull(message = "Vai trò không được để trống")
    private Long roleId;

    /**
     * Danh sách quyền tùy chỉnh ghi đè lên quyền default của role.
     * key = action_name (ví dụ: "job:delete_other"), value = true (grant) / false (revoke).
     * Nếu truyền null, sẽ giữ nguyên cấu hình cũ hoặc reset về mặc định tùy logic.
     */
    private Map<String, Boolean> customActions;

    /**
     * Lý do thay đổi quyền (tùy chọn).
     */
    private String reason;
}
