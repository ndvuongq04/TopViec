import { defineStore } from 'pinia'
import { ref } from 'vue'
import { employerServiceManagementService } from '@/services/employerServiceManagement.service'
import type {
    ReqApplyAddonDTO,
    ReqRenewSubscriptionDTO,
    ResCompanySubscriptionDTO,
    ResCompanyAddonDTO,
    ResJobPostAddonDTO,
    ResCompanyBrandingDTO,
    ResSubscriptionRenewalDTO,
} from '@/types/servicePackage.types'

// ─── Helper ───────────────────────────────────────────────────────────────────

function extractErrorMessage(err: unknown): string {
    const data = (err as any)?.response?.data
    if (data?.message && typeof data.message === 'object') {
        return String(Object.values(data.message)[0])
    }
    return data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
}

// ─── Store ────────────────────────────────────────────────────────────────────

export const useEmployerServiceManagementStore = defineStore('employerServiceManagement', () => {
    const subscription    = ref<ResCompanySubscriptionDTO | null>(null)
    const addons          = ref<ResCompanyAddonDTO[]>([])
    const lastRenewalResult = ref<ResSubscriptionRenewalDTO | null>(null)
    const loading         = ref(false)
    const renewing        = ref(false)
    const error           = ref<string | null>(null)

    // ─── Subscription ─────────────────────────────────────────────────────────

    async function fetchMySubscription() {
        loading.value = true
        error.value   = null
        try {
            subscription.value = await employerServiceManagementService.getMySubscription()
        } catch (err) {
            error.value = extractErrorMessage(err)
        } finally {
            loading.value = false
        }
    }

    // ─── Addons ───────────────────────────────────────────────────────────────

    async function renewSubscription(
        payload: ReqRenewSubscriptionDTO,
    ): Promise<ResSubscriptionRenewalDTO> {
        renewing.value = true
        error.value = null
        try {
            const result = await employerServiceManagementService.renewSubscription(payload)
            lastRenewalResult.value = result
            subscription.value = await employerServiceManagementService.getMySubscription()
            return result
        } catch (err) {
            error.value = extractErrorMessage(err)
            throw err
        } finally {
            renewing.value = false
        }
    }

    async function fetchMyAddons() {
        loading.value = true
        error.value   = null
        try {
            addons.value = await employerServiceManagementService.getMyAddons()
        } catch (err) {
            error.value = extractErrorMessage(err)
        } finally {
            loading.value = false
        }
    }

    // ─── Apply addon to job post ──────────────────────────────────────────────

    async function applyAddonToJobPost(
        jobPostingId: number,
        payload: ReqApplyAddonDTO,
    ): Promise<ResJobPostAddonDTO> {
        loading.value = true
        error.value   = null
        try {
            const result = await employerServiceManagementService.applyAddonToJobPost(
                jobPostingId,
                payload,
            )
            // Trừ quantity_remaining trên addon vừa được áp dụng (cập nhật local state)
            const addonIdx = addons.value.findIndex(a => a.id === payload.companyAddonId)
            if (addonIdx !== -1) {
                addons.value[addonIdx] = {
                    ...addons.value[addonIdx],
                    quantityRemaining: addons.value[addonIdx].quantityRemaining - 1,
                }
            }
            return result
        } catch (err) {
            error.value = extractErrorMessage(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    // ─── Apply branding to company ────────────────────────────────────────────

    async function applyBrandingToCompany(payload: ReqApplyAddonDTO): Promise<ResCompanyBrandingDTO> {
        loading.value = true
        error.value   = null
        try {
            return await employerServiceManagementService.applyBrandingToCompany(payload)
        } catch (err) {
            error.value = extractErrorMessage(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    // ─── Reset ────────────────────────────────────────────────────────────────

    function reset() {
        subscription.value = null
        addons.value       = []
        lastRenewalResult.value = null
        loading.value      = false
        renewing.value     = false
        error.value        = null
    }

    return {
        subscription,
        addons,
        lastRenewalResult,
        loading,
        renewing,
        error,
        fetchMySubscription,
        renewSubscription,
        fetchMyAddons,
        applyAddonToJobPost,
        applyBrandingToCompany,
        reset,
    }
})
