export enum ServiceCategory {
    JOB_POSTING    = 'JOB_POSTING',
    CANDIDATE      = 'CANDIDATE',
    BRANDING       = 'BRANDING',
    ADDON_PACKAGE  = 'ADDON_PACKAGE',
}

export const SERVICE_CATEGORY_LABELS: Record<ServiceCategory, string> = {
    [ServiceCategory.JOB_POSTING]:   'Nhóm tin tuyển dụng',
    [ServiceCategory.CANDIDATE]:     'Nhóm hồ sơ',
    [ServiceCategory.BRANDING]:      'Nhóm thương hiệu',
    [ServiceCategory.ADDON_PACKAGE]: 'Gói dịch vụ thêm',
}

export interface ServiceCategoryIconMeta {
    icon:      string
    iconBg:    string
    iconColor: string
}

export const SERVICE_CATEGORY_ICON_MAP: Record<ServiceCategory, ServiceCategoryIconMeta> = {
    [ServiceCategory.JOB_POSTING]:   { icon: 'campaign',      iconBg: '#eff6ff', iconColor: '#2563eb' },
    [ServiceCategory.CANDIDATE]:     { icon: 'person_search', iconBg: '#ecfdf5', iconColor: '#059669' },
    [ServiceCategory.BRANDING]:      { icon: 'verified',      iconBg: '#fdf4ff', iconColor: '#9333ea' },
    [ServiceCategory.ADDON_PACKAGE]: { icon: 'star',          iconBg: '#fff7ed', iconColor: '#ea580c' },
}
