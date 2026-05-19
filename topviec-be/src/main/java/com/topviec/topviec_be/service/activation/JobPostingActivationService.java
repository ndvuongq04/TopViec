package com.topviec.topviec_be.service.activation;

import com.topviec.topviec_be.dto.response.ResJobPostAddonDTO;
import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.entity.CompanyAddon;
import com.topviec.topviec_be.entity.JobPostAddon;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.enums.services.JobPostAddonStatus;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyAddonRepository;
import com.topviec.topviec_be.repository.JobPostAddonRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Category-level service cho nhóm JOB_POSTING (Tin tuyển dụng).
 *
 * Chứa logic áp dụng các dịch vụ đặc thù cho tin tuyển dụng, bao gồm:
 * - JOB_POST_HOT: Tin HOT
 * - JOB_POST_URGENT: Tin Gấp
 * - JOB_POST_REFRESH: Làm mới tin
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostingActivationService {

    public static final ServiceCategory CATEGORY = ServiceCategory.JOB_POSTING;

    public static final String CODE_HOT = "JOB_POSTING_HOT";
    public static final String CODE_URGENT = "JOB_POSTING_URGENT";
    public static final String CODE_REFRESH = "JOB_POSTING_REFRESH";

    private final JobPostingRepository jobPostingRepository;
    private final JobPostAddonRepository jobPostAddonRepository;
    private final CompanyAddonRepository companyAddonRepository;

    /**
     * Dispatcher method: phân luồng xử lý tùy theo từng loại service code
     */
    @Transactional
    public ResJobPostAddonDTO activate(String serviceCode, JobPosting jobPosting, CompanyAddon companyAddon,
            AddonService addonService) {
        log.info("[JobPostingActivationService] Kích hoạt dịch vụ {} cho tin #{}", serviceCode, jobPosting.getId());

        switch (serviceCode) {
            case CODE_HOT:
                return applyHotService(jobPosting, companyAddon, addonService);
            case CODE_URGENT:
                return applyUrgentService(jobPosting, companyAddon, addonService);
            case CODE_REFRESH:
                return applyRefreshService(jobPosting, companyAddon, addonService);
            default:
                throw AppException
                        .badRequest("Mã dịch vụ nhóm Tuyển Dụng không hợp lệ hoặc chưa được hỗ trợ: " + serviceCode);
        }
    }

    /**
     * Xử lý kích hoạt Tin HOT
     */
    private ResJobPostAddonDTO applyHotService(JobPosting jobPosting, CompanyAddon companyAddon,
            AddonService addonService) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra tin đã HOT chưa (dựa vào JobPostAddon)
        long activeHotCountForJob = jobPostAddonRepository.countActiveAddonForJob(jobPosting.getId(), CODE_HOT, now);
        if (activeHotCountForJob > 0) {
            throw AppException.badRequest("Tin tuyển dụng này đang ở trạng thái HOT. Không cần áp dụng thêm.");
        }

        // 2. Tính thời hạn HOT
        int durationDays = addonService.getDurationDays() != null ? addonService.getDurationDays() : 30;
        LocalDateTime hotExpiredAt = now.plusDays(durationDays);

        // 4. Bật cờ isHot trên JobPosting để dễ truy vấn
        jobPosting.setIsHot(true);
        jobPostingRepository.save(jobPosting);

        // 5. Tạo & trả JobPostAddon (status = ACTIVE)
        return createJobPostAddonRecord(jobPosting.getId(), companyAddon, addonService, now, hotExpiredAt);
    }

    /**
     * Xử lý kích hoạt Tin Gấp
     */
    private ResJobPostAddonDTO applyUrgentService(JobPosting jobPosting, CompanyAddon companyAddon,
            AddonService addonService) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra tin đã URGENT chưa
        long activeUrgentCountForJob = jobPostAddonRepository.countActiveAddonForJob(jobPosting.getId(), CODE_URGENT, now);
        if (activeUrgentCountForJob > 0) {
            throw AppException.badRequest("Tin tuyển dụng này đang ở trạng thái TUYỂN GẤP. Không cần áp dụng thêm.");
        }

        // 2. Tính thời hạn URGENT (mặc định 14 ngày nếu không cấu hình)
        int durationDays = addonService.getDurationDays() != null ? addonService.getDurationDays() : 14;
        LocalDateTime urgentExpiredAt = now.plusDays(durationDays);

        // 4. Bật cờ isUrgent trên JobPosting để dễ truy vấn
        jobPosting.setIsUrgent(true);
        jobPostingRepository.save(jobPosting);

        // 5. Tạo & trả JobPostAddon (status = ACTIVE)
        return createJobPostAddonRecord(jobPosting.getId(), companyAddon, addonService, now, urgentExpiredAt);
    }

    /**
     * Xử lý kích hoạt Làm mới tin
     */
    private ResJobPostAddonDTO applyRefreshService(JobPosting jobPosting, CompanyAddon companyAddon,
            AddonService addonService) {
        // TODO: Logic làm mới tin (ví dụ update refreshed_at = now)
        throw AppException.badRequest("Tính năng áp dụng Dịch Vụ Làm Mới Tin đang được xây dựng.");
    }

    /**
     * Hàm dùng chung để tạo record lịch sử sử dụng dịch vụ & trừ quota CompanyAddon
     */
    private ResJobPostAddonDTO createJobPostAddonRecord(Long jobPostingId, CompanyAddon companyAddon,
            AddonService addonService, LocalDateTime startedAt, LocalDateTime expiredAt) {
        JobPostAddon jobPostAddon = JobPostAddon.builder()
                .jobPostingId(jobPostingId)
                .companyAddonId(companyAddon.getId())
                .addonServiceId(addonService.getId())
                .startedAt(startedAt)
                .expiredAt(expiredAt)
                .status(JobPostAddonStatus.ACTIVE)
                .build();
        JobPostAddon saved = jobPostAddonRepository.save(jobPostAddon);

        companyAddon.setQuantityRemaining(companyAddon.getQuantityRemaining() - 1);
        companyAddonRepository.save(companyAddon);

        return ResJobPostAddonDTO.builder()
                .id(saved.getId())
                .jobPostingId(saved.getJobPostingId())
                .companyAddonId(saved.getCompanyAddonId())
                .addonServiceId(saved.getAddonServiceId())
                .addonName(addonService.getName())
                .status(saved.getStatus())
                .startedAt(saved.getStartedAt())
                .expiredAt(saved.getExpiredAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
