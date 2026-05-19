package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.users.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResEmployerProfileDTO {

    // Thông tin tài khoản
    private Long userId;
    private String email;
    private UserStatus accountStatus;
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime lastLoginAt;

    // Thông tin thành viên trong công ty
    private Long memberId;
    private MemberRole roleName;
    private String memberStatus;
    private LocalDateTime memberCreatedAt;

    // Thông tin công ty
    private Long companyId;
    private String companyName;
    private String companySlug;
    private String companyLogoUrl;
}
