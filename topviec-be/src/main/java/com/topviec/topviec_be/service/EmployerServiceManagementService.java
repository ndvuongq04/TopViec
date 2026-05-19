package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqApplyAddonDTO;
import com.topviec.topviec_be.dto.request.ReqRenewSubscriptionDTO;
import com.topviec.topviec_be.dto.response.ResCompanyAddonDTO;
import com.topviec.topviec_be.dto.response.ResCompanyBrandingDTO;
import com.topviec.topviec_be.dto.response.ResCompanySubscriptionDTO;
import com.topviec.topviec_be.dto.response.ResJobPostAddonDTO;
import com.topviec.topviec_be.dto.response.ResSubscriptionRenewalDTO;

import java.util.List;

public interface EmployerServiceManagementService {

    /** Lấy thông tin gói dịch vụ hiện tại NTD đang dùng và hạn mức còn lại */
    ResCompanySubscriptionDTO getMySubscription(Long userId);

    /** Lấy danh sách các dịch vụ lẻ mà NTD đã mua và số lượng còn lại */
    List<ResCompanyAddonDTO> getMyAddons(Long userId);

    /** Áp dụng dịch vụ lẻ cho một tin tuyển dụng */
    ResJobPostAddonDTO applyAddonToJobPost(Long userId, Long jobPostingId, ReqApplyAddonDTO request);

    /** Áp dụng dịch vụ BRANDING cho công ty (route tự động theo service code của addon) */
    ResCompanyBrandingDTO applyBrandingToCompany(Long userId, ReqApplyAddonDTO request);

    /** Gia hạn gói subscription hiện tại (cùng gói, nối thời gian, cộng dồn quota) */
    ResSubscriptionRenewalDTO renewSubscription(Long userId, ReqRenewSubscriptionDTO request);
}
