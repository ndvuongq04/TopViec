import type { ResJobPostingSummary } from './jobPosting.types'
import type { ResultPaginationDTO } from './common.types'

export interface ResSavedJobDTO {
  id: number
  jobPostId: number
  savedAt: string // LocalDateTime mapped to ISO string
  jobPosting: ResJobPostingSummary
}

export type ResSavedJobPagination = ResultPaginationDTO<ResSavedJobDTO>
