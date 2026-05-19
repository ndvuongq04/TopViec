package com.topviec.topviec_be.service;

import com.topviec.topviec_be.entity.UserSession;

import jakarta.servlet.http.HttpServletRequest;

public interface UserSessionService {
    void createSession(Long userId, String refreshToken, HttpServletRequest request);

    UserSession validateRefreshToken(String refreshToken);

    void revokeSession(String refreshToken);
}
