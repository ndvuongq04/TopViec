import type { CvParseStatus, CvType, CvVisibility } from '@/constants/cvs.constants';

// ─── Request ──────────────────────────────────────────────

export interface ReqRenameCv {
    title: string;
}

export interface ReqShareCv {
    visibility?: CvVisibility;
}

export interface ReqUploadCv {
    title: string;
    isDefault?: boolean;
}

// ─── Response ─────────────────────────────────────────────

export interface ResCv {
    id: number;
    title: string;
    cvType: CvType;
    fileUrl: string | null;
    pdfUrl: string | null;
    pdfDirty?: boolean;
    templateId?: number | null;
    isDefault: boolean;
    visibility: CvVisibility;
    shareToken: string | null;
    shareExpiresAt: string | null; // ISO 8601
    parseStatus: CvParseStatus;
    viewCount: number;

    // Audit
    createdBy: number;
    createdAt: string; // ISO 8601
    updatedAt: string; // ISO 8601
}

export type ResCvPdfExport = Partial<ResCv> & {
    pdfUrl: string | null;
    pdfDirty?: boolean;
}
