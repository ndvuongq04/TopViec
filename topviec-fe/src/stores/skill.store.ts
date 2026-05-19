import { defineStore } from 'pinia'
import { ref } from 'vue'
import { skillService } from '@/services/skill.service'
import type { ResSkillDTO, MasterDataQueryParams } from '@/types/masterData.types'
import type { PaginationMeta } from '@/types/common.types'

export const useSkillStore = defineStore('skill', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const skills = ref<ResSkillDTO[]>([])
    const selectedSkill = ref<ResSkillDTO | null>(null)
    const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const data = (err as any)?.response?.data
        if (data?.message && typeof data.message === 'object') {
            const firstError = Object.values(data.message)[0]
            error.value = String(firstError)
        } else {
            error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** Lấy danh sách Skill */
    async function fetchSkills(params?: MasterDataQueryParams) {
        loading.value = true
        error.value = null
        try {
            const data = await skillService.getSkills(params)
            skills.value = data.result
            meta.value = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Lấy chi tiết Skill */
    async function fetchSkillById(id: number | string) {
        loading.value = true
        error.value = null
        try {
            selectedSkill.value = await skillService.getSkillById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Reset store */
    function reset() {
        skills.value = []
        selectedSkill.value = null
        meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value = false
        error.value = null
    }

    return {
        // state
        skills,
        selectedSkill,
        meta,
        loading,
        error,
        // actions
        fetchSkills,
        fetchSkillById,
        reset,
    }
})
