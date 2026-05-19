package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "user_sessions", indexes = {
        // 2 field được query thường xuyên nhất, index giúp tăng tốc
        @Index(name = "idx_user_sessions_user_id", columnList = "user_id"),
        @Index(name = "idx_user_sessions_refresh_token_hash", columnList = "refresh_token_hash")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Chỉ lưu SHA-256 hash, không bao giờ lưu raw refresh token
    @Column(name = "refresh_token_hash", nullable = false, unique = true)
    private String refreshTokenHash;

    // Lưu JSON linh hoạt: {"device": "Chrome/Windows", "os": "Windows 11", ...}
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "device_info", columnDefinition = "json")
    private Map<String, Object> deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // NULL = session đang hoạt động | có giá trị = đã bị thu hồi
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Cập nhật mỗi khi dùng refresh token để lấy access token mới
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Helper method — kiểm tra session còn hợp lệ không
    public boolean isValid() {
        return revokedAt == null && LocalDateTime.now().isBefore(expiresAt);
    }

    // Helper method — thu hồi session
    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }
}