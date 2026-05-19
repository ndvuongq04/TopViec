import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ResInterviewScheduleDTO,
  ResInterviewHistoryDTO,
  ResInterviewRoundDTO,
  ResConfirmUpdateInfoDTO,
  ResSlotSelectionPageDTO,
} from '@/types/interview.types';

const BASE_URL = '/interview-schedules';

const publicInterviewService = {
  /**
   * UV xác nhận chọn slot PV (không cần đăng nhập).
   */
  async confirmSlot(token: string, slotId: number): Promise<{ message: string }> {
    const res = await axiosInstance.get<RestResponse<{ message: string }>>(
      `${BASE_URL}/confirm`,
      { params: { token, slotId } }
    );
    return res.data.data;
  },

  /**
   * Lấy danh sách lịch phỏng vấn của ứng viên trong đơn ứng tuyển (yêu cầu đăng nhập)
   * GET /interview-schedules/applications/:applicationId
   */
  async getMyInterviews(applicationId: number): Promise<ResInterviewScheduleDTO[]> {
    const res = await axiosInstance.get<RestResponse<ResInterviewScheduleDTO[]>>(
      `${BASE_URL}/applications/${applicationId}`
    );
    return res.data.data;
  },

  /**
   * Lấy lịch sử phỏng vấn của ứng viên trong đơn ứng tuyển (yêu cầu đăng nhập)
   * GET /interview-schedules/applications/:applicationId/history
   */
  async getMyInterviewHistory(applicationId: number): Promise<ResInterviewHistoryDTO> {
    const res = await axiosInstance.get<RestResponse<ResInterviewHistoryDTO>>(
      `${BASE_URL}/applications/${applicationId}/history`
    );
    return res.data.data;
  },

  /**
   * Lấy thông tin lịch PV để hiển thị trước khi UV xác nhận (Public, không cần Auth).
   * GET /interview-schedules/confirm-update/info?token=xxx
   */
  async getConfirmUpdateInfo(token: string): Promise<ResConfirmUpdateInfoDTO> {
    const res = await axiosInstance.get<RestResponse<ResConfirmUpdateInfoDTO>>(
      `${BASE_URL}/confirm-update/info`,
      { params: { token } }
    );
    return res.data.data;
  },

  /**
   * UV xác nhận lịch PV NĐT vừa cập nhật (Public, không cần Auth).
   * PATCH /interview-schedules/confirm-update?token=xxx
   */
  async confirmUpdatedSchedule(token: string): Promise<{ message: string }> {
    const res = await axiosInstance.patch<RestResponse<{ message: string }>>(
      `${BASE_URL}/confirm-update`,
      null,
      { params: { token } }
    );
    return res.data.data;
  },

  /**
   * UV đã đăng nhập xác nhận lịch PV trực tiếp trên hệ thống (yêu cầu đăng nhập).
   * PATCH /interview-schedules/:scheduleId/confirm
   */
  async confirmScheduleByCandidate(scheduleId: number): Promise<{ message: string }> {
    const res = await axiosInstance.patch<RestResponse<{ message: string }>>(
      `${BASE_URL}/${scheduleId}/confirm`
    );
    return res.data.data;
  },

  /**
   * Lấy danh sách slot còn chỗ để hiển thị trang chọn lịch (Public, không cần Auth).
   * GET /interview-schedules/slots?token=xxx
   */
  async getSlotsByToken(token: string): Promise<ResSlotSelectionPageDTO> {
    const res = await axiosInstance.get<RestResponse<ResSlotSelectionPageDTO>>(
      `${BASE_URL}/slots`,
      { params: { token } }
    );
    return res.data.data;
  },

  /**
   * Lấy thông tin chi tiết 1 vòng phỏng vấn
   * GET /interview-schedules/interview-rounds/:roundId
   */
  async getRoundDetail(roundId: number): Promise<ResInterviewRoundDTO> {
    const res = await axiosInstance.get<RestResponse<ResInterviewRoundDTO>>(
      `${BASE_URL}/interview-rounds/${roundId}`
    );
    return res.data.data;
  },
};

export default publicInterviewService;
