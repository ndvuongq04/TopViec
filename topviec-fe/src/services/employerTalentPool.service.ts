import axiosInstance from './axios';
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types';
import type { ReqInviteFromTalentPool } from '@/types/application.types';
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types';

export type TalentPoolSource = 'REVIEW_CV' | 'INTERVIEW' | 'SEARCH_IN_DB';

export interface ReqAddToTalentPoolDTO {
  candidateUserId: number;
  source: TalentPoolSource;
  note?: string;
}

export interface ResTalentPoolDTO {
  id: number;
  companyId: number;
  candidateUserId: number;
  candidateName: string;
  candidateAvatarUrl?: string;
  addedBy: number;
  source: string;
  note?: string;
  createdAt: string;
}

export interface ResTalentPoolCandidateDTO {
  talentPoolId: number;
  source: string;
  note?: string;
  addedAt: string;
  candidateUserId: number;
  candidateName: string;
  candidateEmail: string;
  candidateAvatarUrl?: string;
  preferredJobTitle?: string;
  preferredWorkType?: string;
  preferredLocationId?: number;
  preferredLocationName?: string;
  expectedSalaryMin?: number;
  expectedSalaryMax?: number;
  salaryNegotiable?: boolean;
  jobSeekingStatus?: string;
}

export type CvType = 'UPLOADED' | 'CREATED'
export type CvVisibility = 'PUBLIC' | 'PRIVATE' | 'RECRUITER_ONLY'

export interface ResTalentPoolCandidateDetailDTO {
  // Talent Pool entry
  talentPoolId: number
  source: string
  note?: string
  addedAt: string
  addedBy: number
  addedByName?: string

  // Thông tin cơ bản
  candidateUserId: number
  fullName: string
  avatarUrl?: string
  bio?: string
  gender?: string
  linkedinUrl?: string
  githubUrl?: string
  personalWebsite?: string
  profileCompletionPct?: number
  jobSeekingStatus?: string

  // Thông tin có thể ẩn
  phone?: string
  phoneHidden?: boolean
  email?: string
  emailHidden?: boolean
  dateOfBirth?: string
  dateOfBirthHidden?: boolean

  // Mong muốn công việc
  preferredJobTitle?: string
  preferredWorkType?: string
  preferredLocationId?: number
  preferredLocationName?: string
  expectedSalaryMin?: number
  expectedSalaryMax?: number
  salaryNegotiable?: boolean
  salaryHidden?: boolean

  // CV mặc định
  defaultCv?: {
    cvId: number
    title: string
    cvType: CvType
    fileUrl?: string
    pdfUrl?: string
    visibility: CvVisibility
    viewCount: number
    createdAt: string
  }
}

export interface TalentPoolParams {
  source?: string;
  search?: string;
  page?: number;
  size?: number;
}

export interface ResCandidateSearchResultDTO {
  candidateUserId: number;
  fullName: string;
  avatarUrl?: string;
  preferredJobTitle?: string;
  preferredWorkType?: string;
  preferredLocationId?: number;
  preferredLocationName?: string;
  expectedSalaryMin?: number;
  expectedSalaryMax?: number;
  salaryNegotiable?: boolean;
  jobSeekingStatus?: string;
  alreadyInPool: boolean;
}

export interface CandidateSearchParams {
  locationId: number;
  page?: number;
  size?: number;
}

const BASE_URL = '/employer/talent-pool';

const employerTalentPoolService = {
  async addToTalentPool(data: ReqAddToTalentPoolDTO): Promise<ResTalentPoolDTO> {
    const res = await axiosInstance.post<RestResponse<ResTalentPoolDTO>>(BASE_URL, data);
    return res.data.data;
  },

  async updateNote(talentPoolId: number, note: string): Promise<void> {
    await axiosInstance.patch(`${BASE_URL}/${talentPoolId}/note`, { note });
  },

  async removeFromTalentPool(talentPoolId: number): Promise<void> {
    await axiosInstance.delete(`${BASE_URL}/${talentPoolId}`);
  },

  async getTalentPoolCandidateDetail(talentPoolId: number): Promise<ResTalentPoolCandidateDetailDTO> {
    const res = await axiosInstance.get<RestResponse<ResTalentPoolCandidateDetailDTO>>(`${BASE_URL}/${talentPoolId}`);
    return res.data.data;
  },

  async getCandidateDetail(candidateUserId: number): Promise<ResTalentPoolCandidateDetailDTO> {
    const res = await axiosInstance.get<RestResponse<ResTalentPoolCandidateDetailDTO>>(
      `${BASE_URL}/candidates/${candidateUserId}`,
    );
    return res.data.data;
  },

  async getTalentPool(params: TalentPoolParams): Promise<ResultPaginationDTO<ResTalentPoolCandidateDTO>> {
    const query: Record<string, any> = {}
    if (params.source) query.source = params.source
    if (params.search) query.search = params.search
    if (params.page !== undefined) query.page = params.page
    if (params.size !== undefined) query.size = params.size
    const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResTalentPoolCandidateDTO>>>(BASE_URL, { params: query });
    return res.data.data;
  },

  async invite(talentPoolId: number, data: ReqInviteFromTalentPool): Promise<ResEmployerApplicationDTO> {
    const res = await axiosInstance.post<RestResponse<ResEmployerApplicationDTO>>(
      `${BASE_URL}/${talentPoolId}/invite`,
      data,
    );
    return res.data.data;
  },

  async searchCandidates(params: CandidateSearchParams): Promise<ResultPaginationDTO<ResCandidateSearchResultDTO>> {
    const query: Record<string, any> = { locationId: params.locationId };
    if (params.page !== undefined) query.page = params.page;
    if (params.size !== undefined) query.size = params.size;
    const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResCandidateSearchResultDTO>>>(
      `${BASE_URL}/search-candidates`,
      { params: query },
    );
    return res.data.data;
  },
};

export default employerTalentPoolService;
