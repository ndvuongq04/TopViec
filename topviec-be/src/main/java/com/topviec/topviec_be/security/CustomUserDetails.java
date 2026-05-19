package com.topviec.topviec_be.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.enums.users.UserType;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final UserType userType;
    private final UserStatus status;

    // Nhận vào entity User và extract các field cần thiết
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.userType = user.getUserType();
        this.status = user.getStatus();
    }

    // Trả về danh sách quyền của user — Spring Security dùng để kiểm tra hasRole()
    // VD: UserType.ADMIN → "ROLE_ADMIN"
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userType.name()));
    }

    // Spring dùng field này để identify user — vì login bằng email nên trả về email
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // false → Spring tự động throw LockedException — không cần tự kiểm tra
    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED_PERM;
    }

    // false → Spring tự động throw DisabledException — không cần tự kiểm tra
    // Chỉ cho phép đăng nhập khi status = ACTIVE (pending chưa verify email cũng bị
    // chặn)
    @Override
    public boolean isEnabled() {
        // Cho phép ACTIVE và PENDING đăng nhập
        // Chỉ chặn BANNED, LOCKED_PERM, DELETED...
        return status == UserStatus.ACTIVE || status == UserStatus.PENDING;
    }

    // Tài khoản không có khái niệm hết hạn → luôn true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Password không có khái niệm hết hạn → luôn true
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}