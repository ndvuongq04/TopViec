package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.*;
import com.topviec.topviec_be.dto.response.*;

import java.util.List;

public interface InterviewService {

        // ── Vòng phỏng vấn ────────────────────────────────────────────────────────

        ResInterviewRoundDTO createRound(Long jobPostId, Long userId, Long companyId,
                        ReqCreateInterviewRoundDTO request);

        List<ResInterviewRoundDTO> getRounds(Long jobPostId, Long companyId);

        /** Lấy chi tiết 1 vòng PV (dành cho candidate / public) */
        ResInterviewRoundDTO getRoundDetail(Long roundId);

        ResInterviewRoundDTO updateRound(Long roundId, Long userId, Long companyId,
                        ReqUpdateInterviewRoundDTO request);

        void deleteRound(Long roundId, Long userId, Long companyId);

        // ── Lịch phỏng vấn ───────────────────────────────────────────────────────

        /** Cách 1: NTT đặt lịch thủ công */
        ResInterviewScheduleDTO createSchedule(Long roundId, Long userId, Long companyId,
                        ReqCreateInterviewScheduleDTO request);

        /** Cách 2: Tạo slot để UV chọn */
        void createSlots(Long roundId, Long userId, Long companyId,
                        ReqCreateInterviewSlotsDTO request);

        /** Lấy danh sách slot của 1 vòng PV */
        List<ResInterviewSlotDTO> getSlots(Long roundId, Long companyId);

        /** UV xác nhận chọn slot (public, không cần auth) */
        String confirmSlot(String token, Long slotId);

        /**
         * Lấy danh sách slot còn chỗ theo token (public, dành cho trang chọn slot của
         * UV)
         */
        ResSlotSelectionPageDTO getSlotsByToken(String token);

        /** Lấy thông tin lịch PV qua token để FE hiển thị trước khi UV xác nhận */
        ResConfirmUpdateInfoDTO getConfirmUpdateInfo(String token);

        /** UV đã đăng nhập xác nhận lịch trực tiếp trên hệ thống */
        String confirmScheduleByCandidate(Long scheduleId, Long userId);

        /** UV xác nhận lịch (public thông qua link email) */
        String confirmUpdatedSchedule(String token);

        /** Danh sách lịch PV của 1 tin */
        List<ResInterviewScheduleDTO> getSchedules(Long jobPostId, Long companyId,
                        Long roundId, String status, String search);

        /** Lấy lịch PV của 1 đơn ứng tuyển (dành cho candidate) */
        List<ResInterviewScheduleDTO> getMyInterviews(Long userId, Long applicationId);

        /** Nhắc nhở UV xác nhận lịch PV (gửi lại email xác nhận) */
        void remindConfirmSchedule(Long scheduleId, Long userId, Long companyId);

        /** Sửa lịch PV */
        ResInterviewScheduleDTO updateSchedule(Long scheduleId, Long userId, Long companyId,
                        ReqUpdateInterviewScheduleDTO request);

        /** Hủy lịch PV */
        void deleteSchedule(Long scheduleId, Long userId, Long companyId);

        // ── Kết quả phỏng vấn ────────────────────────────────────────────────────

        ResInterviewResultDTO createResult(Long scheduleId, Long userId, Long companyId,
                        ReqInterviewResultDTO request);

        ResInterviewResultDTO getResult(Long scheduleId, Long companyId);

        // ── Lịch sử PV ──────────────────────────────────────────────────────────

        ResInterviewHistoryDTO getInterviewHistory(Long applicationId, Long companyId);

        /** Lấy lịch sử PV của UV (dành cho candidate) */
        ResInterviewHistoryDTO getMyInterviewHistory(Long userId, Long applicationId);

        // ── Lọc UV theo trạng thái lịch ─────────────────────────────────────────

        /** UV chưa có lịch PV thật HOẶC chưa được gửi slot trong vòng này */
        List<ResInterviewScheduleDTO> getPendingCandidates(Long roundId, Long companyId);

        // ── Overdue ──────────────────────────────────────────────────────────────

        List<ResOverdueApplicationDTO> getOverdueApplications(Long jobPostId, Long companyId);

        void extendDeadline(Long applicationId, Long userId, Long companyId, ReqExtendDeadlineDTO request);

        ResInterviewScheduleDTO forceSchedule(Long applicationId, Long userId, Long companyId,
                        ReqForceScheduleDTO request);

        // ── Offer ────────────────────────────────────────────────────────────────

        ResEmployerApplicationDTO updateOffer(Long applicationId, Long userId, Long companyId,
                        ReqOfferResultDTO request);

        // ── Job Posting interview phase ──────────────────────────────────────────

        ResInterviewReadinessDTO checkReadiness(Long jobPostId, Long companyId);

        void startInterviewing(Long jobPostId, Long userId, Long companyId);

        void completeRecruitment(Long jobPostId, Long userId, Long companyId,
                        ReqCompleteRecruitmentDTO request);

        // ── Thống kê phỏng vấn ───────────────────────────────────────────────────

        com.topviec.topviec_be.dto.response.ResEmployerInterviewStatisticsDTO getEmployerInterviewStatistics(Long companyId);
}
