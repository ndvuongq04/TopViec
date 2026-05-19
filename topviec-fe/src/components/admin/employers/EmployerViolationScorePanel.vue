<template>
  <div class="space-y-6">
    <div
      v-if="!employerId"
      class="rounded-xl border border-amber-200 bg-amber-50 p-5 text-sm text-amber-800"
    >
      Không xác định được tài khoản employer để tải điểm vi phạm.
    </div>

    <template v-else>
      <div v-if="store.loading && !store.violationScore" class="grid grid-cols-1 gap-6 xl:grid-cols-3">
        <div class="col-span-2 rounded-xl border border-slate-200 bg-white p-6">
          <div class="h-6 w-40 animate-pulse rounded bg-slate-100"></div>
          <div class="mt-6 h-40 animate-pulse rounded-xl bg-slate-100"></div>
        </div>
        <div class="rounded-xl border border-slate-200 bg-white p-6">
          <div class="h-6 w-32 animate-pulse rounded bg-slate-100"></div>
          <div class="mt-6 h-40 animate-pulse rounded-xl bg-slate-100"></div>
        </div>
      </div>

      <div
        v-else-if="store.error && !store.violationScore"
        class="rounded-xl border border-red-200 bg-red-50 p-5 text-sm text-red-700"
      >
        {{ store.error }}
      </div>

      <template v-else-if="violationScore">
        <div class="grid grid-cols-1 gap-6 xl:grid-cols-3">
          <section class="xl:col-span-2 rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
            <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
              <div>
                <p class="text-xs font-bold uppercase tracking-[0.16em] text-slate-400">Hồ sơ vi phạm</p>
                <h3 class="mt-2 text-2xl font-black tracking-tight text-slate-900">
                  {{ violationScore.company?.name ?? 'Nhà tuyển dụng' }}
                </h3>
                <p class="mt-1 text-sm text-slate-500">{{ violationScore.employerEmail }}</p>
              </div>

              <span
                class="inline-flex items-center gap-2 rounded-full px-3 py-1 text-xs font-bold uppercase tracking-[0.14em]"
                :class="levelClass"
              >
                <span class="material-symbols-outlined text-sm">{{ levelIcon }}</span>
                {{ levelLabel }}
              </span>
            </div>

            <div class="mt-6 grid grid-cols-1 gap-4 md:grid-cols-3">
              <div class="rounded-2xl bg-slate-950 px-5 py-5 text-white">
                <p class="text-xs uppercase tracking-[0.14em] text-slate-400">Điểm hiện tại</p>
                <p class="mt-3 text-4xl font-black">{{ violationScore.score.totalScore }}</p>
              </div>

              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-5 py-5">
                <p class="text-xs uppercase tracking-[0.14em] text-slate-400">Điều kiện reset</p>
                <p class="mt-3 text-lg font-bold text-slate-900">
                  {{ violationScore.score.canResetScore ? 'Đủ điều kiện' : 'Chưa đủ điều kiện' }}
                </p>
                <p class="mt-1 text-sm text-slate-500">{{ canResetHint }}</p>
              </div>

              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-5 py-5">
                <p class="text-xs uppercase tracking-[0.14em] text-slate-400">Lần reset gần nhất</p>
                <p class="mt-3 text-lg font-bold text-slate-900">
                  {{ formatDateTime(violationScore.score.lastResetAt) }}
                </p>
                <p class="mt-1 text-sm text-slate-500">
                  {{ violationScore.score.resetByAdminName || 'Chưa có lịch sử reset' }}
                </p>
              </div>
            </div>

            <div class="mt-6">
              <div class="h-3 overflow-hidden rounded-full bg-slate-100">
                <div class="flex h-full">
                  <div class="w-[40%] bg-emerald-400"></div>
                  <div class="w-[30%] bg-amber-300"></div>
                  <div class="w-[30%] bg-rose-300"></div>
                </div>
              </div>
              <div class="mt-2 flex justify-between text-[11px] font-bold uppercase tracking-[0.12em] text-slate-500">
                <span>Bình thường 0-19</span>
                <span>Hạn chế 20-49</span>
                <span>Khoa 50+</span>
              </div>
            </div>
          </section>

          <section v-if="can('violation.reset')" class="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 class="text-lg font-black tracking-tight text-slate-900">Tác vụ quản trị</h3>

            <div class="mt-5 space-y-5">
              <div class="rounded-2xl border border-slate-200 p-4">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <p class="text-sm font-bold text-slate-900">Reset điểm về 0</p>
                    <p class="mt-1 text-sm text-slate-500">
                      Chỉ hợp lệ khi không tái phạm nhóm B trong 6 tháng gần nhất.
                    </p>
                  </div>
                  <span
                    class="rounded-full px-2.5 py-1 text-[11px] font-bold uppercase tracking-[0.12em]"
                    :class="violationScore.score.canResetScore ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'"
                  >
                    {{ violationScore.score.canResetScore ? 'Ready' : 'Blocked' }}
                  </span>
                </div>

                <textarea
                  v-model="resetNote"
                  class="mt-4 min-h-24 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-800 outline-none transition focus:border-[#963131] focus:bg-white"
                  placeholder="Nhập lý do reset điểm..."
                />

                <button
                  class="mt-4 inline-flex items-center justify-center rounded-xl bg-[#963131] px-4 py-2.5 text-sm font-bold text-white transition hover:bg-[#7f2727] disabled:cursor-not-allowed disabled:opacity-50"
                  :disabled="submittingReset || !violationScore.score.canResetScore || !resetNote.trim()"
                  @click="handleReset"
                >
                  {{ submittingReset ? 'Đang reset...' : 'Reset điểm' }}
                </button>
              </div>

              <div class="rounded-2xl border border-slate-200 p-4">
                <p class="text-sm font-bold text-slate-900">Giảm điểm thủ công</p>
                <p class="mt-1 text-sm text-slate-500">
                  Dùng khi nhà tuyển dụng đã khắc phục hậu quả và cần điều chỉnh lại score.
                </p>

                <div class="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-[120px_1fr]">
                  <div>
                    <label class="mb-2 block text-xs font-bold uppercase tracking-[0.12em] text-slate-400">
                      Số điểm
                    </label>
                    <input
                      v-model.number="adjustPoints"
                      type="number"
                      min="1"
                      class="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-800 outline-none transition focus:border-[#963131] focus:bg-white"
                    />
                  </div>

                  <div>
                    <label class="mb-2 block text-xs font-bold uppercase tracking-[0.12em] text-slate-400">
                      Ghi chú
                    </label>
                    <textarea
                      v-model="adjustNote"
                      class="min-h-24 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-800 outline-none transition focus:border-[#963131] focus:bg-white"
                      placeholder="Nhập lý do giảm điểm..."
                    />
                  </div>
                </div>

                <button
                  class="mt-4 inline-flex items-center justify-center rounded-xl bg-slate-900 px-4 py-2.5 text-sm font-bold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                  :disabled="submittingAdjust || !canAdjust"
                  @click="handleAdjust"
                >
                  {{ submittingAdjust ? 'Đang cập nhật...' : 'Giảm điểm' }}
                </button>
              </div>
            </div>
          </section>
        </div>

        <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-xs font-bold uppercase tracking-[0.16em] text-slate-400">Violation Log</p>
              <h3 class="mt-2 text-xl font-black tracking-tight text-slate-900">Lich su vi pham</h3>
            </div>
            <button
              class="inline-flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2 text-sm font-bold text-slate-600 transition hover:border-slate-300 hover:text-slate-900"
              :disabled="store.loading"
              @click="reload"
            >
              <span class="material-symbols-outlined text-base">refresh</span>
              Làm mới
            </button>
          </div>

          <div v-if="!violationScore.history.length" class="mt-5 rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-5 py-10 text-center text-sm text-slate-500">
            Chưa có log vi phạm nào.
          </div>

          <div v-else class="mt-5 overflow-hidden rounded-2xl border border-slate-200">
            <div class="hidden grid-cols-[150px_140px_120px_1fr_160px] gap-4 bg-slate-50 px-5 py-3 text-xs font-bold uppercase tracking-[0.12em] text-slate-500 lg:grid">
              <span>Thời gian</span>
              <span>Loại</span>
              <span>Điểm</span>
              <span>Ghi chú</span>
              <span>Người tạo</span>
            </div>

            <div
              v-for="item in violationScore.history"
              :key="item.id"
              class="border-t border-slate-100 px-5 py-4"
            >
              <div class="hidden grid-cols-[150px_140px_120px_1fr_160px] gap-4 text-sm lg:grid">
                <div>
                  <p class="font-semibold text-slate-900">{{ formatDateTime(item.createdAt) }}</p>
                  <p class="mt-1 text-xs text-slate-400">#{{ item.id }}</p>
                </div>
                <div>
                  <span class="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-bold text-slate-700">
                    {{ getViolationTypeLabel(item.violationType) }}
                  </span>
                  <p class="mt-2 text-xs text-slate-500">{{ getViolationSourceLabel(item.source) }}</p>
                </div>
                <div>
                  <span
                    class="inline-flex rounded-full px-2.5 py-1 text-xs font-bold"
                    :class="item.points >= 0 ? 'bg-rose-100 text-rose-700' : 'bg-emerald-100 text-emerald-700'"
                  >
                    {{ item.points > 0 ? `+${item.points}` : item.points }} điểm
                  </span>
                </div>
                <div class="text-slate-600">
                  {{ item.note || 'Không có ghi chú.' }}
                  <p v-if="item.complaintId" class="mt-1 text-xs text-slate-400">
                    Complaint ID: {{ item.complaintId }}
                  </p>
                </div>
                <div class="text-slate-600">{{ item.createdByAdminName || 'Hệ thống' }}</div>
              </div>

              <div class="space-y-2 text-sm lg:hidden">
                <div class="flex items-center justify-between gap-3">
                  <p class="font-semibold text-slate-900">{{ formatDateTime(item.createdAt) }}</p>
                  <span
                    class="inline-flex rounded-full px-2.5 py-1 text-xs font-bold"
                    :class="item.points >= 0 ? 'bg-rose-100 text-rose-700' : 'bg-emerald-100 text-emerald-700'"
                  >
                    {{ item.points > 0 ? `+${item.points}` : item.points }} điểm
                  </span>
                </div>
                <div class="flex items-center gap-2">
                  <span class="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-bold text-slate-700">
                    {{ getViolationTypeLabel(item.violationType) }}
                  </span>
                  <span class="text-xs text-slate-500">{{ getViolationSourceLabel(item.source) }}</span>
                </div>
                <p class="text-slate-600">{{ item.note || 'Không có ghi chú.' }}</p>
                <p class="text-xs text-slate-400">
                  {{ item.createdByAdminName || 'Hệ thống' }}
                  <span v-if="item.complaintId"> · Complaint ID: {{ item.complaintId }}</span>
                </p>
              </div>
            </div>
          </div>
        </section>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import {
  COMPLAINT_TYPE_OPTIONS,
  VIOLATION_SOURCE_OPTIONS,
} from '@/constants/complaints.constants'
import { useConfirm } from '@/composables/useConfirm'
import { useToast } from '@/composables/useToast'
import { useAdminViolationScoreStore } from '@/stores/adminViolationScore.store'
import { useAdminPermission } from '@/composables/useAdminPermission'

const { can } = useAdminPermission()

const props = defineProps<{
  employerId: number | null
}>()

const store = useAdminViolationScoreStore()
const toast = useToast()
const { confirm } = useConfirm()

const resetNote = ref('')
const adjustNote = ref('')
const adjustPoints = ref<number | null>(null)
const submittingReset = ref(false)
const submittingAdjust = ref(false)

const complaintTypeLabelMap = Object.fromEntries(
  COMPLAINT_TYPE_OPTIONS.map((item) => [item.value, item.label]),
) as Record<string, string>

const violationSourceLabelMap = Object.fromEntries(
  VIOLATION_SOURCE_OPTIONS.map((item) => [item.value, item.label]),
) as Record<string, string>

const violationScore = computed(() => store.violationScore)
const canAdjust = computed(() => {
  const points = Number(adjustPoints.value)
  const currentScore = violationScore.value?.score.totalScore ?? 0
  return Number.isFinite(points) && points > 0 && points <= currentScore && Boolean(adjustNote.value.trim())
})

const levelLabel = computed(() => {
  const value = violationScore.value?.score.scoreLevel
  if (value === 'suspended') return 'Khóa tài khoản'
  if (value === 'limited') return 'Bị hạn chế'
  return 'Bình thường'
})

const levelIcon = computed(() => {
  const value = violationScore.value?.score.scoreLevel
  if (value === 'suspended') return 'block'
  if (value === 'limited') return 'warning'
  return 'verified'
})

const levelClass = computed(() => {
  const value = violationScore.value?.score.scoreLevel
  if (value === 'suspended') return 'bg-rose-100 text-rose-700'
  if (value === 'limited') return 'bg-amber-100 text-amber-700'
  return 'bg-emerald-100 text-emerald-700'
})

const canResetHint = computed(() => {
  const score = violationScore.value?.score
  if (!score) return '-'
  if (score.canResetScore) return 'Có thể reset ngay nếu admin xác nhận.'
  if (score.lastGroupBViolationAt) {
    return `Theo doi den sau 6 thang tu ${formatDateTime(score.lastGroupBViolationAt)}.`
  }
  return 'Chưa đủ điều kiện reset.'
})

function formatDateTime(value: string | null | undefined) {
  if (!value) return '-'
  return new Date(value).toLocaleString('vi-VN')
}

function formatLabel(value: string | null | undefined) {
  if (!value) return '-'
  return value.replace(/_/g, ' ')
}

function getViolationTypeLabel(value: string | null | undefined) {
  if (!value) return '-'
  if (value === 'score_adjustment') return 'Điều chỉnh điểm'
  return complaintTypeLabelMap[value] ?? formatLabel(value)
}

function getViolationSourceLabel(value: string | null | undefined) {
  if (!value) return '-'
  return violationSourceLabelMap[value] ?? formatLabel(value)
}

async function load() {
  if (!props.employerId) return
  try {
    await store.fetchByEmployerId(props.employerId)
  } catch {
    toast.error('Lỗi', store.error ?? 'Không thể tải điểm vi phạm.')
  }
}

async function reload() {
  await load()
}

async function handleReset() {
  if (!props.employerId || !resetNote.value.trim()) return

  const accepted = await confirm({
    title: 'Reset điểm vi phạm',
    message: 'Xác nhận reset toàn bộ điểm vi phạm của nhà tuyển dụng này về 0?',
    confirmText: 'Reset điểm',
    confirmColor: 'red',
    icon: 'restart_alt',
  })

  if (!accepted) return

  submittingReset.value = true
  try {
    await store.resetScore(props.employerId, { note: resetNote.value.trim() })
    resetNote.value = ''
    toast.success('Thành công', 'Đã reset điểm vi phạm về 0.')
  } catch {
    toast.error('Lỗi', store.error ?? 'Không thể reset điểm vi phạm.')
  } finally {
    submittingReset.value = false
  }
}

async function handleAdjust() {
  if (!props.employerId || !canAdjust.value) return

  submittingAdjust.value = true
  try {
    await store.adjustScore(props.employerId, {
      pointsToDecrease: Number(adjustPoints.value),
      note: adjustNote.value.trim(),
    })
    adjustPoints.value = null
    adjustNote.value = ''
    toast.success('Thành công', 'Đã cập nhật điểm vi phạm.')
  } catch {
    toast.error('Lỗi', store.error ?? 'Không thể giảm điểm vi phạm.')
  } finally {
    submittingAdjust.value = false
  }
}

watch(
  () => props.employerId,
  () => {
    store.reset()
    load()
  },
  { immediate: true },
)
</script>
