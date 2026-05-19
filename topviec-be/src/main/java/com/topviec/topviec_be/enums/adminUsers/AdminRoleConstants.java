package com.topviec.topviec_be.enums.adminUsers;

/**
 * Compile-time string constants của AdminRole.
 * Dùng cho @PreAuthorize — vì annotation chỉ chấp nhận constant expression.
 *
 * Giá trị phải khớp với AdminRole enum value.
 */
public final class AdminRoleConstants {

    private AdminRoleConstants() {
    } // Không cho khởi tạo

    public static final String SUPER_ADMIN = "super_admin";
    public static final String CONTENT_MODERATOR = "content_moderator";
    public static final String SUPPORT_ADMIN = "support_admin";
    public static final String FINANCE_ADMIN = "finance_admin";
}