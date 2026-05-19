import type {
    AdminCvTemplateRowVM,
    CandidateTemplateCardVM,
    CvOnlineAwardItem,
    CvOnlineCertificationItem,
    CvOnlineCustomSectionItem,
    CvOnlineEducationItem,
    CvOnlineExperienceItem,
    CvOnlineExtraData,
    CvOnlineHobbyItem,
    CvOnlineLanguageItem,
    CvOnlinePersonalInfo,
    CvOnlineProjectItem,
    CvOnlineSkillItem,
    CvTemplateDetail,
    CvTemplateListItem,
} from '@/types/cvOnline.types'

export const CV_ONLINE_PLACEHOLDERS = [
    '{{fullName}}',
    '{{headline}}',
    '{{email}}',
    '{{phone}}',
    '{{address}}',
    '{{city}}',
    '{{country}}',
    '{{website}}',
    '{{linkedin}}',
    '{{github}}',
    '{{careerObjective}}',
    '{{#experiences}}...{{/experiences}}',
    '{{#educations}}...{{/educations}}',
    '{{#skills}}...{{/skills}}',
    '{{#certifications}}...{{/certifications}}',
    '{{#languages}}...{{/languages}}',
    '{{#projects}}...{{/projects}}',
    '{{#hobbies}}...{{/hobbies}}',
    '{{#awards}}...{{/awards}}',
    '{{#customSections}}...{{/customSections}}',
] as const

export function createEmptyPersonalInfo(): CvOnlinePersonalInfo {
    return {
        fullName: '',
        headline: '',
        email: '',
        phone: '',
        address: '',
        city: '',
        country: '',
        website: '',
        linkedin: '',
        github: '',
    }
}

export function createEmptyExperience(): CvOnlineExperienceItem {
    return {
        id: `exp-${Date.now()}`,
        jobTitle: '',
        company: '',
        location: '',
        startDate: '',
        endDate: '',
        isCurrent: false,
        description: '',
    }
}

export function createEmptyEducation(): CvOnlineEducationItem {
    return {
        id: `edu-${Date.now()}`,
        school: '',
        degree: '',
        fieldOfStudy: '',
        startDate: '',
        endDate: '',
        description: '',
    }
}

export function createEmptySkill(): CvOnlineSkillItem {
    return {
        id: `skill-${Date.now()}`,
        name: '',
        level: '',
        description: '',
    }
}

export function createEmptyCertification(): CvOnlineCertificationItem {
    return {
        id: `cert-${Date.now()}`,
        name: '',
        issuer: '',
        issuedAt: '',
        expiresAt: '',
        credentialId: '',
        credentialUrl: '',
        description: '',
    }
}

export function createEmptyLanguage(): CvOnlineLanguageItem {
    return {
        id: `lang-${Date.now()}`,
        name: '',
        level: '',
        certificate: '',
    }
}

export function createEmptyProject(): CvOnlineProjectItem {
    return {
        id: `project-${Date.now()}`,
        name: '',
        role: '',
        organization: '',
        startDate: '',
        endDate: '',
        projectUrl: '',
        description: '',
    }
}

export function createEmptyHobby(): CvOnlineHobbyItem {
    return {
        id: `hobby-${Date.now()}`,
        name: '',
        description: '',
    }
}

export function createEmptyAward(): CvOnlineAwardItem {
    return {
        id: `award-${Date.now()}`,
        title: '',
        issuer: '',
        awardedAt: '',
        description: '',
    }
}

export function createEmptyCustomSection(): CvOnlineCustomSectionItem {
    return {
        id: `custom-${Date.now()}`,
        sectionTitle: '',
        itemTitle: '',
        itemSubtitle: '',
        itemMeta: '',
        description: '',
    }
}

export function createEmptyCvOnlineExtraData(): CvOnlineExtraData {
    return {
        personalInfo: createEmptyPersonalInfo(),
        careerObjective: '',
        experiences: [],
        educations: [],
        skills: [],
        certifications: [],
        languages: [],
        projects: [],
        hobbies: [],
        awards: [],
        customSections: [],
    }
}

export function normalizeExtraData(extraData?: Partial<CvOnlineExtraData> | null): CvOnlineExtraData {
    const base = createEmptyCvOnlineExtraData()
    const source = extraData ?? {}

    return {
        personalInfo: {
            ...base.personalInfo,
            ...(source.personalInfo ?? {}),
        },
        careerObjective: source.careerObjective ?? '',
        experiences: (source.experiences ?? []).map((item) => ({
            ...createEmptyExperience(),
            ...item,
            id: item.id || `exp-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        educations: (source.educations ?? []).map((item) => ({
            ...createEmptyEducation(),
            ...item,
            id: item.id || `edu-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        skills: (source.skills ?? []).map((item) => ({
            ...createEmptySkill(),
            ...item,
            id: item.id || `skill-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        certifications: (source.certifications ?? []).map((item) => ({
            ...createEmptyCertification(),
            ...item,
            id: item.id || `cert-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        languages: (source.languages ?? []).map((item) => ({
            ...createEmptyLanguage(),
            ...item,
            id: item.id || `lang-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        projects: (source.projects ?? []).map((item) => ({
            ...createEmptyProject(),
            ...item,
            id: item.id || `project-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        hobbies: (source.hobbies ?? []).map((item) => ({
            ...createEmptyHobby(),
            ...item,
            id: item.id || `hobby-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        awards: (source.awards ?? []).map((item) => ({
            ...createEmptyAward(),
            ...item,
            id: item.id || `award-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
        customSections: (source.customSections ?? []).map((item) => ({
            ...createEmptyCustomSection(),
            ...item,
            id: item.id || `custom-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        })),
    }
}

export function mapTemplateToCandidateCard(template: CvTemplateListItem): CandidateTemplateCardVM {
    return {
        id: template.id,
        name: template.name,
        thumbnail: template.thumbnailUrl || 'https://placehold.co/800x1130/e2e8f0/334155?text=CV',
        tags: [
            template.isDefault ? 'Default' : 'Template',
            template.isActive ? 'Active' : 'Inactive',
        ],
        categories: ['all', template.isDefault ? 'professional' : 'simple'],
        colors: ['#0f172a', '#1d4ed8', '#cbd5e1'],
        isNew: false,
        language: 'vi',
    }
}

export function mapTemplateToAdminRow(template: CvTemplateListItem): AdminCvTemplateRowVM {
    const status = template.isActive ? 'active' : 'draft'

    return {
        id: template.id,
        thumbnail: template.thumbnailUrl || '',
        name: template.name,
        code: template.slug.toUpperCase(),
        versionTag: template.versionTag ?? null,
        categories: template.description || 'CV Online Template',
        features: template.isDefault ? ['Default', 'Active'] : ['Active template'],
        tier: template.isDefault ? 'premium' : 'free',
        tierLabel: template.isDefault ? 'Default' : 'Free',
        status,
        statusLabel: status === 'active' ? 'Dang hoat dong' : 'Tam ngung',
        usageCount: 0,
        isDraft: !template.isActive,
        isDefault: template.isDefault,
        raw: template,
    }
}

export function mapTemplateDetailToForm(template: CvTemplateDetail) {
    return {
        basic: {
            name: template.name,
            code: template.slug.toUpperCase(),
            slug: template.slug,
            description: template.description ?? '',
        },
        media: {
            thumbnail: template.thumbnailUrl ?? '',
            primaryColor: '#963131',
            fontFamily: 'Inter',
        },
        classification: {
            category: template.isDefault ? 'Executive' : 'Professional',
            levels: ['Entry Level (0-2 years)'],
            industries: ['Technology'],
            features: template.isDefault ? ['Default'] : ['ATS-friendly'],
        },
        settings: {
            layout: 'two-column',
            tier: template.isDefault ? 'premium' : 'free',
            customization: {
                allowColorChange: true,
                allowFontChange: true,
                allowSectionReorder: false,
                allowAvatarToggle: true,
            },
        },
        publish: {
            status: template.isActive ? 'published' : 'draft',
            visibility: template.isActive ? 'active' : 'inactive',
            sortOrder: 0,
        },
        content: {
            htmlContent: template.htmlContent,
            cssContent: template.cssContent,
        },
    }
}
