<template>
  <section class="candidate-table">
    <!-- Table Header -->
    <div class="candidate-table__header">
      <div class="candidate-table__header-left">
        <span class="material-symbols-outlined icon-primary">groups</span>
        <h3 class="candidate-table__title">
          Danh sách ứng viên:
          <span class="candidate-table__title--highlight">{{ stageName }}</span>
        </h3>
      </div>
      <div class="candidate-table__actions">
        <!-- Bộ lọc trạng thái (Dropdown) -->
        <div class="filter-dropdown">
          <select
            class="filter-select"
            :value="statusFilter"
            @change="$emit('update:statusFilter', ($event.target as HTMLSelectElement).value)"
          >
            <option v-for="tab in statusTabs" :key="tab.value" :value="tab.value">
               {{ tab.label }}
            </option>
          </select>
          <span class="material-symbols-outlined filter-icon">expand_more</span>
        </div>

        <div class="search-wrap">
          <span class="search-icon material-symbols-outlined">search</span>
          <input
            class="search-input"
            type="text"
            placeholder="Tìm kiếm ứng viên..."
            :value="searchValue"
            @input="$emit('update:searchValue', ($event.target as HTMLInputElement).value)"
            @keyup.enter="$emit('search')"
          />
          <button
            v-if="searchValue"
            class="search-clear"
            @click="$emit('update:searchValue', ''); $emit('search')"
          >
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
        <button class="btn-smart-schedule" @click="$emit('export')">
          <span class="material-symbols-outlined btn-icon">auto_schedule</span>
          Đặt lịch thông minh
        </button>
      </div>
    </div>

    <!-- Table -->
    <table class="table">
      <thead>
        <tr>
          <th>Ứng viên</th>
          <th>Ngày &amp; Giờ</th>
          <th>Người phỏng vấn</th>
          <th>Hình thức</th>
          <th>Trạng thái</th>
          <th class="text-right">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <InterviewCandidateRow
          v-for="candidate in candidates"
          :key="candidate.id"
          :candidate="candidate"
          @open-link="emit('openLink', $event)"
          @view-detail="emit('viewDetail', $event)"
          @reschedule="emit('reschedule', $event)"
          @remind="emit('remind', $event)"
          @cancel="emit('cancel', $event)"
          @schedule="emit('schedule', $event)"
          @evaluate="emit('evaluate', $event)"
          @offer="emit('offer', $event)"
          @save-talent-pool="emit('saveTalentPool', $event)"
        />
      </tbody>
    </table>

    <!-- Pagination Footer -->
    <div class="candidate-table__footer">
      <span class="candidate-table__footer-text">
        Hiển thị {{ candidates?.length || 0 }} trên {{ totalCount }} ứng viên vòng {{ stageName }}
      </span>
      <div class="pagination">
        <button
          class="pagination__btn"
          :disabled="currentPage <= 1"
          @click="$emit('pageChange', currentPage - 1)"
        >
          <span class="material-symbols-outlined pagination__icon">chevron_left</span>
        </button>
        <button
          v-for="page in totalPages"
          :key="page"
          class="pagination__page"
          :class="{ 'pagination__page--active': page === currentPage }"
          @click="$emit('pageChange', page)"
        >
          {{ page }}
        </button>
        <button
          class="pagination__btn"
          :disabled="currentPage >= totalPages"
          @click="$emit('pageChange', currentPage + 1)"
        >
          <span class="material-symbols-outlined pagination__icon">chevron_right</span>
        </button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import InterviewCandidateRow from './InterviewCandidateRow.vue'

interface Interviewer {
  id: number
  name: string
  avatarUrl?: string
  initials: string
}

interface InterviewCandidate {
  id: number
  name: string
  role: string
  avatarUrl: string
  dateLabel: string
  dateFormatted: string
  interviewer: Interviewer
  format: string
  formatType: 'online' | 'offline'
  status: 'confirmed' | 'pending' | 'overdue'
  hasSchedule?: boolean
  scheduleStatus?: string
}

defineProps<{
  stageName: string
  candidates: InterviewCandidate[]
  totalCount: number
  currentPage: number
  totalPages: number
  searchValue: string
  statusFilter: string
}>()

const statusTabs = [
  { label: 'Tất cả',        value: 'all' },
  { label: 'Chờ lên lịch', value: 'pending' },
  { label: 'Chờ UV xác nhận',  value: 'scheduled' },
  { label: 'Đã xác nhận',   value: 'confirmed' },
  { label: 'Đã hoàn thành', value: 'completed' },
  { label: 'Đã hủy',        value: 'cancelled' },
  { label: 'Vắng mặt',      value: 'no_show' },
]

const emit = defineEmits<{
  'update:searchValue': [value: string]
  'update:statusFilter': [value: string]
  export: []
  search: []
  openLink: [candidateId: number]
  viewDetail: [candidateId: number]
  reschedule: [candidateId: number]
  pageChange: [page: number]
  remind: [candidateId: number]
  cancel: [candidateId: number]
  schedule: [candidateId: number]
  evaluate: [candidateId: number]
  offer: [candidateId: number]
  saveTalentPool: [candidateId: number]
}>()
</script>

<style scoped>
/* ============================================
   Font Size Scale (skill-font.md):
   1.875rem (30px) = Page title, stats lớn
   1.125rem (18px) = Tên item chính
   1rem     (16px) = Body, button cấp 1
   0.875rem (14px) = Data cells, metadata, chip
   0.75rem  (12px) = th, badge, label nhỏ (bold + uppercase)
   ============================================ */

.candidate-table {
  background: #fff;
  border-radius: 1rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
}

/* --- Section Header --- */
.candidate-table__header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(248, 250, 252, 0.3);
  border-top-left-radius: 1rem;
  border-top-right-radius: 1rem;
}

.candidate-table__header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.icon-primary {
  color: #4b9af6;
}

.candidate-table__title {
  font-size: 1.125rem;            /* 18px – Tiêu đề khối / Item chính */
  font-weight: 700;
  color: #0f172a;
}

.candidate-table__title--highlight {
  color: #4b9af6;
}

.candidate-table__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

/* --- Filter Dropdown --- */
.filter-dropdown {
  position: relative;
  display: flex;
  align-items: center;
}

.filter-select {
  height: 2.25rem;
  padding: 0 2.25rem 0 1rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.8125rem;
  font-weight: 700;
  font-family: inherit;
  color: #475569;
  outline: none;
  cursor: pointer;
  appearance: none;
  transition: all 0.15s;
}

.filter-select:hover {
  border-color: #cbd5e1;
  background: #f8fafc;
}

.filter-select:focus {
  border-color: #4b9af6;
  box-shadow: 0 0 0 3px rgba(75, 154, 246, 0.1);
}

.filter-icon {
  position: absolute;
  right: 0.75rem;
  font-size: 1.1rem !important;
  color: #94a3b8;
  pointer-events: none;
}

/* --- Search --- */
.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
  width: 15rem;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  font-size: 1.1rem !important;
  color: #94a3b8;
  pointer-events: none;
  user-select: none;
}

.search-input {
  width: 100%;
  height: 2.25rem;
  padding: 0 2.25rem 0 2.375rem;
  border-radius: 9999px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.875rem;
  font-family: inherit;
  color: #0f172a;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.search-input::placeholder {
  color: #94a3b8;
}

.search-input:focus {
  border-color: #4b9af6;
  box-shadow: 0 0 0 3px rgba(75, 154, 246, 0.1);
}

.search-clear {
  position: absolute;
  right: 0.5rem;
  width: 1.25rem;
  height: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  padding: 0;
  transition: background 0.15s;
}

.search-clear:hover {
  background: #e2e8f0;
}

.search-clear .material-symbols-outlined {
  font-size: 0.8rem !important;
}

.btn-outline {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #fff;
  padding: 0.375rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.875rem;             /* 14px – nút hành động phụ, bold */
  font-weight: 700;
  color: #475569;
  cursor: pointer;
  transition: background 0.15s;
}

.btn-outline:hover {
  background: #f8fafc;
}

.btn-smart-schedule {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.4rem 1rem;
  background: linear-gradient(135deg, #4b9af6 0%, #3b82f6 100%);
  border: none;
  border-radius: 0.625rem;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(75, 154, 246, 0.35);
  transition: opacity 0.15s, box-shadow 0.15s, transform 0.1s;
  white-space: nowrap;
}

.btn-smart-schedule:hover {
  opacity: 0.92;
  box-shadow: 0 4px 10px rgba(75, 154, 246, 0.45);
  transform: translateY(-1px);
}

.btn-smart-schedule:active {
  opacity: 1;
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(75, 154, 246, 0.3);
}

.btn-icon {
  font-size: 1rem !important;
}

/* --- Table --- */
.table {
  width: 100%;
  text-align: left;
  border-collapse: collapse;
}

.table thead {
  background: rgba(248, 250, 252, 0.5);
}

.table th {
  padding: 1rem 1.5rem;
  font-size: 0.75rem;             /* 12px – tiêu đề bảng (th), bold + uppercase */
  font-weight: 800;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.table th.text-right {
  text-align: right;
}

.table tbody tr {
  border-bottom: 1px solid #f8fafc;
}

/* --- Footer / Pagination --- */
.candidate-table__footer {
  padding: 1rem 1.5rem;
  background: rgba(248, 250, 252, 0.3);
  border-top: 1px solid #f8fafc;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom-left-radius: 1rem;
  border-bottom-right-radius: 1rem;
}

.candidate-table__footer-text {
  font-size: 0.875rem;            /* 14px – metadata */
  color: #64748b;
  font-weight: 500;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.pagination__btn {
  padding: 0.25rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  cursor: pointer;
  background: transparent;
  transition: background 0.15s;
}

.pagination__btn:hover:not(:disabled) {
  background: #fff;
}

.pagination__btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination__icon {
  font-size: 1.25rem;             /* 20px – icon điều hướng */
}

.pagination__page {
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;            /* 14px – pagination text */
  font-weight: 700;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #64748b;
}

.pagination__page--active {
  background: #4b9af6;
  color: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}
</style>