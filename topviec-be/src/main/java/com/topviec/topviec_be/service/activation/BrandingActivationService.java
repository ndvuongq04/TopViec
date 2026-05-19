package com.topviec.topviec_be.service.activation;

import com.topviec.topviec_be.dto.response.ResCompanyBrandingDTO;
import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.CompanyAddon;
import com.topviec.topviec_be.entity.CompanyBranding;
import com.topviec.topviec_be.enums.services.BrandingAddonStatus;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyAddonRepository;
import com.topviec.topviec_be.repository.CompanyBrandingRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandingActivationService {

    public static final ServiceCategory CATEGORY = ServiceCategory.BRANDING;
    public static final String CODE_BANNER_HOME    = "BRANDING_BANNER_HOME";
    public static final String CODE_TOP_EMPLOYER   = "BRANDING_TOP_EMPLOYER";
    public static final String CODE_VERIFIED       = "BRANDING_VERIFIED";

    private final CompanyRepository companyRepository;
    private final CompanyBrandingRepository companyBrandingRepository;
    private final CompanyAddonRepository companyAddonRepository;

    @Transactional
    public ResCompanyBrandingDTO activate(String serviceCode, Long companyId,
            CompanyAddon companyAddon, AddonService addonService) {
        return switch (serviceCode) {
            case CODE_BANNER_HOME -> applyBrandingService(
                    companyId, companyAddon, addonService, CODE_BANNER_HOME,
                    "Công ty đang có Banner trang chủ đang hoạt động. Không cần áp dụng thêm.",
                    30, company -> company.setIsBanner(true));
            case CODE_TOP_EMPLOYER -> applyBrandingService(
                    companyId, companyAddon, addonService, CODE_TOP_EMPLOYER,
                    "Công ty đang có nhãn Top Employer đang hoạt động. Không cần áp dụng thêm.",
                    30, company -> company.setIsTopEmployer(true));
            case CODE_VERIFIED -> applyBrandingService(
                    companyId, companyAddon, addonService, CODE_VERIFIED,
                    "Công ty đang có nhãn Doanh Nghiệp Xác Thực đang hoạt động. Không cần áp dụng thêm.",
                    365, company -> company.setIsBrandVerified(true));
            default -> throw AppException.badRequest(
                    "Mã dịch vụ BRANDING không hợp lệ hoặc chưa được hỗ trợ: " + serviceCode);
        };
    }

    public static boolean isSupported(String serviceCode) {
        return CODE_BANNER_HOME.equals(serviceCode)
                || CODE_TOP_EMPLOYER.equals(serviceCode)
                || CODE_VERIFIED.equals(serviceCode);
    }

    private ResCompanyBrandingDTO applyBrandingService(Long companyId, CompanyAddon companyAddon,
            AddonService addonService, String serviceCode, String duplicateMessage,
            int defaultDurationDays, Consumer<Company> flagSetter) {

        LocalDateTime now = LocalDateTime.now();
        log.info("[BrandingActivationService] Kích hoạt {} cho công ty #{}", serviceCode, companyId);

        // 1. Kiểm tra đang active chưa
        long active = companyBrandingRepository.countActiveForCompany(companyId, serviceCode, now);
        if (active > 0) {
            throw AppException.badRequest(duplicateMessage);
        }

        // 2. Tính thời hạn
        int durationDays = addonService.getDurationDays() != null ? addonService.getDurationDays() : defaultDurationDays;
        LocalDateTime expiredAt = now.plusDays(durationDays);

        // 3. Bật cờ trên Company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy công ty."));
        flagSetter.accept(company);
        companyRepository.save(company);

        // 4. Tạo record CompanyBranding để track expiry
        CompanyBranding branding = CompanyBranding.builder()
                .companyId(companyId)
                .companyAddonId(companyAddon.getId())
                .addonServiceId(addonService.getId())
                .serviceCode(serviceCode)
                .status(BrandingAddonStatus.ACTIVE)
                .startedAt(now)
                .expiredAt(expiredAt)
                .build();
        CompanyBranding saved = companyBrandingRepository.save(branding);

        // 5. Trừ quota CompanyAddon
        companyAddon.setQuantityRemaining(companyAddon.getQuantityRemaining() - 1);
        companyAddonRepository.save(companyAddon);

        return ResCompanyBrandingDTO.builder()
                .id(saved.getId())
                .companyId(saved.getCompanyId())
                .companyAddonId(saved.getCompanyAddonId())
                .addonServiceId(saved.getAddonServiceId())
                .addonName(addonService.getName())
                .serviceCode(serviceCode)
                .status(saved.getStatus())
                .startedAt(saved.getStartedAt())
                .expiredAt(saved.getExpiredAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
