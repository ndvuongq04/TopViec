import { JobSeekingStatus, PreferredWorkType } from '@/constants/candidateProfile.constants'

// ─── Request DTOs ─────────────────────────────────────────────────────────────

export interface ReqCreateCandidateProfileDTO {
    fullName: string
    avatarUrl?: string | null
    dateOfBirth?: string | null        // ISO date: "YYYY-MM-DD"
    gender?: 'male' | 'female' | 'other' | null
    phoneDisplay?: string | null
    bio?: string | null
    linkedinUrl?: string | null
    githubUrl?: string | null
    personalWebsite?: string | null
    expectedSalaryMin?: number | null
    expectedSalaryMax?: number | null
    salaryNegotiable?: boolean
    jobSeekingStatus?: JobSeekingStatus
    preferredJobTitle?: string | null
    preferredWorkType?: PreferredWorkType | null
    preferredLocationId?: number | null
    isCvPublic?: boolean
    hidePhone?: boolean
    hideEmail?: boolean
    hideDateOfBirth?: boolean
    hideExpectedSalary?: boolean
}

export interface ReqUpdateCandidateProfileDTO {
    fullName: string
    avatarUrl?: string | null
    dateOfBirth?: string | null
    gender?: 'male' | 'female' | 'other' | null
    phoneDisplay?: string | null
    bio?: string | null
    linkedinUrl?: string | null
    githubUrl?: string | null
    personalWebsite?: string | null
    expectedSalaryMin?: number | null
    expectedSalaryMax?: number | null
    salaryNegotiable?: boolean | null
    jobSeekingStatus?: JobSeekingStatus | null
    preferredJobTitle?: string | null
    preferredWorkType?: PreferredWorkType | null
    preferredLocationId?: number | null
    cvPublic?: boolean | null
    hidePhone?: boolean | null
    hideEmail?: boolean | null
    hideDateOfBirth?: boolean | null
    hideExpectedSalary?: boolean | null
}

export interface ReqUpdateCandidateProfileVisibilityDTO {
    hidePhone: boolean
    hideEmail: boolean
    hideDateOfBirth: boolean
    hideExpectedSalary: boolean
}

// ─── Response DTO ─────────────────────────────────────────────────────────────

export interface ResCandidateProfileDTO {
    id: number
    userId: number
    fullName: string
    avatarUrl: string | null
    dateOfBirth: string | null          // BE trả LocalDate → JSON serializes thành "YYYY-MM-DD"
    gender: 'male' | 'female' | 'other' | null
    phoneDisplay: string | null
    bio: string | null
    linkedinUrl: string | null
    githubUrl: string | null
    personalWebsite: string | null
    expectedSalaryMin: number | null    // BE: Double
    expectedSalaryMax: number | null    // BE: Double
    salaryNegotiable: boolean
    jobSeekingStatus: JobSeekingStatus
    preferredJobTitle: string | null
    preferredWorkType: PreferredWorkType | null
    preferredLocationId: number | null  // BE: Integer
    profileCompletionPct: number | null // BE tính sẵn, dùng trực tiếp
    isCvPublic: boolean
    hidePhone: boolean
    hideEmail: boolean
    hideDateOfBirth: boolean
    hideExpectedSalary: boolean
    createdAt: string                   // BE: LocalDateTime → ISO string
    updatedAt: string
}
