<template>
  <div class="min-h-screen bg-[#eef2f7]">
    <div class="mx-auto flex max-w-[1320px] flex-col gap-5 px-4 py-6 lg:px-6">
      <header class="rounded-[24px] border border-white/70 bg-white/92 px-5 py-5 shadow-[0_20px_60px_rgba(148,163,184,0.18)] backdrop-blur lg:px-7">
        <div class="flex flex-col gap-5 xl:flex-row xl:items-center xl:justify-between">
          <div class="min-w-0 flex-1">
            <p class="text-xs font-black uppercase tracking-[0.18em] text-orange-600">CV Online Editor</p>
            <input
              :value="currentCv?.title ?? ''"
              class="mt-2 w-full border-none bg-transparent p-0 text-3xl font-black tracking-tight text-slate-900 outline-none"
              placeholder="Ten CV"
              @input="editorStore.patchDraftTitle(($event.target as HTMLInputElement).value)"
            />
          </div>

          <div class="grid gap-3 md:grid-cols-3 xl:min-w-[640px]">
            <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <p class="text-xs font-bold uppercase tracking-[0.14em] text-slate-500">Trang thai</p>
              <p class="mt-1 text-sm font-bold text-slate-900">{{ editorStore.saveStateLabel }}</p>
            </div>
            <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <p class="text-xs font-bold uppercase tracking-[0.14em] text-slate-500">Template</p>
              <p class="mt-1 truncate text-sm font-bold text-slate-900">{{ currentCv?.template?.name ?? 'Unknown' }}</p>
            </div>
            <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <p class="text-xs font-bold uppercase tracking-[0.14em] text-slate-500">PDF</p>
              <p class="mt-1 text-sm font-bold text-slate-900">{{ editorStore.pdfStateLabel }}</p>
            </div>
          </div>
        </div>
      </header>

      <div v-if="editorStore.error" class="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
        {{ editorStore.error }}
      </div>

      <div v-if="editorStore.loading && !currentCv" class="rounded-[24px] border border-slate-200 bg-white px-5 py-10 text-center text-slate-500">
        Dang tai du lieu editor...
      </div>

      <div v-else-if="currentCv" class="space-y-5">
        <section class="rounded-[22px] border border-slate-200 bg-white px-5 py-4 shadow-sm">
          <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div class="grid gap-3 text-sm text-slate-600 sm:grid-cols-2 lg:grid-cols-4">
              <p><strong class="text-slate-900">CV ID:</strong> {{ currentCv.id ?? 'Local only' }}</p>
              <p><strong class="text-slate-900">Tai luc:</strong> {{ loadedLabel }}</p>
              <p><strong class="text-slate-900">Draft:</strong> {{ currentCv.localDraftId }}</p>
              <p><strong class="text-slate-900">Slug:</strong> {{ currentCv.template.slug }}</p>
            </div>

            <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
              <select
                class="tv-select"
                :value="currentCv.templateId"
                :disabled="editorStore.switchingTemplate"
                @change="handleTemplateChange($event)"
              >
                <option v-for="template in templateStore.publicTemplates" :key="template.id" :value="template.id">
                  {{ template.name }}
                </option>
              </select>
              <button class="tv-chip" type="button" :disabled="editorStore.saving" @click="handleManualSave()">
                Luu ngay
              </button>
              <button
                class="tv-chip tv-chip--ghost"
                type="button"
                :disabled="!currentCv.id"
                @click="handleDownloadPdf()"
              >
                Tai PDF
              </button>
            </div>
          </div>

          <p v-if="editorStore.pdfError" class="mt-3 text-sm font-semibold text-rose-600">
            {{ editorStore.pdfError }}
          </p>
        </section>

        <CvEditablePreview
          :template="currentCv.template"
          :data="extraData"
        />
      </div>

      <div v-else class="rounded-[24px] border border-amber-200 bg-amber-50 px-5 py-4 text-amber-800">
        Khong tim thay draft hop le cho route nay.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted } from 'vue'
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import CvEditablePreview from '@/components/candidate/cv-online/CvEditablePreview.vue'
import { useToast } from '@/composables/useToast'
import { useCvOnlineEditorStore } from '@/stores/cvOnlineEditor.store'
import { useCvTemplateStore } from '@/stores/cvTemplate.store'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const editorStore = useCvOnlineEditorStore()
const templateStore = useCvTemplateStore()
let skipNextLocalFlush = false

const currentCv = computed(() => editorStore.currentCv)
const extraData = computed(() => editorStore.extraData)
const loadedLabel = computed(() => {
  if (!editorStore.lastLoadedAt) return 'Chua tai'
  return new Date(editorStore.lastLoadedAt).toLocaleTimeString('vi-VN')
})

onMounted(async () => {
  try {
    await templateStore.fetchPublicTemplates()

    const localDraftId = typeof route.params.localDraftId === 'string' ? route.params.localDraftId : null
    if (localDraftId) {
      await editorStore.loadDraftByLocalId(localDraftId)
      window.addEventListener('beforeunload', handleBeforeUnload)
      window.addEventListener('storage', handleStorageConflict)
      return
    }

    const id = Number(route.params.id)
    if (!(Number.isFinite(id) && id > 0)) return

    const draft = await editorStore.bootstrapDraftFromServerId(id)
    await router.replace({ name: 'CvOnlineEditor', params: { localDraftId: draft.localDraftId } })
    window.addEventListener('beforeunload', handleBeforeUnload)
    window.addEventListener('storage', handleStorageConflict)
  } catch {
    toast.error('Khong tai duoc editor CV', editorStore.error ?? templateStore.error ?? undefined)
  }
})

onBeforeRouteLeave(() => {
  if (skipNextLocalFlush) {
    return
  }
  editorStore.flushLocalDraft()
})

onBeforeUnmount(() => {
  if (!skipNextLocalFlush) {
    editorStore.flushLocalDraft()
  } else {
    skipNextLocalFlush = false
  }
  window.removeEventListener('beforeunload', handleBeforeUnload)
  window.removeEventListener('storage', handleStorageConflict)
})

async function handleManualSave() {
  try {
    const savedDraft = await editorStore.saveDraftNow()
    if (savedDraft?.serverId) {
      skipNextLocalFlush = true
      await router.replace({ name: 'CandidateProfile', query: { tab: 'cvs' } })
    }
    toast.success('Da luu CV online', 'PDF se duoc tao/cap nhat khi ban xem hoac tai.')
  } catch {
    toast.error('Khong luu duoc CV online', editorStore.error ?? undefined)
  }
}

async function handleDownloadPdf() {
  if (!currentCv.value?.id) {
    toast.warning('Chua the tai PDF', 'Ban can luu CV len he thong truoc.')
    return
  }

  try {
    if (editorStore.hasPendingChanges) {
      await editorStore.saveDraftNow()
    }
    await editorStore.downloadPdf()
    toast.success('Dang tai PDF')
  } catch {
    toast.error('Khong tai duoc PDF', editorStore.pdfError ?? undefined)
  }
}

async function handleTemplateChange(event: Event) {
  const templateId = Number((event.target as HTMLSelectElement).value)
  if (!Number.isFinite(templateId) || templateId === currentCv.value?.templateId) return
  try {
    await editorStore.changeTemplate(templateId)
    toast.success('Da doi template va giu nguyen du lieu')
  } catch {
    toast.error('Khong doi duoc template', editorStore.error ?? undefined)
  }
}

function handleBeforeUnload() {
  editorStore.flushLocalDraft()
}

function handleStorageConflict(event: StorageEvent) {
  const localDraftId = editorStore.currentDraft?.localDraftId
  if (!localDraftId) return
  if (event.key !== `cv_online_draft:${localDraftId}`) return
  toast.warning('Draft vua thay doi o tab khac', 'Ban nen tai lai hoac tiep tuc voi mot tab duy nhat.')
}
</script>

<style scoped>
.tv-select {
  min-width: min(100%, 18rem);
  border-radius: 999px;
  border: 1px solid #dbe2ea;
  background: #f8fafc;
  padding: 0.62rem 0.9rem;
  color: #0f172a;
  font-size: 0.9rem;
  font-weight: 700;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.tv-select:focus {
  border-color: #fb923c;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(251, 146, 60, 0.14);
}

.tv-chip {
  display: inline-flex;
  min-height: 2.5rem;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: #0f172a;
  padding: 0.5rem 0.95rem;
  font-size: 0.875rem;
  font-weight: 800;
  color: white;
  cursor: pointer;
  transition: opacity 0.16s ease, background-color 0.16s ease;
}

.tv-chip:hover:not(:disabled) {
  background: #1e293b;
}

.tv-chip:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.tv-chip--ghost {
  border: 1px solid #cbd5e1;
  background: #fff;
  color: #0f172a;
}

.tv-chip--ghost:hover:not(:disabled) {
  background: #f8fafc;
}
</style>
