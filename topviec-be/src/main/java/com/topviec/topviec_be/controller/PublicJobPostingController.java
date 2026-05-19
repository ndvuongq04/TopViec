package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResJobPostingDetail;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.JobPostingService;
import com.topviec.topviec_be.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller public — không yêu cầu đăng nhập.
 * Base URL: /api/v1/job-postings
 */
@RestController
@RequestMapping("/job-postings")
@RequiredArgsConstructor
public class PublicJobPostingController {

    private final JobPostingService jobPostingService;

    /**
     * GET /job-postings
     * GET
     * /job-postings?keyword=java&industryId=1&levelId=2&workType=full_time&page=0&size=10
     * Lấy danh sách tin tuyển dụng đang published, hỗ trợ filter + phân trang.
     */
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getPublicList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long industryId,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) Boolean isUrgent,
            @RequestParam(required = false) Boolean isHot,
            @RequestParam(required = false) Long salaryMin,
            @RequestParam(required = false) Long salaryMax,
            @RequestParam(required = false) Integer experienceYearsMin,
            @RequestParam(required = false) Integer experienceYearsMax,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(jobPostingService.getPublicList(
                keyword, companyId, industryId, levelId, workType,
                isFeatured, isUrgent, isHot, salaryMin, salaryMax,
                experienceYearsMin, experienceYearsMax, pageable));
    }

    /**
     * GET job-postings
     * Lấy danh sách tin của công ty, hỗ trợ filter + phân trang.
     * companyId tự động lấy từ JWT → chỉ trả về tin của công ty đang đăng nhập.
     */
    @GetMapping("company/{companyId}")
    public ResponseEntity<ResultPaginationDTO> getList(
            @PathVariable Long companyId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(jobPostingService.getPublicCompanyList(
                keyword, companyId, pageable));
    }

    /**
     * GET /job-postings/hot
     * Lấy danh sách tin tuyển dụng HOT cho trang chủ.
     */
    @GetMapping("/hot")
    public ResponseEntity<ResultPaginationDTO> getHotJobPosts(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(jobPostingService.getPublicList(
                null, null, null, null, null,
                null, null, true, null, null,
                null, null, pageable));
    }

    /**
     * GET /job-postings/{id}
     * Lấy chi tiết tin → trả về ResJobPostingDetail (đầy đủ locations + skills).
     * Tự động tăng view_count.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResJobPostingDetail> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(jobPostingService.getDetail(id));
    }
}