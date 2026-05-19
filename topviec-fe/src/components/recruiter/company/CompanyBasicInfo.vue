<template>
  <section class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800">
    <div class="px-6 py-4 border-b border-slate-100 dark:border-slate-800">
      <h3 class="font-bold text-[1.125rem]">Thông tin cơ bản</h3>
    </div>
    <div class="p-6 space-y-6">
      <div class="space-y-2">
        <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Hình ảnh công ty</label>
        <div class="relative group">
          <div
            class="w-full h-48 bg-slate-100 dark:bg-slate-800 rounded-xl overflow-hidden border-2 border-dashed border-slate-300 dark:border-slate-700 flex items-center justify-center relative cursor-pointer"
            @click="triggerCoverUpload"
          >
            <template v-if="!coverUrl">
              <div class="text-center space-y-2">
                <span class="material-symbols-outlined text-slate-400 text-4xl">add_photo_alternate</span>
                <p class="text-xs text-slate-500 font-medium">Tải lên ảnh bìa (1600x400)</p>
              </div>
            </template>
            <img
              v-else
              :src="coverUrl"
              class="absolute inset-0 w-full h-full object-cover"
              alt="Ảnh bìa công ty"
            />
            <button
              v-if="coverUrl"
              class="absolute bottom-4 right-4 bg-white/90 dark:bg-slate-900/90 p-2 rounded-lg text-primary shadow-sm hover:bg-white transition-colors cursor-pointer"
              @click.stop="triggerCoverUpload"
            >
              <span class="material-symbols-outlined">edit</span>
            </button>
            <input ref="coverInput" type="file" accept="image/*" class="hidden" @change="onCoverChange" />
          </div>

          <div class="absolute -bottom-6 left-8">
            <div
              class="w-24 h-24 bg-white dark:bg-slate-800 rounded-2xl border-4 border-white dark:border-slate-900 shadow-xl flex items-center justify-center relative overflow-hidden cursor-pointer group/logo"
              @click="triggerLogoUpload"
            >
              <img v-if="logoUrl" :src="logoUrl" class="w-full h-full object-cover" alt="Logo công ty" />
              <span v-else class="material-symbols-outlined text-slate-400 text-3xl">domain</span>
              <div class="absolute inset-0 bg-black/40 opacity-0 group-hover/logo:opacity-100 flex items-center justify-center transition-opacity">
                <span class="material-symbols-outlined text-white">upload</span>
              </div>
              <input ref="logoInput" type="file" accept="image/*" class="hidden" @change="onLogoChange" />
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 pt-8">
        <div class="space-y-2 md:col-span-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Tên công ty</label>
          <input
            :value="companyName"
            type="text"
            class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-transparent focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-base trans-all"
            :class="{ '!border-red-500 !ring-red-500/20': errors?.companyName }"
            placeholder="Nhập tên công ty"
            @input="$emit('update:companyName', ($event.target as HTMLInputElement).value)"
          />
          <p v-if="errors?.companyName" class="text-xs text-red-500 mt-1">{{ errors.companyName }}</p>
        </div>

        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Lĩnh vực</label>
          <SearchableSelect
            :model-value="industry"
            :options="industryOptions"
            placeholder="-- Chọn lĩnh vực --"
            @update:model-value="$emit('update:industry', $event.toString())"
          />
          <p v-if="errors?.industry" class="text-xs text-red-500 mt-1">{{ errors.industry }}</p>
        </div>

        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Quy mô nhân sự</label>
          <SearchableSelect
            :model-value="companySize"
            :options="companySizeOptions"
            placeholder="-- Chọn quy mô --"
            @update:model-value="$emit('update:companySize', $event as CompanySize)"
          />
          <p v-if="errors?.companySize" class="text-xs text-red-500 mt-1">{{ errors.companySize }}</p>
        </div>

        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Mã số thuế</label>
          <input
            :value="taxCode"
            type="text"
            class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-transparent focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-base trans-all"
            :class="{ '!border-red-500 !ring-red-500/20': errors?.taxCode }"
            placeholder="Ví dụ: 0312xxxxxx"
            @input="$emit('update:taxCode', ($event.target as HTMLInputElement).value)"
          />
          <p v-if="errors?.taxCode" class="text-xs text-red-500 mt-1">{{ errors.taxCode }}</p>
        </div>

        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Năm thành lập</label>
          <input
            :value="foundedYear"
            type="number"
            min="1900"
            class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-transparent focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-base trans-all"
            :class="{ '!border-red-500 !ring-red-500/20': errors?.foundedYear }"
            placeholder="Ví dụ: 2010"
            @input="$emit('update:foundedYear', Number(($event.target as HTMLInputElement).value))"
          />
          <p v-if="errors?.foundedYear" class="text-xs text-red-500 mt-1">{{ errors.foundedYear }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import { useToast } from '@/composables/useToast'
import { useIndustryStore } from '@/stores/industry.store'
import { CompanySize } from '@/types/company.types'

const industryStore = useIndustryStore()
const toast = useToast()

const industryOptions = computed(() => industryStore.industries.map((item) => ({ id: item.id.toString(), name: item.name })))

onMounted(() => {
  if (industryStore.industries.length === 0) {
    industryStore.fetchIndustries({ size: 100 })
  }
})

const companySizeOptions = [
  { id: '1-50', name: '1-50 nhân viên' },
  { id: '51-200', name: '51-200 nhân viên' },
  { id: '201-500', name: '201-500 nhân viên' },
  { id: '500+', name: 'Trên 500 nhân viên' },
]

defineProps<{
  companyName: string
  industry: string
  companySize: CompanySize | ''
  taxCode: string
  foundedYear: number | ''
  coverUrl: string
  logoUrl: string
  errors?: Record<string, string>
}>()

const emit = defineEmits<{
  'update:companyName': [value: string]
  'update:industry': [value: string]
  'update:companySize': [value: CompanySize | '']
  'update:taxCode': [value: string]
  'update:foundedYear': [value: number | '']
  'update:coverUrl': [value: string]
  'update:logoUrl': [value: string]
  'cover-file-change': [value: File | null]
  'logo-file-change': [value: File | null]
}>()

const coverInput = ref<HTMLInputElement | null>(null)
const logoInput = ref<HTMLInputElement | null>(null)

function triggerCoverUpload() {
  coverInput.value?.click()
}

function triggerLogoUpload() {
  logoInput.value?.click()
}

function onCoverChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  emit('update:coverUrl', URL.createObjectURL(file))
  emit('cover-file-change', file)
  input.value = ''
  toast.success('Đã chọn ảnh bìa', 'Ảnh sẽ được lưu khi bạn bấm "Lưu thay đổi".')
}

function onLogoChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  emit('update:logoUrl', URL.createObjectURL(file))
  emit('logo-file-change', file)
  input.value = ''
  toast.success('Đã chọn logo', 'Ảnh sẽ được lưu khi bạn bấm "Lưu thay đổi".')
}
</script>
