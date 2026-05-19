<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCvsStore } from '@/stores/cvs.store'
import { useApplicationStore } from '@/stores/application.store'
import { useToast } from '@/composables/useToast'

interface Props {
  show: boolean
  applicationId: number
  currentCvId?: number
  jobTitle?: string
}

const props = defineProps<Props>()
const emit = defineEmits(['close', 'success'])

const cvsStore = useCvsStore()
const applicationStore = useApplicationStore()
const toast = useToast()

const selectedCvId = ref<number | null>(props.currentCvId || null)
const isUpdating = ref(false)
const isUploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

onMounted(async () => {
  if (cvsStore.cvs.length === 0) {
    await cvsStore.fetchMyCvs()
  }
  if (!selectedCvId.value && props.currentCvId) {
    selectedCvId.value = props.currentCvId
  }
})

function handleFileSelect(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    uploadCv(target.files[0])
  }
}

async function uploadCv(file: File) {
  isUploading.value = true
  try {
    const res = await cvsStore.uploadCv(file, { title: file.name })
    toast.success('Thành công', 'Đã tải CV mới lên')
    if (res && res.id) {
       selectedCvId.value = res.id
    } else if (cvsStore.cvs.length > 0) {
      selectedCvId.value = cvsStore.cvs[0].id
    }
  } catch (err) {
    toast.error('Lỗi', 'Không thể tải CV lên')
  } finally {
    isUploading.value = false
  }
}

async function handleConfirm() {
  if (!selectedCvId.value) {
    toast.warning('Chú ý', 'Vui lòng chọn CV mới')
    return
  }
  
  if (selectedCvId.value === props.currentCvId) {
    emit('close')
    return
  }

  isUpdating.value = true
  try {
    await applicationStore.updateApplicationCv(props.applicationId, selectedCvId.value)
    toast.success('Thành công', 'Đã thay đổi CV ứng tuyển')
    emit('success')
    emit('close')
  } catch (err) {
    toast.error('Lỗi', 'Không thể thay đổi CV lúc này')
  } finally {
    isUpdating.value = false
  }
}

function formatUpdateTime(dateStr: string) {
  if (!dateStr) return 'N/A'
  const date = new Date(dateStr)
  return date.toLocaleDateString('vi-VN')
}
</script>

<template>
  <Transition name="fade">
    <div v-if="show" class="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <div class="bg-white dark:bg-surface-dark w-full max-w-lg rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden flex flex-col animate-in fade-in zoom-in duration-300">
        <!-- Header -->
        <div class="px-6 py-5 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
          <div>
            <h3 class="text-lg font-bold text-text-main dark:text-white">Thay đổi CV ứng tuyển</h3>
            <p v-if="jobTitle" class="text-xs text-text-muted mt-0.5 line-clamp-1">Vị trí: {{ jobTitle }}</p>
          </div>
          <button @click="emit('close')" class="w-8 h-8 flex items-center justify-center rounded-full hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
            <span class="material-symbols-outlined text-text-main dark:text-white">close</span>
          </button>
        </div>

        <!-- Body -->
        <div class="p-6 overflow-y-auto max-h-[60vh] custom-scrollbar">
          <div class="flex items-center justify-between mb-4">
            <h5 class="text-xs font-bold text-text-muted uppercase tracking-widest px-1">Danh sách CV của bạn</h5>
            <button 
              @click="fileInput?.click()"
              :disabled="isUploading"
              class="text-xs font-bold text-primary hover:underline flex items-center gap-1.5"
            >
              <span class="material-symbols-outlined text-[18px]">cloud_upload</span>
              {{ isUploading ? 'Đang tải...' : 'Tải CV mới' }}
            </button>
          </div>

          <div class="space-y-3">
            <div 
              v-for="cv in cvsStore.cvs" 
              :key="cv.id"
              @click="selectedCvId = cv.id"
              class="group p-4 rounded-2xl border-2 transition-all cursor-pointer flex items-center gap-3"
              :class="selectedCvId === cv.id 
                ? 'border-primary bg-primary/[0.02]' 
                : 'border-slate-100 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700 bg-white dark:bg-slate-800/40'"
            >
              <div 
                class="w-5 h-5 rounded-full border-2 flex items-center justify-center shrink-0"
                :class="selectedCvId === cv.id ? 'border-primary bg-primary text-white' : 'border-slate-300 dark:border-slate-600'"
              >
                <span v-if="selectedCvId === cv.id" class="material-symbols-outlined text-[12px] font-bold">check</span>
              </div>
              
              <div class="flex-grow min-w-0 flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-slate-100 dark:bg-slate-700 flex items-center justify-center text-slate-400 group-hover:text-primary transition-colors">
                  <span class="material-symbols-outlined text-[18px]">description</span>
                </div>
                <div class="min-w-0">
                  <h6 class="text-sm font-bold text-text-main dark:text-white truncate">{{ cv.title }}</h6>
                  <p class="text-[10px] text-text-muted mt-0.5">Cập nhật: {{ formatUpdateTime(cv.updatedAt) }}</p>
                </div>
              </div>

              <div v-if="cv.id === props.currentCvId" class="shrink-0 px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-700 text-[10px] font-bold text-text-muted">
                Hiện tại
              </div>
            </div>

            <div v-if="cvsStore.cvs.length === 0" class="py-12 text-center bg-slate-50 dark:bg-slate-800/20 rounded-3xl border-2 border-dashed border-slate-200 dark:border-slate-700">
               <span class="material-symbols-outlined text-slate-300 text-4xl mb-2">folder_off</span>
               <p class="text-sm font-bold text-text-muted">Bạn chưa có CV nào</p>
               <p class="text-xs text-text-muted mt-1">Hãy tải CV lên để tiếp tục</p>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-6 bg-slate-50 dark:bg-slate-800/30 border-t border-slate-100 dark:border-slate-800 flex gap-3">
          <button 
            @click="emit('close')"
            class="flex-1 h-12 rounded-xl text-sm font-bold text-text-main dark:text-white hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
          >
            Hủy
          </button>
          <button 
            @click="handleConfirm"
            :disabled="!selectedCvId || selectedCvId === props.currentCvId || isUpdating || isUploading"
            class="flex-1 h-12 bg-primary hover:bg-primary-hover disabled:opacity-50 text-white rounded-xl font-bold text-sm shadow-lg shadow-primary/20 transition-all flex items-center justify-center gap-2"
          >
            <span v-if="isUpdating" class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
            {{ isUpdating ? 'Đang cập nhật...' : 'Xác nhận đổi' }}
          </button>
        </div>
        
        <input type="file" ref="fileInput" class="hidden" accept=".pdf,.doc,.docx" @change="handleFileSelect">
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>
