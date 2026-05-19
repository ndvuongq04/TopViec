package com.topviec.topviec_be.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topviec.topviec_be.dto.request.ReqAddToTalentPoolDTO;
import com.topviec.topviec_be.dto.request.ReqInviteFromTalentPoolDTO;
import com.topviec.topviec_be.event.TalentPoolInviteEvent;
import com.topviec.topviec_be.dto.response.ResApplicationDTO;
import com.topviec.topviec_be.dto.response.ResCandidateSearchResultDTO;
import com.topviec.topviec_be.dto.response.ResCvPdfExportDTO;
import com.topviec.topviec_be.dto.response.ResTalentPoolCandidateDTO;
import com.topviec.topviec_be.dto.response.ResTalentPoolCandidateDetailDTO;
import com.topviec.topviec_be.dto.response.ResTalentPoolDTO;
import com.topviec.topviec_be.dto.response.ResTalentPoolInviteInfoDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Application;
import com.topviec.topviec_be.entity.CandidateProfile;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.Cvs;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.Location;
import com.topviec.topviec_be.entity.TalentPool;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.application.ApplicationStatus;
import com.topviec.topviec_be.enums.application.ApplyMethod;
import com.topviec.topviec_be.enums.cvs.CvType;
import com.topviec.topviec_be.enums.jobs.JobPostStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.ApplicationRepository;
import com.topviec.topviec_be.repository.CandidateProfileRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.CvsRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.LocationRepository;
import com.topviec.topviec_be.repository.TalentPoolRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.CvService;
import com.topviec.topviec_be.service.TalentPoolService;
import com.topviec.topviec_be.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TalentPoolServiceImpl implements TalentPoolService {

    private final TalentPoolRepository talentPoolRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CvsRepository cvsRepository;
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenService tokenService;
    private final CvService cvService;

    @Value("${app.talent-pool-invite-url}")
    private String talentPoolInviteUrl;

    @Value("${app.token.talent-pool-invite-ttl:7}")
    private long talentPoolInviteTtlDays;

    // -------------------------------------------------------------------------
    // POST — thêm UV vào talent pool
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResTalentPoolDTO addToTalentPool(Long userId, Long companyId, ReqAddToTalentPoolDTO request) {
        Long candidateUserId = request.getCandidateUserId();

        CandidateProfile candidateProfile = candidateProfileRepository.findByUserId(candidateUserId)
                .orElseThrow(() -> AppException.notFound("Ứng viên không tồn tại"));

        if (talentPoolRepository.existsByCompanyIdAndCandidateUserId(companyId, candidateUserId)) {
            throw AppException.badRequest("Ứng viên đã có trong talent pool của công ty");
        }

        TalentPool entry = TalentPool.builder()
                .companyId(companyId)
                .candidateUserId(candidateUserId)
                .addedBy(userId)
                .source(request.getSource().getValue())
                .note(request.getNote())
                .build();

        TalentPool saved = talentPoolRepository.save(entry);

        return ResTalentPoolDTO.builder()
                .id(saved.getId())
                .companyId(saved.getCompanyId())
                .candidateUserId(saved.getCandidateUserId())
                .candidateName(candidateProfile.getFullName())
                .candidateAvatarUrl(candidateProfile.getAvatarUrl())
                .addedBy(saved.getAddedBy())
                .source(saved.getSource())
                .note(saved.getNote())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // -------------------------------------------------------------------------
    // GET — danh sách UV trong talent pool
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getTalentPool(Long companyId, String source, String search, Pageable pageable) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;

        Page<TalentPool> page = talentPoolRepository.findByCompanyWithFilter(
                companyId, source, searchParam, pageable);

        List<TalentPool> entries = page.getContent();

        // Batch load — tránh N+1
        List<Long> candidateUserIds = entries.stream()
                .map(TalentPool::getCandidateUserId)
                .distinct()
                .toList();

        Map<Long, CandidateProfile> profileMap = candidateUserIds.isEmpty() ? Map.of()
                : candidateProfileRepository.findByUserIdIn(candidateUserIds).stream()
                        .collect(Collectors.toMap(CandidateProfile::getUserId, cp -> cp));

        Map<Long, User> userMap = candidateUserIds.isEmpty() ? Map.of()
                : userRepository.findAllById(candidateUserIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

        Set<Long> locationIds = profileMap.values().stream()
                .filter(cp -> cp.getPreferredLocationId() != null)
                .map(cp -> cp.getPreferredLocationId().longValue())
                .collect(Collectors.toSet());

        Map<Integer, String> locationNameMap = locationIds.isEmpty() ? Map.of()
                : locationRepository.findAllById(locationIds).stream()
                        .collect(Collectors.toMap(l -> l.getId().intValue(), Location::getName));

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(entries.stream()
                .map(tp -> toListResponse(tp, profileMap, userMap, locationNameMap))
                .toList());

        return result;
    }

    // -------------------------------------------------------------------------
    // GET — chi tiết ứng viên trong talent pool
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResTalentPoolCandidateDetailDTO getTalentPoolCandidateDetail(Long companyId, Long talentPoolId) {
        TalentPool talentPool = talentPoolRepository.findById(talentPoolId)
                .filter(tp -> tp.getDeletedAt() == null)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy ứng viên trong talent pool"));

        if (!talentPool.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền xem thông tin này");
        }

        Long candidateUserId = talentPool.getCandidateUserId();

        CandidateProfile profile = candidateProfileRepository.findByUserId(candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ ứng viên"));

        User candidateUser = userRepository.findById(candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản ứng viên"));

        String addedByName = userRepository.findById(talentPool.getAddedBy())
                .map(User::getEmail)
                .orElse(null);

        String locationName = null;
        if (profile.getPreferredLocationId() != null) {
            locationName = locationRepository.findById(profile.getPreferredLocationId().longValue())
                    .map(Location::getName)
                    .orElse(null);
        }

        Cvs defaultCv = cvsRepository.findDefaultByUserId(candidateUserId).orElse(null);

        return ResTalentPoolCandidateDetailDTO.builder()
                // talent pool entry
                .talentPoolId(talentPool.getId())
                .source(talentPool.getSource())
                .note(talentPool.getNote())
                .addedAt(talentPool.getCreatedAt())
                .addedBy(talentPool.getAddedBy())
                .addedByName(addedByName)
                // thông tin cơ bản
                .candidateUserId(candidateUserId)
                .fullName(profile.getFullName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .gender(profile.getGender())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .personalWebsite(profile.getPersonalWebsite())
                .profileCompletionPct(profile.getProfileCompletionPct())
                .jobSeekingStatus(profile.getJobSeekingStatus())
                // thông tin có thể ẩn
                .phone(profile.getHidePhone() ? null : profile.getPhoneDisplay())
                .phoneHidden(profile.getHidePhone())
                .email(profile.getHideEmail() ? null : candidateUser.getEmail())
                .emailHidden(profile.getHideEmail())
                .dateOfBirth(profile.getHideDateOfBirth() ? null : profile.getDateOfBirth())
                .dateOfBirthHidden(profile.getHideDateOfBirth())
                // mong muốn công việc
                .preferredJobTitle(profile.getPreferredJobTitle())
                .preferredWorkType(profile.getPreferredWorkType())
                .preferredLocationId(profile.getPreferredLocationId())
                .preferredLocationName(locationName)
                .expectedSalaryMin(profile.getHideExpectedSalary() ? null : profile.getExpectedSalaryMin())
                .expectedSalaryMax(profile.getHideExpectedSalary() ? null : profile.getExpectedSalaryMax())
                .salaryNegotiable(profile.getHideExpectedSalary() ? null : profile.getSalaryNegotiable())
                .salaryHidden(profile.getHideExpectedSalary())
                // CV mặc định
                .defaultCv(defaultCv != null ? toDefaultCvDTO(defaultCv) : null)
                .build();
    }

    // -------------------------------------------------------------------------
    // GET — chi tiết UV chưa lưu vào talent pool (theo candidateUserId)
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResTalentPoolCandidateDetailDTO getCandidateDetail(Long companyId, Long candidateUserId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ ứng viên"));

        User candidateUser = userRepository.findById(candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản ứng viên"));

        // Kiểm tra xem UV đã có trong talent pool chưa → nếu có thì bổ sung thông tin
        TalentPool talentPool = talentPoolRepository
                .findByCompanyIdAndCandidateUserId(companyId, candidateUserId)
                .orElse(null);

        String addedByName = null;
        if (talentPool != null && talentPool.getAddedBy() != null) {
            addedByName = userRepository.findById(talentPool.getAddedBy())
                    .map(User::getEmail)
                    .orElse(null);
        }

        String locationName = null;
        if (profile.getPreferredLocationId() != null) {
            locationName = locationRepository.findById(profile.getPreferredLocationId().longValue())
                    .map(Location::getName)
                    .orElse(null);
        }

        Cvs defaultCv = cvsRepository.findDefaultByUserId(candidateUserId).orElse(null);

        return ResTalentPoolCandidateDetailDTO.builder()
                // talent pool entry (null nếu chưa lưu)
                .talentPoolId(talentPool != null ? talentPool.getId() : null)
                .source(talentPool != null ? talentPool.getSource() : null)
                .note(talentPool != null ? talentPool.getNote() : null)
                .addedAt(talentPool != null ? talentPool.getCreatedAt() : null)
                .addedBy(talentPool != null ? talentPool.getAddedBy() : null)
                .addedByName(addedByName)
                // thông tin cơ bản
                .candidateUserId(candidateUserId)
                .fullName(profile.getFullName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .gender(profile.getGender())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .personalWebsite(profile.getPersonalWebsite())
                .profileCompletionPct(profile.getProfileCompletionPct())
                .jobSeekingStatus(profile.getJobSeekingStatus())
                // thông tin có thể ẩn
                .phone(profile.getHidePhone() ? null : profile.getPhoneDisplay())
                .phoneHidden(profile.getHidePhone())
                .email(profile.getHideEmail() ? null : candidateUser.getEmail())
                .emailHidden(profile.getHideEmail())
                .dateOfBirth(profile.getHideDateOfBirth() ? null : profile.getDateOfBirth())
                .dateOfBirthHidden(profile.getHideDateOfBirth())
                // mong muốn công việc
                .preferredJobTitle(profile.getPreferredJobTitle())
                .preferredWorkType(profile.getPreferredWorkType())
                .preferredLocationId(profile.getPreferredLocationId())
                .preferredLocationName(locationName)
                .expectedSalaryMin(profile.getHideExpectedSalary() ? null : profile.getExpectedSalaryMin())
                .expectedSalaryMax(profile.getHideExpectedSalary() ? null : profile.getExpectedSalaryMax())
                .salaryNegotiable(profile.getHideExpectedSalary() ? null : profile.getSalaryNegotiable())
                .salaryHidden(profile.getHideExpectedSalary())
                // CV mặc định
                .defaultCv(defaultCv != null ? toDefaultCvDTO(defaultCv) : null)
                .build();
    }

    // -------------------------------------------------------------------------
    // PATCH — cập nhật note cho UV trong talent pool
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void updateNote(Long companyId, Long talentPoolId, String note) {
        TalentPool talentPool = talentPoolRepository.findById(talentPoolId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy ứng viên trong talent pool"));

        if (!talentPool.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền chỉnh sửa thông tin này");
        }

        talentPool.setNote(note);
        talentPoolRepository.save(talentPool);
    }

    // -------------------------------------------------------------------------
    // DELETE — xóa UV khỏi talent pool
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void removeFromTalentPool(Long companyId, Long talentPoolId) {
        TalentPool talentPool = talentPoolRepository.findById(talentPoolId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy ứng viên trong talent pool"));

        if (!talentPool.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền xóa ứng viên này khỏi talent pool");
        }

        talentPoolRepository.delete(talentPool);
    }

    // -------------------------------------------------------------------------
    // POST — mời UV trong talent pool ứng tuyển vào tin tuyển dụng
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResApplicationDTO invite(Long employerUserId, Long companyId, Long talentPoolId,
            ReqInviteFromTalentPoolDTO request) {

        TalentPool talentPool = talentPoolRepository.findById(talentPoolId)
                .filter(tp -> tp.getDeletedAt() == null)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy ứng viên trong talent pool"));

        if (!talentPool.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thực hiện thao tác này");
        }

        JobPosting job = jobPostingRepository.findByIdAndDeletedAtIsNull(request.getJobPostId())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!job.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Tin tuyển dụng không thuộc về công ty của bạn");
        }

        if (!JobPostStatus.PUBLISHED.getValue().equals(job.getStatus())
                && !JobPostStatus.INTERVIEWING.getValue().equals(job.getStatus())) {
            throw AppException.badRequest("Chỉ có thể mời ứng viên vào tin đang tuyển dụng");
        }

        Long candidateUserId = talentPool.getCandidateUserId();

        User candidateUser = userRepository.findById(candidateUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản ứng viên"));
        CandidateProfile candidateProfile = candidateProfileRepository.findByUserId(candidateUserId).orElse(null);
        String candidateName = candidateProfile != null ? candidateProfile.getFullName() : candidateUser.getEmail();
        Company company = companyRepository.findById(job.getCompanyId()).orElse(null);
        String companyName = company != null ? company.getName() : "";

        // Kiểm tra nếu UV đã có đơn không phải WITHDRAWN
        Optional<Application> existing = applicationRepository
                .findByJobPostIdAndCandidateUserIdAndDeletedAtIsNull(request.getJobPostId(), candidateUserId);
        if (existing.isPresent()) {
            Application ex = existing.get();
            if (!ApplicationStatus.WITHDRAWN.getValue().equals(ex.getStatus())) {
                throw AppException.conflict("Ứng viên đã có đơn ứng tuyển vào tin tuyển dụng này rồi");
            }
            ex.setStatus(ApplicationStatus.INVITED.getValue());
            ex.setApplyMethod(ApplyMethod.INVITED.getValue());
            ex.setWithdrawnAt(null);
            ex.setWithdrawalReason(null);
            try {
                ex.setCvSnapshot(objectMapper.writeValueAsString(job));
            } catch (Exception e) {
                ex.setCvSnapshot("{}");
            }
            Application saved = applicationRepository.save(ex);
            publishInviteEvent(candidateUser.getEmail(), candidateName, companyName, job, saved.getId());
            return toApplicationResponse(saved, job, company);
        }

        Cvs defaultCv = cvsRepository.findDefaultByUserId(candidateUserId)
                .orElseThrow(() -> AppException.badRequest("Ứng viên chưa có CV mặc định"));

        String cvSnapshot;
        try {
            cvSnapshot = objectMapper.writeValueAsString(job);
        } catch (Exception e) {
            cvSnapshot = "{}";
        }

        Application application = applicationRepository.save(Application.builder()
                .candidateUserId(candidateUserId)
                .jobPostId(job.getId())
                .cvId(defaultCv.getId())
                .cvSnapshot(cvSnapshot)
                .status(ApplicationStatus.INVITED.getValue())
                .applyMethod(ApplyMethod.INVITED.getValue())
                .build());

        publishInviteEvent(candidateUser.getEmail(), candidateName, companyName, job, application.getId());
        return toApplicationResponse(application, job, company);
    }

    private void publishInviteEvent(String email, String candidateName, String companyName,
            JobPosting job, Long applicationId) {
        String token = tokenService.generateTalentPoolInviteToken(
                applicationId, job.getId(), Duration.ofDays(talentPoolInviteTtlDays));
        String jobLink = talentPoolInviteUrl + "?token=" + token;
        eventPublisher.publishEvent(new TalentPoolInviteEvent(email, candidateName, companyName, job.getTitle(), jobLink));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private ResTalentPoolCandidateDetailDTO.DefaultCvDTO toDefaultCvDTO(Cvs cv) {
        if (cv.getCvType() == CvType.online
                && (cv.getPdfUrl() == null || cv.getPdfUrl().isBlank() || Boolean.TRUE.equals(cv.getPdfDirty()))) {
            ResCvPdfExportDTO export = cvService.exportOnlineCvPdf(cv.getUserId(), cv.getId());
            cv.setPdfUrl(export.getPdfUrl());
            cv.setPdfDirty(export.getPdfDirty());
        }
        return ResTalentPoolCandidateDetailDTO.DefaultCvDTO.builder()
                .cvId(cv.getId())
                .title(cv.getTitle())
                .cvType(cv.getCvType())
                .fileUrl(cv.getFileUrl())
                .pdfUrl(cv.getPdfUrl())
                .pdfDirty(cv.getPdfDirty())
                .visibility(cv.getVisibility())
                .viewCount(cv.getViewCount())
                .createdAt(cv.getCreatedAt())
                .build();
    }

    private ResApplicationDTO toApplicationResponse(Application a, JobPosting job, Company company) {
        return ResApplicationDTO.builder()
                .id(a.getId())
                .jobPostId(a.getJobPostId())
                .candidateUserId(a.getCandidateUserId())
                .cvId(a.getCvId())
                .status(a.getStatus())
                .applyMethod(a.getApplyMethod())
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

    private ResTalentPoolCandidateDTO toListResponse(
            TalentPool tp,
            Map<Long, CandidateProfile> profileMap,
            Map<Long, User> userMap,
            Map<Integer, String> locationNameMap) {

        CandidateProfile profile = profileMap.get(tp.getCandidateUserId());
        User user = userMap.get(tp.getCandidateUserId());

        return ResTalentPoolCandidateDTO.builder()
                .talentPoolId(tp.getId())
                .source(tp.getSource())
                .note(tp.getNote())
                .addedAt(tp.getCreatedAt())
                .candidateUserId(tp.getCandidateUserId())
                .candidateName(profile != null ? profile.getFullName() : null)
                .candidateEmail(user != null ? user.getEmail() : null)
                .candidateAvatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .preferredJobTitle(profile != null ? profile.getPreferredJobTitle() : null)
                .preferredWorkType(profile != null ? profile.getPreferredWorkType() : null)
                .preferredLocationId(profile != null ? profile.getPreferredLocationId() : null)
                .preferredLocationName(profile != null && profile.getPreferredLocationId() != null
                        ? locationNameMap.get(profile.getPreferredLocationId())
                        : null)
                .expectedSalaryMin(profile != null ? profile.getExpectedSalaryMin() : null)
                .expectedSalaryMax(profile != null ? profile.getExpectedSalaryMax() : null)
                .salaryNegotiable(profile != null ? profile.getSalaryNegotiable() : null)
                .jobSeekingStatus(profile != null ? profile.getJobSeekingStatus() : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchCandidates(Long companyId, Integer locationId, Pageable pageable) {
        Page<CandidateProfile> page = candidateProfileRepository.searchCandidatesByLocation(locationId, pageable);

        List<CandidateProfile> profiles = page.getContent();
        List<Long> userIds = profiles.stream().map(CandidateProfile::getUserId).toList();

        // Batch load location names
        List<Long> locationIds = profiles.stream()
                .map(CandidateProfile::getPreferredLocationId)
                .filter(Objects::nonNull)
                .distinct()
                .map(Integer::longValue)
                .toList();
        Map<Integer, String> locationNameMap = locationIds.isEmpty()
                ? Collections.emptyMap()
                : locationRepository.findAllById(locationIds).stream()
                        .collect(Collectors.toMap(l -> l.getId().intValue(), Location::getName));

        // Batch check talent pool membership
        Set<Long> poolMemberIds = userIds.isEmpty()
                ? Collections.emptySet()
                : new HashSet<>(talentPoolRepository.findExistingCandidateUserIds(companyId, userIds));

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(profiles.stream()
                .map(cp -> ResCandidateSearchResultDTO.builder()
                        .candidateUserId(cp.getUserId())
                        .fullName(cp.getFullName())
                        .avatarUrl(cp.getAvatarUrl())
                        .preferredJobTitle(cp.getPreferredJobTitle())
                        .preferredWorkType(cp.getPreferredWorkType())
                        .preferredLocationId(cp.getPreferredLocationId())
                        .preferredLocationName(cp.getPreferredLocationId() != null
                                ? locationNameMap.get(cp.getPreferredLocationId())
                                : null)
                        .expectedSalaryMin(cp.getExpectedSalaryMin())
                        .expectedSalaryMax(cp.getExpectedSalaryMax())
                        .salaryNegotiable(cp.getSalaryNegotiable())
                        .jobSeekingStatus(cp.getJobSeekingStatus())
                        .alreadyInPool(poolMemberIds.contains(cp.getUserId()))
                        .build())
                .toList());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ResTalentPoolInviteInfoDTO verifyInviteToken(String token) {
        String payload = tokenService.verifyTalentPoolInviteToken(token);
        String[] parts = payload.split(":");
        Long applicationId = Long.parseLong(parts[0]);
        Long jobPostId = Long.parseLong(parts[1]);

        JobPosting job = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Tin tuyển dụng không còn tồn tại"));
        Company company = companyRepository.findById(job.getCompanyId()).orElse(null);

        return ResTalentPoolInviteInfoDTO.builder()
                .applicationId(applicationId)
                .jobPostId(jobPostId)
                .jobTitle(job.getTitle())
                .companyName(company != null ? company.getName() : "")
                .companyLogoUrl(company != null ? company.getLogoUrl() : null)
                .build();
    }
}
