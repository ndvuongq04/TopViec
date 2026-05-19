import { defineStore } from 'pinia'
import { ref } from 'vue'
import { savedJobService } from '@/services/savedJob.service'
import type { ResSavedJobDTO } from '@/types/savedJob.types'
import type { PaginationMeta } from '@/types/common.types'

export const useSavedJobStore = defineStore('savedJob', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const savedJobs = ref<ResSavedJobDTO[]>([])
  const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  // Cache for saved status of jobs to avoid redundant API calls
  // and maintain reactivity across components
  const isSavedMap = ref<Record<number, boolean>>({})

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

  /** Fetch paginated saved jobs */
  async function fetchSavedJobs(page = 0, size = 10) {
    loading.value = true
    error.value = null
    try {
      const data = await savedJobService.getSavedJobs(page, size)
      savedJobs.value = data.result
      meta.value = data.meta
      
      // Update isSavedMap for all fetched jobs
      data.result.forEach((item: ResSavedJobDTO) => {
        isSavedMap.value[item.jobPostId] = true
      })
    } catch (err) {
      setError(err)
    } finally {
      loading.value = false
    }
  }

  /** Check if a specific job is saved */
  async function checkIsSaved(jobPostId: number) {
    // If we already have it in the map, we can rely on it, 
    // but the backend might have changed or first load.
    // For now, let's always fetch to be sure, or only if not in map.
    if (isSavedMap.value[jobPostId] !== undefined) return isSavedMap.value[jobPostId]

    try {
      const data = await savedJobService.isSaved(jobPostId)
      isSavedMap.value[jobPostId] = data.isSaved
      return data.isSaved
    } catch (err) {
      console.error('Error checking saved status:', err)
      return false
    }
  }

  /** Toggle save/unsave */
  async function toggleSave(jobPostId: number) {
    loading.value = true
    error.value = null
    try {
      const data = await savedJobService.toggleSave(jobPostId)
      
      // Toggle logic: if data is returned, it means it's now saved.
      // But the backend 'toggle' might return null if unsaved, or the DTO if saved.
      // Based on the controller: returns ResSavedJobDTO (usually the saved record).
      // If it's a toggle, we need to know the new state.
      // Assuming toggleSave returns the saved job if it's now saved, 
      // or we might need to handle the case where it's removed.
      
      // Let's re-check status or assume toggle succeeded.
      // Typically toggle returns the new state or the object.
      // Let's update map based on current state.
      isSavedMap.value[jobPostId] = !isSavedMap.value[jobPostId]
      
      // Refresh list if we are on the saved jobs page
      // (Optional: depending on UX, we might just remove from list if toggled off)
      if (!isSavedMap.value[jobPostId]) {
        savedJobs.value = savedJobs.value.filter(j => j.jobPostId !== jobPostId)
      } else if (data) {
        // If now saved, we could add to list, but we don't know the full job details 
        // unless data contains it. ResSavedJobDTO contains ResJobPostingSummary.
        const exists = savedJobs.value.some(j => j.jobPostId === jobPostId)
        if (!exists) {
          savedJobs.value.unshift(data)
        }
      }
      
      return isSavedMap.value[jobPostId]
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /** Unsave a job */
  async function unsave(jobPostId: number) {
    loading.value = true
    error.value = null
    try {
      await savedJobService.unsave(jobPostId)
      isSavedMap.value[jobPostId] = false
      savedJobs.value = savedJobs.value.filter(j => j.jobPostId !== jobPostId)
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /** Reset store */
  function reset() {
    savedJobs.value = []
    meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
    isSavedMap.value = {}
    loading.value = false
    error.value = null
  }

  return {
    // state
    savedJobs,
    meta,
    loading,
    error,
    isSavedMap,
    // actions
    fetchSavedJobs,
    checkIsSaved,
    toggleSave,
    unsave,
    reset,
  }
})
