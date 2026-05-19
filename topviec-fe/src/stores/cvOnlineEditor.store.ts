import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import cvOnlineService from '@/services/cvOnline.service'
import {
    createEmptyAward,
    createEmptyCertification,
    createEmptyCustomSection,
    createEmptyCvOnlineExtraData,
    createEmptyEducation,
    createEmptyExperience,
    createEmptyHobby,
    createEmptyLanguage,
    createEmptyProject,
    createEmptySkill,
    normalizeExtraData,
} from '@/constants/cvOnline.constants'
import type {
    CvOnlineAwardItem,
    CvOnlineCertificationItem,
    CvOnlineCustomSectionItem,
    CvOnlineLocalDraft,
    CvOnlinePatchableSection,
    CvOnlinePdfState,
    CvOnlineEducationItem,
    CvOnlineExperienceItem,
    CvOnlineExtraData,
    CvOnlineHobbyItem,
    CvOnlineLanguageItem,
    CvOnlineProjectItem,
    CvOnlineSkillItem,
    CvTemplateDetail,
    ReqUpdateOnlineCvSectionMap,
    ResOnlineCv,
    ResOnlineCvEditorPayload,
    ResOnlineCvSectionUpdate,
} from '@/types/cvOnline.types'

const DRAFT_INDEX_KEY = 'cv_online_draft_index'
const DRAFT_STORAGE_PREFIX = 'cv_online_draft:'
const AUTOSAVE_DELAY_MS = 800
const SECTION_SAVE_ORDER: CvOnlinePatchableSection[] = [
    'personalInfo',
    'experiences',
    'educations',
    'skills',
    'certifications',
    'languages',
]

function isBrowser() {
    return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'
}

function nowIso() {
    return new Date().toISOString()
}

function buildDraftStorageKey(localDraftId: string) {
    return `${DRAFT_STORAGE_PREFIX}${localDraftId}`
}

function generateLocalDraftId() {
    if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
        return crypto.randomUUID()
    }
    return `cv-draft-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`
}

function readDraftIndex(): string[] {
    if (!isBrowser()) return []
    const raw = window.localStorage.getItem(DRAFT_INDEX_KEY)
    if (!raw) return []

    try {
        const parsed = JSON.parse(raw)
        return Array.isArray(parsed) ? parsed.filter((item): item is string => typeof item === 'string') : []
    } catch {
        return []
    }
}

function writeDraftIndex(localDraftIds: string[]) {
    if (!isBrowser()) return
    window.localStorage.setItem(DRAFT_INDEX_KEY, JSON.stringify([...new Set(localDraftIds)]))
}

function persistDraftToStorage(draft: CvOnlineLocalDraft) {
    if (!isBrowser()) return
    window.localStorage.setItem(buildDraftStorageKey(draft.localDraftId), JSON.stringify(draft))
    writeDraftIndex([...readDraftIndex(), draft.localDraftId])
}

function removeDraftFromStorage(localDraftId: string) {
    if (!isBrowser()) return
    window.localStorage.removeItem(buildDraftStorageKey(localDraftId))
    writeDraftIndex(readDraftIndex().filter((item) => item !== localDraftId))
}

function readDraftFromStorage(localDraftId: string): CvOnlineLocalDraft | null {
    if (!isBrowser()) return null
    const raw = window.localStorage.getItem(buildDraftStorageKey(localDraftId))
    if (!raw) return null

    try {
        const parsed = JSON.parse(raw) as Partial<CvOnlineLocalDraft>
        if (
            !parsed.localDraftId ||
            typeof parsed.templateId !== 'number' ||
            !parsed.template ||
            typeof parsed.template.id !== 'number' ||
            typeof parsed.template.htmlContent !== 'string' ||
            typeof parsed.template.cssContent !== 'string'
        ) {
            return null
        }

        return {
            localDraftId: parsed.localDraftId,
            serverId: typeof parsed.serverId === 'number' ? parsed.serverId : null,
            persisted: typeof parsed.persisted === 'boolean' ? parsed.persisted : typeof parsed.serverId === 'number',
            templateId: parsed.templateId,
            title: parsed.title ?? '',
            template: parsed.template as CvTemplateDetail,
            pdfUrl: parsed.pdfUrl ?? null,
            pdfDirty: parsed.pdfDirty ?? false,
            status: parsed.status ?? 'local-only',
            createdAt: parsed.createdAt ?? nowIso(),
            updatedAt: parsed.updatedAt ?? nowIso(),
            lastSyncedAt: parsed.lastSyncedAt ?? null,
            extraData: normalizeExtraData(parsed.extraData),
        }
    } catch {
        return null
    }
}

function readAllDraftsFromStorage(): CvOnlineLocalDraft[] {
    return readDraftIndex()
        .map((localDraftId) => readDraftFromStorage(localDraftId))
        .filter((draft): draft is CvOnlineLocalDraft => Boolean(draft))
        .sort((left, right) => new Date(right.updatedAt).getTime() - new Date(left.updatedAt).getTime())
}

function findRecoverableDraftForPayload(payload: ResOnlineCvEditorPayload) {
    if (!payload.cvId) return null

    const serverUpdatedAt = payload.updatedAt ? new Date(payload.updatedAt).getTime() : 0
    return readAllDraftsFromStorage().find((draft) => {
        if (draft.serverId !== payload.cvId || draft.status === 'synced') return false
        return new Date(draft.updatedAt).getTime() > serverUpdatedAt
    }) ?? null
}

function mapEditorPayloadToLocalDraft(payload: ResOnlineCvEditorPayload): CvOnlineLocalDraft {
    const timestamp = nowIso()

    return {
        localDraftId: generateLocalDraftId(),
        serverId: payload.cvId,
        persisted: payload.persisted,
        templateId: payload.templateId,
        title: payload.title,
        template: payload.template,
        pdfUrl: payload.pdfUrl,
        pdfDirty: payload.pdfDirty ?? false,
        status: payload.persisted ? 'synced' : 'local-only',
        createdAt: payload.createdAt ?? timestamp,
        updatedAt: payload.updatedAt ?? timestamp,
        lastSyncedAt: payload.persisted ? (payload.updatedAt ?? timestamp) : null,
        extraData: normalizeExtraData(payload.extraData),
    }
}

function extractApiErrorMessage(err: unknown, fallback: string) {
    const message = (err as any)?.response?.data?.message

    if (typeof message === 'string') {
        return message
    }

    if (Array.isArray(message)) {
        return message.filter((item): item is string => typeof item === 'string').join('\n') || fallback
    }

    if (message && typeof message === 'object') {
        const firstMessage = Object.values(message).find((value): value is string => typeof value === 'string')
        return firstMessage ?? fallback
    }

    return fallback
}

function stripLocalId<T extends { id: string }>(items: T[]) {
    return items.map(({ id: _id, ...item }) => item)
}

function buildSectionPayload<T extends CvOnlinePatchableSection>(
    section: T,
    extraData: CvOnlineExtraData,
): ReqUpdateOnlineCvSectionMap[T] {
    switch (section) {
        case 'personalInfo':
            return { ...extraData.personalInfo } as ReqUpdateOnlineCvSectionMap[T]
        case 'experiences':
            return stripLocalId(extraData.experiences) as ReqUpdateOnlineCvSectionMap[T]
        case 'educations':
            return stripLocalId(extraData.educations) as ReqUpdateOnlineCvSectionMap[T]
        case 'skills':
            return stripLocalId(extraData.skills) as ReqUpdateOnlineCvSectionMap[T]
        case 'certifications':
            return stripLocalId(extraData.certifications) as ReqUpdateOnlineCvSectionMap[T]
        case 'languages':
            return stripLocalId(extraData.languages) as ReqUpdateOnlineCvSectionMap[T]
    }
}

function isPatchableSection(section: keyof CvOnlineExtraData): section is CvOnlinePatchableSection {
    return SECTION_SAVE_ORDER.includes(section as CvOnlinePatchableSection)
}

type CvOnlineListSection = keyof Pick<
    CvOnlineExtraData,
    | 'experiences'
    | 'educations'
    | 'skills'
    | 'certifications'
    | 'languages'
    | 'projects'
    | 'hobbies'
    | 'awards'
    | 'customSections'
>

export const useCvOnlineEditorStore = defineStore('cvOnlineEditor', () => {
    const currentDraft = ref<CvOnlineLocalDraft | null>(null)
    const loading = ref(false)
    const saving = ref(false)
    const switchingTemplate = ref(false)
    const error = ref<string | null>(null)
    const pdfError = ref<string | null>(null)
    const lastSavedAt = ref<string | null>(null)
    const hasPendingChanges = ref(false)
    const lastLoadedAt = ref<string | null>(null)
    const lastLocalSavedAt = ref<string | null>(null)
    const dirtySectionKeys = ref<CvOnlinePatchableSection[]>([])
    const fullSaveRequired = ref(false)
    let autosaveTimer: ReturnType<typeof setTimeout> | null = null

    const currentCv = computed(() => {
        if (!currentDraft.value) return null

        return {
            id: currentDraft.value.serverId,
            localDraftId: currentDraft.value.localDraftId,
            title: currentDraft.value.title,
            templateId: currentDraft.value.templateId,
            persisted: currentDraft.value.persisted,
            status: currentDraft.value.status,
            template: currentDraft.value.template,
            pdfUrl: currentDraft.value.pdfUrl,
            pdfDirty: currentDraft.value.pdfDirty,
            extraData: currentDraft.value.extraData,
        }
    })

    const extraData = computed<CvOnlineExtraData>(() =>
        normalizeExtraData(currentDraft.value?.extraData ?? createEmptyCvOnlineExtraData()),
    )

    const saveStateLabel = computed(() => {
        if (switchingTemplate.value) return 'Dang doi template...'
        if (saving.value) return 'Dang luu len he thong...'
        if (hasPendingChanges.value) return 'Co thay doi chua luu'
        if (lastSavedAt.value) {
            return `Da luu len he thong luc ${new Date(lastSavedAt.value).toLocaleTimeString('vi-VN')}`
        }
        if (lastLocalSavedAt.value) {
            return `Da luu nhap cuc bo luc ${new Date(lastLocalSavedAt.value).toLocaleTimeString('vi-VN')}`
        }
        return 'Chua co thay doi'
    })

    const pdfState = computed<CvOnlinePdfState>(() => {
        if (pdfError.value) return 'failed'
        if (!currentDraft.value?.serverId) return 'unavailable'
        if (hasPendingChanges.value || currentDraft.value.pdfDirty) return 'stale'
        if (currentDraft.value.pdfUrl) return 'ready'
        return 'unavailable'
    })

    const pdfStateLabel = computed(() => {
        switch (pdfState.value) {
            case 'failed':
                return 'Khong tai duoc PDF'
            case 'stale':
                return 'PDF se duoc tao/cap nhat khi xem hoac tai'
            case 'ready':
                return 'PDF hien tai da moi nhat'
            default:
                return currentDraft.value?.serverId ? 'Chua co PDF' : 'Chua luu CV'
        }
    })

    function setError(err: unknown) {
        error.value = extractApiErrorMessage(err, 'Co loi xay ra. Vui long thu lai.')
    }

    function clearDirtyTracking() {
        dirtySectionKeys.value = []
        fullSaveRequired.value = false
    }

    function markSectionDirty(section: CvOnlinePatchableSection) {
        if (!dirtySectionKeys.value.includes(section)) {
            dirtySectionKeys.value = [...dirtySectionKeys.value, section]
        }
    }

    function clearAutosaveTimer() {
        if (!autosaveTimer) return
        clearTimeout(autosaveTimer)
        autosaveTimer = null
    }

    function applyDraft(nextDraft: CvOnlineLocalDraft) {
        currentDraft.value = {
            ...nextDraft,
            extraData: normalizeExtraData(nextDraft.extraData),
        }
        lastSavedAt.value = nextDraft.lastSyncedAt
        lastLocalSavedAt.value = nextDraft.updatedAt
        pdfError.value = null
        hasPendingChanges.value = nextDraft.status !== 'synced'
        clearDirtyTracking()
        if (nextDraft.status !== 'synced') {
            fullSaveRequired.value = true
        }
    }

    function flushLocalDraft() {
        if (!currentDraft.value) return
        clearAutosaveTimer()
        persistDraftToStorage({
            ...currentDraft.value,
            extraData: normalizeExtraData(currentDraft.value.extraData),
        })
        lastLocalSavedAt.value = currentDraft.value.updatedAt
    }

    function markDirty(scope: CvOnlinePatchableSection | 'full' = 'full') {
        if (!currentDraft.value) return
        currentDraft.value.updatedAt = nowIso()
        if (currentDraft.value.serverId) {
            currentDraft.value.status = 'dirty'
            if (scope === 'full') {
                fullSaveRequired.value = true
            } else {
                markSectionDirty(scope)
            }
        } else {
            currentDraft.value.status = 'local-only'
            fullSaveRequired.value = true
        }
        currentDraft.value.pdfDirty = true
        hasPendingChanges.value = true
    }

    async function createLocalDraftFromTemplate(templateId: number) {
        loading.value = true
        error.value = null
        try {
            const payload = await cvOnlineService.getOnlineCvEditorPayloadByTemplateId(templateId)
            const draft = mapEditorPayloadToLocalDraft(payload)
            persistDraftToStorage(draft)
            applyDraft(draft)
            lastLoadedAt.value = nowIso()
            return draft
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function loadDraftByLocalId(localDraftId: string) {
        loading.value = true
        error.value = null
        try {
            const draft = readDraftFromStorage(localDraftId)
            if (!draft) {
                throw new Error('LOCAL_DRAFT_NOT_FOUND')
            }

            applyDraft(draft)
            lastLoadedAt.value = nowIso()
            return draft
        } catch (err) {
            if ((err as Error).message === 'LOCAL_DRAFT_NOT_FOUND') {
                error.value = 'Khong tim thay ban nhap cuc bo.'
            } else {
                setError(err)
            }
            throw err
        } finally {
            loading.value = false
        }
    }

    async function bootstrapDraftFromServerId(id: number) {
        loading.value = true
        error.value = null
        try {
            const payload = await cvOnlineService.getOnlineCvEditorPayload(id)
            const recoverableDraft = findRecoverableDraftForPayload(payload)
            if (recoverableDraft) {
                applyDraft(recoverableDraft)
                lastLoadedAt.value = nowIso()
                return recoverableDraft
            }

            const draft = mapEditorPayloadToLocalDraft(payload)
            persistDraftToStorage(draft)
            applyDraft(draft)
            lastLoadedAt.value = nowIso()
            return draft
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    function applySavedResponseToDraft(
        savedDraft: CvOnlineLocalDraft,
        response: ResOnlineCv | ResOnlineCvSectionUpdate,
        fallbackExtraData: CvOnlineExtraData,
    ) {
        const syncedAt = response.updatedAt ?? nowIso()

        savedDraft.serverId = response.id
        savedDraft.title = response.title ?? savedDraft.title
        savedDraft.templateId = response.templateId ?? savedDraft.templateId
        savedDraft.template = response.template ?? savedDraft.template
        savedDraft.extraData = normalizeExtraData(response.extraData ?? fallbackExtraData)
        savedDraft.persisted = true
        savedDraft.status = 'synced'
        savedDraft.pdfUrl = response.pdfUrl ?? savedDraft.pdfUrl
        savedDraft.pdfDirty = response.pdfDirty ?? true
        savedDraft.updatedAt = syncedAt
        savedDraft.lastSyncedAt = syncedAt
    }

    async function saveDirtySections(savedDraft: CvOnlineLocalDraft, normalizedExtraData: CvOnlineExtraData) {
        if (!savedDraft.serverId) return null

        let response: ResOnlineCvSectionUpdate | null = null
        const orderedDirtySections = SECTION_SAVE_ORDER.filter((section) => dirtySectionKeys.value.includes(section))

        for (const section of orderedDirtySections) {
            response = await cvOnlineService.updateOnlineCvSection(
                savedDraft.serverId,
                section,
                buildSectionPayload(section, normalizedExtraData),
            )
        }

        return response
    }

    async function saveDraftNow() {
        if (!currentDraft.value) return null
        saving.value = true
        error.value = null
        try {
            const normalizedExtraData = normalizeExtraData(currentDraft.value.extraData)
            const savedDraft = currentDraft.value
            let response: ResOnlineCv | ResOnlineCvSectionUpdate

            if (savedDraft.serverId && !fullSaveRequired.value && dirtySectionKeys.value.length > 0) {
                response = await saveDirtySections(savedDraft, normalizedExtraData) as ResOnlineCvSectionUpdate
            } else if (savedDraft.serverId) {
                response = await cvOnlineService.updateOnlineCv(savedDraft.serverId, {
                    title: savedDraft.title,
                    extraData: normalizedExtraData,
                })
            } else {
                response = await cvOnlineService.createOnlineCv({
                    title: savedDraft.title,
                    templateId: savedDraft.templateId,
                    isDefault: false,
                    extraData: normalizedExtraData,
                })
            }

            applySavedResponseToDraft(savedDraft, response, normalizedExtraData)
            removeDraftFromStorage(savedDraft.localDraftId)
            clearDirtyTracking()
            applyDraft(savedDraft)
            return savedDraft
        } catch (err) {
            setError(err)
            throw err
        } finally {
            saving.value = false
        }
    }

    function queueAutosave() {
        clearAutosaveTimer()
        autosaveTimer = setTimeout(() => {
            flushLocalDraft()
        }, AUTOSAVE_DELAY_MS)
    }

    async function changeTemplate(templateId: number) {
        if (!currentDraft.value) return null
        switchingTemplate.value = true
        error.value = null
        try {
            if (currentDraft.value.serverId) {
                if (hasPendingChanges.value) {
                    await saveDraftNow()
                }
                if (!currentDraft.value?.serverId) return null

                const response = await cvOnlineService.changeTemplate(currentDraft.value.serverId, { templateId })
                currentDraft.value.templateId = response.templateId
                currentDraft.value.template = response.template
                currentDraft.value.extraData = normalizeExtraData(response.extraData ?? currentDraft.value.extraData)
                currentDraft.value.pdfUrl = response.pdfUrl
                currentDraft.value.pdfDirty = response.pdfDirty ?? true
                currentDraft.value.persisted = true
                currentDraft.value.status = 'synced'
                currentDraft.value.updatedAt = response.updatedAt
                currentDraft.value.lastSyncedAt = response.updatedAt
                lastSavedAt.value = response.updatedAt
                lastLocalSavedAt.value = response.updatedAt
                pdfError.value = null
                hasPendingChanges.value = false
                flushLocalDraft()
                return response.template
            }

            const templatePayload = await cvOnlineService.getOnlineCvEditorPayloadByTemplateId(templateId)
            currentDraft.value.templateId = templateId
            currentDraft.value.template = templatePayload.template
            markDirty()
            flushLocalDraft()
            return templatePayload.template
        } catch (err) {
            setError(err)
            throw err
        } finally {
            switchingTemplate.value = false
        }
    }

    async function downloadPdf() {
        if (!currentDraft.value?.serverId) return null
        pdfError.value = null
        try {
            const exported = await cvOnlineService.exportOnlineCvPdf(currentDraft.value.serverId)
            currentDraft.value.pdfUrl = exported.pdfUrl ?? currentDraft.value.pdfUrl
            currentDraft.value.pdfDirty = exported.pdfDirty ?? false
            const blob = await cvOnlineService.downloadOnlineCvPdf(currentDraft.value.serverId)
            const url = window.URL.createObjectURL(blob)
            const anchor = document.createElement('a')
            anchor.href = url
            anchor.download = `${currentDraft.value.title || 'cv-online'}.pdf`
            document.body.appendChild(anchor)
            anchor.click()
            anchor.remove()
            window.URL.revokeObjectURL(url)
            return true
        } catch (err) {
            pdfError.value = extractApiErrorMessage(err, 'Khong the tai PDF luc nay.')
            throw err
        }
    }

    function patchDraftTitle(title: string) {
        if (!currentDraft.value) return
        currentDraft.value.title = title
        markDirty('full')
        queueAutosave()
    }

    function patchExtraData(patch: Partial<CvOnlineExtraData>, scope: CvOnlinePatchableSection | 'full' = 'full') {
        if (!currentDraft.value) return
        currentDraft.value.extraData = normalizeExtraData({
            ...currentDraft.value.extraData,
            ...patch,
        })
        markDirty(scope)
        queueAutosave()
    }

    function patchPersonalInfo(field: keyof CvOnlineExtraData['personalInfo'], value: string) {
        patchExtraData({
            personalInfo: {
                ...extraData.value.personalInfo,
                [field]: value,
            },
        }, 'personalInfo')
    }

    function replaceSection<T extends CvOnlineListSection>(
        section: T,
        value: CvOnlineExtraData[T],
    ) {
        patchExtraData(
            { [section]: value } as Partial<CvOnlineExtraData>,
            isPatchableSection(section) ? section : 'full',
        )
    }

    function moveItemInSection<T extends CvOnlineListSection>(
        section: T,
        fromIdx: number,
        toIdx: number,
    ) {
        const items = [...extraData.value[section]] as CvOnlineExtraData[T] & unknown[]
        if (fromIdx < 0 || fromIdx >= items.length) return
        if (toIdx < 0 || toIdx >= items.length) return

        const [moved] = items.splice(fromIdx, 1)
        items.splice(toIdx, 0, moved)
        replaceSection(section, items as CvOnlineExtraData[T])
    }

    function updateCareerObjective(value: string) {
        patchExtraData({ careerObjective: value }, 'full')
    }

    function addExperience() {
        replaceSection('experiences', [...extraData.value.experiences, createEmptyExperience()])
    }

    function updateExperience(index: number, patch: Partial<CvOnlineExperienceItem>) {
        const next = [...extraData.value.experiences]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('experiences', next)
    }

    function removeExperience(index: number) {
        replaceSection('experiences', extraData.value.experiences.filter((_, currentIndex) => currentIndex !== index))
    }

    function addEducation() {
        replaceSection('educations', [...extraData.value.educations, createEmptyEducation()])
    }

    function updateEducation(index: number, patch: Partial<CvOnlineEducationItem>) {
        const next = [...extraData.value.educations]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('educations', next)
    }

    function removeEducation(index: number) {
        replaceSection('educations', extraData.value.educations.filter((_, currentIndex) => currentIndex !== index))
    }

    function addSkill() {
        replaceSection('skills', [...extraData.value.skills, createEmptySkill()])
    }

    function updateSkill(index: number, patch: Partial<CvOnlineSkillItem>) {
        const next = [...extraData.value.skills]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('skills', next)
    }

    function removeSkill(index: number) {
        replaceSection('skills', extraData.value.skills.filter((_, currentIndex) => currentIndex !== index))
    }

    function addCertification() {
        replaceSection('certifications', [...extraData.value.certifications, createEmptyCertification()])
    }

    function updateCertification(index: number, patch: Partial<CvOnlineCertificationItem>) {
        const next = [...extraData.value.certifications]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('certifications', next)
    }

    function removeCertification(index: number) {
        replaceSection('certifications', extraData.value.certifications.filter((_, currentIndex) => currentIndex !== index))
    }

    function addLanguage() {
        replaceSection('languages', [...extraData.value.languages, createEmptyLanguage()])
    }

    function updateLanguage(index: number, patch: Partial<CvOnlineLanguageItem>) {
        const next = [...extraData.value.languages]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('languages', next)
    }

    function removeLanguage(index: number) {
        replaceSection('languages', extraData.value.languages.filter((_, currentIndex) => currentIndex !== index))
    }

    function addProject() {
        replaceSection('projects', [...extraData.value.projects, createEmptyProject()])
    }

    function updateProject(index: number, patch: Partial<CvOnlineProjectItem>) {
        const next = [...extraData.value.projects]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('projects', next)
    }

    function removeProject(index: number) {
        replaceSection('projects', extraData.value.projects.filter((_, currentIndex) => currentIndex !== index))
    }

    function addHobby() {
        replaceSection('hobbies', [...extraData.value.hobbies, createEmptyHobby()])
    }

    function updateHobby(index: number, patch: Partial<CvOnlineHobbyItem>) {
        const next = [...extraData.value.hobbies]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('hobbies', next)
    }

    function removeHobby(index: number) {
        replaceSection('hobbies', extraData.value.hobbies.filter((_, currentIndex) => currentIndex !== index))
    }

    function addAward() {
        replaceSection('awards', [...extraData.value.awards, createEmptyAward()])
    }

    function updateAward(index: number, patch: Partial<CvOnlineAwardItem>) {
        const next = [...extraData.value.awards]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('awards', next)
    }

    function removeAward(index: number) {
        replaceSection('awards', extraData.value.awards.filter((_, currentIndex) => currentIndex !== index))
    }

    function addCustomSection() {
        replaceSection('customSections', [...extraData.value.customSections, createEmptyCustomSection()])
    }

    function updateCustomSection(index: number, patch: Partial<CvOnlineCustomSectionItem>) {
        const next = [...extraData.value.customSections]
        const current = next[index]
        if (!current) return
        next[index] = { ...current, ...patch }
        replaceSection('customSections', next)
    }

    function removeCustomSection(index: number) {
        replaceSection(
            'customSections',
            extraData.value.customSections.filter((_, currentIndex) => currentIndex !== index),
        )
    }

    function listLocalDrafts() {
        return readAllDraftsFromStorage()
    }

    function discardLocalDraft(localDraftId: string) {
        removeDraftFromStorage(localDraftId)
        if (currentDraft.value?.localDraftId === localDraftId) {
            reset()
        }
    }

    function reset() {
        clearAutosaveTimer()
        currentDraft.value = null
        loading.value = false
        saving.value = false
        switchingTemplate.value = false
        error.value = null
        pdfError.value = null
        lastSavedAt.value = null
        lastLocalSavedAt.value = null
        lastLoadedAt.value = null
        hasPendingChanges.value = false
        clearDirtyTracking()
    }

    return {
        currentDraft,
        currentCv,
        loading,
        saving,
        switchingTemplate,
        error,
        pdfError,
        lastSavedAt,
        lastLocalSavedAt,
        lastLoadedAt,
        hasPendingChanges,
        extraData,
        pdfState,
        pdfStateLabel,
        saveStateLabel,
        createLocalDraftFromTemplate,
        loadDraftByLocalId,
        bootstrapDraftFromServerId,
        saveDraftNow,
        downloadPdf,
        queueAutosave,
        flushLocalDraft,
        changeTemplate,
        patchDraftTitle,
        patchExtraData,
        patchPersonalInfo,
        moveItemInSection,
        updateCareerObjective,
        addExperience,
        updateExperience,
        removeExperience,
        addEducation,
        updateEducation,
        removeEducation,
        addSkill,
        updateSkill,
        removeSkill,
        addCertification,
        updateCertification,
        removeCertification,
        addLanguage,
        updateLanguage,
        removeLanguage,
        addProject,
        updateProject,
        removeProject,
        addHobby,
        updateHobby,
        removeHobby,
        addAward,
        updateAward,
        removeAward,
        addCustomSection,
        updateCustomSection,
        removeCustomSection,
        listLocalDrafts,
        discardLocalDraft,
        reset,
    }
})
