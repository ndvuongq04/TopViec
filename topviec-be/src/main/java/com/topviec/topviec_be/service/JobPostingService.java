package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqCreateJobPostingDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateJobPostingDTO;
import com.topviec.topviec_be.dto.response.ResJobPostingDetail;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

public interface JobPostingService {

        /** Employer tạo tin tuyển dụng mới, mặc định ở trạng thái draft. */
        ResJobPostingDetail create(ReqCreateJobPostingDTO request, Long createdByUserId, Long companyId);

        /**
         * Lấy danh sách tin tuyển dụng (Employer/Admin), hỗ trợ filter + phân trang.
         */
        ResultPaginationDTO getList(String keyword, Long companyId, Long industryId,
                        Long levelId, String workType, String status,
                        Boolean isFeatured, Boolean isUrgent, Boolean isHot,
                        Long salaryMin, Long salaryMax,
                        Integer experienceYearsMin, Integer experienceYearsMax,
                        Pageable pageable);

        /**
         * Lấy danh sách tin tuyển dụng dành riêng cho Employer:
         * - Bao gồm cả tin đã xóa mềm (deletedAt IS NOT NULL)
         * - Kèm theo số lượng hồ sơ apply vào mỗi tin (applicationCount)
         */
        ResultPaginationDTO getEmployerList(String keyword, Long companyId, Long industryId,
                        Long levelId, String workType, String status,
                        Boolean isFeatured, Boolean isUrgent, Boolean isHot,
                        Long salaryMin, Long salaryMax,
                        Integer experienceYearsMin, Integer experienceYearsMax,
                        Pageable pageable);

        /** Lấy danh sách tin published của công ty, hỗ trợ filter + phân trang. */
        ResultPaginationDTO getPublicCompanyList(String keyword, Long companyId,
                        Pageable pageable);

        /** Lấy danh sách tin published (ứng viên), hỗ trợ filter + phân trang. */
        ResultPaginationDTO getPublicList(String keyword, Long companyId, Long industryId,
                        Long levelId, String workType,
                        Boolean isFeatured, Boolean isUrgent, Boolean isHot,
                        Long salaryMin, Long salaryMax,
                        Integer experienceYearsMin, Integer experienceYearsMax,
                        Pageable pageable);

        /** Xem chi tiết tin tuyển dụng, tự động tăng view_count. */
        ResJobPostingDetail getDetail(Long id);

        /** Employer chỉnh sửa tin tuyển dụng. */
        ResJobPostingDetail update(Long id, ReqUpdateJobPostingDTO request, Long updatedByUserId, Long companyId);

        /** Employer tạm dừng tin tuyển dụng. */
        ResJobPostingDetail pause(Long id, Long companyId, Long updatedByUserId);

        /** Employer mở lại tin tuyển dụng. */
        ResJobPostingDetail resume(Long id, Long companyId, Long updatedByUserId);

        /** Employer đóng tin tuyển dụng. */
        ResJobPostingDetail close(Long id, Long companyId, Long updatedByUserId);

        /** Employer gia hạn tin tuyển dụng. */
        ResJobPostingDetail extend(Long id, Long companyId, Long updatedByUserId,
                        com.topviec.topviec_be.dto.request.ReqExtendJobPostDTO request);

        /** Employer làm mới tin tuyển dụng (đẩy lên đầu). */
        ResJobPostingDetail refresh(Long id, Long companyId, Long updatedByUserId);

        // -------------------------------------------------------------------------
        // Admin (Content Mod) — Moderation
        // -------------------------------------------------------------------------

        /** Admin duyệt tin tuyển dụng. */
        ResJobPostingDetail approve(Long id, Long adminId);

        /** Admin từ chối tin tuyển dụng. */
        ResJobPostingDetail reject(Long id, Long adminId,
                        com.topviec.topviec_be.dto.request.ReqRejectJobPostDTO request);

        /** Admin gỡ tin tuyển dụng vi phạm. */
        ResJobPostingDetail takedown(Long id, Long adminId,
                        com.topviec.topviec_be.dto.request.ReqRejectJobPostDTO request);

        /** Employer gửi tin đang DRAFT/REJECTED lên chờ admin duyệt. */
        ResJobPostingDetail pendingApproval(Long id, Long companyId, Long updatedByUserId);

        /** Admin khôi phục tin đang HIDDEN về PUBLISHED. */
        ResJobPostingDetail restore(Long id, Long adminId);

        // -------------------------------------------------------------------------
        // Employer — Soft Delete / Restore
        // -------------------------------------------------------------------------

        /**
         * Employer xóa mềm tin tuyển dụng của mình (chỉ xóa được khi không ở
         * PUBLISHED).
         */
        void softDelete(Long id, Long companyId, Long deletedByUserId);

        /** Employer khôi phục tin đã xóa mềm về trạng thái DRAFT. */
        ResJobPostingDetail restore(Long id, Long companyId, Long restoredByUserId);

        /** Lấy thống kê của một tin tuyển dụng (view, applications, edit count, remaining days). */
        com.topviec.topviec_be.dto.response.ResJobPostingStatisticsDTO getJobPostingStatistics(Long id, Long companyId);
}