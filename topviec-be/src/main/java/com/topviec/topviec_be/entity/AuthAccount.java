package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.topviec.topviec_be.enums.authAccount.AuthProvider;

@Entity
@Table(name = "auth_accounts", uniqueConstraints = {
        // Đảm bảo 1 tài khoản Google không thể liên kết với 2 user khác nhau
        @UniqueConstraint(columnNames = { "provider", "provider_user_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider;

    // ID người dùng từ nhà cung cấp (Google, Facebook, LinkedIn)
    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "provider_email")
    private String providerEmail;

    // Access token do provider cấp sau khi user đồng ý ủy quyền (cần mã hóa trước
    // khi lưu)
    @Column(name = "access_token_enc")
    private String accessTokenEnc;

    // Phải mã hóa trước khi lưu (AES-256 ở tầng Service)
    @Column(name = "refresh_token_enc")
    private String refreshTokenEnc;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}