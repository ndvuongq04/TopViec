import type { CvParseStatus, CvType, CvVisibility } from '@/constants/cvs.constants'
import type { PaginationMeta } from '@/types/common.types'

export type CvOnlineSupportedSection =
    | 'personalInfo'
    | 'careerObjective'
    | 'experiences'
    | 'educations'
    | 'skills'
    | 'certifications'
    | 'languages'
    | 'projects'
    | 'hobbies'
    | 'awards'
    | 'customSections'

export type CvOnlinePatchableSection =
    | 'personalInfo'
    | 'experiences'
    | 'educations'
    | 'skills'
    | 'certifications'
    | 'languages'

export interface CvOnlinePersonalInfo {
    fullName: string
    headline: string
    email: string
    phone: string
    address: string
    city: string
    country: string
    website: string
    linkedin: string
    github: string
}

export interface CvOnlineExperienceItem {
    id: string
    jobTitle: string
    company: string
    location: string
    startDate: string
    endDate: string
    isCurrent: boolean
    description: string
}

export interface CvOnlineEducationItem {
    id: string
    school: string
    degree: string
    fieldOfStudy: string
    startDate: string
    endDate: string
    description: string
}

export interface CvOnlineSkillItem {
    id: string
    name: string
    level: string
    description: string
}

export interface CvOnlineCertificationItem {
    id: string
    name: string
    issuer: string
    issuedAt: string
    expiresAt: string
    credentialId: string
    credentialUrl: string
    description: string
}

export interface CvOnlineLanguageItem {
    id: string
    name: string
    level: string
    certificate: string
}

export interface CvOnlineProjectItem {
    id: string
    name: string
    role: string
    organization: string
    startDate: string
    endDate: string
    projectUrl: string
    description: string
}

export interface CvOnlineHobbyItem {
    id: string
    name: string
    description: string
}

export interface CvOnlineAwardItem {
    id: string
    title: string
    issuer: string
    awardedAt: string
    description: string
}

export interface CvOnlineCustomSectionItem {
    id: string
    sectionTitle: string
    itemTitle: string
    itemSubtitle: string
    itemMeta: string
    description: string
}

export interface CvOnlineExtraData {
    personalInfo: CvOnlinePersonalInfo
    careerObjective: string
    experiences: CvOnlineExperienceItem[]
    educations: CvOnlineEducationItem[]
    skills: CvOnlineSkillItem[]
    certifications: CvOnlineCertificationItem[]
    languages: CvOnlineLanguageItem[]
    projects: CvOnlineProjectItem[]
    hobbies: CvOnlineHobbyItem[]
    awards: CvOnlineAwardItem[]
    customSections: CvOnlineCustomSectionItem[]
}

export type CvOnlineDraftStatus = 'local-only' | 'dirty' | 'synced'

export interface CvOnlineLocalDraft {
    localDraftId: string
    serverId: number | null
    persisted: boolean
    templateId: number
    title: string
    template: CvTemplateDetail
    pdfUrl: string | null
    pdfDirty: boolean
    status: CvOnlineDraftStatus
    createdAt: string
    updatedAt: string
    lastSyncedAt: string | null
    extraData: CvOnlineExtraData
}

export type CvOnlinePdfState = 'unavailable' | 'stale' | 'ready' | 'failed'

export interface CvTemplateListItem {
    id: number
    name: string
    slug: string
    description: string
    thumbnailUrl: string | null
    isActive: boolean
    isDefault: boolean
    versionTag?: string | null
    createdAt: string
    updatedAt: string
}

export interface CvTemplateDetail extends CvTemplateListItem {
    htmlContent: string
    cssContent: string
}

export interface ReqAdminCvTemplatePreview {
    templateId?: number
    htmlContent: string
    cssContent: string
}

export interface ResAdminCvTemplatePreview {
    templateId: number | null
    versionTag: string | null
    renderedHtml: string
    renderedXhtml: string
    sampleData: CvOnlineExtraData
    valid: boolean
    rootPlaceholders: string[]
    sections: string[]
    placeholderErrors: string[]
    cssWarnings: string[]
}

export interface CvTemplateAdminListResponse {
    meta: PaginationMeta
    result: CvTemplateListItem[]
}

export interface ReqGetAdminCvTemplates {
    page?: number
    size?: number
    sort?: string
    keyword?: string
}

export interface ReqCreateCvTemplate {
    name: string
    slug: string
    description: string
    htmlContent: string
    cssContent: string
    isActive: boolean
    isDefault: boolean
    thumbnail?: File | null
}

export interface ReqUpdateCvTemplateMetadata {
    name: string
    slug: string
    description: string
    thumbnailUrl: string
}

export interface ReqUpdateCvTemplateContent {
    htmlContent: string
    cssContent: string
}

export interface ReqCreateOnlineCv {
    title: string
    templateId: number
    isDefault?: boolean
    extraData?: CvOnlineExtraData
}

export interface ReqUpdateOnlineCv {
    title: string
    extraData: CvOnlineExtraData
}

export interface ReqChangeOnlineCvTemplate {
    templateId: number
}

export type ReqUpdateOnlineCvExperienceSectionItem = Omit<CvOnlineExperienceItem, 'id'>
export type ReqUpdateOnlineCvEducationSectionItem = Omit<CvOnlineEducationItem, 'id'>
export type ReqUpdateOnlineCvSkillSectionItem = Omit<CvOnlineSkillItem, 'id'>
export type ReqUpdateOnlineCvCertificationSectionItem = Omit<CvOnlineCertificationItem, 'id'>
export type ReqUpdateOnlineCvLanguageSectionItem = Omit<CvOnlineLanguageItem, 'id'>

export interface ReqUpdateOnlineCvSectionMap {
    personalInfo: CvOnlinePersonalInfo
    experiences: ReqUpdateOnlineCvExperienceSectionItem[]
    educations: ReqUpdateOnlineCvEducationSectionItem[]
    skills: ReqUpdateOnlineCvSkillSectionItem[]
    certifications: ReqUpdateOnlineCvCertificationSectionItem[]
    languages: ReqUpdateOnlineCvLanguageSectionItem[]
}

export interface ResOnlineCvEditorPayload {
    cvId: number | null
    persisted: boolean
    title: string
    cvType: CvType
    templateId: number
    template: CvTemplateDetail
    extraData: CvOnlineExtraData
    pdfUrl: string | null
    pdfDirty: boolean
    isDefault: boolean
    visibility: CvVisibility
    parseStatus: CvParseStatus
    viewCount: number
    createdAt: string | null
    updatedAt: string | null
}

export interface ResOnlineCv {
    id: number
    title: string
    cvType: CvType
    templateId: number
    template: CvTemplateDetail
    extraData: CvOnlineExtraData
    pdfUrl: string | null
    pdfDirty: boolean
    fileUrl?: string | null
    isDefault: boolean
    visibility: CvVisibility
    parseStatus: CvParseStatus
    viewCount: number
    createdAt: string
    updatedAt: string
}

export type ResOnlineCvSectionUpdate = Partial<ResOnlineCv> & {
    id: number
    title: string
    cvType: CvType
    templateId: number
    extraData: CvOnlineExtraData
    pdfUrl: string | null
    pdfDirty: boolean
    isDefault: boolean
    visibility: CvVisibility
}

export interface CandidateTemplateCardVM {
    id: number
    name: string
    thumbnail: string
    tags: string[]
    categories: string[]
    colors: string[]
    isNew: boolean
    language: 'vi' | 'en'
}

export interface AdminCvTemplateRowVM {
    id: number
    thumbnail: string
    name: string
    code: string
    versionTag: string | null
    categories: string
    features: string[]
    tier: 'free' | 'pro' | 'premium'
    tierLabel: string
    status: 'active' | 'draft' | 'archived'
    statusLabel: string
    usageCount: number
    isDraft: boolean
    isDefault: boolean
    raw: CvTemplateListItem
}
