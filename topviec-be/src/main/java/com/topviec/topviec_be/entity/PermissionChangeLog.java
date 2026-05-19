package com.topviec.topviec_be.entity;

import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.companyMember.PermissionChangeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "permission_change_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private PermissionChangeType changeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_role")
    private MemberRole oldRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_role")
    private MemberRole newRole;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_permissions", columnDefinition = "json")
    private Map<String, List<String>> oldPermissions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_permissions", columnDefinition = "json")
    private Map<String, List<String>> newPermissions;

    @Column
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}