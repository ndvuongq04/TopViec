package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.companyMember.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResRoleSummaryDTO {
    private Long id;
    private MemberRole roleName;
}
