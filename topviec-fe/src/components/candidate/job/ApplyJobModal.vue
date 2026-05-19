<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { CV_TYPE } from '@/constants/cvs.constants'
import { useToast } from '@/composables/useToast'
import { useCvsStore } from '@/stores/cvs.store'
import type { ResCv } from '@/types/cvs.types'

interface Props {
  show: boolean
  jobTitle: string
  companyName: string
  companyLogo?: string
}

defineProps<Props>()

const emit = defineEmits<{
  close: []
  confirm: [cvId: number]
}>()

const cvsStore = useCvsStore()
const toast = useToast()

const selectedCvId = ref<number | null>(null)
const acceptedTerms = ref(false)
const isUploading = ref(false)
const openingCvId = ref<number | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)

const selectedCv = computed(() => cvsStore.cvs.find((cv) => cv.id === selectedCvId.value) ?? null)

onMounted(async () => {
  if (cvsStore.cvs.length === 0) {
    await cvsStore.fetchMyCvs()
  }

  const defaultCv = cvsStore.cvs.find((cv) => cv.isDefault)
  if (defaultCv) {
    selectedCvId.value = defaultCv.id
  }
})

function getCvPreviewUrl(cv: ResCv) {
  return cv.fileUrl || cv.pdfUrl
}

function getCvTypeLabel(cv: ResCv) {
  return cv.cvType === CV_TYPE.ONLINE ? 'CV online' : 'CV tải lên'
}

function handleFileSelect(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.item(0)
  if (file) {
    void uploadCv(file)
  }
}

async function uploadCv(file: File) {
  isUploading.value = true
  try {
    const uploadedCv = await cvsStore.uploadCv(file, { title: file.name })
    toast.success('Thành công', 'Đã tải CV lên thành công')
    selectedCvId.value = uploadedCv.id
  } catch {
    toast.error('Lỗi', 'Không thể tải CV lên')
  } finally {
    isUploading.value = false
  }
}

function triggerFileInput() {
  fileInput.value?.click()
}

function formatUpdateTime(dateStr: string) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

async function openCv(cv: ResCv) {
  if (cv.cvType === CV_TYPE.ONLINE) {
    openingCvId.value = cv.id
    const previewWindow = window.open('', '_blank')
    try {
      const exported = await cvsStore.exportPdf(cv.id)
      if (!exported.pdfUrl) {
        throw new Error('PDF_URL_EMPTY')
      }
      if (previewWindow) {
        previewWindow.opener = null
        previewWindow.location.href = exported.pdfUrl
      } else {
        window.open(exported.pdfUrl, '_blank')
      }
    } catch (err: any) {
      previewWindow?.close()
      toast.error('Lỗi', err?.response?.data?.message || 'Không thể tạo bản PDF mới.')
    } finally {
      openingCvId.value = null
    }
    return
  }

  const url = getCvPreviewUrl(cv)
  if (url) {
    window.open(url, '_blank')
    return
  }

  toast.error('Lỗi', 'Không tìm thấy liên kết bản xem trước CV')
}

function handleConfirm() {
  if (!selectedCvId.value) {
    toast.warning('Chú ý', 'Vui lòng chọn CV để ứng tuyển')
    return
  }

  if (!acceptedTerms.value) {
    toast.warning('Chú ý', 'Vui lòng đồng ý với điều khoản dịch vụ')
    return
  }

  emit('confirm', selectedCvId.value)
}
</script>

<template>
  <Transition name="fade">
    <div v-if="show" class="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/60 p-4 backdrop-blur-sm">
      <div
        class="animate-in fade-in zoom-in duration-300 flex max-h-[90vh] w-full max-w-4xl flex-col overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-2xl dark:border-slate-800 dark:bg-surface-dark md:flex-row"
      >
        <div class="w-full shrink-0 border-b border-slate-100 px-8 py-6 dark:border-slate-800 md:hidden">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="font-display text-xl font-bold text-text-main dark:text-white">Nộp hồ sơ ứng tuyển</h3>
              <p class="mt-1 text-xs font-medium uppercase tracking-wider text-text-muted">Sẵn sàng cho hành trình mới</p>
            </div>
            <button
              class="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700"
              @click="emit('close')"
            >
              <span class="material-symbols-outlined text-text-main dark:text-white">close</span>
            </button>
          </div>
        </div>

        <div class="hidden w-full items-center justify-between border-b border-slate-100 px-8 py-6 dark:border-slate-800 md:flex md:absolute md:left-0 md:right-0 md:top-0 md:z-10 md:bg-white/95 md:backdrop-blur dark:md:bg-surface-dark/95">
          <div>
            <h3 class="font-display text-xl font-bold text-text-main dark:text-white">Nộp hồ sơ ứng tuyển</h3>
            <p class="mt-1 text-xs font-medium uppercase tracking-wider text-text-muted">Sẵn sàng cho hành trình mới</p>
          </div>
          <button
            class="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700"
            @click="emit('close')"
          >
            <span class="material-symbols-outlined text-text-main dark:text-white">close</span>
          </button>
        </div>

        <div class="mt-0 flex w-full flex-col md:mt-[88px] md:flex-row">
          <div class="w-full shrink-0 border-r border-slate-100 bg-slate-50 p-8 dark:border-slate-800 dark:bg-slate-800/30 md:w-72">
            <div class="space-y-4">
              <div class="flex h-16 w-16 items-center justify-center rounded-2xl border border-slate-100 bg-white p-2.5 shadow-sm dark:border-slate-600 dark:bg-slate-700">
                <img :src="companyLogo" class="max-h-full max-w-full object-contain" :alt="companyName">
              </div>
              <div>
                <h4 class="line-clamp-2 font-bold leading-tight text-text-main dark:text-white">{{ jobTitle }}</h4>
                <p class="mt-1 line-clamp-1 text-sm text-text-secondary">{{ companyName }}</p>
              </div>
            </div>

            <div class="my-8 h-px w-full bg-slate-200 opacity-50 dark:bg-slate-700"></div>

            <div class="space-y-3">
              <p class="text-[10px] font-bold uppercase tracking-widest text-text-muted">Cần CV mới?</p>
              <RouterLink
                to="/profile?tab=cvs"
                class="group flex w-full items-center gap-3 rounded-2xl border border-slate-200 bg-white p-3 text-left transition-all hover:border-primary hover:shadow-lg hover:shadow-primary/5 dark:border-slate-700 dark:bg-slate-800"
                @click="emit('close')"
              >
                <div class="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary transition-colors group-hover:bg-primary group-hover:text-white">
                  <span class="material-symbols-outlined">auto_awesome</span>
                </div>
                <span class="text-sm font-bold text-text-main dark:text-gray-300">Tạo hồ sơ chuyên nghiệp</span>
              </RouterLink>
            </div>
          </div>

          <div class="flex min-h-0 flex-1 flex-col overflow-hidden p-8">
            <div class="mb-6 flex items-center justify-between">
              <h5 class="px-1 text-sm font-bold uppercase tracking-widest text-text-muted">Danh sách CV của bạn</h5>
              <button
                class="flex items-center gap-1.5 text-xs font-bold text-primary hover:underline"
                :disabled="isUploading"
                @click="triggerFileInput"
              >
                <span class="material-symbols-outlined text-[18px]">cloud_upload</span>
                {{ isUploading ? 'Đang tải...' : 'Tải lên từ máy tính' }}
              </button>
            </div>

            <div class="custom-scrollbar mb-8 flex-grow space-y-3 overflow-y-auto pr-2">
              <template v-if="cvsStore.loading && cvsStore.cvs.length === 0">
                <div class="flex flex-col items-center gap-4 py-12">
                  <div class="h-12 w-12 animate-spin rounded-full border-4 border-primary/20 border-t-primary"></div>
                  <p class="text-sm font-medium italic text-text-muted">Đang chuẩn bị hồ sơ...</p>
                </div>
              </template>

              <template v-else-if="cvsStore.cvs.length > 0">
                <div
                  v-for="cv in cvsStore.cvs"
                  :key="cv.id"
                  class="group relative flex cursor-pointer items-center gap-4 overflow-hidden rounded-2xl border-2 p-5 transition-all"
                  :class="
                    selectedCvId === cv.id
                      ? 'border-primary bg-primary/[0.02] shadow-sm'
                      : 'border-slate-100 bg-white hover:border-slate-300 dark:border-slate-800 dark:bg-slate-800/40 dark:hover:border-slate-700'
                  "
                  @click="selectedCvId = cv.id"
                >
                  <div
                    class="flex h-6 w-6 shrink-0 items-center justify-center rounded-full border-2 transition-all"
                    :class="selectedCvId === cv.id ? 'border-primary bg-primary text-white' : 'border-slate-300 dark:border-slate-600'"
                  >
                    <span v-if="selectedCvId === cv.id" class="material-symbols-outlined text-[16px] font-bold">check</span>
                  </div>

                  <div class="flex min-w-0 flex-grow items-center gap-3">
                    <div class="flex h-10 w-10 items-center justify-center rounded-xl bg-slate-100 text-slate-400 transition-colors group-hover:text-primary dark:bg-slate-700">
                      <span class="material-symbols-outlined">description</span>
                    </div>
                    <div class="min-w-0">
                      <h6 class="truncate text-sm font-bold text-text-main dark:text-white">{{ cv.title }}</h6>
                      <div class="mt-1 flex flex-wrap items-center gap-2">
                        <span
                          class="inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-bold uppercase tracking-[0.08em]"
                          :class="cv.cvType === CV_TYPE.ONLINE ? 'bg-orange-100 text-orange-700' : 'bg-slate-100 text-slate-600'"
                        >
                          {{ getCvTypeLabel(cv) }}
                        </span>
                        <span
                          v-if="cv.isDefault"
                          class="inline-flex items-center rounded-full bg-emerald-100 px-2 py-0.5 text-[10px] font-bold uppercase tracking-[0.08em] text-emerald-700"
                        >
                          Mặc định
                        </span>
                      </div>
                      <p class="mt-1 text-[10px] text-text-muted">Cập nhật: {{ formatUpdateTime(cv.updatedAt) }}</p>
                    </div>
                  </div>

                  <button
                    class="ml-auto flex h-8 w-8 shrink-0 items-center justify-center rounded-lg text-slate-400 transition-all hover:bg-slate-100 hover:text-primary dark:hover:bg-slate-700"
                    :disabled="openingCvId === cv.id"
                    @click.stop="openCv(cv)"
                  >
                    <span class="material-symbols-outlined text-[20px]" :class="{ 'animate-spin': openingCvId === cv.id }">
                      {{ openingCvId === cv.id ? 'progress_activity' : 'visibility' }}
                    </span>
                  </button>
                </div>
              </template>

              <div v-else class="flex flex-col items-center justify-center rounded-3xl border-2 border-dashed border-slate-100 bg-slate-50/50 px-8 py-16 text-center dark:border-slate-800 dark:bg-slate-800/20">
                <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-slate-100 text-slate-300 dark:bg-slate-800">
                  <span class="material-symbols-outlined text-4xl">folder_off</span>
                </div>
                <p class="text-sm font-bold text-text-muted">Chưa có CV nào trên hệ thống</p>
                <p class="mt-1 text-xs text-text-muted">Hãy tải lên hoặc tạo mới ngay bây giờ</p>
              </div>
            </div>

            <div class="mt-auto space-y-6">
              <div
                v-if="selectedCv"
                class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-xs font-medium text-slate-600"
              >
                <p class="font-bold text-slate-900">{{ selectedCv.title }}</p>
                <p class="mt-1">
                  Đang chọn:
                  <span class="font-semibold">{{ getCvTypeLabel(selectedCv) }}</span>
                </p>
                <p v-if="selectedCv.cvType === CV_TYPE.ONLINE" class="mt-1 text-orange-700">
                  CV online sẽ được tạo/cập nhật PDF khi bạn bấm xem hoặc tải.
                </p>
              </div>

              <label class="flex cursor-pointer items-start gap-4 rounded-2xl border border-slate-100 bg-slate-50 p-4 transition-colors hover:bg-slate-100 dark:border-slate-700 dark:bg-slate-800/50 dark:hover:bg-slate-800">
                <input v-model="acceptedTerms" type="checkbox" class="peer sr-only">
                <div class="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-lg border-2 border-slate-300 transition-all peer-checked:border-primary peer-checked:bg-primary dark:border-slate-600">
                  <span v-if="acceptedTerms" class="material-symbols-outlined text-[16px] font-black text-white">check</span>
                </div>
                <span class="text-[11px] font-medium leading-relaxed text-text-secondary">
                  Tôi xác nhận đã đọc và đồng ý với
                  <a href="#" class="font-bold text-primary hover:underline">Điều khoản dịch vụ</a>,
                  <a href="#" class="font-bold text-primary hover:underline">Chính sách bảo mật</a> của TopViec và
                  <a href="#" class="font-bold text-primary hover:underline">Thỏa thuận sử dụng dữ liệu</a> của Nhà tuyển dụng.
                </span>
              </label>

              <div class="flex items-center gap-4">
                <button
                  class="flex h-14 flex-grow items-center justify-center gap-3 rounded-2xl bg-primary text-sm font-bold text-white shadow-xl shadow-primary/25 transition-all hover:-translate-y-0.5 hover:bg-primary-hover disabled:cursor-not-allowed disabled:grayscale disabled:opacity-50 active:translate-y-0"
                  :disabled="!selectedCvId || !acceptedTerms || cvsStore.loading"
                  @click="handleConfirm"
                >
                  <span class="material-symbols-outlined text-[22px]">rocket_launch</span>
                  Gửi đơn ứng tuyển
                </button>
              </div>
            </div>
          </div>
        </div>

        <input ref="fileInput" type="file" class="hidden" accept=".pdf,.doc,.docx" @change="handleFileSelect">
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
