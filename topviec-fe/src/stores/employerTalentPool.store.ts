import { defineStore } from 'pinia';
import { ref } from 'vue';
import employerTalentPoolService from '@/services/employerTalentPool.service';
import type {
  ReqAddToTalentPoolDTO,
  ResTalentPoolCandidateDTO,
  ResTalentPoolCandidateDetailDTO,
  ResCandidateSearchResultDTO,
  TalentPoolParams,
  CandidateSearchParams,
} from '@/services/employerTalentPool.service';
import type { PaginationMeta } from '@/types/common.types';

export const useEmployerTalentPoolStore = defineStore('employerTalentPool', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const candidates = ref<ResTalentPoolCandidateDTO[]>([]);
  const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });

  const currentCandidate = ref<ResTalentPoolCandidateDetailDTO | null>(null);

  const searchResults = ref<ResCandidateSearchResultDTO[]>([]);
  const searchMeta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });

  const loading = ref(false);
  const searchLoading = ref(false);
  const error = ref<string | null>(null);

  // ─── Helpers ────────────────────────────────────────────────────────────────
  function setError(err: unknown) {
    const data = (err as any)?.response?.data;
    if (data?.message && typeof data.message === 'object') {
      error.value = String(Object.values(data.message)[0]);
    } else {
      error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.';
    }
  }

  // ─── Actions ────────────────────────────────────────────────────────────────

  async function fetchTalentPool(params: TalentPoolParams) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerTalentPoolService.getTalentPool(params);
      candidates.value = data.result;
      meta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function fetchCandidateDetail(talentPoolId: number) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerTalentPoolService.getTalentPoolCandidateDetail(talentPoolId);
      currentCandidate.value = data;
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function fetchCandidateDetailByUserId(candidateUserId: number) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerTalentPoolService.getCandidateDetail(candidateUserId);
      currentCandidate.value = data;
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function addToTalentPool(reqData: ReqAddToTalentPoolDTO) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerTalentPoolService.addToTalentPool(reqData);
      // Cập nhật alreadyInPool trong search results nếu ứng viên vừa được thêm
      const found = searchResults.value.find(c => c.candidateUserId === reqData.candidateUserId);
      if (found) found.alreadyInPool = true;
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function updateNote(talentPoolId: number, note: string) {
    loading.value = true;
    error.value = null;
    try {
      await employerTalentPoolService.updateNote(talentPoolId, note);
      if (currentCandidate.value?.talentPoolId === talentPoolId) {
        currentCandidate.value.note = note;
      }
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function removeFromTalentPool(talentPoolId: number) {
    loading.value = true;
    error.value = null;
    try {
      await employerTalentPoolService.removeFromTalentPool(talentPoolId);
      candidates.value = candidates.value.filter(c => c.talentPoolId !== talentPoolId);
      if (currentCandidate.value?.talentPoolId === talentPoolId) {
        currentCandidate.value = null;
      }
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function searchCandidates(params: CandidateSearchParams) {
    searchLoading.value = true;
    error.value = null;
    try {
      const data = await employerTalentPoolService.searchCandidates(params);
      searchResults.value = data.result;
      searchMeta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      searchLoading.value = false;
    }
  }

  function resetSearch() {
    searchResults.value = [];
    searchMeta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
  }

  function reset() {
    candidates.value = [];
    meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
    currentCandidate.value = null;
    searchResults.value = [];
    searchMeta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
    loading.value = false;
    searchLoading.value = false;
    error.value = null;
  }

  return {
    // state
    candidates,
    meta,
    currentCandidate,
    searchResults,
    searchMeta,
    loading,
    searchLoading,
    error,
    // actions
    fetchTalentPool,
    fetchCandidateDetail,
    fetchCandidateDetailByUserId,
    addToTalentPool,
    updateNote,
    removeFromTalentPool,
    searchCandidates,
    resetSearch,
    reset,
  };
});
