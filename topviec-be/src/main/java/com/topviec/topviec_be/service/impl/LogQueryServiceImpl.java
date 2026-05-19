package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.response.*;
import com.topviec.topviec_be.entity.*;
import com.topviec.topviec_be.enums.logging.LogCategory;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.*;
import com.topviec.topviec_be.service.LogQueryService;
import com.topviec.topviec_be.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogQueryServiceImpl implements LogQueryService {

    private final AuditLogRepository auditLogRepository;
    private final BusinessEventLogRepository businessEventLogRepository;
    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final CompanyMemberRepository companyMemberRepository;

    // Repositories for resolving targetName
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final CvsRepository cvsRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final OrderRepository orderRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final ServiceRepository serviceRepository;
    private final AddonServiceRepository addonServiceRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final EmployerViolationScoreRepository employerViolationScoreRepository;
    private final InterviewRoundRepository interviewRoundRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final TalentPoolRepository talentPoolRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintAppealRepository complaintAppealRepository;
    private final CompanySubscriptionRepository companySubscriptionRepository;

    // ═══════════════════════════════════════════════
    // AUDIT LOG
    // ═══════════════════════════════════════════════

    @Override
    public ResultPaginationDTO getAuditLogs(
            List<Long> userIds,
            String action, String category, String severity, String status,
            String keyword, String userRole,
            LocalDate startDate, LocalDate endDate,
            Long companyId,
            Pageable pageable) {

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        // Áp dụng filter theo userRole nếu có
        List<Long> finalUserIds = applyRoleFilter(userIds, userRole);

        // Nếu lọc role mà không tìm thấy user nào → trả về rỗng
        if (finalUserIds != null && finalUserIds.isEmpty()) {
            return buildEmptyPagination(pageable);
        }

        String trimmedKeyword = trimToNull(keyword);

        Page<AuditLog> page = auditLogRepository.findByFilters(
                finalUserIds, action, category, severity, status,
                trimmedKeyword, start, end, pageable);

        // Batch load user info cho tất cả userId trong trang
        List<Long> distinctUserIds = page.getContent().stream()
                .map(AuditLog::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> emailMap = loadEmailMap(distinctUserIds);
        Map<Long, String> roleMap = loadRoleMap(distinctUserIds, companyId);

        List<ResAuditLogDTO> dtos = page.getContent().stream()
                .map(log -> toAuditLogDTO(log, emailMap, roleMap))
                .toList();

        return buildPagination(page, dtos);
    }

    @Override
    public ResAuditLogDetailDTO getAuditLogDetail(Long id) {
        return getAuditLogDetail(id, null);
    }

    @Override
    public ResAuditLogDetailDTO getAuditLogDetail(Long id, Long companyId) {
        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Audit log không tồn tại: " + id));

        String email = null;
        String role = null;
        if (log.getUserId() != null) {
            User user = userRepository.findById(log.getUserId()).orElse(null);
            email = user != null ? user.getEmail() : null;
            role = resolveRole(log.getUserId(), user, companyId);
        }

        ResAuditLogDetailDTO detail = ResAuditLogDetailDTO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userEmail(email)
                .userRole(role)
                .action(log.getAction())
                .category(log.getCategory())
                .severity(log.getSeverity())
                .targetEntity(log.getTargetEntity())
                .targetId(log.getTargetId())
                .targetName(resolveTargetName(log.getTargetEntity(), log.getTargetId()))
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .status(log.getStatus())
                .durationMs(log.getDurationMs())
                .errorMessage(log.getErrorMessage())
                .createdAt(log.getCreatedAt())
                .build();

        return detail;
    }

    // ═══════════════════════════════════════════════
    // BUSINESS EVENT LOG
    // ═══════════════════════════════════════════════

    @Override
    public ResultPaginationDTO getBusinessEventLogs(
            List<Long> userIds,
            String action, String category, String status,
            String keyword, String userRole,
            LocalDate startDate, LocalDate endDate,
            Long companyId,
            Pageable pageable) {

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        List<Long> finalUserIds = applyRoleFilter(userIds, userRole);

        if (finalUserIds != null && finalUserIds.isEmpty()) {
            return buildEmptyPagination(pageable);
        }

        String trimmedKeyword = trimToNull(keyword);

        Page<BusinessEventLog> page = businessEventLogRepository.findByFilters(
                finalUserIds, action, category, status,
                trimmedKeyword, start, end, pageable);

        List<Long> distinctUserIds = page.getContent().stream()
                .map(BusinessEventLog::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> emailMap = loadEmailMap(distinctUserIds);
        Map<Long, String> roleMap = loadRoleMap(distinctUserIds, companyId);

        List<ResBusinessEventLogDTO> dtos = page.getContent().stream()
                .map(log -> toBusinessEventLogDTO(log, emailMap, roleMap))
                .toList();

        return buildPagination(page, dtos);
    }

    @Override
    public ResBusinessEventLogDetailDTO getBusinessEventLogDetail(Long id) {
        return getBusinessEventLogDetail(id, null);
    }

    @Override
    public ResBusinessEventLogDetailDTO getBusinessEventLogDetail(Long id, Long companyId) {
        BusinessEventLog log = businessEventLogRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Business event log không tồn tại: " + id));

        String email = null;
        String role = null;
        if (log.getUserId() != null) {
            User user = userRepository.findById(log.getUserId()).orElse(null);
            email = user != null ? user.getEmail() : null;
            role = resolveRole(log.getUserId(), user, companyId);
        }

        boolean hasChanges = false;
        if (log.getMetadata() != null && log.getMetadata().containsKey("changedFields")) {
            Object changedFieldsObj = log.getMetadata().get("changedFields");
            if (changedFieldsObj instanceof List) {
                hasChanges = !((List<?>) changedFieldsObj).isEmpty();
            }
        }

        return ResBusinessEventLogDetailDTO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userEmail(email)
                .userRole(role)
                .action(log.getAction())
                .category(log.getCategory())
                .targetEntity(log.getTargetEntity())
                .targetId(log.getTargetId())
                .targetName(resolveTargetName(log.getTargetEntity(), log.getTargetId()))
                .hasChanges(hasChanges)
                .metadata(log.getMetadata())
                .status(log.getStatus())
                .durationMs(log.getDurationMs())
                .createdAt(log.getCreatedAt())
                .build();
    }

    // ═══════════════════════════════════════════════
    // STATISTICS — Admin Dashboard
    // ═══════════════════════════════════════════════

    @Override
    public ResAdminLogStatisticsDTO getAdminLogStatistics() {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 1. Lấy danh sách tất cả Admin đang active để tính activeAdmins
        List<Long> allActiveAdminUserIds = adminUserRepository.findAllActiveAdminUserIds();
        long activeAdmins = allActiveAdminUserIds.size();

        if (allActiveAdminUserIds.isEmpty()) {
            return ResAdminLogStatisticsDTO.builder()
                    .totalLogs(0L)
                    .criticalLogs(0L)
                    .systemErrors(0L)
                    .activeAdmins(0L)
                    .build();
        }

        // 2. Xác định phạm vi người dùng để thống kê (targetUserIds)
        // Nếu là super_admin thì lấy hết, nếu không chỉ lấy chính mình
        List<Long> targetUserIds;
        AdminUser currentAdmin = adminUserRepository.findActiveByUserId(currentUserId).orElse(null);

        if (currentAdmin != null && "super_admin".equals(currentAdmin.getAdminRole())) {
            targetUserIds = allActiveAdminUserIds;
        } else {
            targetUserIds = List.of(currentUserId);
        }

        // 3. Tổng log all-time (audit + business) trong phạm vi targetUserIds
        long totalAudit = auditLogRepository.countByUserIdIn(targetUserIds);
        long totalBusiness = businessEventLogRepository.countByUserIdIn(targetUserIds);
        long totalLogs = totalAudit + totalBusiness;

        // 4. Xác định khoảng thời gian hôm nay (cho các metrics động)
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();

        // 5. Log có mức độ nghiêm trọng (severity = HIGH hoặc CRITICAL)
        List<String> criticalSeverities = List.of("HIGH", "CRITICAL");
        long criticalLogs = auditLogRepository
                .countByUserIdsAndSeveritiesAndDateRange(targetUserIds, criticalSeverities, todayStart, todayEnd);

        // 6. Lỗi hệ thống (status = FAILURE)
        long auditErrors = auditLogRepository
                .countByUserIdsAndStatusAndDateRange(targetUserIds, "FAILURE", todayStart, todayEnd);
        long businessErrors = businessEventLogRepository
                .countByUserIdsAndStatusAndDateRange(targetUserIds, "FAILURE", todayStart, todayEnd);
        long systemErrors = auditErrors + businessErrors;

        return ResAdminLogStatisticsDTO.builder()
                .totalLogs(totalLogs)
                .criticalLogs(criticalLogs)
                .systemErrors(systemErrors)
                .activeAdmins(activeAdmins)
                .build();
    }

    // ═══════════════════════════════════════════════
    // STATISTICS — Employer Dashboard
    // ═══════════════════════════════════════════════

    @Override
    public ResEmployerLogStatisticsDTO getEmployerLogStatistics() {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 1. Lấy thông tin công ty và role của user hiện tại
        CompanyMember currentMember = companyMemberRepository
                .findFirstByUserIdAndStatusAndDeletedAtIsNull(currentUserId, "active")
                .orElseThrow(() -> AppException.forbidden("Bạn không thuộc công ty nào hoặc tài khoản chưa kích hoạt"));

        Long companyId = currentMember.getCompanyId();
        MemberRole role = currentMember.getMemberRole();

        // 2. Lấy danh sách tất cả thành viên active trong công ty để tính activeMembers
        List<Long> allMemberUserIds = companyMemberRepository.findAllActiveByCompanyId(companyId)
                .stream()
                .map(CompanyMember::getUserId)
                .toList();

        long activeMembers = allMemberUserIds.size();

        if (allMemberUserIds.isEmpty()) {
            return ResEmployerLogStatisticsDTO.builder()
                    .totalActivity(0L)
                    .candidateProcessing(0L)
                    .dataUpdates(0L)
                    .activeMembers(0L)
                    .build();
        }

        // 3. Xác định phạm vi người dùng để thống kê hoạt động (targetUserIds)
        // Nếu là owner hoặc manager thì lấy hết công ty, nếu không chỉ lấy chính mình
        List<Long> targetUserIds;
        if (role == MemberRole.OWNER || role == MemberRole.MANAGER) {
            targetUserIds = allMemberUserIds;
        } else {
            targetUserIds = List.of(currentUserId);
        }

        // 4. Tổng hoạt động (audit + business) trong phạm vi targetUserIds
        long totalAudit = auditLogRepository.countByUserIdIn(targetUserIds);
        long totalBusiness = businessEventLogRepository.countByUserIdIn(targetUserIds);
        long totalActivity = totalAudit + totalBusiness;

        // 5. Xử lý ứng viên (Application, Interview, Talent Pool, CV Management)
        List<String> candidateCategories = List.of(
                LogCategory.APPLICATION.name(),
                LogCategory.APPLICATION_REVIEW.name(),
                LogCategory.INTERVIEW.name(),
                LogCategory.TALENT_POOL.name(),
                LogCategory.CV_MANAGEMENT.name());
        long candidateAudit = auditLogRepository.countByUserIdsAndCategories(targetUserIds, candidateCategories);
        long candidateBusiness = businessEventLogRepository.countByUserIdsAndCategories(targetUserIds,
                candidateCategories);
        long candidateProcessing = candidateAudit + candidateBusiness;

        // 6. Cập nhật dữ liệu (Job Management, Company Management, Member Management)
        List<String> dataCategories = List.of(
                LogCategory.JOB_MANAGEMENT.name(),
                LogCategory.COMPANY_MANAGEMENT.name(),
                LogCategory.MEMBER_MANAGEMENT.name());
        long dataAudit = auditLogRepository.countByUserIdsAndCategories(targetUserIds, dataCategories);
        long dataBusiness = businessEventLogRepository.countByUserIdsAndCategories(targetUserIds, dataCategories);
        long dataUpdates = dataAudit + dataBusiness;

        return ResEmployerLogStatisticsDTO.builder()
                .totalActivity(totalActivity)
                .candidateProcessing(candidateProcessing)
                .dataUpdates(dataUpdates)
                .activeMembers(activeMembers)
                .build();
    }

    // ═══════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════

    /** Batch load email bằng findAllById — tránh N+1 */
    private Map<Long, String> loadEmailMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty())
            return Map.of();

        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail, (a, b) -> a));
    }

    /**
     * Batch load vai trò người dùng — tránh N+1.
     *
     * Nếu companyId != null (context Employer):
     * → lấy vai trò từ CompanyMember (owner, manager, recruiter, viewer)
     *
     * Nếu companyId == null (context Admin):
     * → ADMIN → lấy adminRole từ AdminUser (super_admin, content_moderator, ...)
     * → EMPLOYER / CANDIDATE → dùng userType làm role
     */
    private Map<Long, String> loadRoleMap(List<Long> userIds, Long companyId) {
        if (userIds == null || userIds.isEmpty())
            return Map.of();

        // ── Employer context: lấy vai trò trong công ty ──
        if (companyId != null) {
            return loadMemberRoleMap(userIds, companyId);
        }

        // ── Admin context: lấy vai trò hệ thống ──
        return loadSystemRoleMap(userIds);
    }

    /**
     * Batch load member role từ CompanyMember — dùng cho Employer context.
     */
    private Map<Long, String> loadMemberRoleMap(List<Long> userIds, Long companyId) {
        List<CompanyMember> members = companyMemberRepository
                .findByCompanyIdAndUserIds(companyId, userIds);

        Map<Long, String> roleMap = new HashMap<>();
        for (CompanyMember m : members) {
            String roleName = m.getMemberRole() != null
                    ? m.getMemberRole().getValue()
                    : "member";
            roleMap.put(m.getUserId(), roleName);
        }
        return roleMap;
    }

    /**
     * Batch load system role — dùng cho Admin context.
     */
    private Map<Long, String> loadSystemRoleMap(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        Map<Long, String> roleMap = new HashMap<>();

        List<Long> adminUserIds = users.stream()
                .filter(u -> u.getUserType() != null
                        && "ADMIN".equalsIgnoreCase(u.getUserType().name()))
                .map(User::getId)
                .toList();

        Map<Long, String> adminRoleMap = new HashMap<>();
        if (!adminUserIds.isEmpty()) {
            adminUserRepository.findByUserIdIn(adminUserIds)
                    .forEach(a -> adminRoleMap.put(a.getUser().getId(), a.getAdminRole()));
        }

        for (User user : users) {
            if (user.getUserType() == null) {
                roleMap.put(user.getId(), null);
            } else if ("ADMIN".equalsIgnoreCase(user.getUserType().name())) {
                roleMap.put(user.getId(), adminRoleMap.getOrDefault(user.getId(), "admin"));
            } else {
                roleMap.put(user.getId(), user.getUserType().getValue());
            }
        }

        return roleMap;
    }

    /**
     * Resolve role cho 1 user đơn lẻ (dùng cho API xem chi tiết).
     */
    private String resolveRole(Long userId, User user, Long companyId) {
        // Employer context: lấy vai trò trong công ty
        if (companyId != null) {
            return companyMemberRepository.findByCompanyIdAndUserId(companyId, userId)
                    .map(m -> m.getMemberRole() != null ? m.getMemberRole().getValue() : "member")
                    .orElse(null);
        }

        // Admin context
        if (user == null || user.getUserType() == null)
            return null;

        if ("ADMIN".equalsIgnoreCase(user.getUserType().name())) {
            return adminUserRepository.findActiveByUserId(userId)
                    .map(AdminUser::getAdminRole)
                    .orElse("admin");
        }
        return user.getUserType().getValue();
    }

    /**
     * Áp dụng filter theo userRole (chỉ dùng cho Admin context).
     */
    private List<Long> applyRoleFilter(List<Long> currentUserIds, String userRole) {
        if (userRole == null || userRole.isBlank()) {
            return currentUserIds;
        }

        String role = userRole.trim().toLowerCase();
        List<Long> roleUserIds;

        if ("employer".equals(role) || "candidate".equals(role)) {
            UserType type = UserType.fromValue(role);
            roleUserIds = userRepository.findAllByUserType(type);
        } else {
            roleUserIds = adminUserRepository.findAllByRole(role).stream()
                    .map(a -> a.getUser().getId())
                    .toList();
        }

        if (currentUserIds == null) {
            return roleUserIds;
        }

        Set<Long> currentSet = new HashSet<>(currentUserIds);
        return roleUserIds.stream()
                .filter(currentSet::contains)
                .toList();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank())
            return null;
        return value.trim();
    }

    private ResAuditLogDTO toAuditLogDTO(AuditLog log, Map<Long, String> emailMap, Map<Long, String> roleMap) {
        return ResAuditLogDTO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userEmail(log.getUserId() != null ? emailMap.get(log.getUserId()) : null)
                .userRole(log.getUserId() != null ? roleMap.get(log.getUserId()) : null)
                .action(log.getAction())
                .category(log.getCategory())
                .severity(log.getSeverity())
                .targetEntity(log.getTargetEntity())
                .targetId(log.getTargetId())
                .targetName(resolveTargetName(log.getTargetEntity(), log.getTargetId()))
                .status(log.getStatus())
                .durationMs(log.getDurationMs())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private ResBusinessEventLogDTO toBusinessEventLogDTO(BusinessEventLog log, Map<Long, String> emailMap,
            Map<Long, String> roleMap) {
        boolean hasChanges = false;
        if (log.getMetadata() != null && log.getMetadata().containsKey("changedFields")) {
            Object changedFieldsObj = log.getMetadata().get("changedFields");
            if (changedFieldsObj instanceof List) {
                hasChanges = !((List<?>) changedFieldsObj).isEmpty();
            }
        }

        return ResBusinessEventLogDTO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userEmail(log.getUserId() != null ? emailMap.get(log.getUserId()) : null)
                .userRole(log.getUserId() != null ? roleMap.get(log.getUserId()) : null)
                .action(log.getAction())
                .category(log.getCategory())
                .targetEntity(log.getTargetEntity())
                .targetId(log.getTargetId())
                .targetName(resolveTargetName(log.getTargetEntity(), log.getTargetId()))
                .hasChanges(hasChanges)
                .status(log.getStatus())
                .durationMs(log.getDurationMs())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private <T> ResultPaginationDTO buildPagination(Page<T> page, Object data) {
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements());
        return new ResultPaginationDTO(meta, data);
    }

    private ResultPaginationDTO buildEmptyPagination(Pageable pageable) {
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                0, 0L);
        return new ResultPaginationDTO(meta, List.of());
    }

    /**
     * Giải quyết tên hiển thị (Target Name) dựa vào Entity Type và ID.
     */
    private String resolveTargetName(String entityType, Long entityId) {
        if (entityId == null || entityType == null)
            return null;

        try {
            switch (entityType.toUpperCase()) {
                case "USER":
                case "ADMIN_USER":
                case "COMPANY_MEMBER":
                    return userRepository.findById(entityId)
                            .map(u -> u.getEmail())
                            .orElse("User #" + entityId);

                case "CANDIDATE_PROFILE":
                    return candidateProfileRepository.findById(entityId)
                            .map(cp -> cp.getFullName())
                            .orElse("Candidate #" + entityId);

                case "JOB_POSTING":
                    return jobPostingRepository.findById(entityId)
                            .map(jp -> jp.getTitle())
                            .orElse("Job #" + entityId);

                case "COMPANY":
                    return companyRepository.findById(entityId)
                            .map(c -> c.getName())
                            .orElse("Company #" + entityId);

                case "CV":
                    return cvsRepository.findById(entityId)
                            .map(c -> c.getTitle())
                            .orElse("CV #" + entityId);

                case "APPLICATION":
                    return applicationRepository.findById(entityId)
                            .map(app -> {
                                String title = app.getJobPosting() != null ? app.getJobPosting().getTitle()
                                        : "Job #" + app.getJobPostId();
                                String candidate = app.getCandidate() != null ? app.getCandidate().getEmail()
                                        : "User #" + app.getCandidateUserId();
                                return title + " - " + candidate;
                            })
                            .orElse("Application #" + entityId);

                case "INTERVIEW":
                    return interviewRepository.findById(entityId)
                            .map(i -> {
                                String title = (i.getApplication() != null
                                        && i.getApplication().getJobPosting() != null)
                                                ? i.getApplication().getJobPosting().getTitle()
                                                : "Interview";
                                String candidate = (i.getApplication() != null
                                        && i.getApplication().getCandidate() != null)
                                                ? i.getApplication().getCandidate().getEmail()
                                                : "Candidate";
                                return "Phỏng vấn: " + title + " (" + candidate + ")";
                            })
                            .orElse("Interview #" + entityId);

                case "INTERVIEW_ROUND":
                    return interviewRoundRepository.findById(entityId)
                            .map(r -> "Vòng: " + r.getRoundName())
                            .orElse("Interview Round #" + entityId);

                case "INTERVIEW_SLOT":
                    return interviewSlotRepository.findById(entityId)
                            .map(s -> "Slot phỏng vấn #" + s.getId())
                            .orElse("Interview Slot #" + entityId);

                case "ORDER":
                    return orderRepository.findById(entityId)
                            .map(o -> o.getOrderCode())
                            .orElse("Order #" + entityId);

                case "SUBSCRIPTION":
                    return companySubscriptionRepository.findById(entityId)
                            .map(sub -> (sub.getServicePackage() != null) ? sub.getServicePackage().getName()
                                    : "Subscription #" + sub.getId())
                            .orElse("Subscription #" + entityId);

                case "SERVICE":
                    return serviceRepository.findById(entityId)
                            .map(s -> s.getName())
                            .orElse("Service #" + entityId);

                case "ADDON_SERVICE":
                    return addonServiceRepository.findById(entityId)
                            .map(a -> a.getName())
                            .orElse("Addon #" + entityId);

                case "SERVICE_PACKAGE":
                    return servicePackageRepository.findById(entityId)
                            .map(sp -> sp.getName())
                            .orElse("Package #" + entityId);

                case "REPORT":
                    return complaintRepository.findById(entityId)
                            .map(c -> "Báo cáo: " + c.getComplaintType() + " - Job #" + c.getJobPostId())
                            .orElse("Report #" + entityId);

                case "APPEAL":
                    return complaintAppealRepository.findById(entityId)
                            .map(a -> "Kháng cáo #" + a.getId() + " cho Khiếu nại #" + a.getComplaintId())
                            .orElse("Appeal #" + entityId);

                case "TALENT_POOL":
                    return talentPoolRepository.findById(entityId)
                            .map(tp -> {
                                String candidate = tp.getCandidateUser() != null ? tp.getCandidateUser().getEmail()
                                        : "User #" + tp.getCandidateUserId();
                                return "Talent Pool: " + candidate;
                            })
                            .orElse("Talent Pool #" + entityId);

                case "VIOLATION_SCORE":
                    return employerViolationScoreRepository.findById(entityId)
                            .map(v -> "Điểm vi phạm NTD #" + v.getEmployerId())
                            .orElse("Violation Score #" + entityId);

                default:
                    return entityType + " #" + entityId;
            }
        } catch (Exception e) {
            return entityId.toString();
        }
    }
}
