package com.topviec.topviec_be.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topviec.topviec_be.dto.request.ReqCreateJobPostingDTO;
import com.topviec.topviec_be.dto.request.ReqJobPostLocationDTO;
import com.topviec.topviec_be.dto.request.ReqJobPostSkillDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateJobPostingDTO;
import com.topviec.topviec_be.dto.response.ResJobPostingDetail;
import com.topviec.topviec_be.dto.response.ResJobPostingSummary;
import com.topviec.topviec_be.dto.response.ResJobPostSkillDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.Industry;
import com.topviec.topviec_be.entity.JobPostEditLog;
import com.topviec.topviec_be.entity.JobPostLocation;
import com.topviec.topviec_be.entity.JobPostSkill;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.Level;
import com.topviec.topviec_be.entity.Skill;
import com.topviec.topviec_be.enums.jobs.EditType;
import com.topviec.topviec_be.enums.jobs.JobPostStatus;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.entity.JobPostAssignment;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.ApplicationRepository;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.IndustryRepository;
import com.topviec.topviec_be.repository.InterviewRoundRepository;
import com.topviec.topviec_be.repository.JobPostAssignmentRepository;
import com.topviec.topviec_be.repository.JobPostEditLogRepository;
import com.topviec.topviec_be.repository.JobPostLocationRepository;
import com.topviec.topviec_be.repository.JobPostSkillRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.LevelRepository;
import com.topviec.topviec_be.repository.SkillRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.JobPostingService;
import com.topviec.topviec_be.specification.JobPostingSpecification;
import com.topviec.topviec_be.util.ChangeTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.topviec.topviec_be.dto.request.ReqExtendJobPostDTO;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostSkillRepository jobPostSkillRepository;
    private final JobPostLocationRepository jobPostLocationRepository;
    private final JobPostEditLogRepository jobPostEditLogRepository;
    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;
    private final LevelRepository levelRepository;
    private final ObjectMapper objectMapper;
    private final SkillRepository skillRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRoundRepository interviewRoundRepository;
    private final JobPostAssignmentRepository jobPostAssignmentRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final UserRepository userRepository;

    // -------------------------------------------------------------------------
    // Employer — Create
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResJobPostingDetail create(ReqCreateJobPostingDTO request, Long createdByUserId, Long companyId) {
        String slug = generateUniqueSlug(request.getTitle());

        JobPosting jobPosting = JobPosting.builder()
                .companyId(companyId)
                .createdByUserId(createdByUserId)
                .title(request.getTitle())
                .slug(slug)
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .benefits(request.getBenefits())
                .industryId(request.getIndustryId())
                .levelId(request.getLevelId())
                .experienceYearsMin(request.getExperienceYearsMin())
                .experienceYearsMax(request.getExperienceYearsMax())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .salaryNegotiable(request.getSalaryNegotiable())
                .workType(request.getWorkType())
                .headcount(request.getHeadcount())
                .deadline(request.getDeadline())
                .status(JobPostStatus.DRAFT.getValue())
                .isFeatured(Boolean.TRUE.equals(request.getIsFeatured()))
                .isUrgent(Boolean.TRUE.equals(request.getIsUrgent()))
                .build();

        JobPosting saved = jobPostingRepository.save(jobPosting);

        saveLocations(saved.getId(), request.getLocations());

        if (request.getSkills() != null) {
            saveSkills(saved.getId(), request.getSkills());
        }

        return toDetailResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Employer / Admin — Read (list)
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getList(String keyword, Long companyId, Long industryId,
            Long levelId, String workType, String status,
            Boolean isFeatured, Boolean isUrgent, Boolean isHot,
            Long salaryMin, Long salaryMax,
            Integer experienceYearsMin, Integer experienceYearsMax,
            Pageable pageable) {

        Specification<JobPosting> spec = JobPostingSpecification.withFilter(
                keyword, companyId, industryId, levelId, workType, status,
                isFeatured, isUrgent, isHot, salaryMin, salaryMax,
                experienceYearsMin, experienceYearsMax);

        return toResultPagination(jobPostingRepository.findAll(spec, pageable), pageable, false);
    }

    // -------------------------------------------------------------------------
    // Employer — Read (list kèm tin đã xóa + applicationCount)
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getEmployerList(String keyword, Long companyId, Long industryId,
            Long levelId, String workType, String status,
            Boolean isFeatured, Boolean isUrgent, Boolean isHot,
            Long salaryMin, Long salaryMax,
            Integer experienceYearsMin, Integer experienceYearsMax,
            Pageable pageable) {

        // Specification không có notDeleted() — lấy hết cả tin đã xóa mềm
        Specification<JobPosting> spec = JobPostingSpecification.withEmployerFilter(
                keyword, companyId, industryId, levelId, workType, status,
                isFeatured, isUrgent, isHot, salaryMin, salaryMax,
                experienceYearsMin, experienceYearsMax);

        return toResultPagination(jobPostingRepository.findAll(spec, pageable), pageable, true);
    }

    // -------------------------------------------------------------------------
    // Public — UV Read (chỉ published)
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getPublicCompanyList(String keyword, Long companyId,
            Pageable pageable) {

        // Specification không có notDeleted() — lấy hết cả tin đã xóa mềm
        Specification<JobPosting> spec = JobPostingSpecification.withPublicCompanyFilter(
                keyword, companyId);

        return toResultPagination(jobPostingRepository.findAll(spec, pageable), pageable, true);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getPublicList(String keyword, Long companyId, Long industryId,
            Long levelId, String workType,
            Boolean isFeatured, Boolean isUrgent, Boolean isHot,
            Long salaryMin, Long salaryMax,
            Integer experienceYearsMin, Integer experienceYearsMax,
            Pageable pageable) {

        Specification<JobPosting> spec = JobPostingSpecification.withPublicFilter(
                keyword, companyId, industryId, levelId, workType,
                isFeatured, isUrgent, isHot, salaryMin, salaryMax,
                experienceYearsMin, experienceYearsMax);

        return toResultPagination(jobPostingRepository.findAll(spec, pageable), pageable, false);
    }

    // -------------------------------------------------------------------------
    // Read — Detail
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResJobPostingDetail getDetail(Long id) {
        // Cho phép lấy cả tin đã xóa mềm (findById thay vì findByIdOrThrow)
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));
        jobPostingRepository.incrementViewCount(id);
        return toDetailResponse(jobPosting);
    }

    // -------------------------------------------------------------------------
    // Employer — Update
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResJobPostingDetail update(Long id, ReqUpdateJobPostingDTO request, Long updatedByUserId, Long companyId) {
        JobPosting jobPosting = findByIdOrThrow(id);

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền chỉnh sửa tin tuyển dụng của công ty khác");
        }

        validateEditable(jobPosting);
        saveEditLog(jobPosting, updatedByUserId);

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(jobPosting);

        if (!jobPosting.getTitle().equals(request.getTitle())) {
            jobPosting.setSlug(generateUniqueSlugExclude(request.getTitle(), id));
        }

        jobPosting.setTitle(request.getTitle());
        jobPosting.setDescription(request.getDescription());
        jobPosting.setRequirements(request.getRequirements());
        jobPosting.setBenefits(request.getBenefits());
        jobPosting.setIndustryId(request.getIndustryId());
        jobPosting.setLevelId(request.getLevelId());
        jobPosting.setExperienceYearsMin(request.getExperienceYearsMin());
        jobPosting.setExperienceYearsMax(request.getExperienceYearsMax());
        jobPosting.setSalaryMin(request.getSalaryMin());
        jobPosting.setSalaryMax(request.getSalaryMax());
        jobPosting.setSalaryNegotiable(request.getSalaryNegotiable());
        jobPosting.setWorkType(request.getWorkType());
        jobPosting.setHeadcount(request.getHeadcount());
        jobPosting.setDeadline(request.getDeadline());
        jobPosting.setUpdatedBy(updatedByUserId);

        if (request.getIsFeatured() != null)
            jobPosting.setIsFeatured(request.getIsFeatured());
        if (request.getIsUrgent() != null)
            jobPosting.setIsUrgent(request.getIsUrgent());

        if (JobPostStatus.PUBLISHED.getValue().equals(jobPosting.getStatus())) {
            jobPosting.setEditCount(jobPosting.getEditCount() + 1);
        }

        jobPosting.setStatus(JobPostStatus.DRAFT.getValue());

        JobPosting updated = jobPostingRepository.save(jobPosting);

        jobPostLocationRepository.deleteByJobPostId(id);
        jobPostLocationRepository.flush();
        saveLocations(id, request.getLocations());

        jobPostSkillRepository.deleteByJobPostId(id);
        jobPostSkillRepository.flush();
        if (request.getSkills() != null) {
            saveSkills(id, request.getSkills());
        }

        // CDC: So sánh + ghi vào log context
        tracker.compare(updated).apply();

        return toDetailResponse(updated);
    }

    // -------------------------------------------------------------------------
    // Employer — Lifecycle Methods
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResJobPostingDetail pause(Long id, Long companyId, Long updatedByUserId) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }
        if (!JobPostStatus.PUBLISHED.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể tạm dừng tin khi đang ở trạng thái PUBLISHED");
        }
        saveEditLog(jobPosting, updatedByUserId);
        ChangeTracker tracker = ChangeTracker.of(jobPosting);
        jobPosting.setStatus(JobPostStatus.PAUSED.getValue());
        jobPosting.setUpdatedBy(updatedByUserId);
        JobPosting saved = jobPostingRepository.save(jobPosting);
        tracker.compare(saved).apply();
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public ResJobPostingDetail resume(Long id, Long companyId, Long updatedByUserId) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }
        if (!JobPostStatus.PAUSED.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể mở lại tin khi đang ở trạng thái PAUSED");
        }
        saveEditLog(jobPosting, updatedByUserId);
        ChangeTracker tracker = ChangeTracker.of(jobPosting);
        jobPosting.setStatus(JobPostStatus.PUBLISHED.getValue());
        jobPosting.setUpdatedBy(updatedByUserId);
        JobPosting saved = jobPostingRepository.save(jobPosting);
        tracker.compare(saved).apply();
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public ResJobPostingDetail close(Long id, Long companyId, Long updatedByUserId) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }
        String status = jobPosting.getStatus();
        if (!JobPostStatus.PUBLISHED.getValue().equals(status)
                && !JobPostStatus.PAUSED.getValue().equals(status)
                && !JobPostStatus.INTERVIEWING.getValue().equals(status)) {
            throw AppException
                    .badRequest("Chỉ có thể đóng tin khi đang ở trạng thái PUBLISHED, PAUSED hoặc INTERVIEWING");
        }
        saveEditLog(jobPosting, updatedByUserId);
        jobPosting.setStatus(JobPostStatus.CLOSED.getValue());
        jobPosting.setUpdatedBy(updatedByUserId);
        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public ResJobPostingDetail extend(Long id, Long companyId, Long updatedByUserId, ReqExtendJobPostDTO request) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }
        if (!JobPostStatus.EXPIRED.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể gia hạn tin khi đã hết hạn (EXPIRED)");
        }
        saveEditLog(jobPosting, updatedByUserId);
        jobPosting.setDeadline(request.getNewDeadline());
        jobPosting.setStatus(JobPostStatus.PUBLISHED.getValue());
        jobPosting.setUpdatedBy(updatedByUserId);
        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public ResJobPostingDetail refresh(Long id, Long companyId, Long updatedByUserId) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }
        if (!JobPostStatus.PUBLISHED.getValue().equals(jobPosting.getStatus())
                && !JobPostStatus.RENEWED.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể làm mới tin khi đang ở trạng thái PUBLISHED hoặc RENEWED");
        }
        jobPosting.setPublishedAt(java.time.LocalDateTime.now());
        jobPosting.setRefreshedAt(java.time.LocalDateTime.now());
        jobPosting.setUpdatedBy(updatedByUserId);
        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Admin (Content Mod) — Moderation
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResJobPostingDetail approve(Long id, Long adminId) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!JobPostStatus.PENDING_APPROVAL.getValue().equals(jobPosting.getStatus())
                && !JobPostStatus.DRAFT.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể duyệt tin khi đang ở trạng thái PENDING_APPROVAL hoặc DRAFT");
        }

        jobPosting.setStatus(JobPostStatus.PUBLISHED.getValue());
        jobPosting.setPublishedAt(java.time.LocalDateTime.now());
        jobPosting.setUpdatedBy(adminId);
        jobPosting.setRejectionReason(null);
        jobPosting.setModerationNote(null);

        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public ResJobPostingDetail reject(Long id, Long adminId,
            com.topviec.topviec_be.dto.request.ReqRejectJobPostDTO request) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!JobPostStatus.PENDING_APPROVAL.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể từ chối tin khi đang ở trạng thái PENDING_APPROVAL");
        }

        jobPosting.setStatus(JobPostStatus.REJECTED.getValue());
        jobPosting.setRejectionReason(request.getRejectionReason());
        jobPosting.setModerationNote(request.getModerationNote());
        jobPosting.setUpdatedBy(adminId);

        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public ResJobPostingDetail takedown(Long id, Long adminId,
            com.topviec.topviec_be.dto.request.ReqRejectJobPostDTO request) {
        JobPosting jobPosting = findByIdOrThrow(id);
        String status = jobPosting.getStatus();

        if (JobPostStatus.DRAFT.getValue().equals(status) ||
                JobPostStatus.PENDING_APPROVAL.getValue().equals(status) ||
                JobPostStatus.REJECTED.getValue().equals(status) ||
                JobPostStatus.HIDDEN.getValue().equals(status)) {
            throw AppException
                    .badRequest("Tin không nằm trong trạng thái có thể gỡ (chỉ gỡ tin đã đăng, tạm dừng, gia hạn...)");
        }

        jobPosting.setStatus(JobPostStatus.REJECTED.getValue());
        jobPosting.setRejectionReason(request.getRejectionReason());
        jobPosting.setModerationNote(request.getModerationNote());
        jobPosting.setUpdatedBy(adminId);

        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public ResJobPostingDetail restore(Long id, Long adminId) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!JobPostStatus.HIDDEN.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể khôi phục tin đang ở trạng thái HIDDEN");
        }
        jobPosting.setStatus(JobPostStatus.PUBLISHED.getValue());
        jobPosting.setUpdatedBy(adminId);
        return toDetailResponse(jobPostingRepository.save(jobPosting));
    }

    @Override
    @Transactional
    public ResJobPostingDetail pendingApproval(Long id, Long companyId, Long updatedByUserId) {
        JobPosting jobPosting = findByIdOrThrow(id);
        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }
        if (!JobPostStatus.DRAFT.getValue().equals(jobPosting.getStatus())
                && !JobPostStatus.REJECTED.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể gửi duyệt tin khi đang ở trạng thái DRAFT hoặc REJECTED. Tin đang bị ẩn do khiếu nại cần Admin khôi phục trước.");
        }
        saveEditLog(jobPosting, updatedByUserId);
        jobPosting.setStatus(JobPostStatus.PENDING_APPROVAL.getValue());
        jobPosting.setUpdatedBy(updatedByUserId);
        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private JobPosting findByIdOrThrow(Long id) {
        return jobPostingRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));
    }

    private void validateEditable(JobPosting jobPosting) {
        String status = jobPosting.getStatus();

        if (JobPostStatus.DRAFT.getValue().equals(status)
                || JobPostStatus.REJECTED.getValue().equals(status)
                || JobPostStatus.HIDDEN.getValue().equals(status)
                || JobPostStatus.RENEWED.getValue().equals(status)) {
            return;
        }

        if (JobPostStatus.PUBLISHED.getValue().equals(status)) {
            if (jobPosting.getEditCount() >= 1) {
                throw AppException.badRequest("Tin đã được chỉnh sửa 1 lần sau khi đăng, không thể chỉnh sửa thêm");
            }
            return;
        }

        throw AppException.badRequest("Không thể chỉnh sửa tin ở trạng thái: " + status);
    }

    private void saveEditLog(JobPosting jobPosting, Long editedBy) {
        try {
            String snapshot = objectMapper.writeValueAsString(jobPosting);
            String editType = JobPostStatus.DRAFT.getValue().equals(jobPosting.getStatus())
                    ? EditType.DRAFT_EDIT.getValue()
                    : EditType.POST_PUBLISH_EDIT.getValue();

            jobPostEditLogRepository.save(JobPostEditLog.builder()
                    .jobPostId(jobPosting.getId())
                    .editedBy(editedBy)
                    .snapshotBefore(snapshot)
                    .editType(editType)
                    .build());
        } catch (Exception e) {
            // Không để lỗi audit chặn nghiệp vụ chính
        }
    }

    private void saveLocations(Long jobPostId, List<ReqJobPostLocationDTO> locationRequests) {
        List<JobPostLocation> locations = locationRequests.stream()
                .map(req -> JobPostLocation.builder()
                        .jobPostId(jobPostId)
                        .provinceId(req.getProvinceId())
                        .addressDetail(req.getAddressDetail())
                        .isRemote(req.getIsRemote())
                        .build())
                .toList();
        jobPostLocationRepository.saveAll(locations);
    }

    private void saveSkills(Long jobPostId, List<ReqJobPostSkillDTO> skillRequests) {
        List<JobPostSkill> skills = skillRequests.stream()
                .map(req -> JobPostSkill.builder()
                        .jobPostId(jobPostId)
                        .skillId(req.getSkillId())
                        .isRequired(req.getIsRequired())
                        .proficiencyMin(req.getProficiencyMin())
                        .build())
                .toList();
        jobPostSkillRepository.saveAll(skills);
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = toSlug(title);
        String slug = baseSlug;
        int count = 1;
        while (jobPostingRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            slug = baseSlug + "-" + count++;
        }
        return slug;
    }

    private String generateUniqueSlugExclude(String title, Long excludeId) {
        String baseSlug = toSlug(title);
        String slug = baseSlug;
        int count = 1;
        while (jobPostingRepository.existsBySlugAndIdNotAndDeletedAtIsNull(slug, excludeId)) {
            slug = baseSlug + "-" + count++;
        }
        return slug;
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized)
                .replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    // ── Mapper: dùng cho danh sách (batch query tránh N+1) ───────────────────

    private ResultPaginationDTO toResultPagination(Page<JobPosting> page, Pageable pageable,
            boolean includeApplicationCount) {
        List<JobPosting> jobs = page.getContent();

        // Batch query 3 bảng — chỉ tốn 3 query dù có bao nhiêu job
        Map<Long, Company> companyMap = companyRepository
                .findAllById(jobs.stream().map(JobPosting::getCompanyId).distinct().toList())
                .stream().collect(Collectors.toMap(Company::getId, c -> c));

        Map<Long, Industry> industryMap = industryRepository
                .findAllById(jobs.stream().map(JobPosting::getIndustryId).distinct().toList())
                .stream().collect(Collectors.toMap(Industry::getId, i -> i));

        Map<Long, Level> levelMap = levelRepository
                .findAllById(jobs.stream().map(JobPosting::getLevelId).distinct().toList())
                .stream().collect(Collectors.toMap(Level::getId, l -> l));

        // Batch load locations kèm province (1 query, tránh N+1)
        List<Long> allJobIds = jobs.stream().map(JobPosting::getId).toList();
        Map<Long, List<ResJobPostingSummary.LocationDTO>> locationMap = new java.util.HashMap<>();
        if (!allJobIds.isEmpty()) {
            jobPostLocationRepository.findByJobPostIdInWithProvince(allJobIds).forEach(loc -> {
                locationMap.computeIfAbsent(loc.getJobPostId(), k -> new java.util.ArrayList<>())
                        .add(ResJobPostingSummary.LocationDTO.builder()
                                .id(loc.getProvinceId())
                                .name(loc.getProvince() != null ? loc.getProvince().getName() : null)
                                .addressDetail(loc.getAddressDetail())
                                .isRemote(loc.getIsRemote())
                                .build());
            });
        }

        // Batch query applicationCount, interviewRoundsCount và hiredCount nếu cần (chỉ
        // dùng cho Employer)
        Map<Long, Integer> appCountMap = new java.util.HashMap<>();
        Map<Long, Integer> interviewRoundsCountMap = new java.util.HashMap<>();
        Map<Long, Integer> hiredCountMap = new java.util.HashMap<>();
        if (includeApplicationCount && !jobs.isEmpty()) {
            List<Long> jobIds = allJobIds;
            applicationRepository.countByJobPostIds(jobIds).forEach(row -> {
                Long jobId = (Long) row[0];
                Long count = (Long) row[1];
                appCountMap.put(jobId, count.intValue());
            });
            interviewRoundRepository.countByJobPostIdsActive(jobIds).forEach(row -> {
                Long jobId = (Long) row[0];
                Long count = (Long) row[1];
                interviewRoundsCountMap.put(jobId, count.intValue());
            });
            applicationRepository.countHiredByJobPostIds(jobIds).forEach(row -> {
                Long jobId = (Long) row[0];
                Long count = (Long) row[1];
                hiredCountMap.put(jobId, count.intValue());
            });
        }

        // Batch load phân công tin tuyển dụng (chỉ cho Employer)
        Map<Long, ResJobPostingSummary.AssignedRecruiterDTO> assignmentMap = new java.util.HashMap<>();
        if (includeApplicationCount && !allJobIds.isEmpty()) {
            List<JobPostAssignment> activeAssignments = jobPostAssignmentRepository
                    .findActiveByJobPostIds(allJobIds);

            if (!activeAssignments.isEmpty()) {
                // Lấy email từ User
                List<Long> assignedUserIds = activeAssignments.stream()
                        .map(JobPostAssignment::getUserId).distinct().toList();
                Map<Long, String> emailMap = userRepository.findAllById(assignedUserIds).stream()
                        .collect(Collectors.toMap(User::getId, User::getEmail));

                // Lấy jobTitle từ CompanyMember
                Long firstCompanyId = jobs.get(0).getCompanyId();
                Map<Long, String> jobTitleMap = new java.util.HashMap<>();
                List<CompanyMember> members = companyMemberRepository
                        .findByCompanyIdAndUserIds(firstCompanyId, assignedUserIds);
                members.forEach(m -> jobTitleMap.put(m.getUserId(), m.getJobTitle()));

                activeAssignments.forEach(a -> assignmentMap.put(a.getJobPostId(),
                        ResJobPostingSummary.AssignedRecruiterDTO.builder()
                                .userId(a.getUserId())
                                .email(emailMap.get(a.getUserId()))
                                .jobTitle(jobTitleMap.get(a.getUserId()))
                                .build()));
            }
        }

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(jobs.stream()
                .map(j -> toSummaryResponse(j, companyMap, industryMap, levelMap,
                        includeApplicationCount ? appCountMap.getOrDefault(j.getId(), 0) : null,
                        includeApplicationCount ? interviewRoundsCountMap.getOrDefault(j.getId(), 0) : null,
                        includeApplicationCount ? hiredCountMap.getOrDefault(j.getId(), 0) : null,
                        includeApplicationCount,
                        locationMap.getOrDefault(j.getId(), java.util.Collections.emptyList()),
                        assignmentMap.get(j.getId())))
                .toList());
        return result;
    }

    private ResJobPostingSummary toSummaryResponse(JobPosting j,
            Map<Long, Company> companyMap,
            Map<Long, Industry> industryMap,
            Map<Long, Level> levelMap,
            Integer applicationCount,
            Integer interviewRoundsCount,
            Integer hiredCount,
            boolean includeDeletedAt,
            List<ResJobPostingSummary.LocationDTO> locations,
            ResJobPostingSummary.AssignedRecruiterDTO assignedRecruiter) {
        Company company = companyMap.get(j.getCompanyId());
        Industry industry = industryMap.get(j.getIndustryId());
        Level level = levelMap.get(j.getLevelId());

        return ResJobPostingSummary.builder()
                .id(j.getId())
                .title(j.getTitle())
                .slug(j.getSlug())
                .company(company == null ? null
                        : ResJobPostingSummary.CompanyDTO.builder()
                                .id(company.getId())
                                .name(company.getName())
                                .slug(company.getSlug())
                                .logoUrl(company.getLogoUrl())
                                .address(company.getAddress())
                                .isTopEmployer(company.getIsTopEmployer())
                                .isBrandVerified(company.getIsBrandVerified())
                                .build())
                .industry(industry == null ? null
                        : ResJobPostingSummary.IndustryDTO.builder()
                                .id(industry.getId())
                                .name(industry.getName())
                                .build())
                .level(level == null ? null
                        : ResJobPostingSummary.LevelDTO.builder()
                                .id(level.getId())
                                .name(level.getName())
                                .build())
                .workType(j.getWorkType())
                .status(j.getStatus())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .salaryNegotiable(j.getSalaryNegotiable())
                .isFeatured(j.getIsFeatured())
                .isUrgent(j.getIsUrgent())
                .isHot(j.getIsHot())
                .viewCount(j.getViewCount())
                .applicationCount(applicationCount)
                .interviewRoundsCount(interviewRoundsCount)
                .headcount(j.getHeadcount())
                .hiredCount(hiredCount)
                .deadline(j.getDeadline())
                .publishedAt(j.getPublishedAt())
                .createdAt(j.getCreatedAt())
                .deletedAt(includeDeletedAt ? j.getDeletedAt() : null)
                .locations(locations)
                .assignedRecruiter(assignedRecruiter)
                .build();
    }

    // ── Mapper: dùng cho chi tiết (kèm locations + skills) ───────────────────

    private ResJobPostingDetail toDetailResponse(JobPosting j) {
        List<ResJobPostingDetail.LocationDTO> locations = jobPostLocationRepository
                .findByJobPostIdWithProvince(j.getId())
                .stream()
                .map(loc -> ResJobPostingDetail.LocationDTO.builder()
                        .id(loc.getProvinceId())
                        .name(loc.getProvince() != null ? loc.getProvince().getName() : null)
                        .addressDetail(loc.getAddressDetail())
                        .isRemote(loc.getIsRemote())
                        .build())
                .toList();

        List<ResJobPostSkillDTO> skills = jobPostSkillRepository
                .findByJobPostId(j.getId())
                .stream()
                .map(skill -> {
                    Skill skillEntity = skillRepository.findById(skill.getSkillId()).orElse(null);
                    return ResJobPostSkillDTO.builder()
                            .id(skill.getId())
                            .skillName(skillEntity != null ? skillEntity.getName() : null)
                            .skillId(skill.getSkillId())
                            .isRequired(skill.getIsRequired())
                            .proficiencyMin(skill.getProficiencyMin())
                            .build();
                })
                .toList();

        Company company = companyRepository.findById(j.getCompanyId()).orElse(null);
        Industry industry = industryRepository.findById(j.getIndustryId()).orElse(null);
        Level level = levelRepository.findById(j.getLevelId()).orElse(null);

        int applicationCount = (int) applicationRepository
                .countByJobPostIdAndDeletedAtIsNull(j.getId());

        return ResJobPostingDetail.builder()
                .id(j.getId())
                .title(j.getTitle())
                .slug(j.getSlug())
                .description(j.getDescription())
                .requirements(j.getRequirements())
                .benefits(j.getBenefits())
                .company(company == null ? null
                        : ResJobPostingDetail.CompanyDTO.builder()
                                .id(company.getId())
                                .name(company.getName())
                                .slug(company.getSlug())
                                .logoUrl(company.getLogoUrl())
                                .isTopEmployer(company.getIsTopEmployer())
                                .isBrandVerified(company.getIsBrandVerified())
                                .build())
                .industry(industry == null ? null
                        : ResJobPostingDetail.IndustryDTO.builder()
                                .id(industry.getId())
                                .name(industry.getName())
                                .build())
                .level(level == null ? null
                        : ResJobPostingDetail.LevelDTO.builder()
                                .id(level.getId())
                                .name(level.getName())
                                .build())
                .experienceYearsMin(j.getExperienceYearsMin())
                .experienceYearsMax(j.getExperienceYearsMax())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .salaryNegotiable(j.getSalaryNegotiable())
                .workType(j.getWorkType())
                .headcount(j.getHeadcount())
                .deadline(j.getDeadline())
                .status(j.getStatus())
                .isFeatured(j.getIsFeatured())
                .isUrgent(j.getIsUrgent())
                .isHot(j.getIsHot())
                .viewCount(j.getViewCount())
                .editCount(j.getEditCount())
                .applicationCount(applicationCount)
                .publishedAt(j.getPublishedAt())
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .locations(locations)
                .skills(skills)
                .build();
    }

    // -------------------------------------------------------------------------
    // Employer — Soft Delete / Restore
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void softDelete(Long id, Long companyId, Long deletedByUserId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }

        if (JobPostStatus.PUBLISHED.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Không thể xóa tin tuyển dụng đang hiển thị (PUBLISHED)");
        }

        jobPosting.setStatus(JobPostStatus.DELETED.getValue());
        jobPosting.setDeletedAt(java.time.LocalDateTime.now());
        jobPosting.setUpdatedBy(deletedByUserId);
        jobPostingRepository.save(jobPosting);
    }

    @Override
    @Transactional
    public ResJobPostingDetail restore(Long id, Long companyId, Long restoredByUserId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng của công ty khác");
        }

        if (!JobPostStatus.DELETED.getValue().equals(jobPosting.getStatus())) {
            throw AppException.badRequest("Chỉ có thể khôi phục tin ở trạng thái DELETED");
        }

        jobPosting.setStatus(JobPostStatus.DRAFT.getValue());
        jobPosting.setDeletedAt(null);
        jobPosting.setUpdatedBy(restoredByUserId);
        JobPosting saved = jobPostingRepository.save(jobPosting);
        return toDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public com.topviec.topviec_be.dto.response.ResJobPostingStatisticsDTO getJobPostingStatistics(Long id, Long companyId) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Bạn không có quyền xem thống kê tin tuyển dụng của công ty khác");
        }

        int viewCount = jobPosting.getViewCount() != null ? jobPosting.getViewCount() : 0;
        int editCount = jobPosting.getEditCount() != null ? jobPosting.getEditCount() : 0;
        String editStatus = editCount + "/1";

        long applicationCount = applicationRepository.countByJobPostIdAndDeletedAtIsNull(id);

        long remainingDays = 0;
        if (jobPosting.getExpiredAt() != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (jobPosting.getExpiredAt().isAfter(now)) {
                remainingDays = java.time.Duration.between(now, jobPosting.getExpiredAt()).toDays();
            }
        }

        return com.topviec.topviec_be.dto.response.ResJobPostingStatisticsDTO.builder()
                .viewCount(viewCount)
                .applicationCount(applicationCount)
                .editStatus(editStatus)
                .remainingDays(remainingDays)
                .build();
    }
}