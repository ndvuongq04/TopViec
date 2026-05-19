export const APPLICATION_STATUS = {
  PENDING: 'pending',
  INVITED: 'invited',
  SEEN: 'seen',
  INTERVIEWING: 'interviewing',
  OFFERED: 'offered',
  HIRED: 'hired',
  REJECTED: 'rejected',
  WITHDRAWN: 'withdrawn',
  EXPIRED: 'expired',
  CONSIDERING: 'considering',
  CV_PASSED: 'cv_passed',
  SCHEDULE_PENDING: 'schedule_pending',
  OVERDUE: 'overdue',
} as const;

export type ApplicationStatus = (typeof APPLICATION_STATUS)[keyof typeof APPLICATION_STATUS];

// ─────────────────────────────────────────────

export const APPLY_METHOD = {
  NORMAL: 'normal',
  QUICK: 'quick',
  BULK: 'bulk',
  INVITED: 'invited',
} as const;

export type ApplyMethod = (typeof APPLY_METHOD)[keyof typeof APPLY_METHOD];

// ─────────────────────────────────────────────

export const APPLICATION_STATUS_OPTIONS = [
  { value: APPLICATION_STATUS.PENDING, label: 'Đã gửi', color: 'blue' },
  { value: APPLICATION_STATUS.INVITED, label: 'Được mời', color: 'cyan' },
  { value: APPLICATION_STATUS.SEEN, label: 'NTD Đã xem', color: 'purple' },
  { value: APPLICATION_STATUS.CONSIDERING, label: 'Đang cân nhắc', color: 'purple' },
  { value: APPLICATION_STATUS.CV_PASSED, label: 'Đạt vòng CV', color: 'success' },
  { value: APPLICATION_STATUS.SCHEDULE_PENDING, label: 'Chờ chọn lịch', color: 'orange' },
  { value: APPLICATION_STATUS.OVERDUE, label: 'Quá hạn chọn lịch', color: 'red' },
  { value: APPLICATION_STATUS.INTERVIEWING, label: 'Đang phỏng vấn', color: 'orange' },
  { value: APPLICATION_STATUS.OFFERED, label: 'Gửi đề nghị Offering', color: 'green' },
  { value: APPLICATION_STATUS.HIRED, label: 'Đã tuyển', color: 'success' },
  { value: APPLICATION_STATUS.REJECTED, label: 'Từ chối', color: 'red' },
  { value: APPLICATION_STATUS.WITHDRAWN, label: 'Rút đơn', color: 'gray' },
  { value: APPLICATION_STATUS.EXPIRED, label: 'Hết hạn', color: 'default' },
];

export const APPLY_METHOD_OPTIONS = [
  { value: APPLY_METHOD.NORMAL, label: 'Nộp thông thường' },
  { value: APPLY_METHOD.QUICK, label: 'Ứng tuyển nhanh' },
  { value: APPLY_METHOD.BULK, label: 'Ứng tuyển hàng loạt' },
  { value: APPLY_METHOD.INVITED, label: 'Được mời từ Talent Pool' },
];
