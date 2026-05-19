package com.topviec.topviec_be.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.topviec.topviec_be.config.JwtConfig;
import com.topviec.topviec_be.enums.users.UserStatus;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    // Tạo Access Token — không có admin_role (dùng cho candidate, employer)
    public String generateAccessToken(Long userId, String email, String role, UserStatus status) {
        Instant now = Instant.now();

        JwsHeader header = JwsHeader.with(JwtConfig.JWT_ALGORITHM).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .claim("emailVerified", status != UserStatus.PENDING)
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtProperties.getAccessTokenExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    // Tạo Access Token — có admin_role (dùng cho admin_users)
    public String generateAccessToken(Long userId, String email, String role, UserStatus status, String adminRole) {
        Instant now = Instant.now();

        JwsHeader header = JwsHeader.with(JwtConfig.JWT_ALGORITHM).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .claim("emailVerified", status != UserStatus.PENDING)
                .claim("adminRole", adminRole) // "super_admin" | "content_moderator" | ...
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtProperties.getAccessTokenExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    // Tạo Refresh Token
    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();

        JwsHeader header = JwsHeader.with(JwtConfig.JWT_ALGORITHM).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtProperties.getRefreshTokenExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}