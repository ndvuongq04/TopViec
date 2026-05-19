<template>
  <Teleport to="body">
    <div v-if="modelValue" class="cdm-overlay" @click.self="close">
      <div class="cdm-panel">
        <button class="cdm-close" @click="close">
          <span class="material-symbols-outlined">close</span>
        </button>

        <!-- Loading -->
        <div v-if="loading" class="cdm-loading">
          <span class="material-symbols-outlined cdm-spin">progress_activity</span>
        </div>

        <!-- Error -->
        <div v-else-if="error" class="cdm-error">
          <span class="material-symbols-outlined">error</span>
          <p>{{ error }}</p>
        </div>

        <!-- Content -->
        <template v-else-if="detail">
          <!-- Header -->
          <div class="cdm-header">
            <img v-if="detail.avatarUrl" :src="detail.avatarUrl" :alt="detail.fullName" class="cdm-avatar" />
            <div v-else class="cdm-avatar-initials">{{ initials }}</div>
            <div class="cdm-header-info">
              <h2 class="cdm-name">{{ detail.fullName }}</h2>
              <p v-if="detail.preferredJobTitle" class="cdm-job-title">{{ detail.preferredJobTitle }}</p>
              <div class="cdm-badges">
                <span class="cdm-badge cdm-badge--source">{{ sourceLabel }}</span>
                <span v-if="detail.jobSeekingStatus" class="cdm-badge cdm-badge--status">{{ seekingLabel }}</span>
              </div>
            </div>
          </div>

          <div class="cdm-body">
            <!-- Thông tin liên hệ -->
            <section class="cdm-section">
              <h3 class="cdm-section-title">Thông tin liên hệ</h3>
              <div class="cdm-grid">
                <div class="cdm-field">
                  <span class="material-symbols-outlined">mail</span>
                  <span v-if="!detail.emailHidden && detail.email">{{ detail.email }}</span>
                  <span v-else class="cdm-hidden">Đã ẩn</span>
                </div>
                <div class="cdm-field">
                  <span class="material-symbols-outlined">phone</span>
                  <span v-if="!detail.phoneHidden && detail.phone">{{ detail.phone }}</span>
                  <span v-else class="cdm-hidden">Đã ẩn</span>
                </div>
                <div class="cdm-field">
                  <span class="material-symbols-outlined">cake</span>
                  <span v-if="!detail.dateOfBirthHidden && detail.dateOfBirth">{{ formatDate(detail.dateOfBirth) }}</span>
                  <span v-else class="cdm-hidden">Đã ẩn</span>
                </div>
                <div v-if="detail.gender" class="cdm-field">
                  <span class="material-symbols-outlined">person</span>
                  <span>{{ genderLabel }}</span>
                </div>
                <div v-if="detail.linkedinUrl" class="cdm-field">
                  <span class="material-symbols-outlined">link</span>
                  <a :href="detail.linkedinUrl" target="_blank" class="cdm-link">LinkedIn</a>
                </div>
                <div v-if="detail.githubUrl" class="cdm-field">
                  <span class="material-symbols-outlined">code</span>
                  <a :href="detail.githubUrl" target="_blank" class="cdm-link">GitHub</a>
                </div>
              </div>
            </section>

            <!-- Mong muốn công việc -->
            <section class="cdm-section">
              <h3 class="cdm-section-title">Mong muốn công việc</h3>
              <div class="cdm-grid">
                <div v-if="detail.preferredWorkType" class="cdm-field">
                  <span class="material-symbols-outlined">work</span>
                  <span>{{ workTypeLabel }}</span>
                </div>
                <div v-if="detail.preferredLocationName" class="cdm-field">
                  <span class="material-symbols-outlined">location_on</span>
                  <span>{{ detail.preferredLocationName }}</span>
                </div>
                <div class="cdm-field">
                  <span class="material-symbols-outlined">payments</span>
                  <span v-if="detail.salaryHidden" class="cdm-hidden">Đã ẩn</span>
                  <span v-else>{{ salaryDisplay }}</span>
                </div>
              </div>
            </section>

            <!-- Giới thiệu -->
            <section v-if="detail.bio" class="cdm-section">
              <h3 class="cdm-section-title">Giới thiệu</h3>
              <p class="cdm-bio">{{ detail.bio }}</p>
            </section>

            <!-- CV mặc định -->
            <section v-if="detail.defaultCv" class="cdm-section">
              <h3 class="cdm-section-title">CV đính kèm</h3>
              <div class="cdm-cv-card">
                <span class="material-symbols-outlined cdm-cv-icon">description</span>
                <div class="cdm-cv-info">
                  <p class="cdm-cv-title">{{ detail.defaultCv.title }}</p>
                  <p class="cdm-cv-meta">{{ detail.defaultCv.cvType === 'UPLOADED' ? 'CV tải lên' : 'CV tạo online' }}</p>
                </div>
                <a
                  v-if="detail.defaultCv.pdfUrl || detail.defaultCv.fileUrl"
                  :href="detail.defaultCv.pdfUrl || detail.defaultCv.fileUrl"
                  target="_blank"
                  class="cdm-cv-btn"
                >
                  <span class="material-symbols-outlined">open_in_new</span>
                  Xem CV
                </a>
              </div>
            </section>

            <!-- Ghi chú talent pool -->
            <section v-if="showTalentPoolMeta && detail.note" class="cdm-section">
              <h3 class="cdm-section-title">Ghi chú</h3>
              <p class="cdm-note">{{ detail.note }}</p>
            </section>

            <!-- Meta -->
            <div v-if="showTalentPoolMeta" class="cdm-meta-footer">
              <span>Lưu bởi: <strong>{{ detail.addedByName || detail.addedBy }}</strong></span>
              <span>Ngày lưu: <strong>{{ formatDate(detail.addedAt) }}</strong></span>
            </div>
          </div>
        </template>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import employerTalentPoolService, { type ResTalentPoolCandidateDetailDTO } from '@/services/employerTalentPool.service'

const props = defineProps<{
  modelValue: boolean
  talentPoolId: number | null
  candidateUserId?: number | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: boolean): void
}>()

const detail = ref<ResTalentPoolCandidateDetailDTO | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const showTalentPoolMeta = computed(() => Boolean(props.talentPoolId))

watch(
  () => [props.modelValue, props.talentPoolId, props.candidateUserId],
  async ([open, talentPoolId, candidateUserId]) => {
    if (!open || (!talentPoolId && !candidateUserId)) return

    loading.value = true
    error.value = null
    detail.value = null

    try {
      detail.value = talentPoolId
        ? await employerTalentPoolService.getTalentPoolCandidateDetail(talentPoolId)
        : await employerTalentPoolService.getCandidateDetail(candidateUserId!)
    } catch {
      error.value = 'Không thể tải thông tin ứng viên.'
    } finally {
      loading.value = false
    }
  },
)

function close() {
  emit('update:modelValue', false)
}

const initials = computed(() => {
  const parts = (detail.value?.fullName ?? '').trim().split(' ')
  return parts.length >= 2
    ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
    : (detail.value?.fullName ?? '??').slice(0, 2).toUpperCase()
})

const SOURCE_MAP: Record<string, string> = {
  'Duyệt CV': 'Duyệt CV',
  'PV UV': 'Phỏng vấn',
  'Tìm kiếm UV trong DB': 'Tìm kiếm',
}
const sourceLabel = computed(() => SOURCE_MAP[detail.value?.source ?? ''] ?? detail.value?.source ?? '')

const SEEKING_MAP: Record<string, string> = {
  active: 'Đang tìm việc gấp',
  passive: 'Sẵn sàng nếu có cơ hội',
  not_looking: 'Không tìm việc',
}
const seekingLabel = computed(() => SEEKING_MAP[detail.value?.jobSeekingStatus ?? ''] ?? detail.value?.jobSeekingStatus ?? '')

const GENDER_MAP: Record<string, string> = {
  male: 'Nam',
  female: 'Nữ',
  other: 'Khác',
}
const genderLabel = computed(() => GENDER_MAP[detail.value?.gender ?? ''] ?? '')

const WORK_TYPE_MAP: Record<string, string> = {
  full_time: 'Toàn thời gian',
  part_time: 'Bán thời gian',
  remote: 'Làm từ xa',
  hybrid: 'Kết hợp',
}
const workTypeLabel = computed(() => WORK_TYPE_MAP[detail.value?.preferredWorkType ?? ''] ?? detail.value?.preferredWorkType ?? '')

const salaryDisplay = computed(() => {
  const d = detail.value
  if (!d) return ''
  if (d.salaryNegotiable) return 'Thương lượng'
  if (d.expectedSalaryMin && d.expectedSalaryMax) {
    return `${d.expectedSalaryMin.toLocaleString('vi-VN')} - ${d.expectedSalaryMax.toLocaleString('vi-VN')} VNĐ`
  }
  if (d.expectedSalaryMin) return `Từ ${d.expectedSalaryMin.toLocaleString('vi-VN')} VNĐ`
  if (d.expectedSalaryMax) return `Đến ${d.expectedSalaryMax.toLocaleString('vi-VN')} VNĐ`
  return 'Chưa cập nhật'
})

function formatDate(iso: string): string {
  if (!iso) return '—'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped>
.cdm-overlay {
  position: fixed; inset: 0; z-index: 1000;
  background: rgba(0, 0, 0, 0.45);
  display: flex; align-items: center; justify-content: center;
}

.cdm-panel {
  position: relative;
  width: 640px; max-width: calc(100vw - 32px);
  max-height: 90vh; overflow-y: auto;
  background: #fff; border-radius: 1rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  display: flex; flex-direction: column;
}

.cdm-close {
  position: absolute; top: 16px; right: 16px; z-index: 1;
  display: flex; align-items: center; justify-content: center;
  width: 32px; height: 32px; border: none; border-radius: 6px;
  background: #f1f4f9; color: #64748b; cursor: pointer;
  transition: color 0.15s;
}
.cdm-close:hover { color: #0f172a; }

.cdm-loading, .cdm-error {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; gap: 12px; padding: 60px 24px;
  color: #64748b; font-size: 0.9rem;
}
.cdm-spin { animation: spin 1s linear infinite; font-size: 2rem; color: #4b9af6; }
@keyframes spin { to { transform: rotate(360deg); } }
.cdm-error .material-symbols-outlined { font-size: 2.5rem; color: #ef4444; }

/* Header */
.cdm-header {
  display: flex; align-items: flex-start; gap: 16px;
  padding: 24px 24px 20px;
  border-bottom: 1px solid #f1f4f9;
}
.cdm-avatar {
  width: 72px; height: 72px; flex-shrink: 0;
  border-radius: 50%; object-fit: cover;
}
.cdm-avatar-initials {
  display: flex; width: 72px; height: 72px; flex-shrink: 0;
  align-items: center; justify-content: center;
  border-radius: 50%; background: #f1f4f9;
  color: #4b9af6; font-size: 1.5rem; font-weight: 700;
}
.cdm-header-info { flex: 1; min-width: 0; padding-right: 36px; }
.cdm-name { color: #0f172a; font-size: 1.25rem; font-weight: 700; line-height: 1.3; }
.cdm-job-title { margin-top: 2px; color: #64748b; font-size: 0.9rem; }
.cdm-badges { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; }
.cdm-badge {
  padding: 2px 8px; border-radius: 4px;
  font-size: 0.72rem; font-weight: 700;
  letter-spacing: 0.04em; text-transform: uppercase;
}
.cdm-badge--source { background: #dbeafe; color: #1d4ed8; }
.cdm-badge--status { background: #d1fae5; color: #065f46; }

/* Body */
.cdm-body { padding: 20px 24px; display: flex; flex-direction: column; gap: 20px; }

.cdm-section-title {
  font-size: 0.8rem; font-weight: 700; letter-spacing: 0.06em;
  text-transform: uppercase; color: #94a3b8; margin-bottom: 10px;
}

.cdm-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.cdm-field {
  display: flex; align-items: center; gap: 8px;
  color: #334155; font-size: 0.875rem;
}
.cdm-field .material-symbols-outlined { font-size: 1rem; color: #94a3b8; flex-shrink: 0; }
.cdm-hidden { color: #94a3b8; font-style: italic; }
.cdm-link { color: #4b9af6; text-decoration: none; }
.cdm-link:hover { text-decoration: underline; }

.cdm-bio {
  color: #475569; font-size: 0.875rem; line-height: 1.6;
  white-space: pre-wrap;
}

/* CV */
.cdm-cv-card {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; border-radius: 8px;
  border: 1px solid #e2e8f0; background: #f8fafc;
}
.cdm-cv-icon { font-size: 2rem; color: #4b9af6; }
.cdm-cv-info { flex: 1; min-width: 0; }
.cdm-cv-title { font-weight: 600; color: #0f172a; font-size: 0.875rem; }
.cdm-cv-meta { font-size: 0.75rem; color: #64748b; margin-top: 2px; }
.cdm-cv-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 12px; border-radius: 6px;
  background: #4b9af6; color: #fff;
  font-size: 0.8rem; font-weight: 600; text-decoration: none;
  transition: background 0.15s;
}
.cdm-cv-btn:hover { background: #3b82f6; }
.cdm-cv-btn .material-symbols-outlined { font-size: 0.9rem; }

.cdm-note {
  color: #475569; font-size: 0.875rem; line-height: 1.6;
  background: #fffbeb; border-left: 3px solid #fbbf24;
  padding: 10px 14px; border-radius: 0 6px 6px 0;
  white-space: pre-wrap;
}

.cdm-meta-footer {
  display: flex; gap: 20px; flex-wrap: wrap;
  padding-top: 12px; border-top: 1px solid #f1f4f9;
  color: #94a3b8; font-size: 0.75rem;
}
.cdm-meta-footer strong { color: #64748b; }
</style>
