// src/types/masterData.types.ts
import type { PaginationMeta } from './common.types'

// --- Parameter Types ---
export interface MasterDataQueryParams {
    keyword?: string
    page?: number
    size?: number
}

// --- Level ---
export interface ResLevelDTO {
    id: number
    name: string
    description?: string // Tùy chọn, tùy BE trả về
    [key: string]: any
}

// --- Location ---
export interface ResLocationDTO {
    id: number
    name: string
    [key: string]: any
}

export interface ResSkillDTO {
    id: number
    name: string
    [key: string]: any
}

// --- Industry ---
export interface ResIndustryDTO {
    id: number
    name: string
    [key: string]: any
}
