package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqCreateAppealDTO;
import com.topviec.topviec_be.dto.request.ReqUnsuspendDTO;
import com.topviec.topviec_be.dto.response.ResAppealDTO;
import com.topviec.topviec_be.entity.AdminUser;
import com.topviec.topviec_be.entity.Complaint;
import com.topviec.topviec_be.entity.ComplaintAppeal;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.EmployerViolationScore;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.ViolationLog;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.complaints.AppealStatus;
import com.topviec.topviec_be.enums.jobs.JobPostStatus;
import com.topviec.topviec_be.enums.complaints.ViolationSource;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.repository.ComplaintAppealRepository;
import com.topviec.topviec_be.repository.ComplaintRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.EmployerViolationScoreRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.repository.ViolationLogRepository;
import com.topviec.topviec_be.service.AppealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppealServiceImpl implements AppealService {

    private final ComplaintAppealRepository appealRepository;
    private final ComplaintRepository complaintRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final ViolationLogRepository violationLogRepository;
    private final EmployerViolationScoreRepository violationScoreRepository;

    @Override
    @Transactional
    public ResAppealDTO create(Long employerUserId, ReqCreateAppealDTO request) {
        // Validate employer account exists and is EMPLOYER type
        userRepository.findById(employerUserId)
                .filter(u -> u.getUserType() == UserType.EMPLOYER)
                .orElseThrow(() -> AppException.forbidden("Chỉ nhà tuyển dụng mới được nộp kháng cáo"));

        // Validate complaint exists and is PROCESSING or RESOLVED
        Complaint complaint = complaintRepository.findByIdAndDeletedAtIsNull(request.getComplaintId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy báo cáo"));

        if (!List.of("processing", "resolved").contains(
                complaint.getStatus() != null ? complaint.getStatus().toLowerCase() : "")) {
            throw AppException.badRequest("Chỉ có thể kháng cáo khi báo cáo đang xử lý hoặc đã xử lý");
        }

        // Validate employer is the one penalized (company of job post belongs to employer)
        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng liên quan"));

        Company company = companyRepository.findById(jobPosting.getCompanyId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty liên quan"));

        Long companyOwnerUserId = company.getUserId() != null ? company.getUserId() : company.getCreatedBy();
        if (!employerUserId.equals(companyOwnerUserId)) {
            throw AppException.forbidden("Bạn không có quyền kháng cáo báo cáo này");
        }

        // Prevent duplicate appeals (pending or approved already exist)
        boolean alreadyAppealed = appealRepository.existsByComplaintIdAndEmployerIdAndStatusIn(
                request.getComplaintId(),
                employerUserId,
                List.of(AppealStatus.PENDING.getValue(), AppealStatus.APPROVED.getValue()));
        if (alreadyAppealed) {
            throw AppException.conflict("Bạn đã nộp kháng cáo cho báo cáo này rồi");
        }

        ComplaintAppeal appeal = appealRepository.save(ComplaintAppeal.builder()
                .complaintId(request.getComplaintId())
                .employerId(employerUserId)
                .content(request.getContent().trim())
                .status(AppealStatus.PENDING.getValue())
                .build());

        return toResponse(appeal, complaint, jobPosting, company, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResAppealDTO> getByEmployer(Long employerId) {
        userRepository.findById(employerId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));

        List<ComplaintAppeal> appeals = appealRepository.findByEmployerIdOrderByCreatedAtDesc(employerId);
        if (appeals.isEmpty()) {
            return Collections.emptyList();
        }

        // Batch load complaints
        List<Long> complaintIds = appeals.stream().map(ComplaintAppeal::getComplaintId).distinct().toList();
        Map<Long, Complaint> complaintMap = complaintRepository.findAllById(complaintIds).stream()
                .collect(Collectors.toMap(Complaint::getId, c -> c));

        // Batch load job postings and companies
        List<Long> jobPostIds = complaintMap.values().stream()
                .map(Complaint::getJobPostId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, JobPosting> jobPostMap = jobPostIds.isEmpty()
                ? Collections.emptyMap()
                : jobPostingRepository.findAllById(jobPostIds).stream()
                        .collect(Collectors.toMap(JobPosting::getId, j -> j));

        List<Long> companyIds = jobPostMap.values().stream()
                .map(JobPosting::getCompanyId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Company> companyMap = companyIds.isEmpty()
                ? Collections.emptyMap()
                : companyRepository.findAllById(companyIds).stream()
                        .collect(Collectors.toMap(Company::getId, c -> c));

        // Batch load admin reviewers
        List<Long> adminIds = appeals.stream()
                .map(ComplaintAppeal::getReviewedBy)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, AdminUser> adminMap = adminIds.isEmpty()
                ? Collections.emptyMap()
                : adminUserRepository.findAllById(adminIds).stream()
                        .collect(Collectors.toMap(AdminUser::getAdminUsersId, a -> a));

        return appeals.stream()
                .map(appeal -> {
                    Complaint complaint = complaintMap.get(appeal.getComplaintId());
                    JobPosting jobPosting = complaint != null ? jobPostMap.get(complaint.getJobPostId()) : null;
                    Company company = jobPosting != null ? companyMap.get(jobPosting.getCompanyId()) : null;
                    AdminUser reviewer = appeal.getReviewedBy() != null ? adminMap.get(appeal.getReviewedBy()) : null;
                    return toResponse(appeal, complaint, jobPosting, company, reviewer);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResAppealDTO> getByComplaint(Long complaintId) {
        complaintRepository.findByIdAndDeletedAtIsNull(complaintId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy báo cáo"));

        return appealRepository.findByComplaintId(complaintId)
                .map(appeal -> buildSingleResponse(appeal));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResAppealDTO> getByComplaintAsEmployer(Long employerUserId, Long complaintId) {
        Complaint complaint = complaintRepository.findByIdAndDeletedAtIsNull(complaintId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy báo cáo"));

        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng liên quan"));

        Company company = companyRepository.findById(jobPosting.getCompanyId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty liên quan"));

        Long companyOwnerUserId = company.getUserId() != null ? company.getUserId() : company.getCreatedBy();
        if (!employerUserId.equals(companyOwnerUserId)) {
            throw AppException.forbidden("Bạn không có quyền xem kháng cáo của báo cáo này");
        }

        return appealRepository.findByComplaintId(complaintId)
                .map(appeal -> {
                    AdminUser reviewer = appeal.getReviewedBy() != null
                            ? adminUserRepository.findById(appeal.getReviewedBy()).orElse(null)
                            : null;
                    return toResponse(appeal, complaint, jobPosting, company, reviewer);
                });
    }

    private ResAppealDTO buildSingleResponse(ComplaintAppeal appeal) {
        Complaint complaint = complaintRepository.findByIdAndDeletedAtIsNull(appeal.getComplaintId()).orElse(null);
        JobPosting jobPosting = complaint != null
                ? jobPostingRepository.findById(complaint.getJobPostId()).orElse(null)
                : null;
        Company company = jobPosting != null
                ? companyRepository.findById(jobPosting.getCompanyId()).orElse(null)
                : null;
        AdminUser reviewer = appeal.getReviewedBy() != null
                ? adminUserRepository.findById(appeal.getReviewedBy()).orElse(null)
                : null;
        return toResponse(appeal, complaint, jobPosting, company, reviewer);
    }

    @Override
    @Transactional
    public ResAppealDTO unsuspend(Long adminUserId, Long companyId, ReqUnsuspendDTO request) {
        AdminUser adminUser = adminUserRepository.findActiveByUserId(adminUserId)
                .orElseThrow(() -> AppException.forbidden("Không tìm thấy tài khoản admin hợp lệ"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty"));

        ComplaintAppeal appeal = appealRepository.findById(request.getAppealId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy kháng cáo"));

        if (!AppealStatus.PENDING.getValue().equals(appeal.getStatus())) {
            throw AppException.badRequest("Chỉ có thể duyệt kháng cáo ở trạng thái pending");
        }

        Complaint complaint = complaintRepository.findByIdAndDeletedAtIsNull(appeal.getComplaintId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy khiếu nại liên quan"));

        // Validate: kháng cáo phải thuộc về công ty được chỉ định (qua complaint → jobPosting → companyId)
        JobPosting jobPosting = jobPostingRepository.findById(complaint.getJobPostId()).orElse(null);
        if (jobPosting == null || !companyId.equals(jobPosting.getCompanyId())) {
            throw AppException.badRequest("Kháng cáo này không thuộc về công ty được chỉ định");
        }

        // Tính tổng điểm cần gỡ từ các violation log gắn với complaint này
        Long employerId = appeal.getEmployerId();
        List<ViolationLog> logsToReverse = violationLogRepository.findByComplaintId(complaint.getId())
                .stream()
                .filter(log -> log.getPoints() != null && log.getPoints() > 0)
                .toList();

        int pointsToReverse = logsToReverse.stream().mapToInt(ViolationLog::getPoints).sum();

        // Trừ điểm vi phạm
        EmployerViolationScore score = violationScoreRepository.findByEmployerId(employerId)
                .orElseGet(() -> EmployerViolationScore.builder().employerId(employerId).totalScore(0).build());

        int newScore = Math.max(0, (score.getTotalScore() != null ? score.getTotalScore() : 0) - pointsToReverse);
        score.setTotalScore(newScore);
        violationScoreRepository.save(score);

        // Ghi log đảo điểm
        if (pointsToReverse > 0) {
            String logNote = request.getNote() != null && !request.getNote().isBlank()
                    ? request.getNote().trim()
                    : "Kháng cáo được chấp thuận — gỡ điểm vi phạm";
            violationLogRepository.save(ViolationLog.builder()
                    .employerId(employerId)
                    .jobPostId(complaint.getJobPostId())
                    .violationType("appeal_approved")
                    .points(-pointsToReverse)
                    .source(ViolationSource.ADMIN.getValue())
                    .complaintId(complaint.getId())
                    .note(logNote)
                    .createdBy(adminUser.getAdminUsersId())
                    .build());
        }

        // Mở khóa công ty và đồng bộ điểm
        company.setViolationScore(newScore);
        if (CompanyStatus.SUSPENDED.getValue().equals(company.getStatus())) {
            company.setStatus(CompanyStatus.ACTIVE.getValue());
            company.setSuspendedAt(null);
            company.setSuspendedReason(null);
        }
        company.setUpdatedBy(adminUser.getUser().getId());
        companyRepository.save(company);

        if (JobPostStatus.HIDDEN.getValue().equals(jobPosting.getStatus())) {
            jobPosting.setStatus(JobPostStatus.PUBLISHED.getValue());
            jobPosting.setUpdatedBy(adminUser.getUser().getId());
            jobPostingRepository.save(jobPosting);
        }

        // Cập nhật trạng thái kháng cáo
        String adminNote = request.getNote() != null && !request.getNote().isBlank()
                ? request.getNote().trim() : null;
        appeal.setStatus(AppealStatus.APPROVED.getValue());
        appeal.setReviewedBy(adminUser.getAdminUsersId());
        appeal.setReviewedAt(LocalDateTime.now());
        appeal.setAdminNote(adminNote);
        appealRepository.save(appeal);

        return toResponse(appeal, complaint, jobPosting, company, adminUser);
    }

    private ResAppealDTO toResponse(
            ComplaintAppeal appeal,
            Complaint complaint,
            JobPosting jobPosting,
            Company company,
            AdminUser reviewer) {

        ResAppealDTO.ComplaintInfo complaintInfo = null;
        if (complaint != null) {
            complaintInfo = ResAppealDTO.ComplaintInfo.builder()
                    .id(complaint.getId())
                    .reportCode(String.format("RP%06d", complaint.getId()))
                    .complaintType(complaint.getComplaintType())
                    .violationGroup(complaint.getViolationGroup())
                    .status(complaint.getStatus())
                    .jobPostId(complaint.getJobPostId())
                    .jobPostTitle(jobPosting != null ? jobPosting.getTitle() : null)
                    .companyName(company != null ? company.getName() : null)
                    .createdAt(complaint.getCreatedAt())
                    .build();
        }

        ResAppealDTO.AdminInfo adminInfo = reviewer == null ? null : ResAppealDTO.AdminInfo.builder()
                .adminUserId(reviewer.getAdminUsersId())
                .fullName(reviewer.getFullName())
                .build();

        return ResAppealDTO.builder()
                .id(appeal.getId())
                .employerId(appeal.getEmployerId())
                .complaint(complaintInfo)
                .content(appeal.getContent())
                .status(appeal.getStatus())
                .adminNote(appeal.getAdminNote())
                .reviewedByAdmin(adminInfo)
                .reviewedAt(appeal.getReviewedAt())
                .createdAt(appeal.getCreatedAt())
                .updatedAt(appeal.getUpdatedAt())
                .build();
    }
}
