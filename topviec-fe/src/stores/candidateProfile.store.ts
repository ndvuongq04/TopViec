import { defineStore } from 'pinia'
import { ref } from 'vue'
import candidateProfileService from '@/services/candidateProfile.service'
import type {
  ReqCreateCandidateProfileDTO,
  ReqUpdateCandidateProfileDTO,
  ReqUpdateCandidateProfileVisibilityDTO,
  ResCandidateProfileDTO,
} from '@/types/candidateProfile.types'

export const useCandidateProfileStore = defineStore('candidateProfile', () => {
  const profile = ref<ResCandidateProfileDTO | null>(null)
  const pendingAvatarFile = ref<File | null>(null)
  const pendingAvatarPreviewUrl = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  function setError(err: unknown) {
    error.value =
      (err as any)?.response?.data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
  }

  function clearPendingAvatar() {
    pendingAvatarFile.value = null
    pendingAvatarPreviewUrl.value = null
  }

  async function fetchMyProfile() {
    loading.value = true
    error.value = null
    try {
      profile.value = await candidateProfileService.getMyProfile()
      clearPendingAvatar()
    } catch (err) {
      setError(err)
    } finally {
      loading.value = false
    }
  }

  async function createProfile(payload: ReqCreateCandidateProfileDTO) {
    loading.value = true
    error.value = null
    try {
      profile.value = await candidateProfileService.createProfile(payload)
      clearPendingAvatar()
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateProfile(payload: ReqUpdateCandidateProfileDTO) {
    loading.value = true
    error.value = null
    try {
      profile.value = await candidateProfileService.updateProfile(payload)
      clearPendingAvatar()
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateVisibility(payload: ReqUpdateCandidateProfileVisibilityDTO) {
    loading.value = true
    error.value = null
    try {
      profile.value = await candidateProfileService.updateVisibility(payload)
      return profile.value
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteProfile() {
    loading.value = true
    error.value = null
    try {
      await candidateProfileService.deleteProfile()
      profile.value = null
      clearPendingAvatar()
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  function setPendingAvatar(file: File | null, previewUrl: string | null) {
    pendingAvatarFile.value = file
    pendingAvatarPreviewUrl.value = previewUrl
  }

  function reset() {
    profile.value = null
    clearPendingAvatar()
    loading.value = false
    error.value = null
  }

  return {
    profile,
    pendingAvatarFile,
    pendingAvatarPreviewUrl,
    loading,
    error,
    fetchMyProfile,
    createProfile,
    updateProfile,
    updateVisibility,
    deleteProfile,
    setPendingAvatar,
    clearPendingAvatar,
    reset,
  }
})
