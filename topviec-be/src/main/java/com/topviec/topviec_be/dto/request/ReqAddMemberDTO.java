package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ReqAddMemberDTO {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu tạm không được để trống")
    private String tempPassword;

    @NotNull(message = "Vai trò không được để trống")
    private Long roleId;

    /**
     * Tùy chỉnh quyền riêng lẻ (ghi đè lên quyền mặc định của role).
     * null = dùng nguyên quyền mặc định từ RoleDefault.
     */
    private Map<String, Boolean> customActions;
}
