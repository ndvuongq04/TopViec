package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqCreateAdmin;
import com.topviec.topviec_be.dto.request.ReqUpdateAdmin;
import com.topviec.topviec_be.dto.response.ResAdminUser;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.AdminUser;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.AdminUserService;
import com.topviec.topviec_be.service.AdminUserService;
import com.topviec.topviec_be.util.ChangeTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // -------------------------------------------------------------------------
    // Create — tạo User (user_type=admin) + AdminUser trong 1 transaction
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResAdminUser createAdmin(ReqCreateAdmin request, Long createdByUserId) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng: " + request.getEmail());
        }

        // Bước 1: Tạo tài khoản User với user_type = admin
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.ADMIN)
                .status(UserStatus.ACTIVE)
                .twoFactorEnabled(false)
                .createdBy(createdByUserId)
                .build();

        User savedUser = userRepository.save(user);

        // Bước 2: Tạo AdminUser gắn với User vừa tạo
        AdminUser adminUser = AdminUser.builder()
                .user(savedUser)
                .adminRole(request.getAdminRole().getValue())
                .fullName(request.getFullName())
                .department(request.getDepartment())
                .createdBy(createdByUserId)
                .build();

        return toResponse(adminUserRepository.save(adminUser));
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAllAdmins(String keyword, String adminRole, Pageable pageable) {
        // Chuẩn hóa input: blank string → null để query không lọc
        String keywordParam = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String adminRoleParam = (adminRole != null && !adminRole.isBlank()) ? adminRole.trim() : null;

        Page<AdminUser> page = adminUserRepository.findAllWithFilter(keywordParam, adminRoleParam, pageable);
        return toResultPagination(page, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResAdminUser getAdminById(Long adminUsersId) {
        AdminUser admin = adminUserRepository.findActiveById(adminUsersId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin với id: " + adminUsersId));
        return toResponse(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public ResAdminUser getMyAdminProfile(Long userId) {
        AdminUser admin = adminUserRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin admin"));
        return toResponse(admin);
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResAdminUser updateAdmin(Long adminUsersId, ReqUpdateAdmin request, Long updatedByUserId) {
        AdminUser admin = adminUserRepository.findById(adminUsersId)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin với id: " + adminUsersId));

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(admin);

        admin.setAdminRole(request.getAdminRole().getValue());
        admin.setFullName(request.getFullName());
        admin.setDepartment(request.getDepartment());
        admin.setUpdatedBy(updatedByUserId);

        AdminUser saved = adminUserRepository.save(admin);

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Toggle active
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResAdminUser toggleActive(Long adminUsersId, Long updatedByUserId) {
        AdminUser admin = adminUserRepository.findById(adminUsersId)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin với id: " + adminUsersId));

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(admin);

        admin.setIsActive(!admin.getIsActive());
        admin.setUpdatedBy(updatedByUserId);

        AdminUser saved = adminUserRepository.save(admin);

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Delete — soft delete AdminUser + vô hiệu hóa User tương ứng
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void deleteAdmin(Long adminUsersId, Long deletedByUserId) {
        AdminUser admin = adminUserRepository.findById(adminUsersId)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin với id: " + adminUsersId));

        // Soft delete AdminUser
        admin.setIsActive(false);
        admin.setDeletedAt(LocalDateTime.now());
        admin.setUpdatedBy(deletedByUserId);
        adminUserRepository.save(admin);

        // Vô hiệu hóa User để chặn đăng nhập ngay lập tức
        User user = admin.getUser();
        user.setStatus(UserStatus.LOCKED_PERM);
        user.setUpdatedBy(deletedByUserId);
        userRepository.save(user);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Map entity → response DTO.
     */
    private ResAdminUser toResponse(AdminUser admin) {
        return ResAdminUser.builder()
                .adminUsersId(admin.getAdminUsersId())
                .userId(admin.getUser().getId())
                .email(admin.getUser().getEmail())
                .adminRole(admin.getAdminRole())
                .fullName(admin.getFullName())
                .department(admin.getDepartment())
                .isActive(admin.getIsActive())
                .createdAt(admin.getCreatedAt())
                .createdBy(admin.getCreatedBy())
                .updatedAt(admin.getUpdatedAt())
                .updatedBy(admin.getUpdatedBy())
                .build();
    }

    /**
     * Chuyển Page<AdminUser> sang ResultPaginationDTO theo chuẩn project.
     */
    private ResultPaginationDTO toResultPagination(Page<AdminUser> page, Pageable pageable) {
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(page.getContent().stream().map(this::toResponse).toList());
        return result;
    }
}