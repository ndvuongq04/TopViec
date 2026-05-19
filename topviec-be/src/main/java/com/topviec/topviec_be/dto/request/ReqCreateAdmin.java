package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.adminUsers.AdminRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCreateAdmin {

    // ── Thông tin tài khoản User ──────────────────────────────────────────────

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    private String password;

    // ── Thông tin AdminUser ───────────────────────────────────────────────────

    @NotNull(message = "admin_role không được để trống")
    private AdminRole adminRole; // JSON nhận "super_admin" -> AdminRole.SUPER_ADMIN qua @JsonCreator

    @NotBlank(message = "full_name không được để trống")
    private String fullName;

    private String department;
}