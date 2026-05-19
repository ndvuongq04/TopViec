<template>
  <div class="space-y-6">
    <!-- Header/Upload Section -->
    <div class="bg-white dark:bg-surface-dark p-6 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
        <div>
          <h3 class="text-lg font-bold text-text-main dark:text-white flex items-center gap-2">
            <span class="material-symbols-outlined text-primary">description</span>
            Quản lý CV ({{ cvs.length }}/5)
          </h3>
          <p class="text-sm text-text-muted mt-1">Tải lên và quản lý các bản CV của bạn để ứng tuyển nhanh chóng.</p>
        </div>
        <button
          class="flex items-center gap-2 px-6 py-2.5 bg-primary text-white font-bold rounded-2xl hover:bg-primary-hover transition-all shadow-lg shadow-primary/20 cursor-pointer"
          @click="triggerUpload"
          :disabled="cvs.length >= 10 || uploading"
        >
          <span v-if="uploading" class="material-symbols-outlined animate-spin text-[20px]">progress_activity</span>
          <span v-else class="material-symbols-outlined text-[20px]">cloud_upload</span>
          {{ uploading ? 'Đang tải lên...' : 'Tải lên CV mới' }}
        </button>
        <input ref="fileInput" type="file" accept=".pdf,.docx" class="hidden" @change="onFileChange" />
      </div>

      <!-- Upload Placeholder (Nếu chưa có CV) -->
      <div
        v-if="cvs.length === 0 && !loading"
        class="border-2 border-dashed border-slate-200 dark:border-slate-700 rounded-3xl p-12 flex flex-col items-center justify-center text-center hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer group"
        @click="triggerUpload"
      >
        <div class="h-16 w-16 bg-primary/10 text-primary rounded-full flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
          <span class="material-symbols-outlined text-3xl">add_notes</span>
        </div>
        <h4 class="text-lg font-bold text-text-main dark:text-white">Bạn chưa có CV nào</h4>
        <p class="text-sm text-text-muted mt-2 max-w-sm">Tải lên CV đầu tiên để bắt đầu hành trình tìm kiếm công việc mơ ước.</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading && cvs.length === 0" class="py-12 flex flex-col items-center justify-center gap-4 text-text-muted">
        <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
        <p>Đang tải danh sách CV...</p>
      </div>

      <!-- CV List -->
      <div v-else class="flex flex-col gap-6">
        <div
          v-for="cv in cvs"
          :key="cv.id"
          class="bg-white dark:bg-slate-800/20 border border-slate-200 dark:border-slate-700/50 rounded-2xl p-6 transition-all hover:shadow-md"
        >
          <!-- Top Row: Title & Date -->
          <div class="flex items-start justify-between gap-4 mb-4">
            <div class="flex items-center gap-2 flex-1 min-w-0">
              <h4 class="text-base font-bold text-text-main dark:text-white uppercase tracking-tight truncate">
                {{ cv.title }}
              </h4>
              <span
                class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider"
                :class="cv.cvType === CV_TYPE.ONLINE
                  ? 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300'
                  : 'bg-slate-100 text-slate-600 dark:bg-slate-700/50 dark:text-slate-300'"
              >
                {{ cv.cvType === CV_TYPE.ONLINE ? 'CV online' : 'CV tải lên' }}
              </span>
              <span
                v-if="cv.cvType === CV_TYPE.ONLINE"
                class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider"
                :class="cv.pdfDirty
                  ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300'
                  : cv.pdfUrl
                    ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300'
                    : 'bg-slate-100 text-slate-500 dark:bg-slate-700/50 dark:text-slate-400'"
              >
                {{ getOnlinePdfStatusLabel(cv) }}
              </span>
              <button 
                class="p-1 text-text-muted hover:text-primary transition-colors hover:bg-slate-100 dark:hover:bg-slate-700 rounded-md cursor-pointer"
                title="Sửa tên file"
                @click="openRenameModal(cv)"
              >
                <span class="material-symbols-outlined text-[18px]">edit</span>
              </button>
            </div>
            <div class="flex flex-col items-end gap-1 shrink-0">
               <div class="flex items-center gap-1.5 text-text-muted text-xs font-medium">
                <span class="material-symbols-outlined text-[16px]">schedule</span>
                <span>{{ formatDate(cv.createdAt) }}</span>
              </div>
              <span 
                class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider"
                :class="cv.visibility === CV_VISIBILITY.PUBLIC 
                  ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' 
                  : 'bg-slate-100 text-slate-500 dark:bg-slate-700/50 dark:text-slate-400'"
              >
                {{ cv.visibility === CV_VISIBILITY.PUBLIC ? 'Công khai' : 'Riêng tư' }}
              </span>
            </div>
          </div>



          <!-- Bottom Row: Actions -->
          <div class="flex flex-wrap items-center justify-end gap-2 sm:gap-3">
            <button 
              v-if="!cv.isDefault"
              class="flex items-center gap-1.5 px-3 py-1.5 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold text-text-main dark:text-white hover:bg-slate-50 dark:hover:bg-slate-800 transition-all bg-white dark:bg-slate-900 shadow-sm cursor-pointer"
              @click="setDefault(cv)"
            >
              <span class="material-symbols-outlined text-[18px]">star</span>
              Đặt CV chính
            </button>
            <span v-else class="flex items-center gap-1.5 px-3 py-1.5 bg-green-50 text-green-600 dark:bg-green-900/20 dark:text-green-400 border border-green-100 dark:border-green-800 rounded-xl text-xs font-bold shadow-sm">
              <span class="material-symbols-outlined text-[18px]">check_circle</span>
              CV chính
            </span>

            <!-- Nút Bật/Tắt xem công khai cho NTT -->
            <button 
              class="flex items-center gap-1.5 px-3 py-1.5 border transition-all rounded-xl text-xs font-bold shadow-sm cursor-pointer"
              :class="cv.visibility === CV_VISIBILITY.PUBLIC 
                ? 'bg-blue-600 border-blue-600 text-white hover:bg-blue-700' 
                : 'bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700 text-text-main dark:text-white hover:bg-slate-50 dark:hover:bg-slate-800'"
              @click="toggleVisibility(cv)"
              :disabled="actionLoading === cv.id"
              :title="cv.visibility === CV_VISIBILITY.PUBLIC ? 'Nhà tuyển dụng có thể tìm thấy CV này' : 'Chỉ bạn mới có thể sử dụng CV này để ứng tuyển'"
            >
              <span class="material-symbols-outlined text-[18px]">
                {{ cv.visibility === CV_VISIBILITY.PUBLIC ? 'lock_open' : 'lock' }}
              </span>
              {{ cv.visibility === CV_VISIBILITY.PUBLIC ? 'CV công khai' : 'CV riêng tư' }}
            </button>

            <!-- Nút Share -->
            <button 
              class="flex items-center gap-1.5 px-3 py-1.5 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold text-text-main dark:text-white hover:bg-slate-50 dark:hover:bg-slate-800 transition-all bg-white dark:bg-slate-900 shadow-sm active:scale-95 cursor-pointer"
              @click="toast.info('Đang phát triển', 'Tính năng này đang được phát triển.')"
              :title="'Chia sẻ link CV cho người khác'"
            >
              <span class="material-symbols-outlined text-[18px]">share</span>
              Chia sẻ
            </button>

            <button 
              class="flex items-center gap-1.5 px-3 py-1.5 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold text-text-main dark:text-white hover:bg-slate-50 dark:hover:bg-slate-800 transition-all bg-white dark:bg-slate-900 shadow-sm cursor-pointer"
              type="button"
              :disabled="viewingCvId === cv.id"
              @click="handleViewCv(cv)"
            >
              <span class="material-symbols-outlined text-[18px]" :class="{ 'animate-spin': viewingCvId === cv.id }">
                {{ viewingCvId === cv.id ? 'progress_activity' : 'visibility' }}
              </span>
              {{ viewingCvId === cv.id ? 'Dang tao PDF' : 'Xem' }}
            </button>

            <button
              v-if="cv.cvType === CV_TYPE.ONLINE"
              class="flex items-center gap-1.5 px-3 py-1.5 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold text-text-main dark:text-white hover:bg-slate-50 dark:hover:bg-slate-800 transition-all bg-white dark:bg-slate-900 shadow-sm cursor-pointer"
              type="button"
              @click="handleEditCv(cv)"
            >
              <span class="material-symbols-outlined text-[18px]">edit_square</span>
              Sua
            </button>

            <button
              v-if="cv.cvType === CV_TYPE.ONLINE"
              class="flex items-center gap-1.5 px-3 py-1.5 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold text-text-main dark:text-white hover:bg-slate-50 dark:hover:bg-slate-800 transition-all bg-white dark:bg-slate-900 shadow-sm cursor-pointer disabled:opacity-60"
              type="button"
              :disabled="duplicatingCvId === cv.id"
              @click="handleDuplicateCv(cv)"
            >
              <span class="material-symbols-outlined text-[18px]">
                {{ duplicatingCvId === cv.id ? 'progress_activity' : 'content_copy' }}
              </span>
              Nhan ban
            </button>

            <button
              class="flex items-center gap-1.5 px-3 py-1.5 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold text-text-main dark:text-white hover:bg-slate-50 dark:hover:bg-slate-800 transition-all bg-white dark:bg-slate-900 shadow-sm cursor-pointer"
              type="button"
              :disabled="downloadingCvId === cv.id"
              @click="handleDownloadCv(cv)"
            >
              <span class="material-symbols-outlined text-[18px]" :class="{ 'animate-spin': downloadingCvId === cv.id }">
                {{ downloadingCvId === cv.id ? 'progress_activity' : 'download' }}
              </span>
              Tải về
            </button>

            <button 
              class="flex items-center gap-1.5 px-3 py-1.5 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-bold text-text-main dark:text-white hover:bg-red-50 hover:text-red-500 hover:border-red-100 dark:hover:bg-red-900/20 transition-all bg-white dark:bg-slate-900 shadow-sm cursor-pointer"
              @click="handleDelete(cv)"
            >
              <span class="material-symbols-outlined text-[18px]">delete</span>
              Xóa
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <GlobalModal
      :visible="renameModal.show"
      title="Đổi tên CV"
      subtitle="Nhập tên mới cho bản CV này"
      icon="edit_note"
      max-width="md"
      @close="renameModal.show = false"
      @confirm="submitRename"
      :loading="renaming"
    >
      <div class="space-y-4">
        <div class="space-y-2">
          <label class="text-sm font-semibold text-text-main dark:text-white">Tên CV</label>
          <input
            v-model="renameModal.newTitle"
            type="text"
            placeholder="VD: CV Kỹ sư phần mềm - 2024"
            class="w-full px-4 py-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-2xl outline-none focus:border-primary transition-all text-sm"
            @keyup.enter="submitRename"
          />
        </div>
      </div>
    </GlobalModal>

    <GlobalConfirmModal />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCvsStore } from '@/stores/cvs.store'
import { CV_TYPE, CV_VISIBILITY } from '@/constants/cvs.constants'
import { useConfirm } from '@/composables/useConfirm'
import { useToast } from '@/composables/useToast'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import GlobalConfirmModal from '@/components/ui/GlobalConfirmModal.vue'
import type { ResCv } from '@/types/cvs.types'

const cvStore = useCvsStore()
const router = useRouter()
const { confirm } = useConfirm()
const toast = useToast()

const cvs = computed(() => cvStore.cvs)
const loading = computed(() => cvStore.loading)

const cvsStore = cvStore
const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const actionLoading = ref<number | null>(null)
const duplicatingCvId = ref<number | null>(null)
const viewingCvId = ref<number | null>(null)
const downloadingCvId = ref<number | null>(null)

// ─── Formatting ──────────────────────────────────────────────────────────────
function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date)
}

// ─── Actions ──────────────────────────────────────────────────────────────────
function triggerUpload() {
  fileInput.value?.click()
}

async function onFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return

  // Basic validation
  if (file.size > 5 * 1024 * 1024) {
    alert('File quá lớn. Vui lòng chọn file dưới 5MB.')
    return
  }

  uploading.value = true
  try {
    await cvsStore.uploadCv(file, { title: file.name, isDefault: cvs.value.length === 0 })
    toast.success('Tải lên thành công', `Đã thêm CV "${file.name}" vào danh sách.`)
    if (fileInput.value) fileInput.value.value = ''
  } catch (err: any) {
    toast.error('Tải lên thất bại', err.response?.data?.message || 'Có lỗi xảy ra khi tải CV.')
  } finally {
    uploading.value = false
  }
}

async function handleDelete(cv: ResCv) {
  const ok = await confirm({
    title: 'Xóa CV?',
    message: `Bạn có chắc chắn muốn xóa "${cv.title}"? Thao tác này không thể hoàn tác.`,
    confirmText: 'Xóa ngay',
    cancelText: 'Hủy',
    confirmColor: 'red',
    icon: 'delete_forever'
  })

  if (ok) {
    try {
      await cvsStore.deleteCv(cv.id)
      toast.success('Đã xóa CV', `CV "${cv.title}" đã được gỡ khỏi hệ thống.`)
    } catch (err: any) {
      toast.error('Xóa thất bại', err.response?.data?.message || 'Không thể xóa CV này.')
    }
  }
}

async function toggleVisibility(cv: ResCv) {
  actionLoading.value = cv.id
  try {
    const newVisibility = cv.visibility === CV_VISIBILITY.PUBLIC 
      ? CV_VISIBILITY.PRIVATE 
      : CV_VISIBILITY.PUBLIC
    
    await cvsStore.shareCv(cv.id, { visibility: newVisibility })
    toast.success(
      newVisibility === CV_VISIBILITY.PUBLIC ? 'Đã bật tìm việc' : 'Đã tắt tìm việc',
      newVisibility === CV_VISIBILITY.PUBLIC 
        ? 'NTT hiện có thể tìm thấy CV này.' 
        : 'Chỉ bạn mới có thể thấy CV này.'
    )
  } catch (err: any) {
    toast.error('Thao tác thất bại', err.response?.data?.message || 'Không thể cập nhật quyền riêng tư.')
  } finally {
    actionLoading.value = null
  }
}

// ─── Rename Logic ─────────────────────────────────────────────────────────────
const renameModal = reactive({
  show: false,
  cvId: null as number | null,
  newTitle: '',
})
const renaming = ref(false)

function openRenameModal(cv: ResCv) {
  renameModal.cvId = cv.id
  renameModal.newTitle = cv.title
  renameModal.show = true
}

async function submitRename() {
  if (!renameModal.cvId || !renameModal.newTitle.trim()) return
  
  renaming.value = true
  try {
    await cvsStore.renameCv(renameModal.cvId, { title: renameModal.newTitle.trim() })
    toast.success('Đã đổi tên', 'Tên CV đã được cập nhật.')
    renameModal.show = false
  } catch (err: any) {
    toast.error('Đổi tên thất bại', err.response?.data?.message || 'Không thể đổi tên CV.')
  } finally {
    renaming.value = false
  }
}

async function setDefault(cv: ResCv) {
  try {
    await cvsStore.setDefaultCv(cv.id)
    toast.success('Đã đặt làm CV chính', `CV "${cv.title}" hiện là CV mặc định của bạn.`)
  } catch (err: any) {
    toast.error('Thao tác thất bại', err.response?.data?.message || 'Không thể đặt CV mặc định.')
  }
}

function getOnlinePdfStatusLabel(cv: ResCv) {
  if (cv.pdfDirty) return 'PDF can cap nhat'
  if (cv.pdfUrl) return 'PDF moi nhat'
  return 'Chua tao PDF'
}

async function handleViewCv(cv: ResCv) {
  if (cv.cvType !== CV_TYPE.ONLINE) {
    const previewUrl = cv.fileUrl || cv.pdfUrl
    if (previewUrl) {
      window.open(previewUrl, '_blank', 'noopener,noreferrer')
    } else {
      toast.error('Khong mo duoc CV', 'CV nay hien khong co duong dan xem truoc hop le.')
    }
    return
  }

  viewingCvId.value = cv.id
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
      window.open(exported.pdfUrl, '_blank', 'noopener,noreferrer')
    }
  } catch (err: any) {
    previewWindow?.close()
    toast.error('Khong mo duoc CV', err?.response?.data?.message || 'Khong the tao ban PDF moi luc nay.')
  } finally {
    viewingCvId.value = null
  }
}

function handleEditCv(cv: ResCv) {
  void router.push({ name: 'CvOnlineEditorLegacy', params: { id: cv.id } })
}

async function handleDuplicateCv(cv: ResCv) {
  duplicatingCvId.value = cv.id
  try {
    const duplicated = await cvsStore.duplicateCv(cv.id)
    toast.success('Da nhan ban CV online', `Da tao ban sao "${duplicated?.title ?? cv.title}".`)
  } catch (err: any) {
    toast.error('Khong nhan ban duoc CV', err.response?.data?.message || 'Vui long thu lai sau.')
  } finally {
    duplicatingCvId.value = null
  }
}

async function handleDownloadCv(cv: ResCv) {
  if (cv.cvType === CV_TYPE.ONLINE) {
    downloadingCvId.value = cv.id
    try {
      await cvsStore.exportPdf(cv.id)
      const blob = await cvsStore.downloadPdf(cv.id)
      const url = window.URL.createObjectURL(blob)
      const anchor = document.createElement('a')
      anchor.href = url
      anchor.download = `${cv.title || 'cv-online'}.pdf`
      document.body.appendChild(anchor)
      anchor.click()
      anchor.remove()
      window.URL.revokeObjectURL(url)
    } catch (err: any) {
      toast.error('Khong tai duoc PDF', err?.response?.data?.message || 'Khong the tai file PDF luc nay.')
    } finally {
      downloadingCvId.value = null
    }
    return
  }

  if (cv.fileUrl) {
    const anchor = document.createElement('a')
    anchor.href = cv.fileUrl
    anchor.download = cv.title
    document.body.appendChild(anchor)
    anchor.click()
    anchor.remove()
    return
  }

  toast.error('Khong tai duoc CV', 'CV nay hien khong co duong dan tai ve hop le.')
}

async function copyUrl(url: string) {
  try {
    await navigator.clipboard.writeText(url)
    toast.success('Đã sao chép', 'Đã lưu liên kết vào bộ nhớ tạm.')
  } catch (err) {
    toast.error('Lỗi sao chép', 'Không thể truy cập bộ nhớ tạm.')
  }
}

void copyUrl
</script>
