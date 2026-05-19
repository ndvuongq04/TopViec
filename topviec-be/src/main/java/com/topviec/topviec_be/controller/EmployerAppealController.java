package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.ReqCreateAppealDTO;
import com.topviec.topviec_be.dto.response.ResAppealDTO;
import com.topviec.topviec_be.service.AppealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * NTD nộp kháng cáo sau khi bị xử lý vi phạm nhóm B.
 * Base URL: /employer/appeals
 */
@RestController
@RequestMapping("/employer/appeals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerAppealController {

    private final AppealService appealService;

    /**
     * POST /employer/appeals
     * NTD nộp kháng cáo cho một báo cáo nhóm B đã bị xử lý (resolved).
     * Chỉ được kháng cáo 1 lần mỗi báo cáo.
     */
    @PostMapping
    @LogAction(LogActionType.CREATE_APPEAL)
    public ResponseEntity<ResAppealDTO> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqCreateAppealDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appealService.create(extractUserId(jwt), request));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
