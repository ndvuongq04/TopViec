package com.topviec.topviec_be.util;

import com.topviec.topviec_be.exception.AppException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * Lấy userId từ JWT subject trong SecurityContext.
     * Subject được set là String.valueOf(userId) khi generate token.
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw AppException.unauthorized("Chưa đăng nhập");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subject = jwt.getSubject();

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw AppException.unauthorized("Token không hợp lệ");
        }
    }
}