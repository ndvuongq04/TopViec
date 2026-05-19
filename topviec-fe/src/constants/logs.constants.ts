export enum LogCategory {
  AUTH = 'AUTH',
  CANDIDATE_PROFILE = 'CANDIDATE_PROFILE',
  CV_MANAGEMENT = 'CV_MANAGEMENT',
  APPLICATION = 'APPLICATION',
  SAVED_JOB = 'SAVED_JOB',
  COMPANY_FOLLOW = 'COMPANY_FOLLOW',
  REPORTING = 'REPORTING',
  COMPANY_MANAGEMENT = 'COMPANY_MANAGEMENT',
  MEMBER_MANAGEMENT = 'MEMBER_MANAGEMENT',
  JOB_MANAGEMENT = 'JOB_MANAGEMENT',
  APPLICATION_REVIEW = 'APPLICATION_REVIEW',
  TALENT_POOL = 'TALENT_POOL',
  INTERVIEW = 'INTERVIEW',
  BILLING = 'BILLING',
  APPEAL = 'APPEAL',
  ADMIN_USER_MANAGEMENT = 'ADMIN_USER_MANAGEMENT',
  COMPANY_ADMIN = 'COMPANY_ADMIN',
  COMPANY_MODERATION = 'COMPANY_MODERATION',
  CANDIDATE_ADMIN = 'CANDIDATE_ADMIN',
  MODERATION = 'MODERATION',
  REPORT_MODERATION = 'REPORT_MODERATION',
  VIOLATION_MANAGEMENT = 'VIOLATION_MANAGEMENT',
  SERVICE_CATALOG = 'SERVICE_CATALOG',
  ORDER_MANAGEMENT = 'ORDER_MANAGEMENT',
  SYSTEM_MAINTENANCE = 'SYSTEM_MAINTENANCE',
}

export enum LogType {
  AUDIT = 'AUDIT',
  BUSINESS = 'BUSINESS',
  BOTH = 'BOTH',
}

export enum Severity {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL',
}

export enum LogActionType {
  // AUTH
  CHANGE_PASSWORD = 'CHANGE_PASSWORD',
  REGISTER_CANDIDATE = 'REGISTER_CANDIDATE',
  REGISTER_EMPLOYER = 'REGISTER_EMPLOYER',
  VERIFY_EMAIL = 'VERIFY_EMAIL',

  // CANDIDATE_PROFILE
  UPDATE_CANDIDATE_PROFILE = 'UPDATE_CANDIDATE_PROFILE',
  UPDATE_CANDIDATE_VISIBILITY = 'UPDATE_CANDIDATE_VISIBILITY',
  DELETE_CANDIDATE_PROFILE = 'DELETE_CANDIDATE_PROFILE',
  CREATE_CANDIDATE_PROFILE = 'CREATE_CANDIDATE_PROFILE',

  // CV_MANAGEMENT
  DELETE_CV = 'DELETE_CV',
  UPLOAD_CV = 'UPLOAD_CV',
  SHARE_CV = 'SHARE_CV',
  VIEW_PUBLIC_CV = 'VIEW_PUBLIC_CV',

  // APPLICATION
  UPDATE_APPLICATION_CV = 'UPDATE_APPLICATION_CV',
  APPLY_JOB = 'APPLY_JOB',
  QUICK_APPLY_JOB = 'QUICK_APPLY_JOB',
  BULK_APPLY_JOB = 'BULK_APPLY_JOB',
  WITHDRAW_APPLICATION = 'WITHDRAW_APPLICATION',
  ACCEPT_TALENT_POOL_INVITE = 'ACCEPT_TALENT_POOL_INVITE',
  DECLINE_TALENT_POOL_INVITE = 'DECLINE_TALENT_POOL_INVITE',

  // SAVED_JOB & COMPANY_FOLLOW
  SAVE_JOB = 'SAVE_JOB',
  FOLLOW_COMPANY = 'FOLLOW_COMPANY',

  // REPORTING
  CREATE_REPORT = 'CREATE_REPORT',

  // COMPANY_MANAGEMENT
  UPDATE_COMPANY_PROFILE = 'UPDATE_COMPANY_PROFILE',

  // MEMBER_MANAGEMENT
  ADD_MEMBER = 'ADD_MEMBER',
  UPDATE_MEMBER_PERMISSION = 'UPDATE_MEMBER_PERMISSION',
  TOGGLE_MEMBER_ACTION_PERMISSION = 'TOGGLE_MEMBER_ACTION_PERMISSION',
  REMOVE_MEMBER = 'REMOVE_MEMBER',

  // JOB_MANAGEMENT
  UPDATE_JOB_POSTING = 'UPDATE_JOB_POSTING',
  PAUSE_JOB_POSTING = 'PAUSE_JOB_POSTING',
  RESUME_JOB_POSTING = 'RESUME_JOB_POSTING',
  DELETE_JOB_POSTING = 'DELETE_JOB_POSTING',
  RESTORE_JOB_POSTING = 'RESTORE_JOB_POSTING',
  CREATE_JOB_POSTING = 'CREATE_JOB_POSTING',
  CLOSE_JOB_POSTING = 'CLOSE_JOB_POSTING',
  EXTEND_JOB_POSTING = 'EXTEND_JOB_POSTING',
  SUBMIT_JOB_POSTING_APPROVAL = 'SUBMIT_JOB_POSTING_APPROVAL',

  // APPLICATION_REVIEW
  UPDATE_APPLICATION = 'UPDATE_APPLICATION',

  // TALENT_POOL
  VIEW_CANDIDATE_DETAIL = 'VIEW_CANDIDATE_DETAIL',
  UPDATE_TALENT_POOL_NOTE = 'UPDATE_TALENT_POOL_NOTE',
  REMOVE_FROM_TALENT_POOL = 'REMOVE_FROM_TALENT_POOL',
  SEARCH_CANDIDATES = 'SEARCH_CANDIDATES',
  ADD_TO_TALENT_POOL = 'ADD_TO_TALENT_POOL',
  INVITE_FROM_TALENT_POOL = 'INVITE_FROM_TALENT_POOL',

  // INTERVIEW
  UPDATE_INTERVIEW_ROUND = 'UPDATE_INTERVIEW_ROUND',
  DELETE_INTERVIEW_ROUND = 'DELETE_INTERVIEW_ROUND',
  CREATE_INTERVIEW_SLOTS = 'CREATE_INTERVIEW_SLOTS',
  UPDATE_INTERVIEW_SCHEDULE = 'UPDATE_INTERVIEW_SCHEDULE',
  DELETE_INTERVIEW_SCHEDULE = 'DELETE_INTERVIEW_SCHEDULE',
  EXTEND_INTERVIEW_DEADLINE = 'EXTEND_INTERVIEW_DEADLINE',
  FORCE_SCHEDULE_INTERVIEW = 'FORCE_SCHEDULE_INTERVIEW',
  CREATE_INTERVIEW_ROUND = 'CREATE_INTERVIEW_ROUND',
  CREATE_INTERVIEW_SCHEDULE = 'CREATE_INTERVIEW_SCHEDULE',
  SEND_OFFER = 'SEND_OFFER',
  UPDATE_OFFER_RESULT = 'UPDATE_OFFER_RESULT',
  START_INTERVIEW_PHASE = 'START_INTERVIEW_PHASE',
  START_INTERVIEWING = 'START_INTERVIEWING',
  COMPLETE_RECRUITMENT = 'COMPLETE_RECRUITMENT',
  CONFIRM_INTERVIEW_SCHEDULE = 'CONFIRM_INTERVIEW_SCHEDULE',
  CANDIDATE_CONFIRM_INTERVIEW = 'CANDIDATE_CONFIRM_INTERVIEW',
  CONFIRM_UPDATED_INTERVIEW_SCHEDULE = 'CONFIRM_UPDATED_INTERVIEW_SCHEDULE',
  CANDIDATE_CONFIRM_UPDATED_SCHEDULE = 'CANDIDATE_CONFIRM_UPDATED_SCHEDULE',
  CREATE_INTERVIEW_RESULT = 'CREATE_INTERVIEW_RESULT',
  RECORD_INTERVIEW_RESULT = 'RECORD_INTERVIEW_RESULT',

  // BILLING
  CREATE_ORDER = 'CREATE_ORDER',
  CANCEL_ORDER = 'CANCEL_ORDER',
  RENEW_SUBSCRIPTION = 'RENEW_SUBSCRIPTION',
  APPLY_JOB_POST_ADDON = 'APPLY_JOB_POST_ADDON',
  APPLY_COMPANY_BRANDING = 'APPLY_COMPANY_BRANDING',

  // APPEAL
  CREATE_APPEAL = 'CREATE_APPEAL',
  EMPLOYER_SUBMIT_APPEAL = 'EMPLOYER_SUBMIT_APPEAL',
  RESPOND_TO_REPORT = 'RESPOND_TO_REPORT',

  // ADMIN_USER_MANAGEMENT
  CREATE_ADMIN = 'CREATE_ADMIN',
  CREATE_ADMIN_USER = 'CREATE_ADMIN_USER',
  UPDATE_ADMIN = 'UPDATE_ADMIN',
  UPDATE_ADMIN_USER = 'UPDATE_ADMIN_USER',
  TOGGLE_ADMIN_ACTIVE = 'TOGGLE_ADMIN_ACTIVE',
  TOGGLE_ADMIN_USER_ACTIVE = 'TOGGLE_ADMIN_USER_ACTIVE',
  DELETE_ADMIN = 'DELETE_ADMIN',
  DELETE_ADMIN_USER = 'DELETE_ADMIN_USER',

  // COMPANY_ADMIN
  ADMIN_UPDATE_COMPANY = 'ADMIN_UPDATE_COMPANY',
  UPDATE_COMPANY_BY_ADMIN = 'UPDATE_COMPANY_BY_ADMIN',
  DELETE_COMPANY = 'DELETE_COMPANY',
  CREATE_EMPLOYER_COMPANY = 'CREATE_EMPLOYER_COMPANY',

  // COMPANY_MODERATION
  REJECT_COMPANY_VERIFICATION = 'REJECT_COMPANY_VERIFICATION',
  UNSUSPEND_COMPANY = 'UNSUSPEND_COMPANY',
  VERIFY_COMPANY = 'VERIFY_COMPANY',
  SUSPEND_COMPANY = 'SUSPEND_COMPANY',

  // CANDIDATE_ADMIN
  TOGGLE_CANDIDATE_STATUS = 'TOGGLE_CANDIDATE_STATUS',

  // MODERATION
  REJECT_JOB_POSTING = 'REJECT_JOB_POSTING',
  ADMIN_RESTORE_JOB_POSTING = 'ADMIN_RESTORE_JOB_POSTING',
  RESTORE_JOB_POSTING_BY_ADMIN = 'RESTORE_JOB_POSTING_BY_ADMIN',
  APPROVE_JOB_POSTING = 'APPROVE_JOB_POSTING',
  TAKEDOWN_JOB_POSTING = 'TAKEDOWN_JOB_POSTING',

  // REPORT_MODERATION
  CONFIRM_REPORT = 'CONFIRM_REPORT',
  PROCESS_REPORT = 'PROCESS_REPORT',

  // VIOLATION_MANAGEMENT
  RESET_VIOLATION_SCORE = 'RESET_VIOLATION_SCORE',
  ADJUST_VIOLATION_SCORE = 'ADJUST_VIOLATION_SCORE',
  UNSUSPEND_EMPLOYER_AFTER_APPEAL = 'UNSUSPEND_EMPLOYER_AFTER_APPEAL',

  // SERVICE_CATALOG
  CREATE_SERVICE = 'CREATE_SERVICE',
  UPDATE_SERVICE = 'UPDATE_SERVICE',
  CREATE_ADDON_SERVICE = 'CREATE_ADDON_SERVICE',
  UPDATE_ADDON_SERVICE = 'UPDATE_ADDON_SERVICE',
  CREATE_SERVICE_PACKAGE = 'CREATE_SERVICE_PACKAGE',
  UPDATE_SERVICE_PACKAGE = 'UPDATE_SERVICE_PACKAGE',

  // ORDER_MANAGEMENT
  ADMIN_UPDATE_ORDER_STATUS = 'ADMIN_UPDATE_ORDER_STATUS',
  UPDATE_ORDER_STATUS = 'UPDATE_ORDER_STATUS',

  // SYSTEM_MAINTENANCE
  AUTO_RESET_VIOLATION_SCORE = 'AUTO_RESET_VIOLATION_SCORE',
  EXPIRE_SUBSCRIPTION = 'EXPIRE_SUBSCRIPTION',
  EXPIRE_JOB_POST_HOT = 'EXPIRE_JOB_POST_HOT',
  EXPIRE_JOB_POST_URGENT = 'EXPIRE_JOB_POST_URGENT',
  EXPIRE_COMPANY_BRANDING = 'EXPIRE_COMPANY_BRANDING',
}

// ─── Labels cho UI ──────────────────────────────────────────────────────────────

export const LOG_CATEGORY_LABELS: Record<LogCategory, string> = {
  [LogCategory.AUTH]: 'Xác thực',
  [LogCategory.CANDIDATE_PROFILE]: 'Hồ sơ ứng viên',
  [LogCategory.CV_MANAGEMENT]: 'Quản lý CV',
  [LogCategory.APPLICATION]: 'Ứng tuyển',
  [LogCategory.SAVED_JOB]: 'Việc làm đã lưu',
  [LogCategory.COMPANY_FOLLOW]: 'Theo dõi công ty',
  [LogCategory.REPORTING]: 'Báo cáo vi phạm',
  [LogCategory.COMPANY_MANAGEMENT]: 'Quản lý công ty',
  [LogCategory.MEMBER_MANAGEMENT]: 'Quản lý thành viên',
  [LogCategory.JOB_MANAGEMENT]: 'Quản lý tin tuyển dụng',
  [LogCategory.APPLICATION_REVIEW]: 'Duyệt hồ sơ ứng tuyển',
  [LogCategory.TALENT_POOL]: 'Nguồn ứng viên',
  [LogCategory.INTERVIEW]: 'Phỏng vấn',
  [LogCategory.BILLING]: 'Thanh toán',
  [LogCategory.APPEAL]: 'Khiếu nại',
  [LogCategory.ADMIN_USER_MANAGEMENT]: 'Quản lý Admin',
  [LogCategory.COMPANY_ADMIN]: 'Quản trị công ty',
  [LogCategory.COMPANY_MODERATION]: 'Kiểm duyệt công ty',
  [LogCategory.CANDIDATE_ADMIN]: 'Quản trị ứng viên',
  [LogCategory.MODERATION]: 'Kiểm duyệt tin',
  [LogCategory.REPORT_MODERATION]: 'Kiểm duyệt báo cáo',
  [LogCategory.VIOLATION_MANAGEMENT]: 'Quản lý vi phạm',
  [LogCategory.SERVICE_CATALOG]: 'Danh mục dịch vụ',
  [LogCategory.ORDER_MANAGEMENT]: 'Quản lý đơn hàng',
  [LogCategory.SYSTEM_MAINTENANCE]: 'Bảo trì hệ thống',
}

export const LOG_TYPE_LABELS: Record<LogType, string> = {
  [LogType.AUDIT]: 'Nhật ký hệ thống',
  [LogType.BUSINESS]: 'Nhật ký nghiệp vụ',
  [LogType.BOTH]: 'Tất cả nhật ký',
}

export const SEVERITY_LABELS: Record<Severity, string> = {
  [Severity.LOW]: 'Thấp',
  [Severity.MEDIUM]: 'Trung bình',
  [Severity.HIGH]: 'Cao',
  [Severity.CRITICAL]: 'Nghiêm trọng',
}

export const SEVERITY_COLORS: Record<Severity, string> = {
  [Severity.LOW]: 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400',
  [Severity.MEDIUM]: 'bg-blue-100 text-blue-600 dark:bg-blue-900/30 dark:text-blue-400',
  [Severity.HIGH]: 'bg-orange-100 text-orange-600 dark:bg-orange-900/30 dark:text-orange-400',
  [Severity.CRITICAL]: 'bg-red-100 text-red-600 dark:bg-red-900/30 dark:text-red-400',
}

export const LOG_ACTION_TYPE_LABELS: Record<LogActionType, string> = {
  [LogActionType.CHANGE_PASSWORD]: 'Đổi mật khẩu',
  [LogActionType.REGISTER_CANDIDATE]: 'Đăng ký ứng viên',
  [LogActionType.REGISTER_EMPLOYER]: 'Đăng ký nhà tuyển dụng',
  [LogActionType.VERIFY_EMAIL]: 'Xác thực email',
  [LogActionType.UPDATE_CANDIDATE_PROFILE]: 'Cập nhật hồ sơ ứng viên',
  [LogActionType.UPDATE_CANDIDATE_VISIBILITY]: 'Cập nhật trạng thái hiển thị hồ sơ',
  [LogActionType.DELETE_CANDIDATE_PROFILE]: 'Xóa hồ sơ ứng viên',
  [LogActionType.CREATE_CANDIDATE_PROFILE]: 'Tạo hồ sơ ứng viên',
  [LogActionType.DELETE_CV]: 'Xóa CV',
  [LogActionType.UPLOAD_CV]: 'Tải lên CV',
  [LogActionType.SHARE_CV]: 'Chia sẻ CV',
  [LogActionType.VIEW_PUBLIC_CV]: 'Xem CV công khai',
  [LogActionType.UPDATE_APPLICATION_CV]: 'Cập nhật CV ứng tuyển',
  [LogActionType.APPLY_JOB]: 'Ứng tuyển việc làm',
  [LogActionType.QUICK_APPLY_JOB]: 'Ứng tuyển nhanh',
  [LogActionType.BULK_APPLY_JOB]: 'Ứng tuyển hàng loạt',
  [LogActionType.WITHDRAW_APPLICATION]: 'Rút hồ sơ ứng tuyển',
  [LogActionType.ACCEPT_TALENT_POOL_INVITE]: 'Chấp nhận lời mời Talent Pool',
  [LogActionType.DECLINE_TALENT_POOL_INVITE]: 'Từ chối lời mời Talent Pool',
  [LogActionType.SAVE_JOB]: 'Lưu việc làm',
  [LogActionType.FOLLOW_COMPANY]: 'Theo dõi công ty',
  [LogActionType.CREATE_REPORT]: 'Tạo báo cáo vi phạm',
  [LogActionType.UPDATE_COMPANY_PROFILE]: 'Cập nhật thông tin công ty',
  [LogActionType.ADD_MEMBER]: 'Thêm thành viên mới',
  [LogActionType.UPDATE_MEMBER_PERMISSION]: 'Cập nhật quyền thành viên',
  [LogActionType.TOGGLE_MEMBER_ACTION_PERMISSION]: 'Bật/tắt quyền thao tác thành viên',
  [LogActionType.REMOVE_MEMBER]: 'Xóa thành viên',
  [LogActionType.UPDATE_JOB_POSTING]: 'Cập nhật tin tuyển dụng',
  [LogActionType.PAUSE_JOB_POSTING]: 'Tạm dừng tin tuyển dụng',
  [LogActionType.RESUME_JOB_POSTING]: 'Tiếp tục tin tuyển dụng',
  [LogActionType.DELETE_JOB_POSTING]: 'Xóa tin tuyển dụng',
  [LogActionType.RESTORE_JOB_POSTING]: 'Khôi phục tin tuyển dụng',
  [LogActionType.CREATE_JOB_POSTING]: 'Tạo mới tin tuyển dụng',
  [LogActionType.CLOSE_JOB_POSTING]: 'Đóng tin tuyển dụng',
  [LogActionType.EXTEND_JOB_POSTING]: 'Gia hạn tin tuyển dụng',
  [LogActionType.SUBMIT_JOB_POSTING_APPROVAL]: 'Gửi duyệt tin tuyển dụng',
  [LogActionType.UPDATE_APPLICATION]: 'Cập nhật trạng thái ứng tuyển',
  [LogActionType.VIEW_CANDIDATE_DETAIL]: 'Xem chi tiết ứng viên',
  [LogActionType.UPDATE_TALENT_POOL_NOTE]: 'Cập nhật ghi chú Talent Pool',
  [LogActionType.REMOVE_FROM_TALENT_POOL]: 'Xóa khỏi Talent Pool',
  [LogActionType.SEARCH_CANDIDATES]: 'Tìm kiếm ứng viên',
  [LogActionType.ADD_TO_TALENT_POOL]: 'Thêm vào Talent Pool',
  [LogActionType.INVITE_FROM_TALENT_POOL]: 'Gửi lời mời từ Talent Pool',
  [LogActionType.UPDATE_INTERVIEW_ROUND]: 'Cập nhật vòng phỏng vấn',
  [LogActionType.DELETE_INTERVIEW_ROUND]: 'Xóa vòng phỏng vấn',
  [LogActionType.CREATE_INTERVIEW_SLOTS]: 'Tạo khung giờ phỏng vấn',
  [LogActionType.UPDATE_INTERVIEW_SCHEDULE]: 'Cập nhật lịch phỏng vấn',
  [LogActionType.DELETE_INTERVIEW_SCHEDULE]: 'Xóa lịch phỏng vấn',
  [LogActionType.EXTEND_INTERVIEW_DEADLINE]: 'Gia hạn thời gian phỏng vấn',
  [LogActionType.FORCE_SCHEDULE_INTERVIEW]: 'Ép lịch phỏng vấn',
  [LogActionType.CREATE_INTERVIEW_ROUND]: 'Tạo vòng phỏng vấn',
  [LogActionType.CREATE_INTERVIEW_SCHEDULE]: 'Tạo lịch phỏng vấn',
  [LogActionType.SEND_OFFER]: 'Gửi đề nghị (Offer)',
  [LogActionType.UPDATE_OFFER_RESULT]: 'Cập nhật kết quả đề nghị',
  [LogActionType.START_INTERVIEW_PHASE]: 'Bắt đầu giai đoạn phỏng vấn',
  [LogActionType.START_INTERVIEWING]: 'Bắt đầu phỏng vấn',
  [LogActionType.COMPLETE_RECRUITMENT]: 'Hoàn thành quy trình tuyển dụng',
  [LogActionType.CONFIRM_INTERVIEW_SCHEDULE]: 'Xác nhận lịch phỏng vấn',
  [LogActionType.CANDIDATE_CONFIRM_INTERVIEW]: 'Ứng viên xác nhận phỏng vấn',
  [LogActionType.CONFIRM_UPDATED_INTERVIEW_SCHEDULE]: 'Xác nhận cập nhật lịch phỏng vấn',
  [LogActionType.CANDIDATE_CONFIRM_UPDATED_SCHEDULE]: 'Ứng viên xác nhận cập nhật lịch',
  [LogActionType.CREATE_INTERVIEW_RESULT]: 'Tạo kết quả phỏng vấn',
  [LogActionType.RECORD_INTERVIEW_RESULT]: 'Ghi nhận kết quả phỏng vấn',
  [LogActionType.CREATE_ORDER]: 'Tạo đơn hàng mới',
  [LogActionType.CANCEL_ORDER]: 'Hủy đơn hàng',
  [LogActionType.RENEW_SUBSCRIPTION]: 'Gia hạn gói dịch vụ',
  [LogActionType.APPLY_JOB_POST_ADDON]: 'Sử dụng dịch vụ bổ trợ tin đăng',
  [LogActionType.APPLY_COMPANY_BRANDING]: 'Sử dụng dịch vụ quảng bá thương hiệu',
  [LogActionType.CREATE_APPEAL]: 'Tạo khiếu nại',
  [LogActionType.EMPLOYER_SUBMIT_APPEAL]: 'Nhà tuyển dụng gửi khiếu nại',
  [LogActionType.RESPOND_TO_REPORT]: 'Phản hồi báo cáo vi phạm',
  [LogActionType.CREATE_ADMIN]: 'Tạo Admin mới',
  [LogActionType.CREATE_ADMIN_USER]: 'Tạo người dùng Admin',
  [LogActionType.UPDATE_ADMIN]: 'Cập nhật thông tin Admin',
  [LogActionType.UPDATE_ADMIN_USER]: 'Cập nhật người dùng Admin',
  [LogActionType.TOGGLE_ADMIN_ACTIVE]: 'Bật/tắt trạng thái Admin',
  [LogActionType.TOGGLE_ADMIN_USER_ACTIVE]: 'Bật/tắt trạng thái người dùng Admin',
  [LogActionType.DELETE_ADMIN]: 'Xóa Admin',
  [LogActionType.DELETE_ADMIN_USER]: 'Xóa người dùng Admin',
  [LogActionType.ADMIN_UPDATE_COMPANY]: 'Admin cập nhật thông tin công ty',
  [LogActionType.UPDATE_COMPANY_BY_ADMIN]: 'Cập nhật công ty bởi Admin',
  [LogActionType.DELETE_COMPANY]: 'Xóa công ty',
  [LogActionType.CREATE_EMPLOYER_COMPANY]: 'Tạo mới công ty tuyển dụng',
  [LogActionType.REJECT_COMPANY_VERIFICATION]: 'Từ chối xác thực công ty',
  [LogActionType.UNSUSPEND_COMPANY]: 'Gỡ đình chỉ công ty',
  [LogActionType.VERIFY_COMPANY]: 'Xác thực công ty',
  [LogActionType.SUSPEND_COMPANY]: 'Đình chỉ công ty',
  [LogActionType.TOGGLE_CANDIDATE_STATUS]: 'Thay đổi trạng thái ứng viên',
  [LogActionType.REJECT_JOB_POSTING]: 'Từ chối tin tuyển dụng',
  [LogActionType.ADMIN_RESTORE_JOB_POSTING]: 'Admin khôi phục tin tuyển dụng',
  [LogActionType.RESTORE_JOB_POSTING_BY_ADMIN]: 'Khôi phục tin tuyển dụng bởi Admin',
  [LogActionType.APPROVE_JOB_POSTING]: 'Duyệt tin tuyển dụng',
  [LogActionType.TAKEDOWN_JOB_POSTING]: 'Gỡ bỏ tin tuyển dụng',
  [LogActionType.CONFIRM_REPORT]: 'Xác nhận báo cáo vi phạm',
  [LogActionType.PROCESS_REPORT]: 'Xử lý báo cáo vi phạm',
  [LogActionType.RESET_VIOLATION_SCORE]: 'Đặt lại điểm vi phạm',
  [LogActionType.ADJUST_VIOLATION_SCORE]: 'Điều chỉnh điểm vi phạm',
  [LogActionType.UNSUSPEND_EMPLOYER_AFTER_APPEAL]: 'Gỡ đình chỉ sau khiếu nại',
  [LogActionType.CREATE_SERVICE]: 'Tạo dịch vụ mới',
  [LogActionType.UPDATE_SERVICE]: 'Cập nhật dịch vụ',
  [LogActionType.CREATE_ADDON_SERVICE]: 'Tạo dịch vụ bổ trợ mới',
  [LogActionType.UPDATE_ADDON_SERVICE]: 'Cập nhật dịch vụ bổ trợ',
  [LogActionType.CREATE_SERVICE_PACKAGE]: 'Tạo gói dịch vụ mới',
  [LogActionType.UPDATE_SERVICE_PACKAGE]: 'Cập nhật gói dịch vụ',
  [LogActionType.ADMIN_UPDATE_ORDER_STATUS]: 'Admin cập nhật trạng thái đơn hàng',
  [LogActionType.UPDATE_ORDER_STATUS]: 'Cập nhật trạng thái đơn hàng',
  [LogActionType.AUTO_RESET_VIOLATION_SCORE]: 'Tự động đặt lại điểm vi phạm',
  [LogActionType.EXPIRE_SUBSCRIPTION]: 'Hết hạn gói dịch vụ',
  [LogActionType.EXPIRE_JOB_POST_HOT]: 'Hết hạn tin HOT',
  [LogActionType.EXPIRE_JOB_POST_URGENT]: 'Hết hạn tin tuyển gấp',
  [LogActionType.EXPIRE_COMPANY_BRANDING]: 'Hết hạn quảng bá thương hiệu',
}

// ─── Role Labels ─────────────────────────────────────────────────────────────
export const USER_ROLE_LABELS: Record<string, string> = {
  // Admin Roles
  'super_admin': 'Super Admin',
  'content_moderator': 'Content Moderator',
  'support_admin': 'Support Admin',
  'finance_admin': 'Finance Admin',
  // Employer Roles
  'owner': 'Chủ sở hữu',
  'manager': 'Quản lý',
  'recruiter': 'Chuyên viên tuyển dụng',
  'viewer': 'Người xem',
}

export const USER_ROLE_OPTIONS = Object.entries(USER_ROLE_LABELS).map(([value, label]) => ({
  label,
  value,
}))

// ─── Select Options ─────────────────────────────────────────────────────────────

export const LOG_CATEGORY_OPTIONS = Object.entries(LOG_CATEGORY_LABELS).map(([value, label]) => ({
  label,
  value: value as LogCategory,
}))

export const LOG_TYPE_OPTIONS = Object.entries(LOG_TYPE_LABELS).map(([value, label]) => ({
  label,
  value: value as LogType,
}))

export const SEVERITY_OPTIONS = Object.entries(SEVERITY_LABELS).map(([value, label]) => ({
  label,
  value: value as Severity,
}))

export const LOG_ACTION_TYPE_OPTIONS = Object.entries(LOG_ACTION_TYPE_LABELS).map(([value, label]) => ({
  label,
  value: value as LogActionType,
}))

export const LOG_STATUS_OPTIONS = [
  { label: 'Thành công', value: 'SUCCESS' },
  { label: 'Thất bại', value: 'FAILURE' },
]
