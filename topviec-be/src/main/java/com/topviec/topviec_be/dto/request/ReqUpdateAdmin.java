package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.adminUsers.AdminRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateAdmin {

    @NotNull(message = "admin_role không được để trống")
    private AdminRole adminRole; // JSON nhận "finance_admin" -> AdminRole.FINANCE_ADMIN qua @JsonCreator

    @NotBlank(message = "full_name không được để trống")
    private String fullName;

    private String department;
}