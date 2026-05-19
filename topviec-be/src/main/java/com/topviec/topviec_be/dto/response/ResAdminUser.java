package com.topviec.topviec_be.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResAdminUser {

    // ── User info ─────────────────────────────────────────────────────────────
    private Long userId;
    private String email;

    // ── AdminUser info ────────────────────────────────────────────────────────
    private Long adminUsersId;
    private String adminRole;
    private String fullName;
    private String department;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}