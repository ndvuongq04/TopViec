package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqConfirmReportDTO;
import com.topviec.topviec_be.dto.request.ReqCreateReportDTO;
import com.topviec.topviec_be.dto.request.ReqProcessReportDTO;
import com.topviec.topviec_be.dto.response.ResAdminReportStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResCandidateReportSummaryDTO;
import com.topviec.topviec_be.dto.response.ResEmployerComplaintDetailDTO;
import com.topviec.topviec_be.dto.response.ResEmployerComplaintSummaryDTO;
import com.topviec.topviec_be.dto.response.ResReportDetailDTO;
import com.topviec.topviec_be.dto.response.ResReportSummaryDTO;
import com.topviec.topviec_be.dto.response.ResViolationReasonDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.AdminUser;
import com.topviec.topviec_be.entity.CandidateProfile;
import com.topviec.topviec_be.entity.Complaint;
import com.topviec.topviec_be.entity.ComplaintEvidence;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.EmployerViolationScore;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.entity.ViolationLog;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.complaints.ComplaintPriority;
import com.topviec.topviec_be.enums.complaints.ComplaintStatus;
import com.topviec.topviec_be.enums.complaints.ComplaintType;
import com.topviec.topviec_be.enums.complaints.ViolationGroup;
import com.topviec.topviec_be.enums.complaints.ViolationSource;
import com.topviec.topviec_be.enums.jobs.JobPostStatus;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.repository.CandidateProfileRepository;
import com.topviec.topviec_be.repository.ComplaintEvidenceRepository;
import com.topviec.topviec_be.repository.ComplaintRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.EmployerViolationScoreRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.repository.ViolationLogRepository;
import com.topviec.topviec_be.service.EmailService;
import com.topviec.topviec_be.service.ReportService;
import com.topviec.topviec_be.event.ComplaintAutoClosedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final int MAX_REPORTS_PER_DAY = 3;
    private static final int MIN_ACCOUNT_AGE_DAYS = 7;
    private static final long GROUP_A_SLA_HOURS = 42;
    private static final long GROUP_B_SLA_HOURS = 72;
    private static final int GROUP_A_POINTS = 10;
    private static final int GROUP_B_POINTS = 30;
    private static final DateTimeFormatter EMAIL_DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DECISION_APPROVE = "approve";
    private static final String DECISION_REJECT = "reject";
    private static final List<String> EMPLOYER_VISIBLE_STATUSES = List.of(
            ComplaintStatus.PROCESSING.getValue(),
            ComplaintStatus.RESOLVED.getValue(),
            ComplaintStatus.AUTO_CLOSED.getValue());

    private final ComplaintRepository complaintRepository;
    private final ComplaintEvidenceRepository complaintEvidenceRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final AdminUserRepository adminUserRepository;
    private final ViolationLogRepository violationLogRepository;
    private final EmployerViolationScoreRepository employerViolationScoreRepository;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ResReportDetailDTO create(Long reporterUserId, ReqCreateReportDTO request) {
        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy người báo cáo"));
        if (reporter.getUserType() != UserType.CANDIDATE) {
            throw AppException.forbidden("Chỉ ứng viên mới được tạo báo cáo");
        }

        JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(request.getJobPostId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin bị báo cáo"));

        validateCreateRequest(reporter, jobPosting.getId(), request);

        ComplaintType complaintType = request.getComplaintType();
        ViolationGroup violationGroup = determineGroup(complaintType);

        Complaint complaint = complaintRepository.save(Complaint.builder()
                .jobPostId(jobPosting.getId())
                .reporterUserId(reporterUserId)
                .complaintType(complaintType.getValue())
                .violationGroup(violationGroup.getValue())
                .description(trimToNull(request.getDescription()))
                .priority(defaultPriority(violationGroup).getValue())
                .status(ComplaintStatus.PENDING.getValue())
                .updatedBy(reporterUserId)
                .build());

        if (request.getEvidences() != null && !request.getEvidences().isEmpty()) {
            complaintEvidenceRepository.saveAll(request.getEvidences().stream()
                    .map(item -> ComplaintEvidence.builder()
                            .complaintId(complaint.getId())
                            .fileUrl(item.getFileUrl().trim())
                            .fileType(item.getFileType().getValue())
                            .build())
                    .toList());
        }

        return getDetail(complaint.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getReports(
            String search,
            String status,
            String group,
            String complaintType,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        Page<Complaint> page = complaintRepository.findAdminReports(
                normalizeKeyword(search),
                normalizeValue(status),
                normalizeValue(group),
                normalizeValue(complaintType),
                fromDate != null ? fromDate.atStartOfDay() : null,
                toDate != null ? toDate.plusDays(1).atStartOfDay() : null,
                pageable);

        return toPagination(page, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResReportDetailDTO getDetail(Long reportId) {
        Complaint complaint = findComplaintOrThrow(reportId);

        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId()).orElse(null);
        Company company = jobPosting != null ? companyRepository.findById(jobPosting.getCompanyId()).orElse(null)
                : null;
        User reporter = userRepository.findById(complaint.getReporterUserId()).orElse(null);
        CandidateProfile reporterProfile = reporter != null
                ? candidateProfileRepository.findByUserId(reporter.getId()).orElse(null)
                : null;
        AdminUser assignedAdmin = complaint.getAssignedTo() != null
                ? adminUserRepository.findById(complaint.getAssignedTo()).orElse(null)
                : null;
        List<ComplaintEvidence> evidences = complaintEvidenceRepository.findByComplaintId(complaint.getId());
        ProcessingInfo processingInfo = calculateProcessingInfo(complaint);

        return ResReportDetailDTO.builder()
                .id(complaint.getId())
                .reportCode(buildReportCode(complaint.getId()))
                .complaintType(complaint.getComplaintType())
                .violationGroup(complaint.getViolationGroup())
                .priority(complaint.getPriority())
                .status(complaint.getStatus())
                .description(complaint.getDescription())
                .resolutionNote(complaint.getResolutionNote())
                .createdAt(complaint.getCreatedAt())
                .updatedAt(complaint.getUpdatedAt())
                .resolvedAt(complaint.getResolvedAt())
                .emailSentAt(complaint.getEmailSentAt())
                .employerDeadline(complaint.getEmployerDeadline())
                .employerRespondedAt(complaint.getEmployerRespondedAt())
                .remainingProcessingHours(processingInfo.remainingHours())
                .totalAllowedProcessingHours(processingInfo.totalHours())
                .reporter(ResReportDetailDTO.ReporterInfo.builder()
                        .userId(complaint.getReporterUserId())
                        .fullName(resolveReporterName(reporterProfile, reporter))
                        .email(reporter != null ? reporter.getEmail() : null)
                        .build())
                .jobPosting(jobPosting == null ? null
                        : ResReportDetailDTO.JobInfo.builder()
                                .id(jobPosting.getId())
                                .title(jobPosting.getTitle())
                                .status(jobPosting.getStatus())
                                .company(company == null ? null
                                        : ResReportDetailDTO.CompanyInfo.builder()
                                                .id(company.getId())
                                                .name(company.getName())
                                                .logoUrl(company.getLogoUrl())
                                                .status(company.getStatus())
                                                .violationScore(company.getViolationScore())
                                                .build())
                                .build())
                .assignedAdmin(assignedAdmin == null ? null
                        : ResReportDetailDTO.AssignedAdminInfo.builder()
                                .adminUserId(assignedAdmin.getAdminUsersId())
                                .fullName(assignedAdmin.getFullName())
                                .adminRole(assignedAdmin.getAdminRole())
                                .build())
                .evidences(evidences.stream()
                        .map(item -> ResReportDetailDTO.EvidenceInfo.builder()
                                .id(item.getId())
                                .fileUrl(item.getFileUrl())
                                .fileType(item.getFileType())
                                .createdAt(item.getCreatedAt())
                                .build())
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public ResReportDetailDTO confirm(Long adminUserId, Long reportId, ReqConfirmReportDTO request) {
        Complaint complaint = findComplaintOrThrow(reportId);
        AdminUser adminUser = adminUserRepository.findActiveByUserId(adminUserId)
                .orElseThrow(() -> AppException.forbidden("Không tìm thấy tài khoản admin hợp lệ"));

        if (!ComplaintStatus.PENDING.getValue().equals(complaint.getStatus())) {
            throw AppException.badRequest("Báo cáo phải ở trạng thái pending mới được xác nhận");
        }

        complaint.setAssignedTo(adminUser.getAdminUsersId());
        complaint.setUpdatedBy(adminUserId);
        complaint.setResolutionNote(trimToNull(request.getResolutionNote()));

        if (Boolean.FALSE.equals(request.getApproved())) {
            complaint.setStatus(ComplaintStatus.REJECTED.getValue());
            complaint.setResolvedAt(LocalDateTime.now());
            complaintRepository.save(complaint);
            return getDetail(complaint.getId());
        }

        // Cả nhóm A và B: chỉ chuyển sang PROCESSING, chưa cộng điểm
        complaint.setStatus(ComplaintStatus.PROCESSING.getValue());

        if (ViolationGroup.A.getValue().equalsIgnoreCase(complaint.getViolationGroup())) {
            // Nhóm A: NTD có 48h sửa tin — scheduler xử lý nếu quá hạn
            complaint.setEmailSentAt(LocalDateTime.now());
            complaint.setEmployerDeadline(LocalDateTime.now().plusHours(GROUP_A_SLA_HOURS));
            sendComplaintGroupAEmail(complaint);
        }

        complaintRepository.save(complaint);
        return getDetail(complaint.getId());
    }

    @Override
    @Transactional
    public ResReportDetailDTO process(Long adminUserId, Long reportId, ReqProcessReportDTO request) {
        Complaint complaint = findComplaintOrThrow(reportId);
        AdminUser adminUser = adminUserRepository.findActiveByUserId(adminUserId)
                .orElseThrow(() -> AppException.forbidden("Không tìm thấy tài khoản admin hợp lệ"));

        if (!ComplaintStatus.PROCESSING.getValue().equals(complaint.getStatus())) {
            throw AppException.badRequest("Báo cáo phải ở trạng thái processing mới được xử lý thủ công");
        }

        String decision = normalizeValue(request.getDecision());
        complaint.setAssignedTo(adminUser.getAdminUsersId());
        complaint.setUpdatedBy(adminUserId);
        complaint.setResolutionNote(trimToNull(request.getResolutionNote()));

        if (DECISION_REJECT.equals(decision)) {
            complaint.setStatus(ComplaintStatus.REJECTED.getValue());
            complaint.setResolvedAt(LocalDateTime.now());
            complaintRepository.save(complaint);
            return getDetail(complaint.getId());
        }

        if (!DECISION_APPROVE.equals(decision)) {
            throw AppException.badRequest("decision khong hop le. Chi chap nhan: approve | reject");
        }

        // Admin chủ động xử lý — cộng điểm + ẩn/reject tin
        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin bị báo cáo"));
        Company company = companyRepository.findById(jobPosting.getCompanyId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty"));
        String note = trimToNull(request.getResolutionNote());

        boolean isGroupB = ViolationGroup.B.getValue().equalsIgnoreCase(complaint.getViolationGroup());
        int points = isGroupB ? GROUP_B_POINTS : GROUP_A_POINTS;
        String newJobStatus = isGroupB ? JobPostStatus.HIDDEN.getValue() : JobPostStatus.REJECTED.getValue();

        addViolationScore(company, jobPosting, complaint, adminUser, points, note);
        jobPosting.setStatus(newJobStatus);
        jobPosting.setRejectionReason(complaint.getComplaintType());
        jobPosting.setModerationNote(note);
        jobPosting.setUpdatedBy(adminUser.getUser().getId());
        jobPostingRepository.save(jobPosting);

        complaint.setStatus(ComplaintStatus.RESOLVED.getValue());
        complaint.setResolvedAt(LocalDateTime.now());

        // Gửi email thông báo nhóm B khi xử lý xong
        if (isGroupB) {
            sendComplaintGroupBEmail(jobPosting, company, complaint);
        }
        complaintRepository.save(complaint);
        return getDetail(complaint.getId());
    }

    @Override
    @Transactional
    public void autoCloseExpiredGroupAComplaints() {
        List<Complaint> expired = complaintRepository.findExpiredGroupAComplaints(LocalDateTime.now());
        for (Complaint complaint : expired) {
            try {
                processExpiredGroupAComplaint(complaint);
            } catch (Exception e) {
                log.error("Failed to auto-close complaint id={}: {}", complaint.getId(), e.getMessage(), e);
            }
        }
    }

    private void processExpiredGroupAComplaint(Complaint complaint) {
        JobPosting job = jobPostingRepository.findById(complaint.getJobPostId()).orElse(null);
        Company company = null;

        if (job != null) {
            company = companyRepository.findById(job.getCompanyId()).orElse(null);
            if (company != null) {
                addViolationScoreSystem(company, job.getId(), complaint, GROUP_A_POINTS);
            }
            job.setStatus(JobPostStatus.REJECTED.getValue());
            job.setRejectionReason(complaint.getComplaintType());
            jobPostingRepository.save(job);
        } else {
            log.warn("Job not found for expired complaint id={}, jobPostId={} — closing without penalty",
                    complaint.getId(), complaint.getJobPostId());
        }

        complaint.setStatus(ComplaintStatus.RESOLVED.getValue());
        complaint.setResolvedAt(LocalDateTime.now());
        complaintRepository.save(complaint);

        if (job != null && company != null) {
            String employerEmail = resolveEmployerEmail(company);
            if (employerEmail != null) {
                eventPublisher.publishEvent(new ComplaintAutoClosedEvent(
                        employerEmail,
                        job.getTitle(),
                        complaint.getComplaintType(),
                        buildReportCode(complaint.getId())));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResViolationReasonDTO> getViolationReasons() {
        return List.of(
                toReason(ComplaintType.FRAUDULENT, "Lừa đảo", ViolationGroup.B, true, ComplaintPriority.IMPORTANT),
                toReason(ComplaintType.PAYMENT_ISSUE, "Thu phí / yêu cầu thanh toán", ViolationGroup.B, true,
                        ComplaintPriority.IMPORTANT),
                toReason(ComplaintType.SPAM, "Spam / đăng lặp lại", ViolationGroup.A, false, ComplaintPriority.NORMAL),
                toReason(ComplaintType.WRONG_INFO, "Thông tin sai lệch", ViolationGroup.A, false,
                        ComplaintPriority.NORMAL),
                toReason(ComplaintType.INAPPROPRIATE, "Nội dung không phù hợp", ViolationGroup.A, false,
                        ComplaintPriority.NORMAL),
                toReason(ComplaintType.OTHER, "Lý do khác", ViolationGroup.A, false, ComplaintPriority.NORMAL));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getMyReports(Long reporterUserId, String status, Pageable pageable) {
        Page<Complaint> page = complaintRepository.findMyReports(reporterUserId, normalizeValue(status), pageable);

        List<Complaint> complaints = page.getContent();
        List<Long> jobIds = complaints.stream().map(Complaint::getJobPostId).filter(Objects::nonNull).distinct()
                .toList();

        Map<Long, JobPosting> jobMap = jobIds.isEmpty()
                ? Collections.emptyMap()
                : jobPostingRepository.findAllById(jobIds).stream()
                        .collect(Collectors.toMap(JobPosting::getId, j -> j));

        List<Long> companyIds = jobMap.values().stream()
                .map(JobPosting::getCompanyId).filter(Objects::nonNull).distinct().toList();
        Map<Long, Company> companyMap = companyIds.isEmpty()
                ? Collections.emptyMap()
                : companyRepository.findAllById(companyIds).stream()
                        .collect(Collectors.toMap(Company::getId, c -> c));

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(complaints.stream()
                .map(c -> toCandidateSummary(c, jobMap, companyMap))
                .toList());
        return result;
    }

    private ResCandidateReportSummaryDTO toCandidateSummary(
            Complaint complaint,
            Map<Long, JobPosting> jobMap,
            Map<Long, Company> companyMap) {

        JobPosting jobPosting = jobMap.get(complaint.getJobPostId());
        Company company = jobPosting != null ? companyMap.get(jobPosting.getCompanyId()) : null;

        return ResCandidateReportSummaryDTO.builder()
                .id(complaint.getId())
                .reportCode(buildReportCode(complaint.getId()))
                .jobPost(jobPosting == null ? null
                        : ResCandidateReportSummaryDTO.JobPostInfo.builder()
                                .id(jobPosting.getId())
                                .title(jobPosting.getTitle())
                                .build())
                .company(company == null ? null
                        : ResCandidateReportSummaryDTO.CompanyInfo.builder()
                                .id(company.getId())
                                .name(company.getName())
                                .logoUrl(company.getLogoUrl())
                                .build())
                .complaintType(complaint.getComplaintType())
                .status(complaint.getStatus())
                .createdAt(complaint.getCreatedAt())
                .build();
    }

    private void validateCreateRequest(User reporter, Long jobPostId, ReqCreateReportDTO request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        if (reporter.getCreatedAt() != null
                && reporter.getCreatedAt().isAfter(now.minusDays(MIN_ACCOUNT_AGE_DAYS))) {
            throw AppException.badRequest("Tài khoản phải hoạt động tối thiểu 7 ngày mới được báo cáo");
        }

        long dailyCount = complaintRepository.countByReporterUserIdAndCreatedAtBetween(
                reporter.getId(), startOfDay, startOfDay.plusDays(1));
        if (dailyCount >= MAX_REPORTS_PER_DAY) {
            throw AppException.badRequest("Bạn đã dùng hết giới hạn 3 báo cáo trong ngày");
        }

        if (complaintRepository.existsByJobPostIdAndReporterUserIdAndDeletedAtIsNull(jobPostId, reporter.getId())) {
            throw AppException.conflict("Bạn đã báo cáo tin tuyển dụng này trước đó");
        }

        // if (determineGroup(request.getComplaintType()) == ViolationGroup.B
        // && (request.getEvidences() == null || request.getEvidences().isEmpty())) {
        // throw AppException.badRequest("Nhóm vi phạm B bắt buộc phải có ít nhất 1 bằng
        // chứng");
        // }
    }

    private void addViolationScoreSystem(Company company, Long jobPostId, Complaint complaint, int points) {
        Long employerUserId = company.getUserId() != null ? company.getUserId() : company.getCreatedBy();
        if (employerUserId == null)
            return;

        violationLogRepository.save(ViolationLog.builder()
                .employerId(employerUserId)
                .jobPostId(jobPostId)
                .violationType(complaint.getComplaintType())
                .points(points)
                .source(ViolationSource.SYSTEM.getValue())
                .complaintId(complaint.getId())
                .createdBy(null)
                .build());

        EmployerViolationScore score = employerViolationScoreRepository.findByEmployerId(employerUserId)
                .orElseGet(() -> EmployerViolationScore.builder()
                        .employerId(employerUserId)
                        .totalScore(0)
                        .build());
        score.setTotalScore((score.getTotalScore() == null ? 0 : score.getTotalScore()) + points);
        employerViolationScoreRepository.save(score);

        company.setViolationScore(score.getTotalScore());
        companyRepository.save(company);
    }

    private void addViolationScore(
            Company company,
            JobPosting jobPosting,
            Complaint complaint,
            AdminUser adminUser,
            int points,
            String note) {

        Long employerUserId = company.getUserId() != null ? company.getUserId() : company.getCreatedBy();
        if (employerUserId == null) {
            return;
        }

        violationLogRepository.save(ViolationLog.builder()
                .employerId(employerUserId)
                .jobPostId(jobPosting.getId())
                .violationType(complaint.getComplaintType())
                .points(points)
                .source(ViolationSource.COMPLAINT.getValue())
                .complaintId(complaint.getId())
                .note(note)
                .createdBy(adminUser.getAdminUsersId())
                .build());

        EmployerViolationScore score = employerViolationScoreRepository.findByEmployerId(employerUserId)
                .orElseGet(() -> EmployerViolationScore.builder()
                        .employerId(employerUserId)
                        .totalScore(0)
                        .build());

        score.setTotalScore((score.getTotalScore() == null ? 0 : score.getTotalScore()) + points);
        // lastGroupBViolationAt chỉ track nhóm B (dùng cho điều kiện reset điểm)
        if (ViolationGroup.B.getValue().equalsIgnoreCase(complaint.getViolationGroup())) {
            score.setLastGroupBViolationAt(LocalDateTime.now());
        }
        employerViolationScoreRepository.save(score);

        company.setViolationScore(score.getTotalScore());
        company.setUpdatedBy(adminUser.getUser().getId());

        if (score.getTotalScore() >= 50 && !CompanyStatus.SUSPENDED.getValue().equals(company.getStatus())) {
            suspendCompany(company, adminUser.getUser().getId(),
                    note != null ? note : "Tam khoa cong ty do diem vi pham dat nguong");
        } else {
            companyRepository.save(company);
        }
    }

    private void suspendCompany(Company company, Long adminUserId, String reason) {
        company.setStatus(CompanyStatus.SUSPENDED.getValue());
        company.setSuspendedAt(LocalDateTime.now());
        company.setSuspendedReason(reason);
        company.setUpdatedBy(adminUserId);
        companyRepository.save(company);
    }

    private Complaint findComplaintOrThrow(Long reportId) {
        return complaintRepository.findByIdAndDeletedAtIsNull(reportId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy báo cáo"));
    }

    private ResultPaginationDTO toPagination(Page<Complaint> page, Pageable pageable) {
        List<Complaint> complaints = page.getContent();
        List<Long> jobIds = complaints.stream().map(Complaint::getJobPostId).filter(Objects::nonNull).distinct()
                .toList();
        List<Long> reporterIds = complaints.stream().map(Complaint::getReporterUserId).filter(Objects::nonNull)
                .distinct().toList();
        List<Long> adminIds = complaints.stream().map(Complaint::getAssignedTo).filter(Objects::nonNull).distinct()
                .toList();

        Map<Long, JobPosting> jobMap = jobIds.isEmpty()
                ? Collections.emptyMap()
                : jobPostingRepository.findAllById(jobIds).stream()
                        .collect(Collectors.toMap(JobPosting::getId, item -> item));

        List<Long> companyIds = jobMap.values().stream().map(JobPosting::getCompanyId).filter(Objects::nonNull)
                .distinct().toList();
        Map<Long, Company> companyMap = companyIds.isEmpty()
                ? Collections.emptyMap()
                : companyRepository.findAllById(companyIds).stream()
                        .collect(Collectors.toMap(Company::getId, item -> item));

        Map<Long, User> userMap = reporterIds.isEmpty()
                ? Collections.emptyMap()
                : userRepository.findAllById(reporterIds).stream()
                        .collect(Collectors.toMap(User::getId, item -> item));

        Map<Long, CandidateProfile> profileMap = reporterIds.isEmpty()
                ? Collections.emptyMap()
                : candidateProfileRepository.findByUserIdIn(reporterIds).stream()
                        .collect(Collectors.toMap(CandidateProfile::getUserId, item -> item));

        Map<Long, AdminUser> adminMap = adminIds.isEmpty()
                ? Collections.emptyMap()
                : adminUserRepository.findAllById(adminIds).stream()
                        .collect(Collectors.toMap(AdminUser::getAdminUsersId, item -> item));

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(complaints.stream()
                .map(complaint -> toSummary(complaint, jobMap, companyMap, userMap, profileMap, adminMap))
                .toList());
        return result;
    }

    private ResReportSummaryDTO toSummary(
            Complaint complaint,
            Map<Long, JobPosting> jobMap,
            Map<Long, Company> companyMap,
            Map<Long, User> userMap,
            Map<Long, CandidateProfile> profileMap,
            Map<Long, AdminUser> adminMap) {

        JobPosting jobPosting = jobMap.get(complaint.getJobPostId());
        Company company = jobPosting != null ? companyMap.get(jobPosting.getCompanyId()) : null;
        User reporter = userMap.get(complaint.getReporterUserId());
        CandidateProfile candidateProfile = profileMap.get(complaint.getReporterUserId());
        AdminUser adminUser = adminMap.get(complaint.getAssignedTo());
        ProcessingInfo processingInfo = calculateProcessingInfo(complaint);

        return ResReportSummaryDTO.builder()
                .id(complaint.getId())
                .reportCode(buildReportCode(complaint.getId()))
                .reporterUserId(complaint.getReporterUserId())
                .reporterName(resolveReporterName(candidateProfile, reporter))
                .jobPostId(complaint.getJobPostId())
                .jobPostTitle(jobPosting != null ? jobPosting.getTitle() : null)
                .companyId(company != null ? company.getId() : null)
                .companyName(company != null ? company.getName() : null)
                .complaintType(complaint.getComplaintType())
                .violationGroup(complaint.getViolationGroup())
                .priority(complaint.getPriority())
                .status(complaint.getStatus())
                .createdAt(complaint.getCreatedAt())
                .processingDeadline(processingInfo.deadline())
                .remainingProcessingHours(processingInfo.remainingHours())
                .totalAllowedProcessingHours(processingInfo.totalHours())
                .assignedAdminName(adminUser != null ? adminUser.getFullName() : null)
                .build();
    }

    private ProcessingInfo calculateProcessingInfo(Complaint complaint) {
        long totalHours = ViolationGroup.B.getValue().equalsIgnoreCase(complaint.getViolationGroup())
                ? GROUP_B_SLA_HOURS
                : GROUP_A_SLA_HOURS;

        LocalDateTime deadline = complaint.getEmployerDeadline() != null
                ? complaint.getEmployerDeadline()
                : complaint.getCreatedAt().plusHours(totalHours);

        long remainingHours = 0;
        if (!List.of(
                ComplaintStatus.RESOLVED.getValue(),
                ComplaintStatus.REJECTED.getValue(),
                ComplaintStatus.AUTO_CLOSED.getValue()).contains(complaint.getStatus())) {
            remainingHours = Math.max(0, Duration.between(LocalDateTime.now(), deadline).toHours());
        }

        return new ProcessingInfo(deadline, remainingHours, totalHours);
    }

    private String resolveReporterName(CandidateProfile profile, User user) {
        if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
            return profile.getFullName();
        }
        return user != null ? user.getEmail() : null;
    }

    private String buildReportCode(Long id) {
        return String.format("RP%06d", id);
    }

    private ComplaintPriority defaultPriority(ViolationGroup group) {
        return group == ViolationGroup.B ? ComplaintPriority.IMPORTANT : ComplaintPriority.NORMAL;
    }

    private ViolationGroup determineGroup(ComplaintType complaintType) {
        return switch (complaintType) {
            case FRAUDULENT, PAYMENT_ISSUE -> ViolationGroup.B;
            case SPAM, WRONG_INFO, INAPPROPRIATE, OTHER -> ViolationGroup.A;
        };
    }

    private ResViolationReasonDTO toReason(
            ComplaintType type,
            String name,
            ViolationGroup group,
            boolean requiresEvidence,
            ComplaintPriority priority) {
        return ResViolationReasonDTO.builder()
                .code(type.getValue())
                .name(name)
                .group(group.getValue())
                .requiresEvidence(requiresEvidence)
                .priority(priority.getValue())
                .build();
    }

    private String normalizeValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase();
    }

    private String normalizeKeyword(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Xử lý search mã báo cáo: "RP000001" → "1" để LIKE so với CAST(id AS string).
     * Nếu không phải dạng RP prefix thì giữ nguyên keyword.
     */
    private String normalizeReportCodeSearch(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.toLowerCase().startsWith("rp")) {
            String numeric = trimmed.substring(2).replaceAll("[^0-9]", "");
            if (!numeric.isBlank()) {
                return String.valueOf(Long.parseLong(numeric));
            }
        }
        return trimmed;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    // ── NTD xem khiếu nại bị báo cáo ─────────────────────────────────────────

    private void sendComplaintGroupAEmail(Complaint complaint) {
        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId()).orElse(null);
        if (jobPosting == null) {
            return;
        }

        Company company = companyRepository.findById(jobPosting.getCompanyId()).orElse(null);
        String employerEmail = resolveEmployerEmail(company);
        if (employerEmail == null) {
            return;
        }

        emailService.sendComplaintGroupAEmail(
                employerEmail,
                jobPosting.getTitle(),
                complaint.getComplaintType(),
                buildReportCode(complaint.getId()),
                formatDeadline(complaint.getEmployerDeadline()));
    }

    private void sendComplaintGroupBEmail(JobPosting jobPosting, Company company, Complaint complaint) {
        String employerEmail = resolveEmployerEmail(company);
        if (employerEmail == null) {
            return;
        }

        emailService.sendComplaintGroupBEmail(
                employerEmail,
                jobPosting.getTitle(),
                complaint.getComplaintType(),
                buildReportCode(complaint.getId()));
    }

    private String resolveEmployerEmail(Company company) {
        if (company == null) {
            return null;
        }
        if (company.getEmail() != null && !company.getEmail().isBlank()) {
            return company.getEmail().trim();
        }

        Long employerUserId = company.getUserId() != null ? company.getUserId() : company.getCreatedBy();
        if (employerUserId == null) {
            return null;
        }

        return userRepository.findById(employerUserId)
                .map(User::getEmail)
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .orElse(null);
    }

    private String formatDeadline(LocalDateTime deadline) {
        if (deadline == null) {
            return "";
        }
        return deadline.format(EMAIL_DEADLINE_FORMATTER);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getEmployerReports(
            Long employerUserId,
            String search,
            String status,
            String group,
            String complaintType,
            Pageable pageable) {

        Company company = companyRepository.findByUserId(employerUserId)
                .orElseGet(() -> companyRepository.findByCreatedBy(employerUserId).orElse(null));

        if (company == null) {
            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(pageable.getPageNumber());
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(0);
            meta.setTotals(0);
            ResultPaginationDTO empty = new ResultPaginationDTO();
            empty.setMeta(meta);
            empty.setResult(List.of());
            return empty;
        }

        List<Long> jobPostIds = jobPostingRepository.findIdsByCompanyId(company.getId());
        if (jobPostIds.isEmpty()) {
            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(pageable.getPageNumber());
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(0);
            meta.setTotals(0);
            ResultPaginationDTO empty = new ResultPaginationDTO();
            empty.setMeta(meta);
            empty.setResult(List.of());
            return empty;
        }

        Page<Complaint> page = complaintRepository.findEmployerReports(
                jobPostIds,
                EMPLOYER_VISIBLE_STATUSES,
                normalizeReportCodeSearch(search),
                normalizeValue(status),
                normalizeValue(group),
                normalizeValue(complaintType),
                pageable);

        List<Long> jobIds = page.getContent().stream()
                .map(Complaint::getJobPostId).filter(Objects::nonNull).distinct().toList();
        Map<Long, JobPosting> jobMap = jobIds.isEmpty()
                ? Collections.emptyMap()
                : jobPostingRepository.findAllById(jobIds).stream()
                        .collect(Collectors.toMap(JobPosting::getId, j -> j));

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(page.getContent().stream()
                .map(c -> toEmployerSummary(c, jobMap))
                .toList());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ResEmployerComplaintDetailDTO getEmployerReportDetail(Long employerUserId, Long reportId) {
        Complaint complaint = findComplaintOrThrow(reportId);
        validateEmployerCanViewComplaint(employerUserId, complaint);

        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId()).orElse(null);
        ProcessingInfo info = calculateProcessingInfo(complaint);

        return toEmployerDetail(complaint, jobPosting, info);
    }

    @Override
    @Transactional
    public ResEmployerComplaintDetailDTO respondToReport(Long employerUserId, Long reportId) {
        Complaint complaint = findComplaintOrThrow(reportId);
        validateEmployerCanViewComplaint(employerUserId, complaint);

        if (!ViolationGroup.A.getValue().equalsIgnoreCase(complaint.getViolationGroup())) {
            throw AppException.badRequest("Chỉ có thể xác nhận sửa với báo cáo thuộc nhóm A");
        }
        if (!ComplaintStatus.PROCESSING.getValue().equals(complaint.getStatus())) {
            throw AppException.badRequest(
                    "Bao cao phai o trang thai processing moi duoc xac nhan da sua");
        }

        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        // Kiểm tra NTD có thực sự chỉnh sửa tin sau khi nhận thông báo không
        LocalDateTime emailSentAt = complaint.getEmailSentAt();
        if (emailSentAt != null
                && (jobPosting.getUpdatedAt() == null
                        || !jobPosting.getUpdatedAt().isAfter(emailSentAt))) {
            throw AppException.badRequest(
                    "Ban chua chinh sua tin tuyen dung. Vui long cap nhat tin truoc khi xac nhan da sua.");
        }

        complaint.setStatus(ComplaintStatus.AUTO_CLOSED.getValue());
        complaint.setEmployerRespondedAt(LocalDateTime.now());
        complaint.setResolvedAt(LocalDateTime.now());
        complaint.setUpdatedBy(employerUserId);
        complaintRepository.save(complaint);

        return toEmployerDetail(complaint, jobPosting, calculateProcessingInfo(complaint));
    }

    private void validateEmployerOwnsComplaint(Long employerUserId, Complaint complaint) {
        Company company = companyRepository.findByUserId(employerUserId)
                .orElseGet(() -> companyRepository.findByCreatedBy(employerUserId).orElse(null));
        if (company == null) {
            throw AppException.forbidden("Bạn không có công ty trên hệ thống");
        }
        List<Long> jobPostIds = jobPostingRepository.findIdsByCompanyId(company.getId());
        if (!jobPostIds.contains(complaint.getJobPostId())) {
            throw AppException.forbidden("Bạn không có quyền xem báo cáo này");
        }
    }

    private void validateEmployerCanViewComplaint(Long employerUserId, Complaint complaint) {
        validateEmployerOwnsComplaint(employerUserId, complaint);
        if (!EMPLOYER_VISIBLE_STATUSES.contains(complaint.getStatus())) {
            throw AppException.forbidden("Bạn chưa thể xem báo cáo này");
        }
    }

    private ResEmployerComplaintSummaryDTO toEmployerSummary(
            Complaint complaint,
            Map<Long, JobPosting> jobMap) {

        JobPosting jobPosting = jobMap.get(complaint.getJobPostId());
        ProcessingInfo info = calculateProcessingInfo(complaint);

        return ResEmployerComplaintSummaryDTO.builder()
                .id(complaint.getId())
                .reportCode(buildReportCode(complaint.getId()))
                .jobPost(jobPosting == null ? null
                        : ResEmployerComplaintSummaryDTO.JobPostInfo.builder()
                                .id(jobPosting.getId())
                                .title(jobPosting.getTitle())
                                .status(jobPosting.getStatus())
                                .build())
                .complaintType(complaint.getComplaintType())
                .violationGroup(complaint.getViolationGroup())
                .priority(complaint.getPriority())
                .status(complaint.getStatus())
                .employerDeadline(complaint.getEmployerDeadline())
                .remainingHours(info.remainingHours())
                .createdAt(complaint.getCreatedAt())
                .build();
    }

    private ResEmployerComplaintDetailDTO toEmployerDetail(
            Complaint complaint,
            JobPosting jobPosting,
            ProcessingInfo info) {

        return ResEmployerComplaintDetailDTO.builder()
                .id(complaint.getId())
                .reportCode(buildReportCode(complaint.getId()))
                .jobPost(jobPosting == null ? null
                        : ResEmployerComplaintDetailDTO.JobPostInfo.builder()
                                .id(jobPosting.getId())
                                .title(jobPosting.getTitle())
                                .status(jobPosting.getStatus())
                                .build())
                .complaintType(complaint.getComplaintType())
                .violationGroup(complaint.getViolationGroup())
                .priority(complaint.getPriority())
                .status(complaint.getStatus())
                .description(complaint.getDescription())
                .emailSentAt(complaint.getEmailSentAt())
                .employerDeadline(complaint.getEmployerDeadline())
                .remainingHours(info.remainingHours())
                .employerRespondedAt(complaint.getEmployerRespondedAt())
                .resolutionNote(complaint.getResolutionNote())
                .resolvedAt(complaint.getResolvedAt())
                .createdAt(complaint.getCreatedAt())
                .updatedAt(complaint.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getReportsByJobPost(
            Long jobPostId,
            String status,
            String group,
            String complaintType,
            Pageable pageable) {

        jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        Page<Complaint> page = complaintRepository.findAdminReportsByJobPostId(
                jobPostId,
                normalizeValue(status),
                normalizeValue(group),
                normalizeValue(complaintType),
                pageable);

        return toPagination(page, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getReportsByComplaint(
            Long complaintId,
            String status,
            String group,
            String complaintType,
            Pageable pageable) {

        Complaint complaint = findComplaintOrThrow(complaintId);
        return getReportsByJobPost(complaint.getJobPostId(), status, group, complaintType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getEmployerReportsByComplaint(
            Long employerUserId,
            Long complaintId,
            String status,
            String group,
            String complaintType,
            Pageable pageable) {

        Complaint complaint = findComplaintOrThrow(complaintId);
        return getEmployerReportsByJobPost(employerUserId, complaint.getJobPostId(), status, group, complaintType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getEmployerReportsByJobPost(
            Long employerUserId,
            Long jobPostId,
            String status,
            String group,
            String complaintType,
            Pageable pageable) {

        Company company = companyRepository.findByUserId(employerUserId)
                .orElseGet(() -> companyRepository.findByCreatedBy(employerUserId).orElse(null));
        if (company == null) {
            throw AppException.forbidden("Bạn không có công ty trên hệ thống");
        }

        JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(company.getId())) {
            throw AppException.forbidden("Bạn không có quyền xem khiếu nại của tin này");
        }

        Page<Complaint> page = complaintRepository.findEmployerReports(
                List.of(jobPostId),
                EMPLOYER_VISIBLE_STATUSES,
                null,
                normalizeValue(status),
                normalizeValue(group),
                normalizeValue(complaintType),
                pageable);

        Map<Long, JobPosting> jobMap = Map.of(jobPosting.getId(), jobPosting);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(page.getContent().stream()
                .map(c -> toEmployerSummary(c, jobMap))
                .toList());
        return result;
    }

    // ── Admin statistics ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ResAdminReportStatisticsDTO getReportStatistics() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Tổng báo cáo (chưa xóa mềm)
        long totalReports = complaintRepository.countByDeletedAtIsNull();

        // 2. Số báo cáo chờ xử lý (status = pending)
        long pendingReports = complaintRepository
                .countByStatusAndDeletedAtIsNull(ComplaintStatus.PENDING.getValue());

        // 3. Số báo cáo nhóm B (vi phạm nặng)
        long groupBReports = complaintRepository
                .countByViolationGroupAndDeletedAtIsNull(ViolationGroup.B.getValue());

        // 4. Số báo cáo quá hạn SLA
        // deadlineA = now - 42h → complaint createdAt < deadlineA means overdue for group A
        // deadlineB = now - 72h → complaint createdAt < deadlineB means overdue for group B
        LocalDateTime deadlineA = now.minusHours(GROUP_A_SLA_HOURS);
        LocalDateTime deadlineB = now.minusHours(GROUP_B_SLA_HOURS);
        long slaOverdueReports = complaintRepository.countSlaOverdue(now, deadlineA, deadlineB);

        return ResAdminReportStatisticsDTO.builder()
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .groupBReports(groupBReports)
                .slaOverdueReports(slaOverdueReports)
                .build();
    }

    private record ProcessingInfo(LocalDateTime deadline, long remainingHours, long totalHours) {
    }
}
