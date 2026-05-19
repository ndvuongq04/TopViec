<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="emit('close')" />

        <!-- Modal -->
        <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-lg flex flex-col max-h-[90vh] overflow-hidden">

          <!-- Header -->
          <div class="flex items-center justify-between px-6 pt-6 pb-4 border-b border-slate-100 shrink-0">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
                <span class="material-symbols-outlined text-primary text-[20px]">flag</span>
              </div>
              <div>
                <h2 class="text-base font-extrabold text-slate-900">Gửi khiếu nại</h2>
                <p class="text-xs text-slate-500">Bước {{ step }} / 2</p>
              </div>
            </div>
            <button
              class="w-8 h-8 rounded-full hover:bg-slate-100 flex items-center justify-center transition-colors cursor-pointer"
              @click="emit('close')"
            >
              <span class="material-symbols-outlined text-slate-500 text-[20px]">close</span>
            </button>
          </div>

          <!-- Step indicator -->
          <div class="flex px-6 pt-4 gap-2 shrink-0">
            <div class="flex-1 h-1 rounded-full" :class="step >= 1 ? 'bg-primary' : 'bg-slate-200'" />
            <div class="flex-1 h-1 rounded-full" :class="step >= 2 ? 'bg-primary' : 'bg-slate-200'" />
          </div>

          <!-- Account warning (hardcoded: tài khoản < 7 ngày) -->
          <div v-if="accountTooNew" class="mx-6 mt-4 p-4 bg-amber-50 border border-amber-200 rounded-xl flex gap-3 shrink-0">
            <span class="material-symbols-outlined text-amber-500 shrink-0">info</span>
            <p class="text-sm text-amber-800">Tài khoản cần ít nhất <strong>7 ngày</strong> để sử dụng tính năng khiếu nại.</p>
          </div>

          <!-- Body (scrollable) -->
          <div class="flex-1 overflow-y-auto px-6 py-5">

            <!-- ── STEP 1: Tin bị báo cáo + Loại vi phạm ── -->
            <template v-if="step === 1">

              <!-- Tin bị báo cáo (pre-filled) -->
              <div class="mb-6">
                <p class="text-xs font-bold text-slate-500 uppercase tracking-wide mb-2">Tin tuyển dụng bị báo cáo</p>
                <div class="flex items-center gap-3 p-3 bg-slate-50 border border-slate-200 rounded-xl">
                  <div class="w-10 h-10 rounded-lg bg-white border border-slate-100 flex items-center justify-center overflow-hidden shrink-0 p-1">
                    <img v-if="companyLogo" :src="companyLogo" :alt="companyName" class="w-full h-full object-contain" />
                    <span v-else class="material-symbols-outlined text-slate-400 text-[20px]">business</span>
                  </div>
                  <div class="min-w-0">
                    <p class="font-bold text-slate-900 text-sm truncate">{{ jobTitle }}</p>
                    <p class="text-xs text-slate-500 truncate">{{ companyName }}</p>
                  </div>
                  <span class="ml-auto material-symbols-outlined text-green-500 text-[18px] shrink-0">check_circle</span>
                </div>
              </div>

              <!-- Loại vi phạm -->
              <div>
                <p class="text-xs font-bold text-slate-500 uppercase tracking-wide mb-3">Chọn loại vi phạm</p>

                <!-- Loading -->
                <div v-if="loadingReasons" class="flex flex-col gap-2">
                  <div v-for="i in 4" :key="i" class="h-14 rounded-xl bg-slate-100 animate-pulse" />
                </div>

                <div v-else class="flex flex-col gap-2">
                  <label
                    v-for="opt in violationReasons"
                    :key="opt.code"
                    class="flex items-start gap-3 p-3 rounded-xl border cursor-pointer transition-all"
                    :class="selectedCode === opt.code
                      ? 'border-primary bg-primary/5'
                      : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'"
                  >
                    <input
                      type="radio"
                      :value="opt.code"
                      v-model="selectedCode"
                      class="mt-0.5 accent-primary shrink-0"
                    />
                    <div class="min-w-0">
                      <div class="flex items-center gap-2 flex-wrap">
                        <span class="text-sm font-semibold text-slate-900">{{ opt.name }}</span>
                        <span v-if="opt.requiresEvidence" class="text-[10px] font-bold text-red-500 flex items-center gap-0.5">
                          <span class="material-symbols-outlined text-[12px]">warning</span> Yêu cầu bằng chứng
                        </span>
                      </div>
                    </div>
                  </label>
                </div>
              </div>

            </template>

            <!-- ── STEP 2: Mô tả & Bằng chứng ── -->
            <template v-if="step === 2">

              <!-- Cảnh báo nhóm B -->
              <div v-if="isGroupB" class="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl flex gap-2">
                <span class="material-symbols-outlined text-red-500 text-[18px] shrink-0 mt-0.5">warning</span>
                <p class="text-sm text-red-700">Loại vi phạm này yêu cầu bằng chứng. Vui lòng đính kèm ảnh chụp màn hình hoặc tài liệu liên quan.</p>
              </div>

              <!-- Loại đã chọn -->
              <div class="mb-5 flex items-center gap-2">
                <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Loại vi phạm:</span>
                <span class="text-xs font-bold px-2 py-0.5 rounded-full"
                  :class="isGroupB ? 'bg-red-100 text-red-600' : 'bg-slate-200 text-slate-600'">
                  {{ selectedOption?.name }}
                </span>
              </div>

              <!-- Mô tả -->
              <div class="mb-5">
                <label class="text-xs font-bold text-slate-500 uppercase tracking-wide block mb-2">
                  Mô tả chi tiết <span v-if="isGroupB" class="text-red-500">*</span>
                  <span v-else class="text-slate-400 font-normal normal-case">(tùy chọn)</span>
                </label>
                <textarea
                  v-model="description"
                  rows="4"
                  class="w-full border border-slate-200 rounded-xl px-4 py-3 text-sm text-slate-800 outline-none resize-none focus:ring-2 focus:ring-[#963131]/20 focus:border-primary/50 transition-all"
                  placeholder="Mô tả cụ thể vấn đề bạn gặp phải..."
                />
              </div>

              <!-- Upload bằng chứng -->
              <div>
                <label class="text-xs font-bold text-slate-500 uppercase tracking-wide block mb-2">
                  Bằng chứng <span v-if="isGroupB" class="text-red-500">*</span>
                  <span v-else class="text-slate-400 font-normal normal-case">(tùy chọn)</span>
                </label>

                <!-- Drop zone -->
                <div
                  class="border-2 border-dashed rounded-xl p-5 text-center cursor-pointer transition-colors"
                  :class="isDragging ? 'border-primary bg-primary/5' : 'border-slate-200 hover:border-primary/50 hover:bg-slate-50'"
                  @dragover.prevent="isDragging = true"
                  @dragleave="isDragging = false"
                  @drop.prevent="onDrop"
                  @click="fileInput?.click()"
                >
                  <span class="material-symbols-outlined text-slate-400 text-[36px] block mb-1">cloud_upload</span>
                  <p class="text-sm font-medium text-slate-600">Kéo thả hoặc <span class="text-primary font-bold">chọn file</span></p>
                  <p class="text-xs text-slate-400 mt-1">PNG, JPG, PDF — tối đa 5 file, mỗi file ≤ 5MB</p>
                </div>
                <input ref="fileInput" type="file" multiple accept="image/*,.pdf" class="hidden" @change="onFileChange" />

                <!-- Danh sách file đã chọn -->
                <div v-if="files.length" class="mt-3 flex flex-col gap-2">
                  <div
                    v-for="(f, i) in files"
                    :key="i"
                    class="flex items-center gap-2 px-3 py-2 bg-slate-50 rounded-lg border border-slate-200"
                  >
                    <span class="material-symbols-outlined text-primary text-[16px]">attach_file</span>
                    <span class="flex-1 text-xs text-slate-700 truncate">{{ f.name }}</span>
                    <span class="text-xs text-slate-400">{{ (f.size / 1024).toFixed(0) }}KB</span>
                    <button class="text-slate-400 hover:text-red-500 transition-colors cursor-pointer" @click.stop="removeFile(i)">
                      <span class="material-symbols-outlined text-[16px]">close</span>
                    </button>
                  </div>
                </div>

                <!-- Lỗi quá 5 file -->
                <p v-if="fileError" class="text-xs text-red-500 mt-2">{{ fileError }}</p>
              </div>

            </template>

          </div>

          <!-- Footer -->
          <div class="flex items-center justify-between px-6 py-4 border-t border-slate-100 shrink-0 gap-3">
            <button
              v-if="step === 2"
              class="h-10 px-5 rounded-xl border border-slate-200 text-sm font-bold text-slate-600 hover:bg-slate-50 transition-colors cursor-pointer"
              @click="step = 1"
            >
              Quay lại
            </button>
            <div v-else />

            <div class="flex gap-3">
              <button
                class="h-10 px-5 rounded-xl border border-slate-200 text-sm font-bold text-slate-600 hover:bg-slate-50 transition-colors cursor-pointer"
                @click="emit('close')"
              >
                Huỷ
              </button>

              <!-- Step 1: Next -->
              <button
                v-if="step === 1"
                class="h-10 px-6 rounded-xl text-sm font-bold text-white transition-all cursor-pointer"
                :class="canGoNext ? 'bg-primary hover:bg-primary-dark shadow-md' : 'bg-slate-200 cursor-not-allowed'"
                :disabled="!canGoNext || accountTooNew"
                @click="step = 2"
              >
                Tiếp theo
              </button>

              <!-- Step 2: Submit -->
              <button
                v-if="step === 2"
                class="h-10 px-6 rounded-xl text-sm font-bold text-white transition-all cursor-pointer"
                :class="canSubmit ? 'bg-primary hover:bg-primary-dark shadow-md' : 'bg-slate-200 cursor-not-allowed'"
                :disabled="!canSubmit"
                @click="handleSubmit"
              >
                <span v-if="submitting" class="flex items-center gap-2">
                  <span class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Đang gửi...
                </span>
                <span v-else>Gửi khiếu nại</span>
              </button>
            </div>
          </div>

        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import candidateReportService from '@/services/candidateReport.service'
import { useToast } from '@/composables/useToast'
import type { ResViolationReason } from '@/types/report.types'

interface Props {
  show: boolean
  jobPostId: number
  jobTitle: string
  companyName: string
  companyLogo?: string
}

const props = defineProps<Props>()
const emit  = defineEmits<{ close: []; submitted: [] }>()

const toast = useToast()

// Hardcode: tài khoản đủ 7 ngày
const accountTooNew = false

const step             = ref(1)
const selectedCode     = ref('')
const description      = ref('')
const files            = ref<File[]>([])
const fileError        = ref('')
const isDragging       = ref(false)
const fileInput        = ref<HTMLInputElement | null>(null)
const submitting       = ref(false)

const violationReasons    = ref<ResViolationReason[]>([])
const loadingReasons      = ref(false)

watch(() => props.show, async (val) => {
  if (val && !violationReasons.value.length) {
    loadingReasons.value = true
    try {
      violationReasons.value = await candidateReportService.getViolationReasons()
    } catch {
      toast.error('Không thể tải danh sách vi phạm', 'Vui lòng thử lại sau.')
    } finally {
      loadingReasons.value = false
    }
  }
}, { immediate: true })

const selectedOption = computed(() => violationReasons.value.find(o => o.code === selectedCode.value))
const isGroupB       = computed(() => selectedOption.value?.group === 'B')

const canGoNext = computed(() => !!selectedCode.value)
const canSubmit = computed(() => {
  if (isGroupB.value) return !!description.value.trim() && files.value.length > 0
  return true
})

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  if (!input.files) return
  addFiles(Array.from(input.files))
}

function onDrop(e: DragEvent) {
  isDragging.value = false
  if (!e.dataTransfer?.files) return
  addFiles(Array.from(e.dataTransfer.files))
}

function addFiles(newFiles: File[]) {
  fileError.value = ''
  const MAX_SIZE  = 5 * 1024 * 1024
  const valid     = newFiles.filter(f => f.size <= MAX_SIZE)
  const combined  = [...files.value, ...valid]

  if (combined.length > 5) {
    fileError.value = 'Tối đa 5 file được phép đính kèm.'
    files.value = combined.slice(0, 5)
    return
  }
  if (valid.length < newFiles.length) {
    fileError.value = 'Một số file vượt quá 5MB và đã bị bỏ qua.'
  }
  files.value = combined
}

function removeFile(index: number) {
  files.value.splice(index, 1)
  fileError.value = ''
}

async function handleSubmit() {
  if (!selectedCode.value) return

  submitting.value = true
  try {
    await candidateReportService.create({
      jobPostId: props.jobPostId,
      complaintType: selectedCode.value as any,
      description: description.value.trim() || undefined,
      evidences: [],
    })

    toast.success('Gửi khiếu nại thành công', 'Chúng tôi sẽ xem xét và phản hồi trong thời gian sớm nhất.')
    emit('submitted')
    resetForm()
  } catch (err: any) {
    const msg = err?.response?.data?.message
    const text = typeof msg === 'object' ? Object.values(msg)[0] as string : (msg ?? 'Có lỗi xảy ra, vui lòng thử lại.')
    toast.error('Gửi khiếu nại thất bại', text)
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  step.value         = 1
  selectedCode.value = ''
  description.value  = ''
  files.value        = []
  fileError.value    = ''
}
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active { transition: opacity 0.2s ease; }
.modal-fade-enter-from,
.modal-fade-leave-to    { opacity: 0; }
</style>
