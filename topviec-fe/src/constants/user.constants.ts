
// ⚠️  Đồng bộ với BE enum: com.topviec.topviec_be.enums.users.UserStatus
export enum UserStatus {
    PENDING     = 'pending',
    ACTIVE      = 'active',
    LOCKED_PERM = 'locked_perm',
}

export const USER_STATUS_LABELS: Record<UserStatus, string> = {
    [UserStatus.PENDING]:     'Chờ duyệt',
    [UserStatus.ACTIVE]:      'Hoạt động',
    [UserStatus.LOCKED_PERM]: 'Đã khóa vĩnh viễn',
}

export const USER_STATUS_STYLES: Record<UserStatus, { bg: string; text: string; dot: string }> = {
    [UserStatus.PENDING]: {
        bg: 'bg-amber-100 text-amber-700',
        text: 'text-amber-700',
        dot: 'bg-amber-700',
    },
    [UserStatus.ACTIVE]: {
        bg: 'bg-green-100 text-green-700',
        text: 'text-green-700',
        dot: 'bg-green-700',
    },
    [UserStatus.LOCKED_PERM]: {
        bg: 'bg-red-100 text-red-700',
        text: 'text-red-700',
        dot: 'bg-red-700',
    },
}
