package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.entity.ActionItem;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResRoleDefaultDTO {
    private Long id;
    private MemberRole roleName;
    private List<ActionItem> actions;
    private LocalDateTime updatedAt;
}
