package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqCreateAppealDTO;
import com.topviec.topviec_be.dto.request.ReqSubmitAppealDTO;
import com.topviec.topviec_be.dto.response.ResAppealDTO;
import com.topviec.topviec_be.dto.response.ResEmployerComplaintDetailDTO;
import com.topviec.topviec_be.dto.response.ResMyViolationScoreDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.AppealService;
import com.topviec.topviec_be.service.ReportService;
import com.topviec.topviec_be.service.ViolationScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * NTD tự quan sát các khiếu nại nhắm vào tin tuyển dụng của mình.
 * Base URL: /employer/me
 */
@RestController
@RequestMapping("/employer/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerComplaintController {

    private final ReportService reportService;
    private final ViolationScoreService violationScoreService;
    private final AppealService appealService;

    /**
     * GET /employer/me/reports
     * Danh sách khiếu nại nhắm vào tin của NTD (ẩn danh người báo cáo).
     * search: mã báo cáo (VD: RP000001) hoặc tên tin tuyển dụng.
     * group: A | B
     * complaintType: wrong_info | spam | inappropriate | fraudulent | payment_issue | other
     * status: pending | processing | waiting_employer | resolved | rejected | auto_closed
     */
    @GetMapping("/reports")
    public ResponseEntity<ResultPaginationDTO> getMyReports(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String complaintType,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(reportService.getEmployerReports(
                extractUserId(jwt), search, status, group, complaintType, pageable));
    }

    /**
     * GET /employer/me/reports/{id}
     * Chi tiết một khiếu nại — chỉ cho phép nếu tin thuộc công ty của NTD.
     */
    @GetMapping("/reports/{id}")
    public ResponseEntity<ResEmployerComplaintDetailDTO> getReportDetail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        return ResponseEntity.ok(reportService.getEmployerReportDetail(extractUserId(jwt), id));
    }

    /**
     * GET /employer/me/job-posts/{jobPostId}/reports
     * Danh sách khiếu nại của 1 tin tuyển dụng thuộc công ty NTD.
     */
    @GetMapping("/job-posts/{jobPostId}/reports")
    public ResponseEntity<ResultPaginationDTO> getReportsByJobPost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long jobPostId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String complaintType,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(reportService.getEmployerReportsByJobPost(
                extractUserId(jwt), jobPostId, status, group, complaintType, pageable));
    }

    /**
     * GET /employer/me/reports/{id}/appeal
     * Kháng cáo của NTD cho khiếu nại {id} — chỉ cho phép nếu tin thuộc công ty NTD.
     */
    @GetMapping("/reports/{id}/appeal")
    public ResponseEntity<ResAppealDTO> getAppealByComplaint(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        return appealService.getByComplaintAsEmployer(extractUserId(jwt), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * GET /employer/me/appeals
     * NTD xem toàn bộ danh sách kháng cáo của chính mình.
     */
    @GetMapping("/appeals")
    public ResponseEntity<java.util.List<ResAppealDTO>> getMyAppeals(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(appealService.getByEmployer(extractUserId(jwt)));
    }

    /**
     * GET /employer/me/violation-score
     * NTD xem điểm vi phạm hiện tại của mình.
     */
    @GetMapping("/violation-score")
    public ResponseEntity<ResMyViolationScoreDTO> getMyViolationScore(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(violationScoreService.getMyScore(extractUserId(jwt)));
    }

    /**
     * POST /employer/me/reports/{id}/respond
     * NTD xác nhận đã sửa tin (nhóm A) → trigger tự đóng báo cáo.
     */
    @PostMapping("/reports/{id}/respond")
    @LogAction(LogActionType.RESPOND_TO_REPORT)
    public ResponseEntity<ResEmployerComplaintDetailDTO> respondToReport(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        return ResponseEntity.ok(reportService.respondToReport(extractUserId(jwt), id));
    }

    /**
     * POST /employer/me/reports/{id}/appeal
     * NTD nộp kháng cáo cho báo cáo nhóm B đã bị xử lý (resolved).
     * Chỉ kháng cáo được 1 lần mỗi báo cáo.
     */
    @PostMapping("/reports/{id}/appeal")
    @LogAction(LogActionType.EMPLOYER_SUBMIT_APPEAL)
    public ResponseEntity<ResAppealDTO> submitAppeal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody ReqSubmitAppealDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appealService.create(extractUserId(jwt), new ReqCreateAppealDTO(id, request.getContent())));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
