package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqApplyJobDTO;
import com.topviec.topviec_be.dto.request.ReqBulkApplyDTO;
import com.topviec.topviec_be.dto.request.ReqWithdrawApplicationDTO;
import com.topviec.topviec_be.dto.response.ResApplicationDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import com.topviec.topviec_be.dto.request.ReqUpdateApplicationCvDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho ứng viên — yêu cầu đăng nhập.
 * Base URL: /api/v1/applications
 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class PublicApplicationController {

    private final ApplicationService applicationService;

    /**
     * POST /applications/{jobPostId}
     * CN-UV-010: Nộp đơn đầy đủ
     * `cvId` hỗ trợ cả CV online lẫn CV tải lên.
     */
    @PostMapping("/{jobPostId}")
    @LogAction(LogActionType.APPLY_JOB)
    public ResponseEntity<ResApplicationDTO> apply(
            @PathVariable Long jobPostId,
            @RequestBody ReqApplyJobDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(applicationService.apply(extractUserId(jwt), jobPostId, request));
    }

    /**
     * POST /applications/{jobPostId}/quick
     * CN-UV-011: Ứng tuyển nhanh (CV mặc định)
     * CV mặc định có thể là CV online hoặc CV tải lên.
     */
    @PostMapping("/{jobPostId}/quick")
    @LogAction(LogActionType.QUICK_APPLY_JOB)
    public ResponseEntity<ResApplicationDTO> quickApply(
            @PathVariable Long jobPostId,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(applicationService.quickApply(extractUserId(jwt), jobPostId));
    }

    /**
     * POST /applications/bulk
     * CN-UV-012: Ứng tuyển hàng loạt (tối đa 10 tin)
     */
    @PostMapping("/bulk")
    @LogAction(LogActionType.BULK_APPLY_JOB)
    public ResponseEntity<List<ResApplicationDTO>> bulkApply(
            @RequestBody ReqBulkApplyDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(applicationService.bulkApply(extractUserId(jwt), request));
    }

    /**
     * GET /applications/me/with-interviews
     * Lấy danh sách đơn của UV có ít nhất 1 lịch PV (dùng cho trang "Lịch PV của tôi").
     * Trả về đúng bất kể application status (interviewing / schedule_pending / overdue / ...).
     */
    @GetMapping("/me/with-interviews")
    public ResponseEntity<List<ResApplicationDTO>> getMyApplicationsWithInterviews(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                applicationService.getMyApplicationsWithInterviews(extractUserId(jwt)));
    }

    /**
     * GET /applications/me
     * CN-UV-013: Theo dõi trạng thái đơn ứng tuyển
     */
    @GetMapping("/me")
    public ResponseEntity<ResultPaginationDTO> getMyApplications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getMyApplications(extractUserId(jwt), status, pageable));
    }

    /**
     * PATCH /applications/{applicationId}/withdraw
     * CN-UV-015: Rút đơn ứng tuyển
     */
    @PatchMapping("/{applicationId}/withdraw")
    @LogAction(LogActionType.WITHDRAW_APPLICATION)
    public ResponseEntity<ResApplicationDTO> withdraw(
            @PathVariable Long applicationId,
            @RequestBody(required = false) ReqWithdrawApplicationDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        if (request == null)
            request = new ReqWithdrawApplicationDTO();

        return ResponseEntity.ok(
                applicationService.withdraw(extractUserId(jwt), applicationId, request));
    }

    /**
     * PATCH /applications/{applicationId}/cv
     * Cập nhật CV cho đơn ứng tuyển (chỉ khi đang pending)
     * `cvId` hỗ trợ cả CV online lẫn CV tải lên.
     */
    @PatchMapping("/{applicationId}/cv")
    @LogAction(LogActionType.UPDATE_APPLICATION_CV)
    public ResponseEntity<ResApplicationDTO> updateApplicationCv(
            @PathVariable Long applicationId,
            @Valid @RequestBody ReqUpdateApplicationCvDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                applicationService.updateApplicationCv(extractUserId(jwt), applicationId, request));
    }

    /**
     * PATCH /applications/{applicationId}/accept-invite
     * UV chấp nhận lời mời từ talent pool (INVITED → PENDING)
     */
    @PatchMapping("/{applicationId}/accept-invite")
    @LogAction(LogActionType.ACCEPT_TALENT_POOL_INVITE)
    public ResponseEntity<ResApplicationDTO> acceptInvite(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                applicationService.acceptInvite(extractUserId(jwt), applicationId));
    }

    /**
     * PATCH /applications/{applicationId}/decline-invite
     * UV từ chối lời mời từ talent pool (INVITED → WITHDRAWN)
     */
    @PatchMapping("/{applicationId}/decline-invite")
    @LogAction(LogActionType.DECLINE_TALENT_POOL_INVITE)
    public ResponseEntity<ResApplicationDTO> declineInvite(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                applicationService.declineInvite(extractUserId(jwt), applicationId));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
