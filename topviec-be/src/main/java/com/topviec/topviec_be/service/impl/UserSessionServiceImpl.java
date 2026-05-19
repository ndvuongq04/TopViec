package com.topviec.topviec_be.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.entity.UserSession;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.repository.UserSessionRepository;
import com.topviec.topviec_be.service.UserSessionService;
import com.topviec.topviec_be.util.HashUtil;
import com.topviec.topviec_be.util.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final HashUtil hashUtil;
    private final IpUtil ipUtil;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    public void createSession(Long userId, String refreshToken, HttpServletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("User không tồn tại"));

        // Hash refresh token trước khi lưu
        String refreshTokenHash = hashUtil.hashSHA256(refreshToken);

        // Lấy device info
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("userAgent", request.getHeader("User-Agent"));

        UserSession session = UserSession.builder()
                .user(user)
                .refreshTokenHash(refreshTokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipUtil.getClientIp(request))
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        userSessionRepository.save(session);
    }

    @Override
    public UserSession validateRefreshToken(String refreshToken) {
        String hash = hashUtil.hashSHA256(refreshToken);
        UserSession session = userSessionRepository.findByRefreshTokenHash(hash)
                .orElseThrow(() -> AppException.unauthorized("Refresh token không hợp lệ"));

        // Kiểm tra session còn hợp lệ không (hết hạn hoặc đã bị revoke)
        if (!session.isValid()) {
            throw AppException.unauthorized("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
        }

        // Cập nhật lastUsedAt
        session.setLastUsedAt(LocalDateTime.now());
        userSessionRepository.save(session);

        return session;
    }

    @Override
    public void revokeSession(String refreshToken) {
        String hash = hashUtil.hashSHA256(refreshToken);
        userSessionRepository.findByRefreshTokenHash(hash)
                .ifPresent(session -> {
                    session.revoke();
                    userSessionRepository.save(session);
                });
    }

}