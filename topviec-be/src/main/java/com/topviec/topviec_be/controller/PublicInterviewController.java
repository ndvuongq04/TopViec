package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.service.InterviewService;
import com.topviec.topviec_be.dto.response.ResConfirmUpdateInfoDTO;
import com.topviec.topviec_be.dto.response.ResInterviewHistoryDTO;
import com.topviec.topviec_be.dto.response.ResInterviewRoundDTO;
import com.topviec.topviec_be.dto.response.ResInterviewScheduleDTO;
import com.topviec.topviec_be.dto.response.ResSlotSelectionPageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;

/**
 * Controller public — UV xác nhận chọn slot PV (không cần đăng nhập).
 * Base URL: /api/v1/interview-schedules
 */
@RestController
@RequestMapping("/interview-schedules")
@RequiredArgsConstructor
public class PublicInterviewController {

    private final InterviewService interviewService;

    /**
     * GET /interview-schedules/slots?token=xxx
     * Lấy danh sách slot còn chỗ để FE hiển thị trang chọn lịch (Public, không cần Auth).
     */
    @GetMapping("/slots")
    public ResponseEntity<ResSlotSelectionPageDTO> getSlotsByToken(@RequestParam String token) {
        return ResponseEntity.ok(interviewService.getSlotsByToken(token));
    }

    /**
     * GET /interview-schedules/confirm?token=xxx&slotId=123
     * UV click link từ email để chọn slot. Không cần auth.
     */
    @GetMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmSlot(
            @RequestParam String token,
            @RequestParam Long slotId) {

        String message = interviewService.confirmSlot(token, slotId);
        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * GET /interview-schedules/applications/{applicationId}
     * Lấy danh sách lịch phỏng vấn của ứng viên trong đơn ứng tuyển này (yêu cầu
     * đăng nhập)
     */
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<List<ResInterviewScheduleDTO>> getMyInterviews(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(interviewService.getMyInterviews(userId, applicationId));
    }

    /**
     * GET /interview-schedules/applications/{applicationId}/history
     * Lấy lịch sử phỏng vấn của ứng viên trong đơn ứng tuyển này (yêu cầu đăng
     * nhập)
     */
    @GetMapping("/applications/{applicationId}/history")
    public ResponseEntity<ResInterviewHistoryDTO> getMyInterviewHistory(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(interviewService.getMyInterviewHistory(userId, applicationId));
    }

    /**
     * GET /interview-schedules/interview-rounds/{roundId}
     * Lấy thông tin chi tiết 1 vòng phỏng vấn
     */
    @GetMapping("/interview-rounds/{roundId}")
    public ResponseEntity<ResInterviewRoundDTO> getRoundDetail(
            @PathVariable Long roundId) {
        return ResponseEntity.ok(interviewService.getRoundDetail(roundId));
    }

    /**
     * PATCH /interview-schedules/{scheduleId}/confirm
     * UV đã đăng nhập xác nhận lịch PV trực tiếp trên hệ thống (yêu cầu đăng nhập).
     */
    @PatchMapping("/{scheduleId}/confirm")
    @LogAction(LogActionType.CONFIRM_INTERVIEW_SCHEDULE)
    public ResponseEntity<Map<String, String>> confirmScheduleByCandidate(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        String message = interviewService.confirmScheduleByCandidate(scheduleId, userId);
        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * GET /interview-schedules/confirm-update/info?token=xxx
     * Lấy thông tin lịch PV để FE hiển thị trước khi UV xác nhận (Public, không cần
     * Auth).
     */
    @GetMapping("/confirm-update/info")
    public ResponseEntity<ResConfirmUpdateInfoDTO> getConfirmUpdateInfo(
            @RequestParam String token) {
        return ResponseEntity.ok(interviewService.getConfirmUpdateInfo(token));
    }

    /**
     * PATCH /interview-schedules/confirm-update?token=xxx
     * UV xác nhận lịch PV NTT vừa cập nhật (Public, không cần Auth).
     */
    @PatchMapping("/confirm-update")
    @LogAction(LogActionType.CONFIRM_UPDATED_INTERVIEW_SCHEDULE)
    public ResponseEntity<Map<String, String>> confirmUpdatedSchedule(
            @RequestParam String token) {
        String message = interviewService.confirmUpdatedSchedule(token);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
