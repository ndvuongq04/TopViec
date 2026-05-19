export const COMPLAINT_STATUS = {
  PENDING: 'pending',
  PROCESSING: 'processing',
  WAITING_EMPLOYER: 'waiting_employer',
  RESOLVED: 'resolved',
  REJECTED: 'rejected',
  AUTO_CLOSED: 'auto_closed',
} as const;

export type ComplaintStatus = (typeof COMPLAINT_STATUS)[keyof typeof COMPLAINT_STATUS];

// ─────────────────────────────────────────────

export const COMPLAINT_STATUS_OPTIONS = [
  { value: COMPLAINT_STATUS.PENDING, label: 'Chờ xử lý', color: 'blue' },
  { value: COMPLAINT_STATUS.PROCESSING, label: 'Đang xử lý', color: 'orange' },
  { value: COMPLAINT_STATUS.WAITING_EMPLOYER, label: 'Chờ nhà tuyển dụng', color: 'purple' },
  { value: COMPLAINT_STATUS.RESOLVED, label: 'Đã giải quyết', color: 'success' },
  { value: COMPLAINT_STATUS.REJECTED, label: 'Từ chối', color: 'red' },
  { value: COMPLAINT_STATUS.AUTO_CLOSED, label: 'Tự động đóng', color: 'default' },
];

// ─────────────────────────────────────────────

export const COMPLAINT_PRIORITY = {
  URGENT: 'urgent',
  IMPORTANT: 'important',
  NORMAL: 'normal',
} as const;

export type ComplaintPriority = (typeof COMPLAINT_PRIORITY)[keyof typeof COMPLAINT_PRIORITY];

// ─────────────────────────────────────────────

export const COMPLAINT_PRIORITY_OPTIONS = [
  { value: COMPLAINT_PRIORITY.URGENT, label: 'Khẩn cấp', color: 'red' },
  { value: COMPLAINT_PRIORITY.IMPORTANT, label: 'Quan trọng', color: 'orange' },
  { value: COMPLAINT_PRIORITY.NORMAL, label: 'Bình thường', color: 'blue' },
];

// ─────────────────────────────────────────────

export const COMPLAINT_TYPE = {
  FRAUDULENT: 'fraudulent',
  SPAM: 'spam',
  WRONG_INFO: 'wrong_info',
  INAPPROPRIATE: 'inappropriate',
  PAYMENT_ISSUE: 'payment_issue',
  OTHER: 'other',
} as const;

export type ComplaintType = (typeof COMPLAINT_TYPE)[keyof typeof COMPLAINT_TYPE];

// ─────────────────────────────────────────────

export const COMPLAINT_TYPE_OPTIONS = [
  { value: COMPLAINT_TYPE.FRAUDULENT, label: 'Lừa đảo' },
  { value: COMPLAINT_TYPE.SPAM, label: 'Spam' },
  { value: COMPLAINT_TYPE.WRONG_INFO, label: 'Thông tin sai lệch' },
  { value: COMPLAINT_TYPE.INAPPROPRIATE, label: 'Nội dung không phù hợp' },
  { value: COMPLAINT_TYPE.PAYMENT_ISSUE, label: 'Vấn đề thanh toán' },
  { value: COMPLAINT_TYPE.OTHER, label: 'Khác' },
];

// ─────────────────────────────────────────────

export const APPEAL_STATUS = {
  PENDING: 'pending',
  APPROVED: 'approved',
  REJECTED: 'rejected',
} as const;

export type AppealStatus = (typeof APPEAL_STATUS)[keyof typeof APPEAL_STATUS];

// ─────────────────────────────────────────────

export const APPEAL_STATUS_OPTIONS = [
  { value: APPEAL_STATUS.PENDING, label: 'Chờ xét duyệt', color: 'blue' },
  { value: APPEAL_STATUS.APPROVED, label: 'Đã chấp thuận', color: 'success' },
  { value: APPEAL_STATUS.REJECTED, label: 'Từ chối', color: 'red' },
];

// ─────────────────────────────────────────────

export const EVIDENCE_FILE_TYPE = {
  IMAGE: 'image',
  PDF: 'pdf',
  VIDEO: 'video',
} as const;

export type EvidenceFileType = (typeof EVIDENCE_FILE_TYPE)[keyof typeof EVIDENCE_FILE_TYPE];

// ─────────────────────────────────────────────

export const EVIDENCE_FILE_TYPE_OPTIONS = [
  { value: EVIDENCE_FILE_TYPE.IMAGE, label: 'Hình ảnh' },
  { value: EVIDENCE_FILE_TYPE.PDF, label: 'PDF' },
  { value: EVIDENCE_FILE_TYPE.VIDEO, label: 'Video' },
];

// ─────────────────────────────────────────────

export const VIOLATION_GROUP = {
  A: 'A',
  B: 'B',
} as const;

export type ViolationGroup = (typeof VIOLATION_GROUP)[keyof typeof VIOLATION_GROUP];

// ─────────────────────────────────────────────

export const VIOLATION_GROUP_OPTIONS = [
  { value: VIOLATION_GROUP.A, label: 'Nhóm A' },
  { value: VIOLATION_GROUP.B, label: 'Nhóm B' },
];

// ─────────────────────────────────────────────

export const VIOLATION_SOURCE = {
  ADMIN: 'admin',
  SYSTEM: 'system',
  COMPLAINT: 'complaint',
} as const;

export type ViolationSource = (typeof VIOLATION_SOURCE)[keyof typeof VIOLATION_SOURCE];

// ─────────────────────────────────────────────

export const VIOLATION_SOURCE_OPTIONS = [
  { value: VIOLATION_SOURCE.ADMIN, label: 'Admin' },
  { value: VIOLATION_SOURCE.SYSTEM, label: 'Hệ thống' },
  { value: VIOLATION_SOURCE.COMPLAINT, label: 'Khiếu nại' },
];
