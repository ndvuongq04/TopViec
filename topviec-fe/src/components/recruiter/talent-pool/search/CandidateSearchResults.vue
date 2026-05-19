<template>
  <div class="cs-results">
    <!-- Header -->
    <div v-if="hasSearched || candidates.length > 0" class="cs-results__header">
      <h3 class="cs-results__count">
        Tìm thấy <strong>{{ meta.totals.toLocaleString('vi-VN') }}</strong> ứng viên
      </h3>
      <div class="cs-results__sort">
        <span class="cs-results__sort-label">Sắp xếp theo:</span>
        <select class="cs-results__sort-select">
          <option>Phù hợp nhất</option>
          <option>Mới cập nhật</option>
          <option>Kinh nghiệm cao</option>
        </select>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="cs-results__loading">
      <span class="material-symbols-outlined cs-results__spin">autorenew</span>
      <span>Đang tìm kiếm ứng viên...</span>
    </div>

    <!-- Empty — chưa tìm kiếm lần nào -->
    <div v-else-if="!hasSearched" class="cs-results__empty">
      <span class="material-symbols-outlined cs-results__empty-icon">manage_search</span>
      <p class="cs-results__empty-title">Chọn địa điểm để tìm kiếm ứng viên</p>
      <p class="cs-results__empty-sub">Chọn địa điểm mong muốn ở bộ lọc bên trái rồi nhấn "Tìm kiếm"</p>
    </div>

    <!-- Empty — đã tìm nhưng không có kết quả -->
    <div v-else-if="candidates.length === 0" class="cs-results__empty">
      <span class="material-symbols-outlined cs-results__empty-icon">person_search</span>
      <p class="cs-results__empty-title">Không tìm thấy ứng viên</p>
      <p class="cs-results__empty-sub">Thử chọn địa điểm khác để mở rộng kết quả</p>
    </div>

    <!-- Result list -->
    <template v-else>
      <div class="cs-results__list">
        <CandidateSearchResultItem
          v-for="c in candidates"
          :key="c.candidateUserId"
          :candidate="c"
          :saving="savingId === c.candidateUserId"
          @save="$emit('save', $event)"
          @view-detail="$emit('view-detail', $event)"
        />
      </div>

      <CandidatePagination
        :current="meta.page + 1"
        :total-pages="meta.pages"
        :centered="true"
        @change="$emit('page-change', $event - 1)"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import CandidateSearchResultItem from './CandidateSearchResultItem.vue'
import CandidatePagination from '../shared/CandidatePagination.vue'
import type { ResCandidateSearchResultDTO } from '@/services/employerTalentPool.service'
import type { PaginationMeta } from '@/types/common.types'

defineProps<{
  candidates: ResCandidateSearchResultDTO[]
  meta: PaginationMeta
  loading: boolean
  hasSearched: boolean
  savingId: number | null
}>()

defineEmits<{
  (e: 'save', candidate: ResCandidateSearchResultDTO): void
  (e: 'view-detail', candidate: ResCandidateSearchResultDTO): void
  (e: 'page-change', page: number): void
}>()
</script>

<style scoped>
.cs-results {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 1.5rem;
  min-height: 320px;
}

.cs-results__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cs-results__count {
  color: #0f172a;
  font-size: 1.125rem;
  font-weight: 700;
}

.cs-results__count strong {
  font-weight: 800;
}

.cs-results__sort {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cs-results__sort-label {
  color: #64748b;
  font-size: 0.875rem;
  font-weight: 600;
}

.cs-results__sort-select {
  padding: 8px 12px;
  border: none;
  border-radius: 0.5rem;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  color: #0f172a;
  cursor: pointer;
  outline: none;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
}

.cs-results__loading {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  min-height: 240px;
  color: #64748b;
  font-size: 1rem;
}

.cs-results__spin {
  font-size: 2rem;
  animation: spin 1s linear infinite;
}

.cs-results__empty {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 240px;
  gap: 8px;
  text-align: center;
}

.cs-results__empty-icon {
  font-size: 3rem;
  color: #cbd5e1;
  margin-bottom: 4px;
}

.cs-results__empty-title {
  color: #0f172a;
  font-size: 1rem;
  font-weight: 700;
}

.cs-results__empty-sub {
  color: #94a3b8;
  font-size: 0.875rem;
}

.cs-results__list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
