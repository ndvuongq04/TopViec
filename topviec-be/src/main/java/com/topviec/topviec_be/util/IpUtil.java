package com.topviec.topviec_be.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IpUtil {

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            ip = ip.split(",")[0].trim(); // Production: lấy IP thật từ Nginx
        } else {
            ip = request.getRemoteAddr(); // Local: lấy trực tiếp
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1"; // Normalize IPv6 → IPv4
        }
        return ip;
    }
}