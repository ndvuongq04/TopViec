package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.request.ReqAssignJobPostDTO;
import com.topviec.topviec_be.dto.request.ReqRevokeAssignmentDTO;
import com.topviec.topviec_be.dto.response.ResJobPostAssignmentDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.JobPostAssignmentService;
import com.topviec.topviec_be.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller dành cho Employer quản lý phân công tin tuyển dụng.
 * Base URL: /api/v1/employer/job-assignments
 *
 * Quy tắc: 1 tin tuyển dụng chỉ có tối đa 1 NTD được phân công tại một thời
 * điểm.
 */
@RestController
@RequestMapping("/employer/job-assignments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerJobPostAssignmentController {

    private final JobPostAssignmentService assignmentService;
    private final CompanyService companyService;

    // =========================================================================
    // Giao việc
    // =========================================================================

    /**
     * POST /employer/job-assignments
     * Phân công tin tuyển dụng cho một NTD.
     * Body: { jobPostId, userId, note }
     * Yêu cầu quyền: job_assignment:manage
     */
    @PostMapping
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'job_assignment:manage')")
    public ResponseEntity<ResJobPostAssignmentDTO> assignJobPost(
            @Valid @RequestBody ReqAssignJobPostDTO request) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        ResJobPostAssignmentDTO result = assignmentService.assignJobPost(request, currentUserId, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * GET /employer/job-assignments/recruiters
     * Lấy danh sách NTD trong công ty kèm số tin đang quản lý.
     * Nếu truyền jobPostId: sẽ đánh dấu NTD đang quản lý tin đó (isCurrentAssignee = true).
     */
    @GetMapping("/recruiters")
    public ResponseEntity<ResultPaginationDTO> getRecruitersWithAssignmentCount(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long jobPostId,
            @PageableDefault(size = 20) Pageable pageable) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        return ResponseEntity.ok(
                assignmentService.getRecruitersWithAssignmentCount(companyId, keyword, jobPostId, pageable));
    }

    // =========================================================================
    // Quản lý theo tin tuyển dụng
    // =========================================================================

    /**
     * GET /employer/job-assignments/job-posts
     * Lấy danh sách tin tuyển dụng của công ty (có thông tin phân công).
     * Params: keyword, status, assigned (true/false/null)
     */
    @GetMapping("/job-posts")
    public ResponseEntity<ResultPaginationDTO> getJobPostsWithAssignment(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean assigned,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        return ResponseEntity.ok(
                assignmentService.getJobPostsWithAssignment(companyId, keyword, status, assigned, pageable));
    }

    /**
     * GET /employer/job-assignments/job-posts/{jobPostId}/current
     * Lấy NTD đang được phân công quản lý tin tuyển dụng cụ thể.
     */
    @GetMapping("/job-posts/{jobPostId}/current")
    public ResponseEntity<ResJobPostAssignmentDTO> getCurrentAssignment(
            @PathVariable Long jobPostId) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        return ResponseEntity.ok(assignmentService.getCurrentAssignment(jobPostId, companyId));
    }

    /**
     * GET /employer/job-assignments/job-posts/unassigned
     * Lấy danh sách tin tuyển dụng chưa được phân công cho member nào.
     * Params: keyword, status
     */
    @GetMapping("/job-posts/unassigned")
    public ResponseEntity<ResultPaginationDTO> getUnassignedJobPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        return ResponseEntity.ok(assignmentService.getUnassignedJobPosts(companyId, keyword, status, pageable));
    }

    // =========================================================================
    // Quản lý theo NTD
    // =========================================================================

    /**
     * GET /employer/job-assignments/recruiters/{userId}/job-posts
     * Lấy danh sách tin tuyển dụng đang được phân công cho một NTD.
     */
    @GetMapping("/recruiters/{userId}/job-posts")
    public ResponseEntity<ResultPaginationDTO> getJobPostsByRecruiter(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "assignedAt") Pageable pageable) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        return ResponseEntity.ok(assignmentService.getJobPostsByRecruiter(userId, companyId, pageable));
    }

    // =========================================================================
    // Thu hồi phân công
    // =========================================================================

    /**
     * PATCH /employer/job-assignments/revoke
     * Thu hồi phân công tin tuyển dụng.
     * Body: { jobPostId, note }
     * Yêu cầu quyền: job_assignment:manage
     */
    @PatchMapping("/revoke")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'job_assignment:manage')")
    public ResponseEntity<ResJobPostAssignmentDTO> revokeAssignment(
            @Valid @RequestBody ReqRevokeAssignmentDTO request) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        return ResponseEntity.ok(assignmentService.revokeAssignment(request, currentUserId, companyId));
    }

    // =========================================================================
    // Đổi người phân công
    // =========================================================================

    /**
     * PUT /employer/job-assignments/reassign
     * Đổi người phân công: thu hồi NTD cũ + giao cho NTD mới trong 1 bước.
     * Body: { jobPostId, userId, note }
     * Yêu cầu quyền: job_assignment:manage
     */
    @PutMapping("/reassign")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'job_assignment:manage')")
    public ResponseEntity<ResJobPostAssignmentDTO> reassignJobPost(
            @Valid @RequestBody ReqAssignJobPostDTO request) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(currentUserId);

        return ResponseEntity.ok(assignmentService.reassignJobPost(request, currentUserId, companyId));
    }
}
