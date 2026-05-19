<template>
  <div class="grow bg-slate-50/50 dark:bg-slate-900/50">
    <CvTemplateHero />
    <CvTemplateFilterTabs
      v-model:active-filter="activeFilter"
      v-model:active-language="activeLanguage"
      :filters="filterTabs"
      :languages="languages"
    />
    <main class="max-w-[1200px] mx-auto px-6 py-12">
      <div v-if="templateStore.error" class="mb-6 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
        {{ templateStore.error }}
      </div>
      <CvTemplateGrid
        :templates="filteredTemplates"
        :has-more="false"
        @use="handleUseTemplate"
      />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import CvTemplateHero from '@/components/candidate/cv-templates/CvTemplateHero.vue'
import CvTemplateFilterTabs from '@/components/candidate/cv-templates/CvTemplateFilterTabs.vue'
import CvTemplateGrid from '@/components/candidate/cv-templates/CvTemplateGrid.vue'
import { useCvOnlineEditorStore } from '@/stores/cvOnlineEditor.store'
import { useCvTemplateStore } from '@/stores/cvTemplate.store'
import { useToast } from '@/composables/useToast'

const activeFilter = ref('all')
const activeLanguage = ref('vi')
const router = useRouter()
const toast = useToast()
const editorStore = useCvOnlineEditorStore()
const templateStore = useCvTemplateStore()

const filterTabs = [
  { value: 'all', label: 'Tat ca', icon: 'grid_view' },
  { value: 'simple', label: 'Don gian', icon: 'check_circle' },
  { value: 'professional', label: 'Chuyen nghiep', icon: 'work' },
  { value: 'modern', label: 'Hien dai', icon: 'bolt' },
  { value: 'impressive', label: 'An tuong', icon: 'star' },
  { value: 'harvard', label: 'Harvard', icon: 'school' },
  { value: 'ats', label: 'ATS', icon: 'fact_check' },
]

const languages = [
  { value: 'vi', label: 'Tieng Viet', color: '#ba1a1a' },
  { value: 'en', label: 'Tieng Anh', color: '#005ea4' },
]

const templates = computed(() => templateStore.candidateTemplates)

const filteredTemplates = computed(() => {
  return templates.value.filter((template) => {
    const matchesFilter =
      activeFilter.value === 'all' || template.categories.includes(activeFilter.value)
    const matchesLanguage = template.language === activeLanguage.value
    return matchesFilter && matchesLanguage
  })
})

onMounted(() => {
  void templateStore.fetchPublicTemplates()
})

async function handleUseTemplate(id: number) {
  try {
    const draft = await editorStore.createLocalDraftFromTemplate(id)
    router.push({ name: 'CvOnlineEditor', params: { localDraftId: draft.localDraftId } })
  } catch {
    toast.error('Khong tao duoc ban nhap CV', templateStore.error ?? editorStore.error ?? undefined)
  }
}
</script>

<style scoped>
/* Page layout is standardized using Tailwind utility classes */
</style>
