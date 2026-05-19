package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqAddMemberDTO;
import com.topviec.topviec_be.dto.request.ReqUpdatePermissionDTO;
import com.topviec.topviec_be.dto.response.ResActionSummaryDTO;
import com.topviec.topviec_be.dto.response.ResCompanyMemberDTO;
import com.topviec.topviec_be.dto.response.ResEmployerMemberStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResEmployerProfileDTO;
import com.topviec.topviec_be.dto.response.ResMemberPermissionDetailDTO;
import com.topviec.topviec_be.dto.response.ResPermissionChangeLogDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.ActionItem;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.entity.PermissionChangeLog;
import com.topviec.topviec_be.entity.RoleDefault;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.companyMember.PermissionChangeType;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.PermissionChangeLogRepository;
import com.topviec.topviec_be.repository.RoleDefaultRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.CompanyMemberService;
import com.topviec.topviec_be.service.EmailService;
import com.topviec.topviec_be.service.TokenService;
import com.topviec.topviec_be.util.ChangeTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyMemberServiceImpl implements CompanyMemberService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final RoleDefaultRepository roleDefaultRepository;
    private final PermissionChangeLogRepository permissionChangeLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    // -------------------------------------------------------------------------
    // Thêm thành viên — TH1: Email chưa có tài khoản
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResCompanyMemberDTO addMember(Long inviterUserId, Long companyId, ReqAddMemberDTO req) {
        // 1. Kiểm tra công ty tồn tại
        if (!companyRepository.existsById(companyId)) {
            throw AppException.notFound("Công ty không tồn tại");
        }

        String email = req.getEmail().trim().toLowerCase();

        // 2. Kiểm tra email đã tồn tại (TH1: chưa có TK)
        if (userRepository.existsByEmail(email)) {
            throw AppException.conflict(
                    "Email này đã có tài khoản trong hệ thống. Vui lòng chọn chức năng thêm thành viên phù hợp.");
        }

        // 3. Lấy quyền mặc định của role từ RoleDefault
        RoleDefault roleDefault = roleDefaultRepository.findById(req.getRoleId())
                .orElseThrow(
                        () -> AppException.notFound("Không tìm thấy cấu hình quyền cho role Id: " + req.getRoleId()));

        // 4. Tạo User mới (status = pending)
        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(req.getTempPassword()))
                .userType(UserType.EMPLOYER)
                .status(UserStatus.PENDING)
                .twoFactorEnabled(false)
                .createdBy(inviterUserId)
                .build();

        User savedUser = userRepository.save(newUser);

        // 5. Tạo CompanyMember (status = pending)
        CompanyMember member = CompanyMember.builder()
                .companyId(companyId)
                .userId(savedUser.getId())
                .roleDefault(roleDefault)
                .status("pending")
                .permissions(buildPermissionsMap(req.getCustomActions()))
                .createdBy(inviterUserId)
                .build();

        CompanyMember savedMember = companyMemberRepository.save(member);

        // 6. Gửi email mời kích hoạt kèm mật khẩu tạm
        String verifyToken = tokenService.generateVerifyEmailToken(email);
        emailService.sendMemberInviteNewUser(email, req.getTempPassword(), verifyToken);

        log.info(">>>>>>>>: Đã thêm thành viên mới [{}] vào công ty [{}] với role Id [{}]", email, companyId,
                req.getRoleId());

        return toResponse(savedMember, email);
    }

    // -------------------------------------------------------------------------
    // Kích hoạt CompanyMember pending sau khi user xác thực email
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void activatePendingMembers(Long userId) {
        List<CompanyMember> pendingMembers = companyMemberRepository.findPendingMembersByUserId(userId);

        if (pendingMembers.isEmpty()) {
            return;
        }

        for (CompanyMember member : pendingMembers) {
            member.setStatus("active");
        }

        companyMemberRepository.saveAll(pendingMembers);
        log.info(">>>>>>>>: Đã kích hoạt {} CompanyMember pending của userId [{}]", pendingMembers.size(), userId);
    }

    // -------------------------------------------------------------------------
    // Phân quyền (CN-NTT-019)
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long companyId, Long userId, String action) {
        CompanyMember member = companyMemberRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElse(null);

        if (member == null || !"active".equals(member.getStatus())) {
            return false;
        }

        // 1. Kiểm tra custom overrides TRƯỚC (ưu tiên cao nhất)
        Map<String, List<String>> permissions = member.getPermissions();
        if (permissions != null) {
            List<String> grant = permissions.get("grant");
            if (grant != null && grant.contains(action)) {
                return true; // custom grant → cho phép, không cần check default
            }

            List<String> revoke = permissions.get("revoke");
            if (revoke != null && revoke.contains(action)) {
                return false; // custom revoke → từ chối, không cần check default
            }
        }

        // 2. Fallback về default role permissions
        RoleDefault roleDefault = member.getRoleDefault();
        if (roleDefault == null || roleDefault.getActions() == null)
            return false;
        return roleDefault.getActions().stream()
                .anyMatch(a -> a.getCode().equals(action) && a.isEnabled());
    }

    @Override
    @Transactional
    public ResCompanyMemberDTO updateMemberPermission(Long inviterUserId, Long companyId, Long targetUserId,
            ReqUpdatePermissionDTO req) {
        // 1. Kiểm tra inviter có quyền thao tác phân quyền không
        if (!hasPermission(companyId, inviterUserId, "member:permission")) {
            throw AppException.forbidden("Bạn không có quyền thay đổi phân quyền thành viên");
        }

        // 2. Lấy thông tin member cần sửa
        CompanyMember targetMember = companyMemberRepository.findByCompanyIdAndUserId(companyId, targetUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy thành viên này trong công ty"));

        MemberRole oldRole = targetMember.getMemberRole();
        Map<String, List<String>> oldPermissions = targetMember.getPermissions();

        // 3. Quy tắc: Không thể dùng API này để tước quyền owner hiện tại (phải qua API
        // chuyển nhượng)
        if (oldRole == MemberRole.OWNER && targetUserId.equals(inviterUserId)) {
            // Owner không tự sửa quyền bản thân được
            throw AppException
                    .badRequest("Owner không thể tự xóa quyền của mình. Phải chuyển Owner cho cộng sự trước.");
        }

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(targetMember);

        // 4. Update role & custom permissions
        Long newRoleId = req.getRoleId();
        RoleDefault newRoleDefault = roleDefaultRepository.findById(newRoleId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy role Id: " + newRoleId));
        MemberRole newRole = newRoleDefault.getRoleName();
        targetMember.setRoleDefault(newRoleDefault);

        Map<String, List<String>> newPermissions = null;
        if (req.getCustomActions() != null) {
            newPermissions = buildPermissionsMap(req.getCustomActions());
            targetMember.setPermissions(newPermissions);
        } else {
            // Nếu null -> reset custom permissions
            targetMember.setPermissions(
                    Map.of("grant", new java.util.ArrayList<>(), "revoke", new java.util.ArrayList<>()));
        }

        CompanyMember savedMember = companyMemberRepository.save(targetMember);

        // CDC: So sánh + ghi vào log context
        tracker.compare(savedMember).apply();

        // 5. Ghi Log
        PermissionChangeType changeType = (oldRole != newRole) ? PermissionChangeType.ROLE_CHANGE
                : PermissionChangeType.PERMISSION_UPDATE;

        PermissionChangeLog logEntry = PermissionChangeLog.builder()
                .companyId(companyId)
                .targetUserId(targetUserId)
                .changedBy(inviterUserId)
                .changeType(changeType)
                .oldRole(oldRole)
                .newRole(newRole)
                .oldPermissions(oldPermissions)
                .newPermissions(savedMember.getPermissions())
                .reason(req.getReason())
                .build();
        permissionChangeLogRepository.save(logEntry);

        // 6. Send email notify target user
        String targetEmail = userRepository.findById(targetUserId)
                .map(User::getEmail)
                .orElse(null);
        if (targetEmail != null) {
            // String companyName = companyRepository.findById(companyId)
            // .map(com.topviec.topviec_be.entity.Company::getName)
            // .orElse("Công ty");
            // emailService.sendPermissionChangedEmail(targetEmail, companyName,
            // newRole.name());
        }

        return toResponse(savedMember, targetEmail);
    }

    @Override
    public void checkPermission(Long companyId, Long userId, String action) {
        if (!hasPermission(companyId, userId, action)) {
            throw AppException.forbidden("Bạn không có quyền thực hiện hành động này: " + action);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getMembers(Long companyId, String status, String role, String keyword,
            Pageable pageable) {

        MemberRole roleEnum = null;
        if (role != null && !role.trim().isEmpty()) {
            roleEnum = MemberRole.fromValue(role);
        }

        Page<CompanyMember> pageMember = companyMemberRepository.findByFilters(companyId, status, roleEnum, keyword,
                pageable);

        List<CompanyMember> list = pageMember.getContent();

        // 1. Lấy danh sách user IDs
        List<Long> userIds = list.stream().map(CompanyMember::getUserId).toList();

        // 2. Fetch Users để lấy email
        Map<Long, String> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));

        // 3. Map to DTO
        List<ResCompanyMemberDTO> res = list.stream()
                .map(m -> toResponse(m, userMap.get(m.getUserId())))
                .toList();

        // 4. Build Result
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageMember.getNumber() + 1); // FE thường dùng page 1-based
        meta.setPageSize(pageMember.getSize());
        meta.setPages(pageMember.getTotalPages());
        meta.setTotals(pageMember.getTotalElements());

        result.setMeta(meta);
        result.setResult(res);

        return result;
    }

    @Override
    @Transactional
    public void removeMember(Long inviterUserId, Long companyId, Long targetUserId) {
        CompanyMember member = companyMemberRepository.findByCompanyIdAndUserId(companyId, targetUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy thành viên này trong công ty"));

        // Quy tắc: Owner không thể tự xóa chính mình bằng API này
        if (targetUserId.equals(inviterUserId) && MemberRole.OWNER == member.getMemberRole()) {
            throw AppException.badRequest("Owner không thể tự xóa khỏi công ty. Hãy chuyển quyền Owner trước.");
        }

        member.setDeletedAt(java.time.LocalDateTime.now());
        member.setStatus("deactivated");
        member.setUpdatedBy(inviterUserId);

        companyMemberRepository.save(member);
        log.info(">>>>>>>>: Đã xóa thành viên userId [{}] khỏi công ty [{}] bởi userId [{}]", targetUserId, companyId,
                inviterUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResEmployerProfileDTO getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));

        CompanyMember member = companyMemberRepository
                .findFirstByUserIdAndStatusAndDeletedAtIsNull(userId, "active")
                .orElseThrow(() -> AppException.notFound("Không tìm thấy thông tin thành viên công ty"));

        Company company = companyRepository.findById(member.getCompanyId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty"));

        RoleDefault roleDefault = member.getRoleDefault();

        return ResEmployerProfileDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accountStatus(user.getStatus())
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .lastLoginAt(user.getLastLoginAt())
                .memberId(member.getId())
                .roleName(roleDefault != null ? roleDefault.getRoleName() : null)
                .memberStatus(member.getStatus())
                .memberCreatedAt(member.getCreatedAt())
                .companyId(company.getId())
                .companyName(company.getName())
                .companySlug(company.getSlug())
                .companyLogoUrl(company.getLogoUrl())
                .build();
    }

    // -------------------------------------------------------------------------
    // Batch permission detail (max 5 users)
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ResMemberPermissionDetailDTO> getBatchMemberPermissions(Long companyId, List<Long> userIds) {
        List<CompanyMember> members = companyMemberRepository.findByCompanyIdAndUserIds(companyId, userIds);

        Map<Long, String> emailMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));

        return members.stream()
                .map(m -> toPermissionDetailResponse(m, emailMap.get(m.getUserId())))
                .toList();
    }

    // -------------------------------------------------------------------------
    // Toggle single action permission (chỉ Owner / Manager)
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResMemberPermissionDetailDTO toggleMemberActionPermission(Long inviterUserId, Long companyId,
            Long targetUserId, String actionCode, boolean enabled) {

        // 1. Xác minh inviter là OWNER hoặc MANAGER
        CompanyMember inviterMember = companyMemberRepository.findByCompanyIdAndUserId(companyId, inviterUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy thông tin thành viên của bạn trong công ty"));

        MemberRole inviterRole = inviterMember.getMemberRole();
        if (inviterRole != MemberRole.OWNER && inviterRole != MemberRole.MANAGER) {
            throw AppException.forbidden("Chỉ Owner và Manager mới có thể thay đổi quyền thành viên");
        }

        // 2. Lấy member cần thay đổi
        CompanyMember targetMember = companyMemberRepository.findByCompanyIdAndUserId(companyId, targetUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy thành viên này trong công ty"));

        MemberRole targetRole = targetMember.getMemberRole();

        // 3. Không thể thay đổi quyền của OWNER
        if (targetRole == MemberRole.OWNER) {
            throw AppException.badRequest("Không thể thay đổi quyền của Owner");
        }

        // 4. MANAGER không thể thay đổi quyền của MANAGER khác
        if (inviterRole == MemberRole.MANAGER && targetRole == MemberRole.MANAGER) {
            throw AppException.forbidden("Manager không thể thay đổi quyền của Manager khác");
        }

        // 5. Cập nhật grant/revoke cho actionCode
        Map<String, List<String>> oldPermissions = targetMember.getPermissions();

        List<String> grantList = new ArrayList<>(
                oldPermissions != null && oldPermissions.get("grant") != null ? oldPermissions.get("grant")
                        : List.of());
        List<String> revokeList = new ArrayList<>(
                oldPermissions != null && oldPermissions.get("revoke") != null ? oldPermissions.get("revoke")
                        : List.of());

        grantList.remove(actionCode);
        revokeList.remove(actionCode);
        if (enabled) {
            grantList.add(actionCode);
        } else {
            revokeList.add(actionCode);
        }

        Map<String, List<String>> newPermissions = new java.util.HashMap<>();
        newPermissions.put("grant", grantList);
        newPermissions.put("revoke", revokeList);

        targetMember.setPermissions(newPermissions);
        targetMember.setUpdatedBy(inviterUserId);
        CompanyMember savedMember = companyMemberRepository.save(targetMember);

        // 6. Ghi log
        PermissionChangeLog logEntry = PermissionChangeLog.builder()
                .companyId(companyId)
                .targetUserId(targetUserId)
                .changedBy(inviterUserId)
                .changeType(PermissionChangeType.PERMISSION_UPDATE)
                .oldRole(targetRole)
                .newRole(targetRole)
                .oldPermissions(oldPermissions)
                .newPermissions(newPermissions)
                .reason("Toggle action [" + actionCode + "] -> " + (enabled ? "grant" : "revoke"))
                .build();
        permissionChangeLogRepository.save(logEntry);

        String targetEmail = userRepository.findById(targetUserId).map(User::getEmail).orElse(null);
        return toPermissionDetailResponse(savedMember, targetEmail);
    }

    // -------------------------------------------------------------------------
    // Lịch sử thay đổi quyền
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ResPermissionChangeLogDTO> getMemberPermissionHistory(Long companyId, Long targetUserId) {
        List<PermissionChangeLog> logs = permissionChangeLogRepository
                .findByCompanyIdAndTargetUserIdOrderByCreatedAtDesc(companyId, targetUserId);

        List<Long> userIds = logs.stream()
                .flatMap(l -> java.util.stream.Stream.of(l.getTargetUserId(), l.getChangedBy()))
                .distinct().toList();
        Map<Long, String> emailMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));
        Map<String, String> codeNameMap = buildActionCodeNameMap();

        return logs.stream().map(l -> toLogResponse(l, emailMap, codeNameMap)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getCompanyPermissionHistory(Long companyId, LocalDate fromDate, LocalDate toDate,
            Pageable pageable) {
        LocalDateTime from = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime to = toDate != null ? toDate.atTime(LocalTime.MAX) : null;

        Page<PermissionChangeLog> page = permissionChangeLogRepository
                .findByCompanyIdAndDateRange(companyId, from, to, pageable);

        List<Long> userIds = page.getContent().stream()
                .flatMap(l -> java.util.stream.Stream.of(l.getTargetUserId(), l.getChangedBy()))
                .distinct().toList();
        Map<Long, String> emailMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));
        Map<String, String> codeNameMap = buildActionCodeNameMap();

        List<ResPermissionChangeLogDTO> items = page.getContent().stream()
                .map(l -> toLogResponse(l, emailMap, codeNameMap)).toList();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(items);
        return result;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Chuyển đổi customActions (Map<String, Boolean>) từ API thành Map<String,
     * List<String>>
     * phục vụ lưu trữ chuẩn theo thiết kế Entity: { "grant": [...], "revoke": [...]
     * }
     */
    private Map<String, List<String>> buildPermissionsMap(Map<String, Boolean> customActions) {
        List<String> grantList = new java.util.ArrayList<>();
        List<String> revokeList = new java.util.ArrayList<>();

        if (customActions != null) {
            customActions.forEach((action, isGranted) -> {
                if (Boolean.TRUE.equals(isGranted)) {
                    grantList.add(action);
                } else {
                    revokeList.add(action);
                }
            });
        }

        return Map.of(
                "grant", grantList,
                "revoke", revokeList);
    }

    private ResMemberPermissionDetailDTO toPermissionDetailResponse(CompanyMember m, String email) {
        RoleDefault roleDefault = m.getRoleDefault();
        Map<String, List<String>> permissions = m.getPermissions();

        List<String> grantList = permissions != null && permissions.get("grant") != null
                ? permissions.get("grant")
                : List.of();
        List<String> revokeList = permissions != null && permissions.get("revoke") != null
                ? permissions.get("revoke")
                : List.of();

        // Quyền custom ghi đè (chỉ những action được tuỳ chỉnh riêng)
        Map<String, Boolean> customPermissions = new HashMap<>();
        grantList.forEach(a -> customPermissions.put(a, true));
        revokeList.forEach(a -> customPermissions.put(a, false));

        // Effective permissions: role default + áp dụng grant/revoke override
        List<ActionItem> effectivePermissions = List.of();
        if (roleDefault != null && roleDefault.getActions() != null) {
            effectivePermissions = roleDefault.getActions().stream()
                    .map(a -> {
                        boolean effective;
                        if (grantList.contains(a.getCode())) {
                            effective = true;
                        } else if (revokeList.contains(a.getCode())) {
                            effective = false;
                        } else {
                            effective = a.isEnabled();
                        }
                        return new ActionItem(a.getName(), a.getCode(), effective);
                    })
                    .toList();
        }

        return ResMemberPermissionDetailDTO.builder()
                .userId(m.getUserId())
                .email(email)
                .roleId(roleDefault != null ? roleDefault.getId() : null)
                .roleName(roleDefault != null ? roleDefault.getRoleName() : null)
                .status(m.getStatus())
                .jobTitle(m.getJobTitle())
                .createdAt(m.getCreatedAt())
                .customPermissions(customPermissions)
                .effectivePermissions(effectivePermissions)
                .build();
    }

    private ResCompanyMemberDTO toResponse(CompanyMember m, String email) {
        // Build actions map từ custom permissions của member
        Map<String, Boolean> actionsMap = new HashMap<>();
        Map<String, List<String>> permissions = m.getPermissions();
        if (permissions != null) {
            List<String> grant = permissions.get("grant");
            if (grant != null)
                grant.forEach(a -> actionsMap.put(a, true));

            List<String> revoke = permissions.get("revoke");
            if (revoke != null)
                revoke.forEach(a -> actionsMap.put(a, false));
        }

        // Lấy thông tin từ RoleDefault relationship
        RoleDefault roleDefault = m.getRoleDefault();
        Long roleId = roleDefault != null ? roleDefault.getId() : null;
        MemberRole roleName = roleDefault != null ? roleDefault.getRoleName() : null;

        return ResCompanyMemberDTO.builder()
                .id(m.getId())
                .companyId(m.getCompanyId())
                .userId(m.getUserId())
                .email(email)
                .roleId(roleId)
                .roleName(roleName)
                .status(m.getStatus())
                .actions(actionsMap)
                .jobTitle(m.getJobTitle())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private Map<String, String> buildActionCodeNameMap() {
        return roleDefaultRepository.findAll().stream()
                .filter(r -> r.getActions() != null)
                .flatMap(r -> r.getActions().stream())
                .collect(Collectors.toMap(
                        ActionItem::getCode,
                        ActionItem::getName,
                        (existing, duplicate) -> existing));
    }

    private Map<String, List<ResActionSummaryDTO>> enrichPermissions(
            Map<String, List<String>> raw, Map<String, String> codeNameMap) {
        if (raw == null)
            return Map.of("grant", List.of(), "revoke", List.of());
        Map<String, List<ResActionSummaryDTO>> result = new HashMap<>();
        raw.forEach((key, codes) -> {
            List<ResActionSummaryDTO> enriched = codes == null ? List.of()
                    : codes.stream()
                            .map(code -> ResActionSummaryDTO.builder()
                                    .code(code)
                                    .name(codeNameMap.getOrDefault(code, code))
                                    .build())
                            .toList();
            result.put(key, enriched);
        });
        return result;
    }

    private ResPermissionChangeLogDTO toLogResponse(PermissionChangeLog l, Map<Long, String> emailMap,
            Map<String, String> codeNameMap) {
        return ResPermissionChangeLogDTO.builder()
                .id(l.getId())
                .targetUserId(l.getTargetUserId())
                .targetEmail(emailMap.get(l.getTargetUserId()))
                .changedBy(l.getChangedBy())
                .changedByEmail(emailMap.get(l.getChangedBy()))
                .changeType(l.getChangeType())
                .oldRole(l.getOldRole())
                .newRole(l.getNewRole())
                .oldPermissions(enrichPermissions(l.getOldPermissions(), codeNameMap))
                .newPermissions(enrichPermissions(l.getNewPermissions(), codeNameMap))
                .reason(l.getReason())
                .createdAt(l.getCreatedAt())
                .build();
    }

    // -------------------------------------------------------------------------
    // Thống kê thành viên
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResEmployerMemberStatisticsDTO getMemberStatistics(Long companyId) {
        long totalMembers = companyMemberRepository.countByCompanyIdAndDeletedAtIsNull(companyId);
        long activeMembers = companyMemberRepository.countByCompanyIdAndStatusAndDeletedAtIsNull(companyId, "active");
        long pendingMembers = companyMemberRepository.countByCompanyIdAndStatusAndDeletedAtIsNull(companyId, "pending");
        long lockedMembers = companyMemberRepository.countByCompanyIdAndStatusAndDeletedAtIsNull(companyId,
                "deactivated");

        return ResEmployerMemberStatisticsDTO.builder()
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .pendingMembers(pendingMembers)
                .lockedMembers(lockedMembers)
                .build();
    }
}
