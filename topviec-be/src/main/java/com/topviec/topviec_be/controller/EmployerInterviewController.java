package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.request.*;
import com.topviec.topviec_be.dto.response.*;
import com.topviec.topviec_be.dto.response.ResEmployerInterviewStatisticsDTO;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.InterviewService;
import com.topviec.topviec_be.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller dành cho Employer — quản lý phỏng vấn.
 * Base URL: /api/v1/employer
 */
@RestController
@RequestMapping("/employer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerInterviewController {

    private final InterviewService interviewService;
    private final CompanyService companyService;

    // ── Vòng phỏng vấn ────────────────────────────────────────────────────────

    @PostMapping("/job-postings/{jobPostId}/interview-rounds")
    @LogAction(LogActionType.CREATE_INTERVIEW_ROUND)
    @PreAuthorize("@employerPerm.canInviteInterviewForJob(authentication, #jobPostId)")
    public ResponseEntity<ResInterviewRoundDTO> createRound(
            @PathVariable Long jobPostId,
            @Valid @RequestBody ReqCreateInterviewRoundDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(interviewService.createRound(jobPostId, userId, companyId, request));
    }

    @GetMapping("/job-postings/{jobPostId}/interview-rounds")
    @PreAuthorize("@employerPerm.canViewApplications(authentication, #jobPostId)")
    public ResponseEntity<List<ResInterviewRoundDTO>> getRounds(
            @PathVariable Long jobPostId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.getRounds(jobPostId, companyId));
    }

    @PatchMapping("/interview-rounds/{roundId}")
    @LogAction(LogActionType.UPDATE_INTERVIEW_ROUND)
    @PreAuthorize("@employerPerm.canInviteInterviewForRound(authentication, #roundId)")
    public ResponseEntity<ResInterviewRoundDTO> updateRound(
            @PathVariable Long roundId,
            @Valid @RequestBody ReqUpdateInterviewRoundDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.updateRound(roundId, userId, companyId, request));
    }

    @DeleteMapping("/interview-rounds/{roundId}")
    @LogAction(LogActionType.DELETE_INTERVIEW_ROUND)
    @PreAuthorize("@employerPerm.canInviteInterviewForRound(authentication, #roundId)")
    public ResponseEntity<Void> deleteRound(@PathVariable Long roundId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        interviewService.deleteRound(roundId, userId, companyId);
        return ResponseEntity.noContent().build();
    }

    // ── Lịch phỏng vấn ───────────────────────────────────────────────────────

    @PostMapping("/interview-rounds/{roundId}/schedules")
    @LogAction(LogActionType.CREATE_INTERVIEW_SCHEDULE)
    @PreAuthorize("@employerPerm.canInviteInterviewForRound(authentication, #roundId)")
    public ResponseEntity<ResInterviewScheduleDTO> createSchedule(
            @PathVariable Long roundId,
            @Valid @RequestBody ReqCreateInterviewScheduleDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(interviewService.createSchedule(roundId, userId, companyId, request));
    }

    @GetMapping("/interview-rounds/{roundId}/schedule-slots")
    @PreAuthorize("@employerPerm.canInviteInterviewForRound(authentication, #roundId)")
    public ResponseEntity<List<ResInterviewSlotDTO>> getSlots(
            @PathVariable Long roundId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.getSlots(roundId, companyId));
    }

    @PostMapping("/interview-rounds/{roundId}/schedule-slots")
    @PreAuthorize("@employerPerm.canInviteInterviewForRound(authentication, #roundId)")
    public ResponseEntity<Void> createSlots(
            @PathVariable Long roundId,
            @Valid @RequestBody ReqCreateInterviewSlotsDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        interviewService.createSlots(roundId, userId, companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/job-postings/{jobPostId}/interview-schedules")
    @PreAuthorize("@employerPerm.canViewApplications(authentication, #jobPostId)")
    public ResponseEntity<List<ResInterviewScheduleDTO>> getSchedules(
            @PathVariable Long jobPostId,
            @RequestParam(required = false) Long roundId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.getSchedules(jobPostId, companyId, roundId, status, search));
    }

    @PutMapping("/interview-schedules/{scheduleId}")
    @LogAction(LogActionType.UPDATE_INTERVIEW_SCHEDULE)
    @PreAuthorize("@employerPerm.canInviteInterviewForSchedule(authentication, #scheduleId)")
    public ResponseEntity<ResInterviewScheduleDTO> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ReqUpdateInterviewScheduleDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.updateSchedule(scheduleId, userId, companyId, request));
    }

    @DeleteMapping("/interview-schedules/{scheduleId}")
    @LogAction(LogActionType.DELETE_INTERVIEW_SCHEDULE)
    @PreAuthorize("@employerPerm.canInviteInterviewForSchedule(authentication, #scheduleId)")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        interviewService.deleteSchedule(scheduleId, userId, companyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/interview-schedules/{scheduleId}/remind")
    @PreAuthorize("@employerPerm.canInviteInterviewForSchedule(authentication, #scheduleId)")
    public ResponseEntity<Void> remindConfirmSchedule(@PathVariable Long scheduleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        interviewService.remindConfirmSchedule(scheduleId, userId, companyId);
        return ResponseEntity.ok().build();
    }

    // ── Kết quả phỏng vấn ────────────────────────────────────────────────────

    @PostMapping("/interview-schedules/{scheduleId}/results")
    @LogAction(LogActionType.CREATE_INTERVIEW_RESULT)
    @PreAuthorize("@employerPerm.canRecordInterviewForSchedule(authentication, #scheduleId)")
    public ResponseEntity<ResInterviewResultDTO> createResult(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ReqInterviewResultDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(interviewService.createResult(scheduleId, userId, companyId, request));
    }

    @GetMapping("/interview-schedules/{scheduleId}/results")
    @PreAuthorize("@employerPerm.canInviteInterviewForSchedule(authentication, #scheduleId)")
    public ResponseEntity<ResInterviewResultDTO> getResult(
            @PathVariable Long scheduleId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.getResult(scheduleId, companyId));
    }

    // ── Lịch sử PV ──────────────────────────────────────────────────────────

    @GetMapping("/applications/{applicationId}/interview-history")
    @PreAuthorize("@employerPerm.canViewApplication(authentication, #applicationId)")
    public ResponseEntity<ResInterviewHistoryDTO> getInterviewHistory(
            @PathVariable Long applicationId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.getInterviewHistory(applicationId, companyId));
    }

    // ── Lọc UV theo trạng thái lịch ─────────────────────────────────────────

    @GetMapping("/interview-rounds/{roundId}/pending-candidates")
    @PreAuthorize("@employerPerm.canInviteInterviewForRound(authentication, #roundId)")
    public ResponseEntity<List<ResInterviewScheduleDTO>> getPendingCandidates(
            @PathVariable Long roundId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.getPendingCandidates(roundId, companyId));
    }

    // ── Overdue ──────────────────────────────────────────────────────────────

    @GetMapping("/job-postings/{jobPostId}/overdue-applications")
    @PreAuthorize("@employerPerm.canViewApplications(authentication, #jobPostId)")
    public ResponseEntity<List<ResOverdueApplicationDTO>> getOverdueApplications(
            @PathVariable Long jobPostId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.getOverdueApplications(jobPostId, companyId));
    }

    @PatchMapping("/applications/{applicationId}/extend-deadline")
    @PreAuthorize("@employerPerm.canRecordInterviewForApplication(authentication, #applicationId)")
    public ResponseEntity<Void> extendDeadline(
            @PathVariable Long applicationId,
            @Valid @RequestBody ReqExtendDeadlineDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        interviewService.extendDeadline(applicationId, userId, companyId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/applications/{applicationId}/force-schedule")
    @PreAuthorize("@employerPerm.canRecordInterviewForApplication(authentication, #applicationId)")
    public ResponseEntity<ResInterviewScheduleDTO> forceSchedule(
            @PathVariable Long applicationId,
            @Valid @RequestBody ReqForceScheduleDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.forceSchedule(applicationId, userId, companyId, request));
    }

    // ── Offer ────────────────────────────────────────────────────────────────

    @PatchMapping("/applications/{applicationId}/offer")
    @LogAction(LogActionType.SEND_OFFER)
    @PreAuthorize("@employerPerm.canRecordInterviewForApplication(authentication, #applicationId)")
    public ResponseEntity<ResEmployerApplicationDTO> updateOffer(
            @PathVariable Long applicationId,
            @Valid @RequestBody ReqOfferResultDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.updateOffer(applicationId, userId, companyId, request));
    }

    // ── Job interview phase ──────────────────────────────────────────────────

    @GetMapping("/job-postings/{jobPostId}/interview-readiness")
    @PreAuthorize("@employerPerm.canViewApplications(authentication, #jobPostId)")
    public ResponseEntity<ResInterviewReadinessDTO> checkReadiness(
            @PathVariable Long jobPostId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        return ResponseEntity.ok(interviewService.checkReadiness(jobPostId, companyId));
    }

    @PatchMapping("/job-postings/{jobPostId}/start-interviewing")
    @LogAction(LogActionType.START_INTERVIEW_PHASE)
    @PreAuthorize("@employerPerm.canRecordInterviewForJob(authentication, #jobPostId)")
    public ResponseEntity<Void> startInterviewing(
            @PathVariable Long jobPostId) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        interviewService.startInterviewing(jobPostId, userId, companyId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/job-postings/{jobPostId}/complete")
    @LogAction(LogActionType.COMPLETE_RECRUITMENT)
    @PreAuthorize("@employerPerm.canRecordInterviewForJob(authentication, #jobPostId)")
    public ResponseEntity<Void> completeRecruitment(
            @PathVariable Long jobPostId,
            @Valid @RequestBody ReqCompleteRecruitmentDTO request) {

        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);

        interviewService.completeRecruitment(jobPostId, userId, companyId, request);
        return ResponseEntity.ok().build();
    }

    // ── Thống kê phỏng vấn ───────────────────────────────────────────────────

    @GetMapping("/interview-statistics")
    public ResponseEntity<ResEmployerInterviewStatisticsDTO> getInterviewStatistics() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long companyId = companyService.getCompanyIdByUserId(userId);
        return ResponseEntity.ok(interviewService.getEmployerInterviewStatistics(companyId));
    }
}
