import type { InterviewStatus, InterviewType, OfferResult } from '@/constants/interview.constants';

// ─── Requests ────────────────────────────────────────────────────────────────

export interface InterviewerDTO {
  name: string;
  email?: string;
  phone?: string;
}

export interface ReqCreateInterviewRoundDTO {
  roundName: string;
  description?: string;
  expectedDuration?: number;
  isFinal?: boolean;
  interviewers?: InterviewerDTO[];
}

export interface ReqUpdateInterviewRoundDTO {
  roundName?: string;
  description?: string;
  expectedDuration?: number;
  isFinal?: boolean;
  interviewers?: InterviewerDTO[];
}

export interface ReqCreateInterviewScheduleDTO {
  applicationId: number;
  scheduledAt: string; // LocalDateTime
  durationMinutes?: number;
  interviewType: InterviewType;
  location?: string;
  meetingLink?: string;
  interviewerNote?: string;
}

export interface ReqUpdateInterviewScheduleDTO {
  scheduledAt?: string;
  interviewType?: InterviewType;
  location?: string;
  meetingLink?: string;
  interviewerNote?: string;
}

export interface SlotDTO {
  startTime:      string;       // ISO LocalDateTime, e.g. "2024-04-10T09:00:00"
  endTime:        string;       // ISO LocalDateTime
  maxCandidates:  number;       // @Min(1)
  interviewType:  string;       // "online" | "onsite"
  location?:      string;
  meetingLink?:   string;
  interviewerName?: string;
}

export interface ReqCreateInterviewSlotsDTO {
  applicationIds: number[];     // @NotEmpty
  deadline:       string;       // ISO LocalDateTime
  slots:          SlotDTO[];    // @Size(min = 3, max = 5)
}

export interface ReqInterviewResultDTO {
  result: 'PASS' | 'FAIL';
  rating?: number;
  note?: string;
  notifyCandidate?: boolean;
}

export interface ReqExtendDeadlineDTO {
  extendDays: number;
}

export interface ReqForceScheduleDTO {
  scheduledAt: string;
  interviewType: InterviewType;
  location?: string;
  meetingLink?: string;
}

export interface ReqCompleteRecruitmentDTO {
  applicationIds: number[];
}

export interface ReqOfferResultDTO {
  result: OfferResult;
}

// ─── Responses ───────────────────────────────────────────────────────────────

export interface InterviewerInfo {
  id: number;
  name: string;
  email?: string;
  phone?: string;
}

export interface ResInterviewRoundDTO {
  id: number;
  jobPostId: number;
  roundNumber: number;
  roundName: string;
  description?: string;
  expectedDuration?: number;
  isFinal: boolean;
  interviewers: InterviewerInfo[];
  candidateCount: number;
  createdAt: string;
}

export interface ResInterviewScheduleDTO {
  id: number;
  applicationId: number;
  candidateUserId: number;
  roundId: number;
  roundNumber: number;
  roundName: string;
  candidateName: string;
  candidateEmail: string;
  candidatePhone?: string;
  scheduledAt: string;
  durationMinutes: number;
  interviewType: InterviewType;
  location?: string;
  meetingLink?: string;
  status: InterviewStatus;
  confirmedByCandidate: boolean;
  isDefault?: boolean;
  interviewerNote?: string;
  // Slot UV đã chọn (null nếu NTT đặt thủ công hoặc chưa chọn)
  slotId?: number;
  slotStartTime?: string;
  slotEndTime?: string;
  slotInterviewerName?: string;
  // Danh sách slot đã gửi cho UV này (theo từng batch)
  sentSlots?: SentSlotDTO[];
  applicationStatus?: string;
  createdAt: string;
  updatedAt: string;
}

export interface SentSlotDTO {
  id: number;
  batchNumber: number;
  deadline: string;
  startTime: string;
  endTime: string;
  interviewType: string;
  location?: string;
  meetingLink?: string;
  interviewerName?: string;
  maxCandidates: number;
  registeredCount: number;
}

export interface ResInterviewResultDTO {
  id: number;
  interviewId: number;
  result: string;
  rating?: number;
  note?: string;
  notifyCandidate: boolean;
  evaluatedBy: number;
  evaluatedAt: string;
}

export interface RoundHistory {
  roundNumber: number;
  roundName: string;
  isFinal: boolean;
  scheduleId?: number;
  scheduledAt?: string;
  interviewType?: string;
  scheduleStatus?: string;
  result?: string;
  rating?: number;
  note?: string;
  evaluatedAt?: string;
}

export interface ResInterviewHistoryDTO {
  applicationId: number;
  candidateUserId: number;
  candidateName: string;
  currentStatus: string;
  cvUrl?: string;
  rounds: RoundHistory[];
}

export interface ResConfirmUpdateInfoDTO {
  scheduleId: number;
  companyName: string;
  jobTitle: string;
  roundNumber: number;
  roundName: string;
  scheduledAt: string;
  durationMinutes: number;
  interviewType: string;
  location?: string;
  meetingLink?: string;
  status: string;
  confirmedByCandidate: boolean;
}

export interface ResOverdueApplicationDTO {
  applicationId: number;
  candidateUserId: number;
  candidateName: string;
  candidateEmail: string;
  candidatePhone?: string;
  reminderCount: number;
  firstReminderAt: string;
  reminderDeadline: string;
  currentRoundName: string;
  currentRoundNumber: number;
}

export interface ResInterviewSlotDTO {
  id:               number;
  roundId:          number;
  slotDeadline?:    string;  // ISO LocalDateTime — hạn chót UV chọn slot
  startTime:        string;  // ISO LocalDateTime
  endTime:          string;
  interviewType:    string;
  location?:        string;
  meetingLink?:     string;
  interviewerName?: string;
  maxCandidates:    number;
  registeredCount:  number;
  createdAt:        string;
}

export interface ResSlotSelectionPageDTO {
  companyName:  string;
  jobTitle:     string;
  roundName:    string;
  roundNumber:  number;
  deadline:     string;  // ISO LocalDateTime — hạn chót UV chọn slot
  slots:        ResInterviewSlotDTO[];
}

/** GET /employer/interviews/interview-statistics */
export interface ResEmployerInterviewStatisticsDTO {
    /** Tổng số lịch phỏng vấn của công ty */
    totalSchedules: number

    /** Tổng số UV đã nộp CV nhưng chưa có lịch mới (isDefault = true) */
    pendingNewSchedules: number

    /** Tổng số UV chưa xác nhận lịch PV (confirmedByCandidate = false) */
    unconfirmedSchedules: number

    /** Tổng số lịch quá hạn (chưa xác nhận/lên lịch nhưng đã qua deadline hoặc application bị overdue) */
    overdueSchedules: number
}
