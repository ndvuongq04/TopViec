<template>
  <div class="saved-tab">
    <CandidateDetailModal v-model="showModal" :talent-pool-id="selectedTalentPoolId" />

    <GlobalModal
      :visible="showInviteModal"
      title="Mời ứng viên từ Talent Pool"
      :subtitle="inviteCandidate?.candidateName ? `Ứng viên: ${inviteCandidate.candidateName}` : undefined"
      icon="mail"
      max-width="lg"
      confirm-text="Gửi email mời"
      confirm-icon="send"
      :loading="inviting"
      @close="closeInviteModal"
      @confirm="handleInviteCandidate"
    >
      <div class="space-y-4">
        <p class="text-sm text-slate-600">
          Chọn tin tuyển dụng để gửi lời mời ứng tuyển cho ứng viên này.
        </p>

        <div class="flex flex-col gap-2">
          <label class="text-sm font-bold text-slate-700" for="invite-job-posting">Tin tuyển dụng</label>
          <select
            id="invite-job-posting"
            v-model.number="selectedJobPostId"
            class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-800 outline-none transition focus:border-primary"
          >
            <option :value="0" disabled>Chọn tin tuyển dụng</option>
            <option v-for="job in inviteJobOptions" :key="job.id" :value="job.id">
              {{ job.title }}
            </option>
          </select>
        </div>

        <p v-if="inviteJobOptions.length === 0" class="text-sm text-amber-700">
          Hiện chưa có tin tuyển dụng khả dụng để gửi lời mời.
        </p>
      </div>
    </GlobalModal>

    <EditNoteModal
      :visible="showEditNote"
      :candidate-name="editNoteCandidate?.candidateName"
      :initial-note="editNoteCandidate?.note"
      :loading="savingNote"
      @close="showEditNote = false"
      @confirm="handleSaveNote"
    />

    <SavedCandidatesFilters
      v-model:search="search"
      v-model:source="source"
      @do-search="onSearch"
    />

    <div v-if="loading" class="saved-tab__loading">
      <span class="material-symbols-outlined saved-tab__spinner">progress_activity</span>
      <span>Đang tải...</span>
    </div>

    <div v-else-if="!loading && candidates.length === 0" class="saved-tab__empty">
      <span class="material-symbols-outlined saved-tab__empty-icon">group_off</span>
      <p>Chưa có ứng viên nào trong TalentPool.</p>
    </div>

    <div v-else class="saved-tab__grid">
      <SavedCandidateCard
        v-for="c in candidates"
        :key="c.talentPoolId"
        :candidate="c"
        @view-detail="openDetail"
        @invite-email="openInviteModal"
        @edit-note="openEditNote"
        @delete="openDeleteConfirm"
      />
    </div>

    <CandidatePagination
      v-if="totalPages > 0"
      :current="currentPage"
      :total-pages="totalPages"
      :total="total"
      :page-size="pageSize"
      :show-info="true"
      @change="onPageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import SavedCandidatesFilters from './SavedCandidatesFilters.vue'
import SavedCandidateCard from './SavedCandidateCard.vue'
import CandidatePagination from '../shared/CandidatePagination.vue'
import CandidateDetailModal from './CandidateDetailModal.vue'
import EditNoteModal from './EditNoteModal.vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { useConfirm } from '@/composables/useConfirm'
import { useToast } from '@/composables/useToast'
import employerTalentPoolService from '@/services/employerTalentPool.service'
import { employerJobPostingService } from '@/services/employerJobPosting.service'
import type { ResTalentPoolCandidateDTO } from '@/services/employerTalentPool.service'
import type { ResJobPostingDetail } from '@/types/jobPosting.types'

const emit = defineEmits<{ 'update:total': [value: number] }>()
const toast = useToast()
const { confirm } = useConfirm()

const showModal = ref(false)
const selectedTalentPoolId = ref<number | null>(null)

function openDetail(talentPoolId: number) {
  selectedTalentPoolId.value = talentPoolId
  showModal.value = true
}

const showEditNote = ref(false)
const savingNote = ref(false)
const editNoteCandidate = ref<ResTalentPoolCandidateDTO | null>(null)

function openEditNote(talentPoolId: number) {
  editNoteCandidate.value = candidates.value.find(c => c.talentPoolId === talentPoolId) ?? null
  showEditNote.value = true
}

async function handleSaveNote(note: string) {
  if (!editNoteCandidate.value) return
  savingNote.value = true
  try {
    await employerTalentPoolService.updateNote(editNoteCandidate.value.talentPoolId, note)
    const target = candidates.value.find(c => c.talentPoolId === editNoteCandidate.value!.talentPoolId)
    if (target) target.note = note || undefined
    showEditNote.value = false
    toast.success('Đã lưu', 'Ghi chú đã được cập nhật.')
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Lưu ghi chú thất bại, vui lòng thử lại.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  } finally {
    savingNote.value = false
  }
}

const showInviteModal = ref(false)
const inviting = ref(false)
const inviteCandidate = ref<ResTalentPoolCandidateDTO | null>(null)
const inviteJobOptions = ref<ResJobPostingDetail[]>([])
const selectedJobPostId = ref(0)

async function openInviteModal(talentPoolId: number) {
  inviteCandidate.value = candidates.value.find(c => c.talentPoolId === talentPoolId) ?? null
  selectedJobPostId.value = 0
  showInviteModal.value = true

  try {
    const res = await employerJobPostingService.getList({ page: 0, size: 100 })
    inviteJobOptions.value = res.result.filter((job) =>
      ['published', 'hidden', 'paused'].includes(String(job.status)),
    )
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể tải danh sách tin tuyển dụng.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  }
}

function closeInviteModal() {
  showInviteModal.value = false
  inviteCandidate.value = null
  selectedJobPostId.value = 0
}

async function handleInviteCandidate() {
  if (!inviteCandidate.value || !selectedJobPostId.value) {
    toast.warning('Thiếu thông tin', 'Vui lòng chọn tin tuyển dụng trước khi gửi email mời.')
    return
  }

  inviting.value = true
  try {
    const candidateName = inviteCandidate.value.candidateName
    await employerTalentPoolService.invite(inviteCandidate.value.talentPoolId, {
      jobPostId: selectedJobPostId.value,
    })
    closeInviteModal()
    toast.success('Đã gửi email', `Đã gửi lời mời ứng tuyển đến ${candidateName}.`)
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể gửi email mời. Vui lòng thử lại.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  } finally {
    inviting.value = false
  }
}

async function openDeleteConfirm(talentPoolId: number) {
  const candidate = candidates.value.find(c => c.talentPoolId === talentPoolId)
  const ok = await confirm({
    title: 'Xóa ứng viên khỏi Talent Pool?',
    message: `Ứng viên "${candidate?.candidateName ?? ''}" sẽ bị xóa vĩnh viễn khỏi danh sách tiềm năng. Hành động này không thể hoàn tác.`,
    confirmText: 'Xóa vĩnh viễn',
    confirmColor: 'red',
    icon: 'delete_forever',
  })
  if (!ok) return
  try {
    await employerTalentPoolService.removeFromTalentPool(talentPoolId)
    candidates.value = candidates.value.filter(c => c.talentPoolId !== talentPoolId)
    total.value -= 1
    emit('update:total', total.value)
    toast.success('Đã xóa', 'Ứng viên đã được xóa khỏi Talent Pool.')
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Xóa thất bại, vui lòng thử lại.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  }
}

const candidates = ref<ResTalentPoolCandidateDTO[]>([])
const loading = ref(false)
const total = ref(0)
const totalPages = ref(0)
const currentPage = ref(1)
const pageSize = 10
const search = ref('')
const source = ref('')

async function fetchData(page = 1) {
  loading.value = true
  try {
    const res = await employerTalentPoolService.getTalentPool({
      search: search.value || undefined,
      source: source.value || undefined,
      page: page - 1,
      size: pageSize,
    })
    candidates.value = res.result
    total.value = res.meta.totals
    totalPages.value = res.meta.pages
    emit('update:total', res.meta.totals)
    currentPage.value = page
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể tải danh sách TalentPool.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  } finally {
    loading.value = false
  }
}

function onSearch() { fetchData(1) }
function onPageChange(p: number) { fetchData(p) }

onMounted(() => fetchData(1))
</script>

<style scoped>
.saved-tab { display: flex; flex-direction: column; gap: 1.5rem; padding-top: 1.5rem; }

.saved-tab__grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.25rem;
}
@media (max-width: 1280px) { .saved-tab__grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px)  { .saved-tab__grid { grid-template-columns: 1fr; } }

.saved-tab__loading {
  display: flex; align-items: center; justify-content: center;
  gap: 0.75rem; padding: 4rem 0; color: #64748b; font-size: 0.9375rem;
}
@keyframes spin { to { transform: rotate(360deg); } }
.saved-tab__spinner { font-size: 1.75rem !important; color: #4b9af6; animation: spin 0.8s linear infinite; }

.saved-tab__empty {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 0.75rem; padding: 4rem 0; color: #94a3b8;
}
.saved-tab__empty-icon { font-size: 3rem !important; }
.saved-tab__empty p { font-size: 0.9375rem; font-weight: 600; }
</style>
