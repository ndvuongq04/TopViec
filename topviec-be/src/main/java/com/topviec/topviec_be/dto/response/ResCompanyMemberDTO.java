package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

import com.topviec.topviec_be.enums.companyMember.MemberRole;

@Data
@Builder
public class ResCompanyMemberDTO {

    private Long id;
    private Long companyId;
    private Long userId;
    private String email;
    private Long roleId;
    private MemberRole roleName;
    private String status;
    private Map<String, Boolean> actions;
    private String jobTitle;
    private LocalDateTime createdAt;
}
