package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqAdminUpdateCompanyDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCompanyDTO;
import com.topviec.topviec_be.dto.response.ResAdminCompanyStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResCompanyDTO;
import com.topviec.topviec_be.dto.response.ResCompanyPlanDTO;
import com.topviec.topviec_be.dto.response.ResEmployerJobStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

        /** Employer xem hồ sơ công ty của chính mình. */
        ResCompanyDTO getMyCompany(Long userId);

        /** Employer cập nhật hồ sơ công ty của mình. */
        ResCompanyDTO updateMyCompany(Long userId, ReqUpdateCompanyDTO request);

        ResCompanyDTO getBySlug(String slug);

        ResCompanyDTO getById(Long id);

        ResultPaginationDTO getPublicCompanies(String keyword, Integer provinceId,
                        Long industryId, Boolean isBanner, Boolean isTopEmployer, Boolean isBrandVerified,
                        Pageable pageable);

        ResCompanyDTO adminGetById(Long id);

        ResultPaginationDTO getAllCompanies(String status, String verificationStatus,
                        String keyword, Pageable pageable);

        /**
         * Gộp updateCompanyStatus + adminUpdateCompany thành 1:
         * - action != null → xử lý status (verify/suspend/unsuspend)
         * - field info != null → partial update thông tin công ty
         * - Có thể thực hiện cả 2 cùng lúc
         */
        ResCompanyDTO adminUpdateCompany(Long companyId, Long adminId, ReqAdminUpdateCompanyDTO request);

        void deleteCompany(Long companyId, Long adminId);

        Long getCompanyIdByUserId(Long userId);

        /**
         * Admin: lấy thống kê tổng quan của công ty (tổng tin, tổng CV, gói dịch vụ).
         */
        ResAdminCompanyStatisticsDTO getCompanyStatistics(Long companyId);

        /**
         * Admin: lấy thông tin chi tiết gói dịch vụ và dịch vụ lẻ hiện tại của công ty.
         */
        ResCompanyPlanDTO getCompanyPlan(Long companyId);

        /**
         * Admin: lấy lịch sử mua/gia hạn gói dịch vụ của công ty.
         */
        ResultPaginationDTO getSubscriptionHistory(Long companyId, Pageable pageable);

        /** Employer: lấy thống kê tin tuyển dụng của công ty. */
        ResEmployerJobStatisticsDTO getEmployerJobStatistics(Long userId);
}