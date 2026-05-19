export const INTERVIEW_STATUS = {
  PENDING: 'pending',
  SCHEDULED: 'scheduled',
  CONFIRMED: 'confirmed',
  COMPLETED: 'completed',
  CANCELLED: 'cancelled',
  NO_SHOW: 'no_show',
} as const;

export type InterviewStatus = typeof INTERVIEW_STATUS[keyof typeof INTERVIEW_STATUS];

export const INTERVIEW_TYPE = {
  ONSITE: 'onsite',
  ONLINE: 'online',
  PHONE: 'phone',
} as const;

export type InterviewType = typeof INTERVIEW_TYPE[keyof typeof INTERVIEW_TYPE];

export const OFFER_RESULT = {
  ACCEPTED: 'accepted',
  DECLINED: 'declined',
} as const;

export type OfferResult = typeof OFFER_RESULT[keyof typeof OFFER_RESULT];

export const INTERVIEW_STATUS_OPTIONS = [
  { value: INTERVIEW_STATUS.PENDING, label: 'Chờ lên lịch', color: 'default' },
  { value: INTERVIEW_STATUS.SCHEDULED, label: 'Chờ UV phản hồi', color: 'blue' },
  { value: INTERVIEW_STATUS.CONFIRMED, label: 'Đã xác nhận', color: 'green' },
  { value: INTERVIEW_STATUS.COMPLETED, label: 'Đã hoàn thành', color: 'gray' },
  { value: INTERVIEW_STATUS.CANCELLED, label: 'Đã hủy', color: 'red' },
  { value: INTERVIEW_STATUS.NO_SHOW, label: 'UV vắng mặt', color: 'orange' },
];

export const INTERVIEW_TYPE_OPTIONS = [
  { value: INTERVIEW_TYPE.ONSITE, label: 'Trực tiếp' },
  { value: INTERVIEW_TYPE.ONLINE, label: 'Trực tuyến' },
  { value: INTERVIEW_TYPE.PHONE, label: 'Điện thoại' },
];
