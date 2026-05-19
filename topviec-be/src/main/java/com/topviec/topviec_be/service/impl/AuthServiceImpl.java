package com.topviec.topviec_be.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topviec.topviec_be.dto.request.ReqChangePasswordDTO;
import com.topviec.topviec_be.dto.request.ReqRegisterCandidateDTO;
import com.topviec.topviec_be.dto.request.ReqRegisterEmployerDTO;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.entity.RoleDefault;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.RoleDefaultRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.AuthService;
import com.topviec.topviec_be.service.CandidateProfileService;
import com.topviec.topviec_be.service.CompanyMemberService;
import com.topviec.topviec_be.service.EmailService;
import com.topviec.topviec_be.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final CandidateProfileService candidateProfileService;
    private final AdminUserRepository adminUserRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final RoleDefaultRepository roleDefaultRepository;
    private final CompanyMemberService companyMemberService;

    @Override
    @Transactional
    public void registerCandidate(ReqRegisterCandidateDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email đã được sử dụng");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.CANDIDATE)
                .status(UserStatus.PENDING) // chờ xác thực email
                .build();

        user = userRepository.save(user);

        // Gọi qua service, không gọi thẳng repo
        candidateProfileService.createDefaultProfile(user.getId(), request.getEmail());

        // Tạo token → lưu Redis → gửi email
        String token = tokenService.generateVerifyEmailToken(request.getEmail());
        emailService.sendVerifyEmail(request.getEmail(), token);
    }

    @Override
    @Transactional
    public void registerEmployer(ReqRegisterEmployerDTO request) {
        // Kiểm tra email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AppException.badRequest("Email đã được sử dụng");
        }

        // Kiểm tra slug công ty chưa tồn tại
        if (companyRepository.existsBySlug(request.getCompanySlug())) {
            throw AppException.conflict("Slug đã được sử dụng, vui lòng chọn slug khác");
        }

        // Bước 1: Tạo User
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.EMPLOYER)
                .status(UserStatus.PENDING)
                .build();

        User savedUser = userRepository.save(user);

        // Bước 2: Tạo Company gắn với User vừa tạo
        // status = pending, verificationStatus = pending → chờ admin duyệt
        Company company = Company.builder()
                .userId(savedUser.getId())
                .name(request.getCompanyName())
                .slug(request.getCompanySlug())
                .description("") // bắt buộc not null trong entity → để trống, NTT bổ sung sau
                .industryId(0L) // bắt buộc not null → placeholder, NTT cập nhật sau
                .companySize("1-50") // default, NTT cập nhật sau
                .createdBy(savedUser.getId())
                .build();

        Company savedCompany = companyRepository.save(company);

        // Bước 3: Tạo CompanyMember với role OWNER
        createOwnerMember(savedCompany.getId(), savedUser.getId());

        // Bước 4: Gửi email xác thực
        String token = tokenService.generateVerifyEmailToken(request.getEmail());
        emailService.sendVerifyEmail(request.getEmail(), token);
    }

    @Override
    @Transactional
    public void registerEmployer(Long adminId, ReqRegisterEmployerDTO request) {
        // Kiểm tra email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AppException.badRequest("Email đã được sử dụng");
        }

        // Kiểm tra slug công ty chưa tồn tại
        if (companyRepository.existsBySlug(request.getCompanySlug())) {
            throw AppException.conflict("Slug đã được sử dụng, vui lòng chọn slug khác");
        }

        // Bước 1: Tạo User
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.EMPLOYER)
                .status(UserStatus.PENDING)
                .build();

        User savedUser = userRepository.save(user);

        // Bước 2: Tạo Company gắn với User vừa tạo
        // status = pending, verificationStatus = pending → chờ admin duyệt
        Company company = Company.builder()
                .userId(savedUser.getId())
                .name(request.getCompanyName())
                .slug(request.getCompanySlug())
                .description("") // bắt buộc not null trong entity → để trống, NTT bổ sung sau
                .industryId(0L) // bắt buộc not null → placeholder, NTT cập nhật sau
                .companySize("1-50") // default, NTT cập nhật sau
                .createdBy(adminId) // admin tạo giúp → createdBy = adminId, không phải userId
                .build();

        Company savedCompany = companyRepository.save(company);

        // Bước 3: Tạo CompanyMember với role OWNER
        createOwnerMember(savedCompany.getId(), savedUser.getId());
    }

    @Override
    public void verifyEmail(String token) {
        // Lấy email từ Redis (tự động xóa token sau khi lấy)
        String email = tokenService.verifyEmailToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User không tồn tại"));

        if (user.getEmailVerifiedAt() != null) {
            throw AppException.badRequest("Email đã được xác thực trước đó");
        }

        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE); // PENDING → ACTIVE
        userRepository.save(user);

        // Kích hoạt các CompanyMember đang pending (nếu có)
        companyMemberService.activatePendingMembers(user.getId());
    }

    @Override
    public void updateLastLogin(Long userId, String ip) {
        userRepository.updateLastLogin(userId, LocalDateTime.now(), ip);
    }

    @Override
    public void forgotPassword(String email) {
        // Không báo lỗi nếu email không tồn tại → tránh email enumeration attack
        // (kẻ tấn công không biết email nào đã đăng ký)
        if (!userRepository.existsByEmail(email))
            return;

        User user = userRepository.findByEmail(email).get();

        String token = tokenService.generateResetPasswordToken(email);
        emailService.sendResetPasswordEmail(email, token, user.getEmail());
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // 1. Xác thực token → lấy email (token tự động bị xóa)
        String email = tokenService.verifyResetPasswordToken(token);

        // 2. Tìm user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User không tồn tại"));

        // 3. Cập nhật password mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resendVerifyEmail(String email) {
        // 1. Kiểm tra user tồn tại
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("Email không tồn tại"));

        // 2. Đã verify rồi thì không cần gửi lại
        if (user.getEmailVerifiedAt() != null) {
            throw AppException.badRequest("Email này đã được xác thực rồi");
        }

        // 3. Tạo token mới → gửi lại email
        String token = tokenService.resendVerifyEmailToken(email);
        emailService.sendVerifyEmail(email, token);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ReqChangePasswordDTO request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw AppException.badRequest("Mật khẩu xác nhận không khớp");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Người dùng không tồn tại"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw AppException.badRequest("Mật khẩu hiện tại không đúng");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw AppException.badRequest("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String getAdminRoleByUserId(Long userId) {
        return adminUserRepository.findActiveByUserId(userId)
                .map(admin -> admin.getAdminRole())
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Tạo CompanyMember với role OWNER sau khi đăng ký công ty.
     * Lấy quyền mặc định từ RoleDefault, status = pending (chờ xác thực email).
     */
    private void createOwnerMember(Long companyId, Long userId) {
        RoleDefault ownerDefault = roleDefaultRepository.findByRoleName(MemberRole.OWNER)
                .orElse(null); // Nếu chưa có RoleDefault thì bỏ qua, không crash ứng dụng

        CompanyMember ownerMember = CompanyMember.builder()
                .companyId(companyId)
                .userId(userId)
                .roleDefault(ownerDefault)
                .status("pending") // Chờ user xác thực email → sẽ chuyển active
                .createdBy(userId)
                .build();

        companyMemberRepository.save(ownerMember);
    }
}