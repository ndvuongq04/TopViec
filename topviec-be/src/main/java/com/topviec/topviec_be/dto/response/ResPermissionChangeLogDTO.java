package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.companyMember.PermissionChangeType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResPermissionChangeLogDTO {

    private Long id;
    private Long targetUserId;
    private String targetEmail;
    private Long changedBy;
    private String changedByEmail;
    private PermissionChangeType changeType;
    private MemberRole oldRole;
    private MemberRole newRole;
    private Map<String, List<ResActionSummaryDTO>> oldPermissions;
    private Map<String, List<ResActionSummaryDTO>> newPermissions;
    private String reason;
    private LocalDateTime createdAt;
}
