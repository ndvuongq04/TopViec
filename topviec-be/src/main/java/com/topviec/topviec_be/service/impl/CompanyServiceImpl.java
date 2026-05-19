package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqAdminUpdateCompanyDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCompanyDTO;
import com.topviec.topviec_be.dto.response.ResAdminCompanyStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResCompanyDTO;
import com.topviec.topviec_be.dto.response.ResEmployerJobStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.enums.company.CompanySize;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.company.VerificationStatus;
import com.topviec.topviec_be.enums.cvs.FileUploadType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.ApplicationRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.repository.CompanySubscriptionRepository;
import com.topviec.topviec_be.repository.IndustryRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.entity.CompanySubscription;
import com.topviec.topviec_be.entity.Industry;
import com.topviec.topviec_be.entity.Order;
import com.topviec.topviec_be.entity.ServicePackage;
import com.topviec.topviec_be.entity.Services;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.FileStorageService;
import com.topviec.topviec_be.util.ChangeTracker;
import com.topviec.topviec_be.repository.CompanyAddonRepository;
import com.topviec.topviec_be.repository.SubscriptionUsageRepository;
import com.topviec.topviec_be.entity.CompanyAddon;
import com.topviec.topviec_be.entity.SubscriptionUsage;
import com.topviec.topviec_be.repository.ServiceRepository;
import com.topviec.topviec_be.dto.response.ResCompanyPlanDTO;
import com.topviec.topviec_be.dto.response.ResSubscriptionHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final IndustryRepository industryRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanySubscriptionRepository companySubscriptionRepository;
    private final CompanyAddonRepository companyAddonRepository;
    private final SubscriptionUsageRepository subscriptionUsageRepository;
    private final ServiceRepository serviceRepository;
    private final FileStorageService fileStorageService;

    // -------------------------------------------------------------------------
    // Employer — Read
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResCompanyDTO getMyCompany(Long userId) {
        return toResponse(findByCreatedByOrThrow(userId));
    }

    // -------------------------------------------------------------------------
    // Employer — Update
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResCompanyDTO updateMyCompany(Long userId, ReqUpdateCompanyDTO request) {
        Company company = findByCreatedByOrThrow(userId);
        String oldLogoUrl = company.getLogoUrl();
        String oldCoverUrl = company.getCoverUrl();
        String oldBusinessLicenseUrl = company.getBusinessLicenseUrl();

        if (CompanyStatus.SUSPENDED.getValue().equals(company.getStatus())) {
            throw AppException.badRequest("Công ty đang bị tạm khóa, không thể cập nhật hồ sơ");
        }
        if (CompanyStatus.DELETED.getValue().equals(company.getStatus())) {
            throw AppException.badRequest("Công ty đã bị xóa");
        }

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(company);

        applyUpdate(company, request, userId);

        // Nếu đang rejected → chuyển về pending để admin duyệt lại
        if (VerificationStatus.REJECTED.getValue().equals(company.getVerificationStatus())) {
            company.setVerificationStatus(VerificationStatus.PENDING.getValue());
            company.setRejectionReason(null);
        }

        Company saved = companyRepository.save(company);

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        deleteReplacedFile(oldLogoUrl, saved.getLogoUrl(), FileUploadType.COMPANY_LOGO);
        deleteReplacedFile(oldCoverUrl, saved.getCoverUrl(), FileUploadType.COMPANY_COVER);
        deleteReplacedFile(oldBusinessLicenseUrl, saved.getBusinessLicenseUrl(), FileUploadType.BUSINESS_LICENSE);

        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Public — UV
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResCompanyDTO getBySlug(String slug) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty"));

        if (!CompanyStatus.ACTIVE.getValue().equals(company.getStatus())) {
            throw AppException.notFound("Không tìm thấy công ty");
        }

        return toResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public ResCompanyDTO getById(Long id) {
        Company company = findByIdOrThrow(id);

        if (!CompanyStatus.ACTIVE.getValue().equals(company.getStatus())) {
            throw AppException.notFound("Không tìm thấy công ty");
        }

        return toResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getPublicCompanies(String keyword, Integer provinceId,
            Long industryId, Boolean isBanner, Boolean isTopEmployer, Boolean isBrandVerified,
            Pageable pageable) {

        String keywordParam = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Page<Company> page = companyRepository.findPublicCompanies(
                keywordParam, provinceId, industryId, isBanner, isTopEmployer, isBrandVerified, pageable);

        return toResultPagination(page, pageable);
    }

    // -------------------------------------------------------------------------
    // Admin — Read
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResCompanyDTO adminGetById(Long id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAllCompanies(String status, String verificationStatus,
            String keyword, Pageable pageable) {

        String statusParam = (status != null && !status.isBlank()) ? status.trim().toLowerCase() : null;
        String verificationStatusParam = (verificationStatus != null && !verificationStatus.isBlank())
                ? verificationStatus.trim().toLowerCase()
                : null;
        String keywordParam = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Page<Company> page = companyRepository.findAllWithFilter(
                statusParam, verificationStatusParam, keywordParam, pageable);

        return toResultPagination(page, pageable);
    }

    // -------------------------------------------------------------------------
    // Admin — Update (gộp status + info thành 1)
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResCompanyDTO adminUpdateCompany(Long companyId, Long adminId,
            ReqAdminUpdateCompanyDTO request) {

        Company company = findByIdOrThrow(companyId);
        String oldLogoUrl = company.getLogoUrl();
        String oldCoverUrl = company.getCoverUrl();
        String oldBusinessLicenseUrl = company.getBusinessLicenseUrl();

        // CDC: Snapshot trước khi sửa
        ChangeTracker tracker = ChangeTracker.of(company);

        // Bước 1: Xử lý action status nếu có
        if (request.getAction() != null && !request.getAction().isBlank()) {
            switch (request.getAction().toLowerCase()) {

                case "verify" -> {
                    if (!VerificationStatus.PENDING.getValue().equals(company.getVerificationStatus())
                            && !VerificationStatus.REJECTED.getValue().equals(company.getVerificationStatus())) {
                        throw AppException
                                .badRequest("Chỉ có thể duyệt hồ sơ đang ở trạng thái chờ duyệt hoặc đã từ chối");
                    }
                    if (Boolean.TRUE.equals(request.getApproved())) {
                        company.setVerificationStatus(VerificationStatus.VERIFIED.getValue());
                        company.setStatus(CompanyStatus.ACTIVE.getValue());
                        company.setVerifiedAt(LocalDateTime.now());
                        company.setVerifiedBy(adminId);
                        company.setRejectionReason(null);
                    } else {
                        if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                            throw AppException.badRequest("Vui lòng nhập lý do từ chối");
                        }
                        company.setVerificationStatus(VerificationStatus.REJECTED.getValue());
                        company.setRejectionReason(request.getRejectionReason());
                        company.setVerifiedBy(adminId);
                    }
                }

                case "suspend" -> {
                    if (CompanyStatus.SUSPENDED.getValue().equals(company.getStatus())) {
                        throw AppException.badRequest("Công ty đã bị tạm khóa rồi");
                    }
                    if (CompanyStatus.DELETED.getValue().equals(company.getStatus())) {
                        throw AppException.badRequest("Công ty đã bị xóa, không thể suspend");
                    }
                    if (request.getSuspendedReason() == null || request.getSuspendedReason().isBlank()) {
                        throw AppException.badRequest("Vui lòng nhập lý do suspend");
                    }
                    company.setStatus(CompanyStatus.SUSPENDED.getValue());
                    company.setSuspendedAt(LocalDateTime.now());
                    company.setSuspendedReason(request.getSuspendedReason());
                }

                case "unsuspend" -> {
                    if (!CompanyStatus.SUSPENDED.getValue().equals(company.getStatus())) {
                        throw AppException.badRequest("Công ty không đang bị tạm khóa");
                    }
                    company.setStatus(CompanyStatus.ACTIVE.getValue());
                    company.setSuspendedAt(null);
                    company.setSuspendedReason(null);
                }

                default -> throw AppException.badRequest(
                        "action không hợp lệ. Chỉ chấp nhận: verify | suspend | unsuspend");
            }
        }

        // Bước 2: Partial update thông tin công ty nếu có field nào được truyền lên
        if (request.getSlug() != null) {
            if (!request.getSlug().equals(company.getSlug())
                    && companyRepository.existsBySlug(request.getSlug())) {
                throw AppException.conflict("Slug đã được sử dụng, vui lòng chọn slug khác");
            }
            company.setSlug(request.getSlug());
        }
        if (request.getName() != null)
            company.setName(request.getName());
        if (request.getLogoUrl() != null)
            company.setLogoUrl(request.getLogoUrl());
        if (request.getCoverUrl() != null)
            company.setCoverUrl(request.getCoverUrl());
        if (request.getDescription() != null)
            company.setDescription(request.getDescription());
        if (request.getIndustryId() != null)
            company.setIndustryId(request.getIndustryId());
        if (request.getCompanySize() != null)
            company.setCompanySize(request.getCompanySize().getValue());
        if (request.getFoundedYear() != null)
            company.setFoundedYear(request.getFoundedYear());
        if (request.getWebsite() != null)
            company.setWebsite(request.getWebsite());
        if (request.getEmail() != null)
            company.setEmail(request.getEmail());
        if (request.getPhone() != null)
            company.setPhone(request.getPhone());
        if (request.getAddress() != null)
            company.setAddress(request.getAddress());
        if (request.getProvinceId() != null)
            company.setProvinceId(request.getProvinceId());
        if (request.getTaxCode() != null) {
            if (!request.getTaxCode().equals(company.getTaxCode())
                    && companyRepository.existsByTaxCode(request.getTaxCode())) {
                throw AppException.conflict("Mã số thuế đã được đăng ký");
            }
            company.setTaxCode(request.getTaxCode());
        }
        if (request.getBusinessLicenseUrl() != null)
            company.setBusinessLicenseUrl(request.getBusinessLicenseUrl());
        if (request.getCulture() != null)
            company.setCulture(request.getCulture());
        if (request.getBenefits() != null)
            company.setBenefits(request.getBenefits());
        if (request.getSocialLinks() != null)
            company.setSocialLinks(request.getSocialLinks());

        company.setUpdatedBy(adminId);
        Company saved = companyRepository.save(company);

        // CDC: So sánh + ghi vào log context
        tracker.compare(saved).apply();

        deleteReplacedFile(oldLogoUrl, saved.getLogoUrl(), FileUploadType.COMPANY_LOGO);
        deleteReplacedFile(oldCoverUrl, saved.getCoverUrl(), FileUploadType.COMPANY_COVER);
        deleteReplacedFile(oldBusinessLicenseUrl, saved.getBusinessLicenseUrl(), FileUploadType.BUSINESS_LICENSE);

        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Admin — Delete
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void deleteCompany(Long companyId, Long adminId) {
        Company company = findByIdOrThrow(companyId);
        company.setDeletedAt(LocalDateTime.now());
        company.setStatus(CompanyStatus.DELETED.getValue());
        company.setUpdatedBy(adminId);
        companyRepository.save(company);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Company findByIdOrThrow(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty"));
    }

    private Company findByCreatedByOrThrow(Long userId) {
        // 1. Kiểm tra xem user có phải người tạo công ty không
        Optional<Company> companyOpt = companyRepository.findByCreatedBy(userId);
        if (companyOpt.isPresent()) {
            return companyOpt.get();
        }

        // 2. Nếu không, kiểm tra xem user có phải là thành viên active của công ty nào
        // không
        Optional<CompanyMember> memberOpt = companyMemberRepository
                .findFirstByUserIdAndStatusAndDeletedAtIsNull(userId, "active");
        if (memberOpt.isPresent()) {
            return companyRepository.findById(memberOpt.get().getCompanyId())
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty của bạn"));
        }

        throw AppException.notFound("Bạn chưa có hồ sơ công ty");
    }

    // Dùng cho employer update — giữ nguyên ReqUpdateCompanyDTO
    private void applyUpdate(Company company, ReqUpdateCompanyDTO request, Long updatedBy) {
        if (request.getSlug() != null) {
            if (!request.getSlug().equals(company.getSlug())
                    && companyRepository.existsBySlug(request.getSlug())) {
                throw AppException.conflict("Slug đã được sử dụng, vui lòng chọn slug khác");
            }
            company.setSlug(request.getSlug());
        }
        if (request.getName() != null)
            company.setName(request.getName());
        if (request.getLogoUrl() != null)
            company.setLogoUrl(request.getLogoUrl());
        if (request.getCoverUrl() != null)
            company.setCoverUrl(request.getCoverUrl());
        if (request.getDescription() != null)
            company.setDescription(request.getDescription());
        if (request.getIndustryId() != null)
            company.setIndustryId(request.getIndustryId());
        if (request.getCompanySize() != null)
            company.setCompanySize(request.getCompanySize().getValue());
        if (request.getFoundedYear() != null)
            company.setFoundedYear(request.getFoundedYear());
        if (request.getWebsite() != null)
            company.setWebsite(request.getWebsite());
        if (request.getEmail() != null)
            company.setEmail(request.getEmail());
        if (request.getPhone() != null)
            company.setPhone(request.getPhone());
        if (request.getAddress() != null)
            company.setAddress(request.getAddress());
        if (request.getProvinceId() != null)
            company.setProvinceId(request.getProvinceId());
        if (request.getTaxCode() != null) {
            if (!request.getTaxCode().equals(company.getTaxCode())
                    && companyRepository.existsByTaxCode(request.getTaxCode())) {
                throw AppException.conflict("Mã số thuế đã được đăng ký");
            }
            company.setTaxCode(request.getTaxCode());
        }
        if (request.getBusinessLicenseUrl() != null)
            company.setBusinessLicenseUrl(request.getBusinessLicenseUrl());
        if (request.getCulture() != null)
            company.setCulture(request.getCulture());
        if (request.getBenefits() != null)
            company.setBenefits(request.getBenefits());
        if (request.getSocialLinks() != null)
            company.setSocialLinks(request.getSocialLinks());
        company.setUpdatedBy(updatedBy);
    }

    private ResultPaginationDTO toResultPagination(Page<Company> page, Pageable pageable) {
        List<Company> companies = page.getContent();
        List<Long> companyIds = companies.stream().map(Company::getId).toList();

        // Batch load industry names (1 query)
        Map<Long, String> industryNameMap = new java.util.HashMap<>();
        List<Long> industryIds = companies.stream()
                .map(Company::getIndustryId).filter(id -> id != null).distinct().toList();
        if (!industryIds.isEmpty()) {
            industryRepository.findAllById(industryIds)
                    .forEach(i -> industryNameMap.put(i.getId(), i.getName()));
        }

        // Batch load job counts (1 query)
        Map<Long, Integer> jobCountMap = new java.util.HashMap<>();
        if (!companyIds.isEmpty()) {
            jobPostingRepository.countActiveByCompanyIds(companyIds).forEach(row -> {
                Long companyId = (Long) row[0];
                Long count = (Long) row[1];
                jobCountMap.put(companyId, count.intValue());
            });
        }

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(companies.stream()
                .map(c -> toResponse(c,
                        industryNameMap.get(c.getIndustryId()),
                        jobCountMap.getOrDefault(c.getId(), 0)))
                .toList());
        return result;
    }

    private ResCompanyDTO toResponse(Company c) {
        String industryName = c.getIndustryId() != null
                ? industryRepository.findById(c.getIndustryId()).map(Industry::getName).orElse(null)
                : null;
        int jobCount = (int) jobPostingRepository.countActiveByCompanyId(c.getId());
        return toResponse(c, industryName, jobCount);
    }

    private ResCompanyDTO toResponse(Company c, String industryName, Integer jobCount) {
        return ResCompanyDTO.builder()
                .id(c.getId())
                .slug(c.getSlug())
                .name(c.getName())
                .logoUrl(c.getLogoUrl())
                .coverUrl(c.getCoverUrl())
                .description(c.getDescription())
                .industryId(c.getIndustryId())
                .industryName(industryName)
                .jobCount(jobCount)
                .companySize(c.getCompanySize() != null ? CompanySize.fromValue(c.getCompanySize()) : null)
                .foundedYear(c.getFoundedYear())
                .website(c.getWebsite())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .provinceId(c.getProvinceId())
                .taxCode(c.getTaxCode())
                .businessLicenseUrl(c.getBusinessLicenseUrl())
                .culture(c.getCulture())
                .benefits(c.getBenefits())
                .socialLinks(c.getSocialLinks())
                .verificationStatus(c.getVerificationStatus() != null
                        ? VerificationStatus.fromValue(c.getVerificationStatus())
                        : null)
                .verifiedAt(c.getVerifiedAt())
                .verifiedBy(c.getVerifiedBy())
                .rejectionReason(c.getRejectionReason())
                .status(c.getStatus() != null ? CompanyStatus.fromValue(c.getStatus()) : null)
                .violationScore(c.getViolationScore())
                .suspendedAt(c.getSuspendedAt())
                .suspendedReason(c.getSuspendedReason())
                .isBanner(c.getIsBanner())
                .isTopEmployer(c.getIsTopEmployer())
                .isBrandVerified(c.getIsBrandVerified())
                .createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCompanyIdByUserId(Long userId) {
        // Dùng chung findByCreatedByOrThrow để hỗ trợ cả OWNER lẫn Member
        return findByCreatedByOrThrow(userId).getId();
    }

    // -------------------------------------------------------------------------
    // Admin — Company Statistics
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResAdminCompanyStatisticsDTO getCompanyStatistics(Long companyId) {
        // Validate company exists
        findByIdOrThrow(companyId);

        // 1. Tổng tin đã đăng (chưa bị xóa mềm)
        long totalJobPostings = jobPostingRepository.countByCompanyIdAndDeletedAtIsNull(companyId);

        // 2. Tổng CV/đơn ứng tuyển đã nhận
        long totalApplicationsReceived = applicationRepository.countByCompanyId(companyId);

        // 3. Gói dịch vụ đang sử dụng (status = ACTIVE)
        List<CompanySubscription> activeSubscriptions = companySubscriptionRepository
                .findByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, SubscriptionStatus.ACTIVE);

        List<ResAdminCompanyStatisticsDTO.ActiveSubscriptionDTO> subscriptionDTOs = activeSubscriptions.stream()
                .map(sub -> {
                    ServicePackage pkg = sub.getServicePackage();
                    return ResAdminCompanyStatisticsDTO.ActiveSubscriptionDTO.builder()
                            .subscriptionId(sub.getId())
                            .servicePackageId(sub.getServicePackageId())
                            .packageName(pkg != null ? pkg.getName() : null)
                            .packageCode(pkg != null ? pkg.getCode() : null)
                            .billingCycle(sub.getBillingCycle())
                            .status(sub.getStatus())
                            .startedAt(sub.getStartedAt())
                            .expiredAt(sub.getExpiredAt())
                            .build();
                })
                .toList();

        return ResAdminCompanyStatisticsDTO.builder()
                .totalJobPostings(totalJobPostings)
                .totalApplicationsReceived(totalApplicationsReceived)
                .activeSubscriptions(subscriptionDTOs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResCompanyPlanDTO getCompanyPlan(Long companyId) {
        // Validate company exists
        findByIdOrThrow(companyId);

        Map<String, String> serviceCodeToNameMap = new java.util.HashMap<>();
        serviceRepository.findAll().forEach(s -> serviceCodeToNameMap.put(s.getCode(), s.getName()));

        // 1. Lấy gói dịch vụ hiện hành (ưu tiên ACTIVE, nếu không có thì lấy gói mới
        // nhất)
        ResCompanyPlanDTO.CurrentPackageDTO currentPackageDTO = null;
        Optional<CompanySubscription> optSubscription = companySubscriptionRepository
                .findFirstByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, SubscriptionStatus.ACTIVE);

        if (optSubscription.isEmpty()) {
            optSubscription = companySubscriptionRepository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId);
        }

        if (optSubscription.isPresent()) {
            CompanySubscription sub = optSubscription.get();
            ServicePackage pkg = sub.getServicePackage();

            // Lấy danh sách usage của subscription này
            List<SubscriptionUsage> usages = subscriptionUsageRepository.findByCompanySubscriptionId(sub.getId());
            List<ResCompanyPlanDTO.UsageDTO> usageDTOs = usages.stream()
                    .map(u -> ResCompanyPlanDTO.UsageDTO.builder()
                            .featureCode(u.getFeatureCode())
                            .featureName(serviceCodeToNameMap.get(u.getFeatureCode()))
                            .total(u.getQuantityTotal())
                            .used(u.getQuantityTotal() - u.getQuantityRemaining())
                            .build())
                    .toList();

            Order packageOrder = sub.getOrder();

            currentPackageDTO = ResCompanyPlanDTO.CurrentPackageDTO.builder()
                    .subscriptionId(sub.getId())
                    .servicePackageId(sub.getServicePackageId())
                    .packageName(pkg != null ? pkg.getName() : null)
                    .packageCode(pkg != null ? pkg.getCode() : null)
                    .billingCycle(sub.getBillingCycle() != null ? sub.getBillingCycle().name() : null)
                    .status(sub.getStatus() != null ? sub.getStatus().name() : null)
                    .startedAt(sub.getStartedAt())
                    .expiredAt(sub.getExpiredAt())
                    .orderId(sub.getOrderId())
                    .orderCode(packageOrder != null ? packageOrder.getOrderCode() : null)
                    .usages(usageDTOs)
                    .build();
        }

        // 2. Lấy danh sách dịch vụ lẻ
        List<CompanyAddon> addons = companyAddonRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
        List<ResCompanyPlanDTO.CurrentAddonDTO> addonDTOs = addons.stream()
                .map(a -> {
                    AddonService addonService = a.getAddonService();
                    Services svc = addonService != null ? addonService.getService() : null;
                    Order order = a.getOrder();
                    return ResCompanyPlanDTO.CurrentAddonDTO.builder()
                            .addonId(a.getId())
                            .addonServiceId(a.getAddonServiceId())
                            .addonName(addonService != null ? addonService.getName() : null)
                            .addonCode(addonService != null ? addonService.getCode() : null)
                            .serviceCategory(svc != null && svc.getCategory() != null ? svc.getCategory().name() : null)
                            .serviceCategoryName(
                                    svc != null && svc.getCategory() != null ? svc.getCategory().getValue() : null)
                            .status(a.getStatus() != null ? a.getStatus().name() : null)
                            .total(a.getQuantityTotal())
                            .used(a.getQuantityTotal() - a.getQuantityRemaining())
                            .startedAt(a.getStartedAt())
                            .expiredAt(a.getExpiredAt())
                            .orderId(a.getOrderId())
                            .orderCode(order != null ? order.getOrderCode() : null)
                            .build();
                })
                .toList();

        return ResCompanyPlanDTO.builder()
                .currentPackage(currentPackageDTO)
                .currentAddons(addonDTOs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getSubscriptionHistory(Long companyId, Pageable pageable) {
        findByIdOrThrow(companyId);

        Page<CompanySubscription> page = companySubscriptionRepository.findByCompanyIdOrderByCreatedAtDesc(companyId,
                pageable);

        List<ResSubscriptionHistoryDTO> results = page.getContent().stream().map(sub -> {
            ServicePackage pkg = sub.getServicePackage();
            if (pkg == null && sub.getServicePackageId() != null) {
                // If lazy loading issue or need explicit fetch
            }
            return ResSubscriptionHistoryDTO.builder()
                    .subscriptionId(sub.getId())
                    .companyId(sub.getCompanyId())
                    .orderId(sub.getOrderId())
                    .servicePackageId(sub.getServicePackageId())
                    .packageName(pkg != null ? pkg.getName() : null)
                    .packageCode(pkg != null ? pkg.getCode() : null)
                    .status(sub.getStatus() != null ? sub.getStatus().name() : null)
                    .billingCycle(sub.getBillingCycle() != null ? sub.getBillingCycle().name() : null)
                    .startedAt(sub.getStartedAt())
                    .expiredAt(sub.getExpiredAt())
                    .purchasedAt(sub.getCreatedAt())
                    .packagePrice(pkg != null ? pkg.getPrice() : null)
                    .build();
        }).toList();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(results);

        return result;
    }

    // -------------------------------------------------------------------------
    // Employer — Job Statistics
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResEmployerJobStatisticsDTO getEmployerJobStatistics(Long userId) {
        Company company = findByCreatedByOrThrow(userId);
        Long companyId = company.getId();

        long totalJobPosts = jobPostingRepository.countByCompanyIdAndDeletedAtIsNull(companyId);
        long activeJobPosts = jobPostingRepository.countActiveByCompanyId(companyId);
        long pendingJobPosts = jobPostingRepository.countByCompanyIdAndStatusAndDeletedAtIsNull(companyId,
                "pending_approval");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        long expiringJobPosts = jobPostingRepository.countExpiringByCompanyId(companyId, now, sevenDaysLater);

        return ResEmployerJobStatisticsDTO.builder()
                .totalJobPosts(totalJobPosts)
                .activeJobPosts(activeJobPosts)
                .pendingJobPosts(pendingJobPosts)
                .expiringJobPosts(expiringJobPosts)
                .build();
    }

    private void deleteReplacedFile(String oldFileUrl, String newFileUrl, FileUploadType type) {
        if (oldFileUrl == null || oldFileUrl.isBlank()) {
            return;
        }
        if (oldFileUrl.equals(newFileUrl)) {
            return;
        }
        fileStorageService.deleteFile(oldFileUrl, type);
    }
}
