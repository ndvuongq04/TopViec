package com.topviec.topviec_be.security;

import com.topviec.topviec_be.service.CompanyMemberService;
import com.topviec.topviec_be.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Component hỗ trợ kiểm tra quyền hạn trong SpEL của @PreAuthorize.
 * Sử dụng: @PreAuthorize("@companyPerm.hasPermission(authentication, 'member:add')")
 */
@Component("companyPerm")
@Slf4j
@RequiredArgsConstructor
public class CompanyPermissionEvaluator {

    private final CompanyMemberService companyMemberService;
    private final CompanyService companyService;

    /**
     * Kiểm tra quyền của người dùng hiện tại đối với một hành động.
     * Tự động lấy companyId từ thông tin người dùng trong Token.
     */
    public boolean hasPermission(Authentication auth, String action) {
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }

        try {
            Long userId = Long.parseLong(jwt.getSubject());
            // Lấy companyId (đối với Employer)
            Long companyId = companyService.getMyCompany(userId).getId();

            log.info(">>>> [PreAuthorize] Checking permission [{}] for userId [{}] in company [{}]", action, userId, companyId);
            return companyMemberService.hasPermission(companyId, userId, action);
        } catch (Exception e) {
            log.error(">>>> [PreAuthorize] Error checking permission: {}", e.getMessage());
            return false;
        }
    }
}
