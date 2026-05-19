<template>
  <div class="search-tab">
    <CandidateDetailModal
      v-model="showModal"
      :talent-pool-id="null"
      :candidate-user-id="selectedCandidateId"
    />
    <CandidateSearchBanner :used="0" :limit="0" @upgrade="() => {}" />
    <div class="search-tab__layout">
      <CandidateSearchFilters @search="handleSearch" />
      <CandidateSearchResults
        :candidates="talentPoolStore.searchResults"
        :meta="talentPoolStore.searchMeta"
        :loading="talentPoolStore.searchLoading"
        :has-searched="hasSearched"
        :saving-id="savingId"
        @save="handleSave"
        @view-detail="handleViewDetail"
        @page-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CandidateSearchBanner from './CandidateSearchBanner.vue'
import CandidateSearchFilters from './CandidateSearchFilters.vue'
import CandidateSearchResults from './CandidateSearchResults.vue'
import CandidateDetailModal from '../saved/CandidateDetailModal.vue'
import { useEmployerTalentPoolStore } from '@/stores/employerTalentPool.store'
import { useToast } from '@/composables/useToast'
import type { ResCandidateSearchResultDTO } from '@/services/employerTalentPool.service'

const talentPoolStore = useEmployerTalentPoolStore()
const toast = useToast()

const hasSearched = ref(false)
const savingId = ref<number | null>(null)
const currentLocationId = ref<number | null>(null)
const showModal = ref(false)
const selectedCandidateId = ref<number | null>(null)

function handleViewDetail(candidate: ResCandidateSearchResultDTO) {
  selectedCandidateId.value = candidate.candidateUserId
  showModal.value = true
}

async function handleSearch(locationId: number) {
  currentLocationId.value = locationId
  hasSearched.value = true
  await talentPoolStore.searchCandidates({ locationId, page: 0, size: 10 })
}

async function handlePageChange(page: number) {
  if (currentLocationId.value === null) return
  await talentPoolStore.searchCandidates({
    locationId: currentLocationId.value,
    page,
    size: 10,
  })
}

async function handleSave(candidate: ResCandidateSearchResultDTO) {
  savingId.value = candidate.candidateUserId
  try {
    await talentPoolStore.addToTalentPool({
      candidateUserId: candidate.candidateUserId,
      source: 'SEARCH_IN_DB',
    })
    toast.success('Đã lưu vào Talent Pool', `${candidate.fullName} đã được thêm thành công.`)
  } catch {
    toast.error('Lưu thất bại', talentPoolStore.error ?? 'Vui lòng thử lại.')
  } finally {
    savingId.value = null
  }
}
</script>

<style scoped>
.search-tab { display: flex; flex-direction: column; gap: 1.5rem; padding-top: 1.5rem; }
.search-tab__layout { display: flex; gap: 2rem; align-items: flex-start; }
</style>
