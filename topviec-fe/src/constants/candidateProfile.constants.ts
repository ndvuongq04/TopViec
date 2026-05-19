export enum JobSeekingStatus {
    ACTIVE = 'active',
    PASSIVE = 'passive',
    NOT_LOOKING = 'not_looking',
}

export const JOB_SEEKING_STATUS_LABELS: Record<JobSeekingStatus, string> = {
    [JobSeekingStatus.ACTIVE]: 'Đang tìm việc',
    [JobSeekingStatus.PASSIVE]: 'Đang cân nhắc',
    [JobSeekingStatus.NOT_LOOKING]: 'Không tìm việc',
}

export enum PreferredWorkType {
    FULL_TIME = 'full_time',
    PART_TIME = 'part_time',
    REMOTE = 'remote',
    HYBRID = 'hybrid',
}

export const PREFERRED_WORK_TYPE_LABELS: Record<PreferredWorkType, string> = {
    [PreferredWorkType.FULL_TIME]: 'Toàn thời gian',
    [PreferredWorkType.PART_TIME]: 'Bán thời gian',
    [PreferredWorkType.REMOTE]: 'Từ xa (Remote)',
    [PreferredWorkType.HYBRID]: 'Linh hoạt (Hybrid)',
}