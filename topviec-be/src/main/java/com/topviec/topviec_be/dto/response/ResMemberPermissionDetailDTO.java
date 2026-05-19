package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.entity.ActionItem;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResMemberPermissionDetailDTO {

    private Long userId;
    private String email;
    private Long roleId;
    private MemberRole roleName;
    private String status;
    private String jobTitle;
    private LocalDateTime createdAt;

    /**
     * Các quyền custom ghi đè (grant: true / revoke: false).
     * Chỉ chứa những action được tuỳ chỉnh riêng cho thành viên này.
     */
    private Map<String, Boolean> customPermissions;

    /**
     * Toàn bộ quyền hạn đã được tính toán (effective).
     * Mỗi action phản ánh trạng thái thực tế sau khi áp dụng role mặc định
     * và các custom grant/revoke override.
     */
    private List<ActionItem> effectivePermissions;
}
