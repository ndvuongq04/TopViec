<template>
  <section class="cv-preview-panel">
    <!-- Breadcrumb & Actions -->
    <div class="panel-header">
      <Breadcrumb :items="breadcrumbItems" :hide-home="true" />
      <div class="panel-actions">
        <button class="btn-action" :disabled="!downloadUrl" @click="handleDownload">
          <span class="material-symbols-outlined">download</span>
          Tải CV
        </button>
        <button class="btn-action" :disabled="!pdfSrc" @click="handlePrint">
          <span class="material-symbols-outlined">print</span>
          In
        </button>
      </div>
    </div>

    <!-- PDF Viewer -->
    <div class="pdf-viewport">
      <!-- No CV -->
      <div v-if="!pdfSrc" class="pdf-state">
        <span class="material-symbols-outlined pdf-state__icon">description</span>
        <p>Ứng viên chưa đính kèm CV</p>
      </div>

      <template v-else>
        <!-- PDF iframe — luôn render để @load hoạt động -->
        <iframe
          :src="pdfSrc"
          class="pdf-iframe"
          frameborder="0"
          @load="loading = false"
          @error="onIframeError"
        />

        <!-- Loading overlay phủ lên iframe, ẩn khi load xong -->
        <div v-if="loading && !iframeError" class="pdf-overlay">
          <span class="material-symbols-outlined loading-spin">progress_activity</span>
          <p>Đang tải CV...</p>
        </div>

        <!-- Fallback khi iframe lỗi -->
        <div v-if="iframeError" class="pdf-fallback">
          <span class="material-symbols-outlined" style="font-size:3rem;color:#94a3b8">broken_image</span>
          <p>Không thể hiển thị PDF trong trình duyệt.</p>
          <a :href="downloadUrl || pdfSrc" target="_blank" class="btn-open-pdf">
            <span class="material-symbols-outlined">open_in_new</span>
            Mở CV trong tab mới
          </a>
        </div>
      </template>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'

const props = defineProps<{
  candidateName: string
  cvPreviewUrl?: string
  cvPdfUrl?: string
  cvFileUrl?: string
}>()

defineEmits<{
  download: []
  print: []
}>()

const route = useRoute()
const loading     = ref(true)
const iframeError = ref(false)

const breadcrumbItems = computed(() => [
  {
    label: 'Danh sách ứng viên',
    to: `/recruiter/jobs/${route.params.id}/applications`,
  },
  { label: props.candidateName || '...' },
])

// Dùng pdfUrl nếu có, fallback sang fileUrl
const pdfSrc = computed(() => props.cvPreviewUrl || props.cvPdfUrl || props.cvFileUrl || '')
const downloadUrl = computed(() => props.cvFileUrl || props.cvPreviewUrl || props.cvPdfUrl || '')

// Reset khi URL thay đổi
watch(pdfSrc, () => {
  loading.value = true
  iframeError.value = false
})

function onIframeError() {
  loading.value = false
  iframeError.value = true
}

function handleDownload() {
  if (downloadUrl.value) window.open(downloadUrl.value, '_blank')
}

function handlePrint() {
  if (pdfSrc.value) {
    const iframe = document.querySelector('.pdf-iframe') as HTMLIFrameElement
    iframe?.contentWindow?.print()
  }
}
</script>

<style scoped>
.cv-preview-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  min-height: 0;
}

/* Header */
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.panel-actions {
  display: flex;
  gap: 0.5rem;
}
.btn-action {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #475569;
  font-size: 0.875rem;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-action:hover:not(:disabled) { background: #f8fafc; }
.btn-action:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-action .material-symbols-outlined { font-size: 1.125rem; }

/* PDF Viewport */
.pdf-viewport {
  position: relative;
  flex: 1;
  min-height: 600px;
  border-radius: 0.75rem;
  border: 1px solid #e2e7f0;
  background: #525659;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pdf-iframe {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  border: none;
  background: #525659;
}

/* States */
.pdf-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  color: #94a3b8;
}
.pdf-state p { font-size: 0.9375rem; }
.pdf-state__icon { font-size: 3rem !important; }

@keyframes spin { to { transform: rotate(360deg); } }
.loading-spin {
  font-size: 2rem !important;
  animation: spin 0.8s linear infinite;
  color: #fff;
}

/* Loading overlay */
.pdf-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  background: #525659;
  color: #cbd5e1;
  pointer-events: none;
}
.pdf-overlay p { font-size: 0.9375rem; }

/* Fallback */
.pdf-fallback {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  background: #f8fafc;
  color: #64748b;
}
.pdf-fallback p { font-size: 0.9375rem; }

.btn-open-pdf {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1.25rem;
  border-radius: 0.5rem;
  background: #4B9AF6;
  color: #fff;
  font-size: 0.875rem;
  font-weight: 700;
  text-decoration: none;
  transition: background 0.15s;
}
.btn-open-pdf:hover { background: #2563eb; }
</style>
