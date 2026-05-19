package com.topviec.topviec_be.service.activation;

import com.topviec.topviec_be.dto.response.ResJobPostAddonDTO;
import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.entity.CompanyAddon;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Router chính cho việc định tuyến kích hoạt dịch vụ lẻ.
 * Định tuyến requests để áp dụng addon/service thông qua Category tương ứng.
 * 
 * Thay vì sử dụng Interface-based Strategy, ta nhóm các methods
 * về cùng 1 file ứng với từng category lớn.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceActivationRouter {

    private final JobPostingActivationService jobPostingActivationService;
    private final CandidateActivationService candidateActivationService;
    private final BrandingActivationService brandingActivationService;
    private final AddonPackageActivationService addonPackageActivationService;

    /**
     * Kích hoạt dịch vụ dựa trên thông tin Service Category & Code.
     *
     * @param category     nhóm dịch vụ (Services.category)
     * @param serviceCode  mã dịch vụ (Services.code)
     * @param jobPosting   tin tuyển dụng
     * @param companyAddon dịch vụ lẻ đã mua
     * @param addonService thông tin cấu hình dịch vụ lẻ
     * @return DTO kết quả
     */
    public ResJobPostAddonDTO activate(ServiceCategory category, String serviceCode, JobPosting jobPosting,
            CompanyAddon companyAddon, AddonService addonService) {

        log.info("[ServiceActivationRouter] Định tuyến áp dụng dịch vụ {} - Category: {}", serviceCode, category);

        if (category == null) {
            throw AppException.badRequest("Dịch vụ không xác định được nhóm (Category null).");
        }

        switch (category) {
            case JOB_POSTING:
                return jobPostingActivationService.activate(serviceCode, jobPosting, companyAddon, addonService);
            case CANDIDATE:
                throw AppException.badRequest("Nhóm dịch vụ CANDIDATE chưa được hỗ trợ.");
            case BRANDING:
                throw AppException.badRequest("Nhóm dịch vụ BRANDING chưa được hỗ trợ.");
            case ADDON_PACKAGE:
                throw AppException.badRequest("Nhóm dịch vụ ADDON_PACKAGE chưa được hỗ trợ.");
            default:
                throw AppException.badRequest("Category dịch vụ không hợp lệ: " + category);
        }
    }
    
    /**
     * Phân giải logic liệu một loại dịch vụ có đang hỗ trợ kích hoạt động riêng không.
     * Hiện tại ta hỗ trợ toàn bộ nhóm JOB_POSTING
     */
    public boolean isSupported(ServiceCategory category, String serviceCode) {
        if (category == ServiceCategory.JOB_POSTING) {
            return JobPostingActivationService.CODE_HOT.equals(serviceCode) ||
                   JobPostingActivationService.CODE_URGENT.equals(serviceCode) ||
                   JobPostingActivationService.CODE_REFRESH.equals(serviceCode);
        }
        return false;
    }
}
