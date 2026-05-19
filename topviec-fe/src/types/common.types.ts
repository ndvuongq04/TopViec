// src/types/common.types.ts
export interface RestResponse<T> {
    statusCode: number
    error: string | null
    message: string | string[]
    data: T
}

export interface PaginationMeta {
    page: number
    pageSize: number
    pages: number
    totals: number
}

export interface ResultPaginationDTO<T> {
    meta: PaginationMeta
    result: T[]
}