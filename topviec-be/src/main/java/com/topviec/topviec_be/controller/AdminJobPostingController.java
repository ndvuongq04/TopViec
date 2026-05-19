package com.topviec.topviec_be.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqRejectJobPostDTO;
import com.topviec.topviec_be.dto.response.ResJobPostingDetail;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.util.SecurityUtil;
import com.topviec.topviec_be.service.JobPostingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/job-postings")
@RequiredArgsConstructor
public class AdminJobPostingController {

    private final JobPostingService jobPostingService;

    /**
     * GET /admin/job-postings
     * Lấy danh sách toàn bộ tin tuyển dụng (không lọc theo companyId).
     */
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getAllJobPostings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long industryId,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) Boolean isUrgent,
            @RequestParam(required = false) Boolean isHot,
            @RequestParam(required = false) Long salaryMin,
            @RequestParam(required = false) Long salaryMax,
            @RequestParam(required = false) Integer experienceYearsMin,
            @RequestParam(required = false) Integer experienceYearsMax,
            Pageable pageable) {

        // Truyền companyId = null để lấy toàn bộ
        return ResponseEntity.ok(jobPostingService.getList(
                keyword, null, industryId, levelId, workType, status,
                isFeatured, isUrgent, isHot, salaryMin, salaryMax,
                experienceYearsMin, experienceYearsMax, pageable));
    }

    /**
     * GET /admin/job-postings/{id}
     * Xem chi tiết tin tuyển dụng.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResJobPostingDetail> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(jobPostingService.getDetail(id));
    }

    /**
     * PATCH /admin/job-postings/{id}/approve
     * Admin duyệt tin tuyển dụng.
     */
    @PatchMapping("/{id}/approve")
    @LogAction(LogActionType.APPROVE_JOB_POSTING)
    public ResponseEntity<ResJobPostingDetail> approve(@PathVariable Long id) {
        Long adminId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(jobPostingService.approve(id, adminId));
    }

    /**
     * PATCH /admin/job-postings/{id}/reject
     * Admin từ chối tin tuyển dụng.
     */
    @PatchMapping("/{id}/reject")
    @LogAction(LogActionType.REJECT_JOB_POSTING)
    public ResponseEntity<ResJobPostingDetail> reject(
            @PathVariable Long id,
            @Valid @RequestBody ReqRejectJobPostDTO request) {
        Long adminId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(jobPostingService.reject(id, adminId, request));
    }

    /**
     * PATCH /admin/job-postings/{id}/takedown
     * Admin gỡ tin tuyển dụng vi phạm.
     */
    @PatchMapping("/{id}/takedown")
    @LogAction(LogActionType.TAKEDOWN_JOB_POSTING)
    public ResponseEntity<ResJobPostingDetail> takedown(
            @PathVariable Long id,
            @Valid @RequestBody ReqRejectJobPostDTO request) {
        Long adminId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(jobPostingService.takedown(id, adminId, request));
    }

    /**
     * PATCH /admin/job-postings/{id}/restore
     * Admin khôi phục tin đang bị ẩn (HIDDEN) do khiếu nại về PUBLISHED.
     */
    @PatchMapping("/{id}/restore")
    @LogAction(LogActionType.ADMIN_RESTORE_JOB_POSTING)
    public ResponseEntity<ResJobPostingDetail> restore(@PathVariable Long id) {
        Long adminId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(jobPostingService.restore(id, adminId));
    }

}
