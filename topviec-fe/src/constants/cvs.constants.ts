export const CV_PARSE_STATUS = {
    PENDING: 'pending',
    SUCCESS: 'success',
    FAILED: 'failed',
    SKIPPED: 'skipped',
} as const;

export type CvParseStatus = (typeof CV_PARSE_STATUS)[keyof typeof CV_PARSE_STATUS];

// ─────────────────────────────────────────────

export const CV_TYPE = {
    ONLINE: 'online',
    UPLOADED: 'uploaded',
} as const;

export type CvType = (typeof CV_TYPE)[keyof typeof CV_TYPE];

// ─────────────────────────────────────────────

export const CV_VISIBILITY = {
    PUBLIC: 'public_cv',
    PRIVATE: 'private_cv',
} as const;

export type CvVisibility = (typeof CV_VISIBILITY)[keyof typeof CV_VISIBILITY];

// ─────────────────────────────────────────────

export const FILE_UPLOAD_TYPE = {
    /** PDF/DOCX — resource_type = raw */
    CV: 'CV',
    /** Ảnh đại diện — resource_type = image */
    AVATAR: 'AVATAR',
    /** Ảnh bìa công ty — resource_type = image */
    COMPANY_COVER: 'COMPANY_COVER',
} as const;

export type FileUploadType = (typeof FILE_UPLOAD_TYPE)[keyof typeof FILE_UPLOAD_TYPE];