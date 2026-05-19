import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import cvTemplateService from '@/services/cvTemplate.service'
import {
    mapTemplateDetailToForm,
    mapTemplateToAdminRow,
    mapTemplateToCandidateCard,
} from '@/constants/cvOnline.constants'
import type {
    CvOnlineExtraData,
    CvTemplateDetail,
    CvTemplateListItem,
    ReqAdminCvTemplatePreview,
    ReqCreateCvTemplate,
    ReqGetAdminCvTemplates,
    ReqUpdateCvTemplateContent,
    ReqUpdateCvTemplateMetadata,
    ResAdminCvTemplatePreview,
} from '@/types/cvOnline.types'

export const useCvTemplateStore = defineStore('cvTemplate', () => {
    const publicTemplates = ref<CvTemplateListItem[]>([])
    const publicTemplateDetails = ref<Record<number, CvTemplateDetail>>({})
    const adminTemplates = ref<CvTemplateListItem[]>([])
    const adminMeta = ref({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const currentAdminTemplate = ref<CvTemplateDetail | null>(null)
    const adminTemplateSampleData = ref<CvOnlineExtraData | null>(null)
    const adminTemplatePreview = ref<ResAdminCvTemplatePreview | null>(null)
    const loading = ref(false)
    const submitting = ref(false)
    const previewing = ref(false)
    const error = ref<string | null>(null)

    const candidateTemplates = computed(() => publicTemplates.value.map(mapTemplateToCandidateCard))
    const adminRows = computed(() => adminTemplates.value.map(mapTemplateToAdminRow))
    const adminKpis = computed(() => {
        const total = adminTemplates.value.length
        const active = adminTemplates.value.filter((item) => item.isActive).length
        const defaults = adminTemplates.value.filter((item) => item.isDefault).length
        const inactive = total - active
        return [
            { label: 'Tong so mau CV', value: String(adminMeta.value.totals || total), icon: 'folder_copy', iconBg: '#e4e2dc', iconColor: '#574240', trend: `Dang hoat dong: ${active}`, trendVariant: 'up' },
            { label: 'Tam ngung', value: String(inactive), icon: 'edit_document', iconBg: '#faeeda', iconColor: '#633806', trend: 'Can kiem tra', trendVariant: 'warn' },
            { label: 'Template mac dinh', value: String(defaults), icon: 'workspace_premium', iconBg: '#eeedfe', iconColor: '#3c3489', trend: 'He thong', trendVariant: 'neutral' },
            { label: 'Page hien tai', value: String(adminMeta.value.page + 1), icon: 'trending_up', iconBg: '#ffdad6', iconColor: '#ba1a1a', trend: `${adminMeta.value.totals} template`, trendVariant: 'up' },
        ]
    })

    function setError(err: unknown) {
        const message = (err as any)?.response?.data?.message
        error.value = typeof message === 'string' ? message : 'Co loi xay ra. Vui long thu lai.'
    }

    async function fetchPublicTemplates() {
        loading.value = true
        error.value = null
        try {
            publicTemplates.value = await cvTemplateService.getActiveTemplates()
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function fetchPublicTemplateById(id: number) {
        loading.value = true
        error.value = null
        try {
            const template = await cvTemplateService.getActiveTemplateById(id)
            publicTemplateDetails.value[id] = template
            return template
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    function getTemplateById(id: number) {
        return publicTemplateDetails.value[id] ?? publicTemplates.value.find((template) => template.id === id) ?? null
    }

    async function fetchAdminTemplates(params: ReqGetAdminCvTemplates) {
        loading.value = true
        error.value = null
        try {
            const response = await cvTemplateService.getAdminTemplates(params)
            adminTemplates.value = response.result
            adminMeta.value = response.meta
            return response
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function fetchAdminTemplateById(id: number) {
        loading.value = true
        error.value = null
        try {
            currentAdminTemplate.value = await cvTemplateService.getAdminTemplateById(id)
            return currentAdminTemplate.value
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function createTemplate(payload: ReqCreateCvTemplate) {
        submitting.value = true
        error.value = null
        try {
            const created = await cvTemplateService.createTemplate(payload)
            currentAdminTemplate.value = created
            return created
        } catch (err) {
            setError(err)
            throw err
        } finally {
            submitting.value = false
        }
    }

    async function fetchAdminTemplateSampleData() {
        loading.value = true
        error.value = null
        try {
            adminTemplateSampleData.value = await cvTemplateService.getAdminTemplateSampleData()
            return adminTemplateSampleData.value
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function previewAdminTemplate(payload: ReqAdminCvTemplatePreview) {
        previewing.value = true
        error.value = null
        try {
            adminTemplatePreview.value = await cvTemplateService.previewAdminTemplate(payload)
            return adminTemplatePreview.value
        } catch (err) {
            setError(err)
            throw err
        } finally {
            previewing.value = false
        }
    }

    async function updateTemplateMetadata(id: number, payload: ReqUpdateCvTemplateMetadata) {
        submitting.value = true
        error.value = null
        try {
            const updated = await cvTemplateService.updateTemplateMetadata(id, payload)
            currentAdminTemplate.value = updated
            return updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            submitting.value = false
        }
    }

    async function updateTemplateContent(id: number, payload: ReqUpdateCvTemplateContent) {
        submitting.value = true
        error.value = null
        try {
            const updated = await cvTemplateService.updateTemplateContent(id, payload)
            currentAdminTemplate.value = updated
            return updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            submitting.value = false
        }
    }

    async function activateTemplate(id: number) {
        submitting.value = true
        error.value = null
        try {
            return await cvTemplateService.activateTemplate(id)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            submitting.value = false
        }
    }

    async function deactivateTemplate(id: number) {
        submitting.value = true
        error.value = null
        try {
            return await cvTemplateService.deactivateTemplate(id)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            submitting.value = false
        }
    }

    async function setDefaultTemplate(id: number) {
        submitting.value = true
        error.value = null
        try {
            return await cvTemplateService.setDefaultTemplate(id)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            submitting.value = false
        }
    }

    function getCurrentTemplateForm() {
        return currentAdminTemplate.value ? mapTemplateDetailToForm(currentAdminTemplate.value) : null
    }

    return {
        publicTemplates,
        publicTemplateDetails,
        adminTemplates,
        adminMeta,
        currentAdminTemplate,
        adminTemplateSampleData,
        adminTemplatePreview,
        loading,
        submitting,
        previewing,
        error,
        candidateTemplates,
        adminRows,
        adminKpis,
        fetchPublicTemplates,
        fetchPublicTemplateById,
        getTemplateById,
        fetchAdminTemplates,
        fetchAdminTemplateById,
        fetchAdminTemplateSampleData,
        previewAdminTemplate,
        createTemplate,
        updateTemplateMetadata,
        updateTemplateContent,
        activateTemplate,
        deactivateTemplate,
        setDefaultTemplate,
        getCurrentTemplateForm,
    }
})
