import { defineStore } from 'pinia';
import { ref } from 'vue';
import employerInterviewService from '@/services/employerInterview.service';
import type {
  ResInterviewRoundDTO,
  ResInterviewScheduleDTO,
  ResInterviewResultDTO,
  ResInterviewHistoryDTO,
  ResOverdueApplicationDTO,
  ReqCreateInterviewRoundDTO,
  ReqUpdateInterviewRoundDTO,
  ReqCreateInterviewScheduleDTO,
  ReqCreateInterviewSlotsDTO,
  ReqUpdateInterviewScheduleDTO,
  ReqInterviewResultDTO,
  ReqExtendDeadlineDTO,
  ReqForceScheduleDTO,
  ReqOfferResultDTO,
  ReqCompleteRecruitmentDTO,
  ResEmployerInterviewStatisticsDTO
} from '@/types/interview.types';

export const useEmployerInterviewStore = defineStore('employerInterview', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const rounds = ref<ResInterviewRoundDTO[]>([]);
  const schedules = ref<ResInterviewScheduleDTO[]>([]);
  const currentResult = ref<ResInterviewResultDTO | null>(null);
  const interviewHistory = ref<ResInterviewHistoryDTO | null>(null);
  const overdueApplications = ref<ResOverdueApplicationDTO[]>([]);
  const interviewStatistics = ref<ResEmployerInterviewStatisticsDTO | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // ─── Helpers ────────────────────────────────────────────────────────────────
  function setError(err: unknown) {
    const data = (err as any)?.response?.data;
    if (data?.message && typeof data.message === 'object') {
      const firstError = Object.values(data.message)[0];
      error.value = String(firstError);
    } else {
      error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.';
    }
  }

  // ─── Actions ────────────────────────────────────────────────────────────────

  // --- Rounds ---
  async function fetchRounds(jobPostId: number) {
    loading.value = true;
    error.value = null;
    try {
      rounds.value = await employerInterviewService.getRounds(jobPostId);
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function createRound(jobPostId: number, data: ReqCreateInterviewRoundDTO) {
    loading.value = true;
    error.value = null;
    try {
      const newRound = await employerInterviewService.createRound(jobPostId, data);
      rounds.value.push(newRound);
      return newRound;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function updateRound(roundId: number, data: ReqUpdateInterviewRoundDTO) {
    loading.value = true;
    error.value = null;
    try {
      const updatedRound = await employerInterviewService.updateRound(roundId, data);
      const index = rounds.value.findIndex(r => r.id === roundId);
      if (index !== -1) rounds.value[index] = updatedRound;
      return updatedRound;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function deleteRound(roundId: number) {
    loading.value = true;
    error.value = null;
    try {
      await employerInterviewService.deleteRound(roundId);
      rounds.value = rounds.value.filter(r => r.id !== roundId);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  // --- Schedules ---
  async function fetchSchedules(jobPostId: number, params?: { roundId?: number; status?: string }) {
    loading.value = true;
    error.value = null;
    try {
      schedules.value = await employerInterviewService.getSchedules(jobPostId, params);
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function createSchedule(roundId: number, data: ReqCreateInterviewScheduleDTO) {
    loading.value = true;
    error.value = null;
    try {
      const newSchedule = await employerInterviewService.createSchedule(roundId, data);
      schedules.value.push(newSchedule);
      return newSchedule;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function createSlots(roundId: number, data: ReqCreateInterviewSlotsDTO) {
    loading.value = true;
    error.value = null;
    try {
      await employerInterviewService.createSlots(roundId, data);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function updateSchedule(scheduleId: number, data: ReqUpdateInterviewScheduleDTO) {
    loading.value = true;
    error.value = null;
    try {
      const updated = await employerInterviewService.updateSchedule(scheduleId, data);
      const index = schedules.value.findIndex(s => s.id === scheduleId);
      if (index !== -1) schedules.value[index] = updated;
      return updated;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function deleteSchedule(scheduleId: number) {
    loading.value = true;
    error.value = null;
    try {
      await employerInterviewService.deleteSchedule(scheduleId);
      schedules.value = schedules.value.filter(s => s.id !== scheduleId);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  // --- Results ---
  async function createResult(scheduleId: number, data: ReqInterviewResultDTO) {
    loading.value = true;
    error.value = null;
    try {
      currentResult.value = await employerInterviewService.createResult(scheduleId, data);
      return currentResult.value;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function fetchResult(scheduleId: number) {
    loading.value = true;
    error.value = null;
    try {
      currentResult.value = await employerInterviewService.getResult(scheduleId);
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  // --- History & Overdue ---
  async function fetchHistory(applicationId: number) {
    loading.value = true;
    error.value = null;
    try {
      interviewHistory.value = await employerInterviewService.getInterviewHistory(applicationId);
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function fetchOverdue(jobPostId: number) {
    loading.value = true;
    error.value = null;
    try {
      overdueApplications.value = await employerInterviewService.getOverdueApplications(jobPostId);
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function extendDeadline(applicationId: number, data: ReqExtendDeadlineDTO) {
    loading.value = true;
    error.value = null;
    try {
      await employerInterviewService.extendDeadline(applicationId, data);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function forceSchedule(applicationId: number, data: ReqForceScheduleDTO) {
    loading.value = true;
    error.value = null;
    try {
      return await employerInterviewService.forceSchedule(applicationId, data);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  // --- Offer & Readiness ---
  async function updateOffer(applicationId: number, data: ReqOfferResultDTO) {
    loading.value = true;
    error.value = null;
    try {
      return await employerInterviewService.updateOffer(applicationId, data);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function startInterviewing(jobPostId: number) {
    loading.value = true;
    error.value = null;
    try {
      await employerInterviewService.startInterviewing(jobPostId);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function completeRecruitment(jobPostId: number, data: ReqCompleteRecruitmentDTO) {
    loading.value = true;
    error.value = null;
    try {
      await employerInterviewService.completeRecruitment(jobPostId, data);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function remindConfirmSchedule(scheduleId: number) {
    loading.value = true;
    error.value = null;
    try {
      await employerInterviewService.remindConfirmSchedule(scheduleId);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  /** Lấy thống kê phỏng vấn */
  async function fetchStatistics() {
    loading.value = true;
    error.value = null;
    try {
      interviewStatistics.value = await employerInterviewService.getInterviewStatistics();
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  function reset() {
    rounds.value = [];
    schedules.value = [];
    currentResult.value = null;
    interviewHistory.value = null;
    overdueApplications.value = [];
    loading.value = false;
    error.value = null;
  }

  return {
    // state
    rounds,
    schedules,
    currentResult,
    interviewHistory,
    overdueApplications,
    loading,
    error,
    // actions
    fetchRounds,
    createRound,
    updateRound,
    deleteRound,
    fetchSchedules,
    createSchedule,
    createSlots,
    updateSchedule,
    deleteSchedule,
    createResult,
    fetchResult,
    fetchHistory,
    fetchOverdue,
    extendDeadline,
    forceSchedule,
    updateOffer,
    startInterviewing,
    completeRecruitment,
    remindConfirmSchedule,
    fetchStatistics,
    interviewStatistics,
    reset
  };
});
