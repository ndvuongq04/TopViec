<template>
  <div class="space-y-10">
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.875rem] font-black text-slate-900 dark:text-slate-100 tracking-tight">Quan ly mau CV</h2>
        <p class="text-slate-500 text-[1rem] mt-1">Danh sach template CV online dang duoc quan ly boi backend phase 1.</p>
      </div>
      <div class="flex gap-3 items-center shrink-0">
        <button
          class="flex items-center gap-2 px-5 py-2.5 rounded-lg text-[1rem] font-bold bg-[#963131] hover:bg-[#963131]/90 text-white transition-all shadow-sm cursor-pointer"
          @click="handleAdd"
        >
          <span class="material-symbols-outlined text-[20px]">add</span>
          Them mau CV
        </button>
      </div>
    </div>

    <CvTemplateKpiCards :stats="templateStore.adminKpis" />
    <CvTemplateFilters v-model="filters" class="mt-14" />

    <div v-if="templateStore.error" class="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
      {{ templateStore.error }}
    </div>

    <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
      <div class="relative">
        <CvTemplateTable
          :templates="filteredTemplates"
          @preview="handlePreview"
          @edit="handleEdit"
          @archive="handleArchive"
        />

        <CvTemplatePagination
          :current="currentPage"
          :total="templateStore.adminMeta.totals"
          :per-page="perPage"
          @change="handlePageChange"
        />
      </div>
    </div>

    <GlobalModal
      :visible="previewModal.open"
      :title="previewModal.title || 'Preview template'"
      :subtitle="previewModal.versionTag ? `Version: <code>${previewModal.versionTag}</code>` : 'Preview CV voi sample data tu backend'"
      icon="visibility"
      max-width="5xl"
      :show-default-footer="false"
      @close="closePreviewModal"
    >
      <div class="space-y-5">
        <div
          v-if="templateStore.previewing"
          class="flex h-[70vh] items-center justify-center rounded-2xl border border-slate-200 bg-slate-50 text-slate-500"
        >
          <div class="flex flex-col items-center gap-3">
            <span class="material-symbols-outlined animate-spin text-3xl">progress_activity</span>
            <p class="text-sm font-medium">Dang render preview tu backend...</p>
          </div>
        </div>

        <iframe
          v-else-if="previewDocument"
          :srcdoc="previewDocument"
          class="h-[70vh] w-full rounded-2xl border border-slate-200 bg-white"
          sandbox="allow-same-origin"
          title="Admin template preview"
        />

        <div
          v-else
          class="flex h-[70vh] items-center justify-center rounded-2xl border border-amber-200 bg-amber-50 px-6 text-center text-amber-700"
        >
          <div class="flex max-w-md flex-col items-center gap-3">
            <span class="material-symbols-outlined text-4xl">warning</span>
            <p class="text-sm font-medium">{{ templateStore.error || 'Khong tao duoc preview cho template nay.' }}</p>
          </div>
        </div>

        <div class="grid gap-4 lg:grid-cols-2">
          <div class="rounded-2xl border border-rose-200 bg-rose-50 p-4">
            <div class="flex items-center gap-2">
              <span class="material-symbols-outlined text-[18px] text-rose-600">error</span>
              <p class="text-sm font-bold text-rose-700">Loi placeholder</p>
            </div>
            <ul v-if="previewResult?.placeholderErrors.length" class="mt-3 space-y-2 text-sm text-rose-700">
              <li v-for="item in previewResult.placeholderErrors" :key="item">{{ item }}</li>
            </ul>
            <p v-else class="mt-3 text-sm text-emerald-700">Khong phat hien loi placeholder.</p>
          </div>

          <div class="rounded-2xl border border-amber-200 bg-amber-50 p-4">
            <div class="flex items-center gap-2">
              <span class="material-symbols-outlined text-[18px] text-amber-600">warning</span>
              <p class="text-sm font-bold text-amber-700">Canh bao CSS</p>
            </div>
            <ul v-if="previewResult?.cssWarnings.length" class="mt-3 space-y-2 text-sm text-amber-700">
              <li v-for="item in previewResult.cssWarnings" :key="item">{{ item }}</li>
            </ul>
            <p v-else class="mt-3 text-sm text-emerald-700">Khong co canh bao CSS dang luu y.</p>
          </div>
        </div>
      </div>
    </GlobalModal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import CvTemplateKpiCards from '@/components/admin/cv-templates/CvTemplateKpiCards.vue'
import CvTemplateFilters from '@/components/admin/cv-templates/CvTemplateFilters.vue'
import CvTemplateTable from '@/components/admin/cv-templates/CvTemplateTable.vue'
import CvTemplatePagination from '@/components/admin/cv-templates/CvTemplatePagination.vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { useCvTemplateStore } from '@/stores/cvTemplate.store'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const toast = useToast()
const templateStore = useCvTemplateStore()
const currentPage = ref(1)
const perPage = ref(10)
const filters = ref({ search: '', status: '', tier: '', sort: 'createdAt,desc' })
const previewModal = ref({
  open: false,
  title: '',
  versionTag: null as string | null,
})

const previewResult = computed(() => templateStore.adminTemplatePreview)
const previewDocument = computed(() => {
  return previewResult.value?.renderedHtml ?? ''
})

const filteredTemplates = computed(() => {
  return templateStore.adminRows.filter((template) => {
    const matchSearch =
      !filters.value.search ||
      template.name.toLowerCase().includes(filters.value.search.toLowerCase()) ||
      template.code.toLowerCase().includes(filters.value.search.toLowerCase())
    const matchStatus = !filters.value.status || template.status === filters.value.status
    const matchTier = !filters.value.tier || template.tier === filters.value.tier
    return matchSearch && matchStatus && matchTier
  })
})

async function fetchTemplates() {
  await templateStore.fetchAdminTemplates({
    page: currentPage.value - 1,
    size: perPage.value,
    keyword: filters.value.search || undefined,
    sort: filters.value.sort,
  })
}

onMounted(() => {
  void fetchTemplates()
})

watch(
  () => [filters.value.search, filters.value.sort],
  () => {
    currentPage.value = 1
    void fetchTemplates()
  },
)

function handlePageChange(page: number) {
  currentPage.value = page
  void fetchTemplates()
}

function handleAdd() {
  router.push({ name: 'admin-cv-template-create' })
}

async function handlePreview(id: number) {
  previewModal.value.open = true
  try {
    if (!templateStore.adminTemplateSampleData) {
      await templateStore.fetchAdminTemplateSampleData()
    }
    const template = await templateStore.fetchAdminTemplateById(id)
    previewModal.value.title = template.name
    previewModal.value.versionTag = template.versionTag ?? null
    await templateStore.previewAdminTemplate({
      templateId: template.id,
      htmlContent: template.htmlContent,
      cssContent: template.cssContent,
    })
    previewModal.value.versionTag = templateStore.adminTemplatePreview?.versionTag ?? template.versionTag ?? null
  } catch {
    toast.error('Khong mo duoc preview', templateStore.error ?? undefined)
  }
}

function handleEdit(id: number) {
  router.push({ name: 'admin-cv-template-edit', params: { id } })
}

function closePreviewModal() {
  previewModal.value.open = false
}

async function handleArchive(id: number) {
  try {
    const row = templateStore.adminRows.find((item) => item.id === id)
    if (!row) return
    if (row.isDefault) {
      toast.warning('Khong the tam ngung template mac dinh')
      return
    }
    if (row.status === 'active') {
      await templateStore.deactivateTemplate(id)
      toast.success('Da tam ngung template')
    } else {
      await templateStore.activateTemplate(id)
      toast.success('Da kich hoat template')
    }
    await fetchTemplates()
  } catch {
    toast.error('Khong cap nhat duoc trang thai', templateStore.error ?? undefined)
  }
}
</script>
