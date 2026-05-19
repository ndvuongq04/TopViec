import { defineStore } from 'pinia'
import { ref } from 'vue'
import { candidateCompanyFollowService } from '@/services/candidateCompanyFollow.service'

export const useCandidateCompanyFollowStore = defineStore('candidateCompanyFollow', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const followMap = ref<Record<number, boolean>>({})
    const followedCompanies = ref<any>(null)
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const msg = (err as any)?.response?.data?.message
        if (typeof msg === 'object' && msg !== null) {
            error.value = Object.values(msg).join(', ')
        } else {
            error.value = msg ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /**
     * POST /candidate/companies/{companyId}/follow
     */
    async function followCompany(companyId: number) {
        loading.value = true
        error.value = null
        try {
            await candidateCompanyFollowService.followCompany(companyId)
            followMap.value[companyId] = true
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /**
     * DELETE /candidate/companies/{companyId}/follow
     */
    async function unfollowCompany(companyId: number) {
        loading.value = true
        error.value = null
        try {
            await candidateCompanyFollowService.unfollowCompany(companyId)
            followMap.value[companyId] = false
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /**
     * GET /candidate/companies/{companyId}/follow/status
     */
    async function checkFollowStatus(companyId: number) {
        loading.value = true
        error.value = null
        try {
            const data = await candidateCompanyFollowService.checkFollowStatus(companyId)
            followMap.value[companyId] = data.isFollowing
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /**
     * GET /candidate/companies/followed
     */
    async function getFollowedCompanies(page = 0, size = 10) {
        loading.value = true
        error.value = null
        try {
            const res = await candidateCompanyFollowService.getFollowedCompanies({ page, size })
            followedCompanies.value = res.data // Đây là payload bên trong 'data' của RestResponse
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    function reset() {
        followMap.value = {}
        followedCompanies.value = null
        loading.value = false
        error.value = null
    }

    return {
        followMap,
        followedCompanies,
        loading,
        error,
        followCompany,
        unfollowCompany,
        checkFollowStatus,
        getFollowedCompanies,
        reset,
    }
})
