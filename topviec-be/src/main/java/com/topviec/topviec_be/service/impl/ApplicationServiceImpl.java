package com.topviec.topviec_be.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topviec.topviec_be.dto.request.ReqApplyJobDTO;
import com.topviec.topviec_be.dto.request.ReqBulkApplyDTO;
import com.topviec.topviec_be.dto.request.ReqWithdrawApplicationDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateApplicationCvDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateApplicationDTO;
import com.topviec.topviec_be.dto.response.ResApplicationDTO;
import com.topviec.topviec_be.dto.response.ResEmployerApplicationDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Application;
import com.topviec.topviec_be.entity.CandidateProfile;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.Cvs;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.application.ApplicationStatus;
import com.topviec.topviec_be.enums.application.ApplyMethod;
import com.topviec.topviec_be.enums.cvs.CvType;
import com.topviec.topviec_be.enums.jobs.JobPostStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.ApplicationRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.CvsRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.CandidateProfileRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.ApplicationService;
import com.topviec.topviec_be.service.CvService;
import com.topviec.topviec_be.util.ChangeTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;
    private final CvsRepository cvsRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final CvService cvService;

    // -------------------------------------------------------------------------
    // CN-UV-010: Nộp đơn đầy đủ
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResApplicationDTO apply(Long candidateUserId, Long jobPostId, ReqApplyJobDTO request) {
        JobPosting job = findAcceptingJobOrThrow(jobPostId);
        Cvs cv = findOwnedCvForApplication(candidateUserId, request.getCvId());

        Application application = getOrInitializeApplication(
                candidateUserId, jobPostId, cv.getId(),
                ApplyMethod.NORMAL.getValue(), job);

        return toResponse(applicationRepository.save(application), job,
                companyRepository.findById(job.getCompanyId()).orElse(null));
    }

    // -------------------------------------------------------------------------
    // CN-UV-011: Ứng tuyển nhanh
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResApplicationDTO quickApply(Long candidateUserId, Long jobPostId) {
        JobPosting job = findAcceptingJobOrThrow(jobPostId);

        // Lấy CV mặc định
        Cvs defaultCv = cvsRepository.findDefaultByUserId(candidateUserId)
                .orElseThrow(() -> AppException.badRequest(
                        "Bạn chưa có CV mặc định. Vui lòng đặt một CV làm mặc định trước khi ứng tuyển nhanh"));

        Application application = getOrInitializeApplication(
                candidateUserId, jobPostId, defaultCv.getId(),
                ApplyMethod.QUICK.getValue(), job);

        return toResponse(applicationRepository.save(application), job,
                companyRepository.findById(job.getCompanyId()).orElse(null));
    }

    // -------------------------------------------------------------------------
    // CN-UV-012: Ứng tuyển hàng loạt (tối đa 10)
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public List<ResApplicationDTO> bulkApply(Long candidateUserId, ReqBulkApplyDTO request) {
        if (request.getJobPostIds() == null || request.getJobPostIds().isEmpty()) {
            throw AppException.badRequest("Danh sách tin tuyển dụng không được trống");
        }
        Cvs cv = findOwnedCvForApplication(candidateUserId, request.getCvId());

        List<Long> distinctJobIds = request.getJobPostIds().stream().distinct().toList();

        if (distinctJobIds.size() > 10) {
            throw AppException.badRequest("Chỉ được ứng tuyển tối đa 10 tin cùng lúc");
        }

        List<ResApplicationDTO> results = new ArrayList<>();

        for (Long jobPostId : distinctJobIds) {
            JobPosting job = findAcceptingJobOrThrow(jobPostId);

            Application application = getOrInitializeApplication(
                    candidateUserId, jobPostId, cv.getId(),
                    ApplyMethod.BULK.getValue(), job);

            Company company = companyRepository.findById(job.getCompanyId()).orElse(null);
            results.add(toResponse(applicationRepository.save(application), job, company));
        }

        return results;
    }

    // -------------------------------------------------------------------------
    // CN-UV-013: Theo dõi trạng thái đơn
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getMyApplications(Long candidateUserId, String status, Pageable pageable) {
        Page<Application> applicationPage = applicationRepository
                .findByCandidate(candidateUserId, status, pageable);

        List<Application> applications = applicationPage.getContent();

        // Batch query companies từ jobPosting (vẫn cần vì JobPosting chưa có relation
        // Company)
        Map<Long, Company> companyMap = companyRepository
                .findAllById(applications.stream()
                        .map(a -> a.getJobPosting().getCompanyId())
                        .distinct().toList())
                .stream().collect(Collectors.toMap(Company::getId, c -> c));

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(applicationPage.getTotalPages());
        meta.setTotals(applicationPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(applications.stream()
                .map(a -> toResponse(a, companyMap))
                .toList());

        return result;
    }

    // -------------------------------------------------------------------------
    // Trang "Lịch PV của tôi" — đơn có ít nhất 1 lịch PV
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ResApplicationDTO> getMyApplicationsWithInterviews(Long candidateUserId) {
        List<Application> applications = applicationRepository
                .findWithInterviewsByCandidate(candidateUserId);

        Map<Long, Company> companyMap = companyRepository
                .findAllById(applications.stream()
                        .map(a -> a.getJobPosting().getCompanyId())
                        .distinct().toList())
                .stream().collect(Collectors.toMap(Company::getId, c -> c));

        return applications.stream()
                .map(a -> toResponse(a, companyMap))
                .toList();
    }

    // -------------------------------------------------------------------------
    // CN-UV-015: Rút đơn
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResApplicationDTO withdraw(Long candidateUserId, Long applicationId,
            ReqWithdrawApplicationDTO request) {
        Application application = applicationRepository
                .findByIdAndCandidateUserId(applicationId, candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn ứng tuyển"));

        ApplicationStatus current = ApplicationStatus.fromValue(application.getStatus());

        if (!current.isWithdrawable()) {
            throw AppException.badRequest(
                    "Không thể rút đơn ở trạng thái: " + current.getValue()
                            + ". Chỉ có thể rút khi đang ở trạng thái pending hoặc seen");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN.getValue());
        application.setWithdrawnAt(LocalDateTime.now());
        application.setWithdrawalReason(request.getWithdrawalReason());

        Application saved = applicationRepository.save(application);

        // Dùng relation trực tiếp thay vì query lại
        Company company = saved.getJobPosting() != null
                ? companyRepository.findById(saved.getJobPosting().getCompanyId()).orElse(null)
                : null;

        return toResponse(saved, company);
    }

    // -------------------------------------------------------------------------
    // Cập nhật CV cho đơn ứng tuyển (chỉ áp dụng khi PENDING)
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResApplicationDTO updateApplicationCv(Long candidateUserId, Long applicationId,
            ReqUpdateApplicationCvDTO request) {
        Application application = applicationRepository
                .findByIdAndCandidateUserId(applicationId, candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn ứng tuyển"));

        if (!ApplicationStatus.PENDING.getValue().equals(application.getStatus())) {
            throw AppException
                    .badRequest("Chỉ được phép thay đổi CV khi đơn ứng tuyển ở trạng thái chờ xử lý (PENDING)");
        }

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(application);

        Cvs cv = findOwnedCvForApplication(candidateUserId, request.getCvId());

        application.setCvId(cv.getId());
        Application saved = applicationRepository.save(application);

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        Company company = saved.getJobPosting() != null
                ? companyRepository.findById(saved.getJobPosting().getCompanyId()).orElse(null)
                : null;

        return toResponse(saved, company);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private JobPosting findAcceptingJobOrThrow(Long jobPostId) {
        JobPosting job = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        String status = job.getStatus();
        if (!JobPostStatus.PUBLISHED.getValue().equals(status)
                && !JobPostStatus.INTERVIEWING.getValue().equals(status)) {
            throw AppException.badRequest("Tin tuyển dụng không còn nhận hồ sơ");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDateTime.now())) {
            throw AppException.badRequest("Tin tuyển dụng đã hết hạn nộp hồ sơ");
        }

        return job;
    }

    private Cvs findOwnedCvForApplication(Long candidateUserId, Long cvId) {
        if (cvId == null) {
            throw AppException.badRequest("Vui lòng chọn CV để ứng tuyển");
        }

        Cvs cv = cvsRepository.findById(cvId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        if (!cv.getUserId().equals(candidateUserId)) {
            throw AppException.forbidden("CV này không thuộc về bạn");
        }

        if (cv.getDeletedAt() != null) {
            throw AppException.badRequest("CV này đã bị xóa");
        }

        if (cv.getCvType() == com.topviec.topviec_be.enums.cvs.CvType.online
                && (cv.getPdfUrl() == null || cv.getPdfUrl().isBlank() || Boolean.TRUE.equals(cv.getPdfDirty()))) {
            cvService.exportOnlineCvPdf(candidateUserId, cv.getId());
            cv = cvsRepository.findById(cvId)
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));
        }

        return cv;
    }

    private Application getOrInitializeApplication(Long candidateUserId, Long jobPostId,
            Long cvId, String applyMethod, JobPosting job) {

        java.util.Optional<Application> existingOpt = applicationRepository
                .findByJobPostIdAndCandidateUserIdAndDeletedAtIsNull(jobPostId, candidateUserId);

        if (existingOpt.isPresent()) {
            Application existing = existingOpt.get();
            if (!ApplicationStatus.WITHDRAWN.getValue().equals(existing.getStatus())) {
                throw AppException.badRequest("Bạn đã ứng tuyển vào tin tuyển dụng này rồi");
            }

            // Cho phép ứng tuyển lại nếu trước đó đã rút đơn
            existing.setCvId(cvId);
            existing.setApplyMethod(applyMethod);
            existing.setStatus(ApplicationStatus.PENDING.getValue());
            try {
                existing.setCvSnapshot(objectMapper.writeValueAsString(job));
            } catch (Exception e) {
                existing.setCvSnapshot("{}");
            }
            existing.setWithdrawnAt(null);
            existing.setWithdrawalReason(null);

            return existing;
        }

        String cvSnapshot = null;
        try {
            cvSnapshot = objectMapper.writeValueAsString(job);
        } catch (Exception e) {
            cvSnapshot = "{}";
        }

        return Application.builder()
                .candidateUserId(candidateUserId)
                .jobPostId(jobPostId)
                .cvId(cvId)
                .cvSnapshot(cvSnapshot)
                .status(ApplicationStatus.PENDING.getValue())
                .applyMethod(applyMethod)
                .build();
    }

    private ResApplicationDTO toResponse(Application a, JobPosting job, Company company) {
        return ResApplicationDTO.builder()
                .id(a.getId())
                .jobPostId(a.getJobPostId())
                .candidateUserId(a.getCandidateUserId())
                .cvId(a.getCvId())
                .status(a.getStatus())
                .applyMethod(a.getApplyMethod())
                .withdrawalReason(a.getWithdrawalReason())
                .withdrawnAt(a.getWithdrawnAt())
                .rejectedAt(a.getRejectedAt())
                .rejectionReason(a.getRejectionReason())
                .expiredAt(a.getExpiredAt())
                .hiredAt(a.getHiredAt())
                .viewedAt(a.getViewedAt())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .jobPosting(job == null ? null
                        : ResApplicationDTO.JobInfo.builder()
                                .id(job.getId())
                                .title(job.getTitle())
                                .slug(job.getSlug())
                                .status(job.getStatus())
                                .deadline(job.getDeadline())
                                .company(company == null ? null
                                        : ResApplicationDTO.CompanyInfo.builder()
                                                .id(company.getId())
                                                .name(company.getName())
                                                .logoUrl(company.getLogoUrl())
                                                .isBrandVerified(company.getIsBrandVerified())
                                                .build())
                                .build())
                .build();
    }

    // Dùng cho getMyApplications (từ map)
    private ResApplicationDTO toResponse(Application a, Map<Long, Company> companyMap) {
        JobPosting job = a.getJobPosting();
        Company company = job != null ? companyMap.get(job.getCompanyId()) : null;
        return toResponse(a, job, company);
    }

    // Dùng cho withdraw (relation đã được fetch)
    private ResApplicationDTO toResponse(Application a, Company company) {
        return toResponse(a, a.getJobPosting(), company);
    }

    // -------------------------------------------------------------------------
    // API cho Employer
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getApplicationsByJobPost(Long userId, Long companyId, Long jobPostId, String status,
            String search, Pageable pageable) {
        JobPosting job = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!job.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền xem hồ sơ của tin tuyển dụng này");
        }

        String searchTrim = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<Application> applicationPage = applicationRepository.findByJobPost(jobPostId, status, searchTrim,
                pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(applicationPage.getTotalPages());
        meta.setTotals(applicationPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(applicationPage.getContent().stream()
                .map(application -> toEmployerResponse(application, false))
                .toList());

        return result;
    }

    @Override
    @Transactional
    public ResEmployerApplicationDTO getApplicationDetailByEmployer(Long userId, Long companyId, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn ứng tuyển"));

        JobPosting job = application.getJobPosting();
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền xem đơn ứng tuyển này");
        }

        // Chuyển status PENDING -> SEEN
        if (ApplicationStatus.PENDING.getValue().equals(application.getStatus())) {
            application.setStatus(ApplicationStatus.SEEN.getValue());
            application.setViewedAt(LocalDateTime.now());
            application = applicationRepository.save(application);
        }

        return toEmployerResponse(application);
    }

    @Override
    @Transactional
    public ResEmployerApplicationDTO updateApplication(Long userId, Long companyId, Long applicationId,
            ReqUpdateApplicationDTO request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn ứng tuyển"));

        JobPosting job = application.getJobPosting();
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền sửa đơn ứng tuyển này");
        }

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(application);

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            ApplicationStatus currentStatus = ApplicationStatus.fromValue(application.getStatus());
            ApplicationStatus nextStatus = ApplicationStatus.fromValue(request.getStatus());

            if (!currentStatus.canTransitionTo(nextStatus)) {
                throw AppException.badRequest("Không thể chuyển trạng thái từ " + currentStatus.getValue() + " sang "
                        + nextStatus.getValue());
            }

            application.setStatus(nextStatus.getValue());

            if (nextStatus == ApplicationStatus.REJECTED) {
                application.setRejectedAt(LocalDateTime.now());
            } else if (nextStatus == ApplicationStatus.HIRED) {
                application.setHiredAt(LocalDateTime.now());
            }
        }

        if (request.getNote() != null) {
            application.setRecruiterNote(request.getNote());
        }
        if (request.getRating() != null) {
            application.setRecruiterRating(request.getRating());
        }
        if (request.getTags() != null) {
            application.setRecruiterTags(request.getTags());
        }

        Application saved = applicationRepository.save(application);

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        return toEmployerResponse(saved);
    }

    @Override
    @Transactional
    public ResApplicationDTO acceptInvite(Long candidateUserId, Long applicationId) {
        Application application = applicationRepository
                .findByIdAndCandidateUserId(applicationId, candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn ứng tuyển"));

        if (!ApplicationStatus.INVITED.getValue().equals(application.getStatus())) {
            throw AppException.badRequest("Chỉ có thể chấp nhận lời mời ở trạng thái invited");
        }

        application.setStatus(ApplicationStatus.PENDING.getValue());
        Application saved = applicationRepository.save(application);

        Company company = saved.getJobPosting() != null
                ? companyRepository.findById(saved.getJobPosting().getCompanyId()).orElse(null)
                : null;

        return toResponse(saved, company);
    }

    @Override
    @Transactional
    public ResApplicationDTO declineInvite(Long candidateUserId, Long applicationId) {
        Application application = applicationRepository
                .findByIdAndCandidateUserId(applicationId, candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn ứng tuyển"));

        if (!ApplicationStatus.INVITED.getValue().equals(application.getStatus())) {
            throw AppException.badRequest("Chỉ có thể từ chối lời mời ở trạng thái invited");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN.getValue());
        application.setWithdrawnAt(LocalDateTime.now());
        Application saved = applicationRepository.save(application);

        Company company = saved.getJobPosting() != null
                ? companyRepository.findById(saved.getJobPosting().getCompanyId()).orElse(null)
                : null;

        return toResponse(saved, company);
    }

    private ResEmployerApplicationDTO toEmployerResponse(Application a) {
        return toEmployerResponse(a, true);
    }

    private ResEmployerApplicationDTO toEmployerResponse(Application a, boolean ensureFreshPdf) {
        User user = null;
        CandidateProfile profile = null;
        Cvs cv = null;

        if (a.getCandidateUserId() != null) {
            user = userRepository.findById(a.getCandidateUserId()).orElse(null);
            profile = candidateProfileRepository.findByUserId(a.getCandidateUserId()).orElse(null);
        }

        if (a.getCvId() != null) {
            cv = cvsRepository.findById(a.getCvId()).orElse(null);
            if (ensureFreshPdf
                    && cv != null
                    && cv.getCvType() == CvType.online
                    && (cv.getPdfUrl() == null || cv.getPdfUrl().isBlank() || Boolean.TRUE.equals(cv.getPdfDirty()))) {
                cvService.exportOnlineCvPdf(cv.getUserId(), cv.getId());
                cv = cvsRepository.findById(a.getCvId()).orElse(cv);
            }
        }

        JobPosting job = a.getJobPosting();

        return ResEmployerApplicationDTO.builder()
                .id(a.getId())
                .jobPostId(a.getJobPostId())
                .jobTitle(job != null ? job.getTitle() : null)
                .candidateUserId(a.getCandidateUserId())
                .candidateName(
                        profile != null ? profile.getFullName() : (user != null ? "User " + user.getId() : "Unknown"))
                .candidateEmail(user != null ? user.getEmail() : null)
                .candidatePhone(profile != null ? profile.getPhoneDisplay() : null)
                .candidateAvatar(profile != null ? profile.getAvatarUrl() : null)
                .cvId(a.getCvId())
                .cvType(cv != null ? cv.getCvType() : null)
                .cvFileUrl(cv != null ? cv.getFileUrl() : null)
                .cvPdfUrl(cv != null ? cv.getPdfUrl() : null)
                .cvPdfDirty(cv != null ? cv.getPdfDirty() : null)
                .cvPreviewUrl(resolveCvPreviewUrl(cv))
                .status(a.getStatus())
                .applyMethod(a.getApplyMethod())
                .recruiterRating(a.getRecruiterRating())
                .recruiterNote(a.getRecruiterNote())
                .recruiterTags(a.getRecruiterTags())
                .viewedAt(a.getViewedAt())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private String resolveCvPreviewUrl(Cvs cv) {
        if (cv == null) {
            return null;
        }
        if (cv.getCvType() == CvType.online) {
            return cv.getPdfUrl();
        }
        return cv.getFileUrl() != null ? cv.getFileUrl() : cv.getPdfUrl();
    }
}
