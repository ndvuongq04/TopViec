package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqCreateReportDTO;
import com.topviec.topviec_be.dto.response.ResReportDetailDTO;
import com.topviec.topviec_be.dto.response.ResViolationReasonDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/candidate/reports")
@RequiredArgsConstructor
public class CandidateReportController {

    private final ReportService reportService;

    @PostMapping
    @LogAction(LogActionType.CREATE_REPORT)
    public ResponseEntity<ResReportDetailDTO> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqCreateReportDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.create(extractUserId(jwt), request));
    }

    @GetMapping("/violation-reasons")
    public ResponseEntity<List<ResViolationReasonDTO>> getViolationReasons() {
        return ResponseEntity.ok(reportService.getViolationReasons());
    }

    @GetMapping("/my")
    public ResponseEntity<ResultPaginationDTO> getMyReports(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(reportService.getMyReports(extractUserId(jwt), status, pageable));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
