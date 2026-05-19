
export enum WorkType {
    FULL_TIME = 'FULL_TIME',
    PART_TIME = 'PART_TIME',
    INTERN = 'INTERN',
    REMOTE = 'REMOTE',
}

export enum JobPostingStatus {
    DRAFT = 'draft',
    PENDING_APPROVAL = 'pending_approval',
    REJECTED = 'rejected',
    SCHEDULED = 'scheduled',
    PUBLISHED = 'published',
    HIDDEN = 'hidden',
    PAUSED = 'paused',
    CLOSED = 'closed',
    EXPIRED = 'expired',
    RENEWED = 'renewed',
    INTERVIEWING = 'interviewing',
    COMPLETED = 'completed',
    DELETED = 'deleted',
}


// ─── Labels cho UI ──────────────────────────────────────────────────────────────

export const WORK_TYPE_LABELS: Record<WorkType, string> = {
    [WorkType.FULL_TIME]: 'Toàn thời gian',
    [WorkType.PART_TIME]: 'Bán thời gian',
    [WorkType.INTERN]: 'Thực tập',
    [WorkType.REMOTE]: 'Remote',
}

export const JOB_POSTING_STATUS_LABELS: Record<JobPostingStatus, string> = {
    [JobPostingStatus.DRAFT]: 'Bản nháp',
    [JobPostingStatus.PENDING_APPROVAL]: 'Chờ duyệt',
    [JobPostingStatus.REJECTED]: 'Bị từ chối',
    [JobPostingStatus.SCHEDULED]: 'Đã lên lịch',
    [JobPostingStatus.PUBLISHED]: 'Đang đăng',
    [JobPostingStatus.HIDDEN]: 'Đã ẩn',
    [JobPostingStatus.PAUSED]: 'Tạm dừng',
    [JobPostingStatus.CLOSED]: 'Đã đóng',
    [JobPostingStatus.EXPIRED]: 'Hết hạn',
    [JobPostingStatus.RENEWED]: 'Đã gia hạn',
    [JobPostingStatus.INTERVIEWING]: 'Đang phỏng vấn',
    [JobPostingStatus.COMPLETED]: 'Hoàn thành',
    [JobPostingStatus.DELETED]: 'Đã xóa',
}

export const JOB_POSTING_STATUS_BADGE: Record<JobPostingStatus, string> = {
    // Chưa active — neutral
    [JobPostingStatus.DRAFT]:
        'bg-slate-100 text-slate-600 dark:bg-slate-700 dark:text-slate-300',

    // Chờ xử lý — vàng
    [JobPostingStatus.PENDING_APPROVAL]:
        'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400',

    // Bị từ chối — đỏ hồng
    [JobPostingStatus.REJECTED]:
        'bg-rose-100 text-rose-700 dark:bg-rose-900/30 dark:text-rose-400',

    // Đã lên lịch, chưa chạy — tím nhạt
    [JobPostingStatus.SCHEDULED]:
        'bg-violet-100 text-violet-700 dark:bg-violet-900/30 dark:text-violet-400',

    // Đang chạy — xanh lá
    [JobPostingStatus.PUBLISHED]:
        'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400',

    // Bị ẩn khỏi hiển thị — amber/slate để phân biệt với paused thủ công
    [JobPostingStatus.HIDDEN]:
        'bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400',

    // Tạm dừng — cam
    [JobPostingStatus.PAUSED]:
        'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',

    // Đã đóng chủ động — slate đậm hơn draft
    [JobPostingStatus.CLOSED]:
        'bg-slate-200 text-slate-500 dark:bg-slate-600 dark:text-slate-400',

    // Hết hạn tự động — đỏ nhạt
    [JobPostingStatus.EXPIRED]:
        'bg-red-100 text-red-600 dark:bg-red-900/30 dark:text-red-400',

    // Gia hạn thành công — teal (gần với published nhưng phân biệt)
    [JobPostingStatus.RENEWED]:
        'bg-teal-100 text-teal-700 dark:bg-teal-900/30 dark:text-teal-400',

    // Đang phỏng vấn — xanh dương
    [JobPostingStatus.INTERVIEWING]:
        'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',

    // Hoàn thành tuyển dụng — indigo (kết thúc tích cực)
    [JobPostingStatus.COMPLETED]:
        'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400',
    [JobPostingStatus.DELETED]:
        'bg-slate-100 text-slate-400 dark:bg-slate-800 dark:text-slate-500 line-through',
}

export const WORK_TYPE_OPTIONS: { label: string; value: WorkType }[] = [
    { label: 'Toàn thời gian', value: WorkType.FULL_TIME },
    { label: 'Bán thời gian', value: WorkType.PART_TIME },
    { label: 'Thực tập', value: WorkType.INTERN },
    { label: 'Remote', value: WorkType.REMOTE },
]

export const JOB_POSTING_DEFAULT_PAGE_SIZE = 10
export const JOB_POSTING_DEFAULT_SORT = 'createdAt'
