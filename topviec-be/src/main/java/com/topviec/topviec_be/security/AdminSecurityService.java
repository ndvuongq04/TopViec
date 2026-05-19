package com.topviec.topviec_be.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Bean hỗ trợ @PreAuthorize kiểm tra admin_role từ JWT claim.
 *
 * Cách dùng trong Controller:
 * @PreAuthorize("@adminSecurity.hasRole(authentication, 'super_admin')")
 * @PreAuthorize("@adminSecurity.hasAnyRole(authentication, 'super_admin',
 * 'content_moderator')")
 */
@Service("adminSecurity")
public class AdminSecurityService {

    /**
     * Kiểm tra JWT có đúng adminRole không.
     */
    public boolean hasRole(Authentication authentication, String requiredRole) {
        String adminRole = extractAdminRole(authentication);
        return requiredRole.equalsIgnoreCase(adminRole);
    }

    /**
     * Kiểm tra JWT có nằm trong danh sách role được phép không.
     */
    public boolean hasAnyRole(Authentication authentication, String... roles) {
        String adminRole = extractAdminRole(authentication);
        if (adminRole == null)
            return false;
        for (String role : roles) {
            if (role.equalsIgnoreCase(adminRole))
                return true;
        }
        return false;
    }

    private String extractAdminRole(Authentication authentication) {
        if (authentication == null)
            return null;
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt))
            return null;
        return jwt.getClaimAsString("adminRole");
    }
}