import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ReqCreateInterviewRoundDTO,
  ReqUpdateInterviewRoundDTO,
  ResInterviewRoundDTO,
  ReqCreateInterviewScheduleDTO,
  ResInterviewScheduleDTO,
  ReqCreateInterviewSlotsDTO,
  ResInterviewSlotDTO,
  ReqUpdateInterviewScheduleDTO,
  ReqInterviewResultDTO,
  ResInterviewResultDTO,
  ResInterviewHistoryDTO,
  ResOverdueApplicationDTO,
  ReqExtendDeadlineDTO,
  ReqForceScheduleDTO,
  ReqOfferResultDTO,
  ReqCompleteRecruitmentDTO,
  ResEmployerInterviewStatisticsDTO
} from '@/types/interview.types';
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types';

const BASE_URL = '/employer';

const employerInterviewService = {
  // ─── Vòng phỏng vấn (Interview Rounds) ───────────────────────────────────

  async createRound(jobPostId: number, data: ReqCreateInterviewRoundDTO): Promise<ResInterviewRoundDTO> {
    const res = await axiosInstance.post<RestResponse<ResInterviewRoundDTO>>(
      `${BASE_URL}/job-postings/${jobPostId}/interview-rounds`,
      data
    );
    return res.data.data;
  },

  async getRounds(jobPostId: number): Promise<ResInterviewRoundDTO[]> {
    const res = await axiosInstance.get<RestResponse<ResInterviewRoundDTO[]>>(
      `${BASE_URL}/job-postings/${jobPostId}/interview-rounds`
    );
    return res.data.data;
  },

  async updateRound(roundId: number, data: ReqUpdateInterviewRoundDTO): Promise<ResInterviewRoundDTO> {
    const res = await axiosInstance.patch<RestResponse<ResInterviewRoundDTO>>(
      `${BASE_URL}/interview-rounds/${roundId}`,
      data
    );
    return res.data.data;
  },

  async deleteRound(roundId: number): Promise<void> {
    await axiosInstance.delete(`${BASE_URL}/interview-rounds/${roundId}`);
  },

  // ─── Lịch phỏng vấn (Interview Schedules) ────────────────────────────────

  async createSchedule(roundId: number, data: ReqCreateInterviewScheduleDTO): Promise<ResInterviewScheduleDTO> {
    const res = await axiosInstance.post<RestResponse<ResInterviewScheduleDTO>>(
      `${BASE_URL}/interview-rounds/${roundId}/schedules`,
      data
    );
    return res.data.data;
  },

  async createSlots(roundId: number, data: ReqCreateInterviewSlotsDTO): Promise<void> {
    await axiosInstance.post(`${BASE_URL}/interview-rounds/${roundId}/schedule-slots`, data);
  },

  async getSlots(roundId: number): Promise<ResInterviewSlotDTO[]> {
    const res = await axiosInstance.get<RestResponse<ResInterviewSlotDTO[]>>(
      `${BASE_URL}/interview-rounds/${roundId}/schedule-slots`
    );
    return res.data.data;
  },

  async getSchedules(jobPostId: number, params?: { roundId?: number; status?: string; search?: string }): Promise<ResInterviewScheduleDTO[]> {
    const res = await axiosInstance.get<RestResponse<ResInterviewScheduleDTO[]>>(
      `${BASE_URL}/job-postings/${jobPostId}/interview-schedules`,
      { params }
    );
    return res.data.data;
  },

  async updateSchedule(scheduleId: number, data: ReqUpdateInterviewScheduleDTO): Promise<ResInterviewScheduleDTO> {
    const res = await axiosInstance.put<RestResponse<ResInterviewScheduleDTO>>(
      `${BASE_URL}/interview-schedules/${scheduleId}`,
      data
    );
    return res.data.data;
  },

  async deleteSchedule(scheduleId: number): Promise<void> {
    await axiosInstance.delete(`${BASE_URL}/interview-schedules/${scheduleId}`);
  },

  // ─── Kết quả phỏng vấn (Interview Results) ───────────────────────────────

  async createResult(scheduleId: number, data: ReqInterviewResultDTO): Promise<ResInterviewResultDTO> {
    const res = await axiosInstance.post<RestResponse<ResInterviewResultDTO>>(
      `${BASE_URL}/interview-schedules/${scheduleId}/results`,
      data
    );
    return res.data.data;
  },

  async getResult(scheduleId: number): Promise<ResInterviewResultDTO> {
    const res = await axiosInstance.get<RestResponse<ResInterviewResultDTO>>(
      `${BASE_URL}/interview-schedules/${scheduleId}/results`
    );
    return res.data.data;
  },

  // ─── Lịch sử PV ──────────────────────────────────────────────────────────

  async getInterviewHistory(applicationId: number): Promise<ResInterviewHistoryDTO> {
    const res = await axiosInstance.get<RestResponse<ResInterviewHistoryDTO>>(
      `${BASE_URL}/applications/${applicationId}/interview-history`
    );
    return res.data.data;
  },

  // ─── Overdue ──────────────────────────────────────────────────────────────

  async getOverdueApplications(jobPostId: number): Promise<ResOverdueApplicationDTO[]> {
    const res = await axiosInstance.get<RestResponse<ResOverdueApplicationDTO[]>>(
      `${BASE_URL}/job-postings/${jobPostId}/overdue-applications`
    );
    return res.data.data;
  },

  async extendDeadline(applicationId: number, data: ReqExtendDeadlineDTO): Promise<void> {
    await axiosInstance.patch(`${BASE_URL}/applications/${applicationId}/extend-deadline`, data);
  },

  async forceSchedule(applicationId: number, data: ReqForceScheduleDTO): Promise<ResInterviewScheduleDTO> {
    const res = await axiosInstance.patch<RestResponse<ResInterviewScheduleDTO>>(
      `${BASE_URL}/applications/${applicationId}/force-schedule`,
      data
    );
    return res.data.data;
  },

  // ─── Offer ────────────────────────────────────────────────────────────────

  async updateOffer(applicationId: number, data: ReqOfferResultDTO): Promise<ResEmployerApplicationDTO> {
    const res = await axiosInstance.patch<RestResponse<ResEmployerApplicationDTO>>(
      `${BASE_URL}/applications/${applicationId}/offer`,
      data
    );
    return res.data.data;
  },

  // ─── Job interview phase ──────────────────────────────────────────────────

  async startInterviewing(jobPostId: number): Promise<void> {
    await axiosInstance.patch(`${BASE_URL}/job-postings/${jobPostId}/start-interviewing`);
  },

  async completeRecruitment(jobPostId: number, data: ReqCompleteRecruitmentDTO): Promise<void> {
    await axiosInstance.patch(`${BASE_URL}/job-postings/${jobPostId}/complete`, data);
  },

  // ─── Lọc UV theo trạng thái lịch ─────────────────────────────────────────

  async getPendingCandidates(roundId: number): Promise<ResInterviewScheduleDTO[]> {
    const res = await axiosInstance.get<RestResponse<ResInterviewScheduleDTO[]>>(
      `${BASE_URL}/interview-rounds/${roundId}/pending-candidates`
    );
    return res.data.data;
  },

  // ─── Nhắc nhở xác nhận lịch ──────────────────────────────────────────────

  async remindConfirmSchedule(scheduleId: number): Promise<void> {
    await axiosInstance.post(`${BASE_URL}/interview-schedules/${scheduleId}/remind`);
  },

  /**
   * GET /employer/interviews/interview-statistics
   * Lấy thống kê phỏng vấn của công ty.
   */
  async getInterviewStatistics(): Promise<ResEmployerInterviewStatisticsDTO> {
    const res = await axiosInstance.get<RestResponse<ResEmployerInterviewStatisticsDTO>>(
      `${BASE_URL}/interview-statistics`
    );
    return res.data.data;
  },
};

export default employerInterviewService;
