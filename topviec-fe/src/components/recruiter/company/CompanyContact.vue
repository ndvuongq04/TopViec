<template>
  <section class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800">
    <div class="px-6 py-4 border-b border-slate-100 dark:border-slate-800">
      <h3 class="font-bold text-[1.125rem]">Liên hệ & Mạng xã hội</h3>
    </div>
    <div class="p-6 space-y-6">

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

        <!-- Email -->
        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Email liên hệ</label>
          <input
            :value="email"
            type="email"
            class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-transparent focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-base trans-all"
            :class="{ '!border-red-500 !ring-red-500/20': errors?.email }"
            placeholder="Ví dụ: hr@company.com"
            @input="$emit('update:email', ($event.target as HTMLInputElement).value)"
          />
          <p v-if="errors?.email" class="text-xs text-red-500 mt-1">{{ errors.email }}</p>
        </div>

        <!-- Phone -->
        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Số điện thoại</label>
          <input
            :value="phone"
            type="tel"
            class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-transparent focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-base trans-all"
            :class="{ '!border-red-500 !ring-red-500/20': errors?.phone }"
            placeholder="Ví dụ: 028xxxxxxx"
            @input="$emit('update:phone', ($event.target as HTMLInputElement).value)"
          />
          <p v-if="errors?.phone" class="text-xs text-red-500 mt-1">{{ errors.phone }}</p>
        </div>

        <!-- Website -->
        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Website chính thức</label>
          <div class="flex">
            <span class="inline-flex items-center px-3 rounded-l-lg border border-r-0 border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-500 text-sm select-none">
              https://
            </span>
            <input
            :value="website"
            type="url"
            class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-transparent focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-base trans-all"
            placeholder="Ví dụ: https://company.com"
            @input="$emit('update:website', ($event.target as HTMLInputElement).value)"
          />
          </div>
        </div>

        <!-- Tỉnh/Thành phố -->
        <div class="space-y-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Tỉnh/Thành phố</label>
          <SearchableSelect
            :model-value="provinceId"
            :options="locationOptions"
            placeholder="-- Chọn tỉnh/thành phố --"
            @update:model-value="emit('update:provinceId', Number($event) || '')"
          />
          <p v-if="errors?.provinceId" class="text-[11px] text-red-500 mt-1">{{ errors.provinceId }}</p>
        </div>

        <!-- Địa chỉ -->
        <div class="space-y-2 md:col-span-2">
          <label class="text-sm font-semibold text-slate-700 dark:text-slate-300">Địa chỉ chi tiết</label>
          <input
            :value="address"
            type="text"
            class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-transparent focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-base trans-all"
            :class="{ '!border-red-500 !ring-red-500/20': errors?.address }"
            placeholder="Số nhà, tên đường, phường/xã..."
            @input="$emit('update:address', ($event.target as HTMLInputElement).value)"
          />
          <p v-if="errors?.address" class="text-xs text-red-500 mt-1">{{ errors.address }}</p>
        </div>

      </div>

      <!-- Mạng xã hội -->
      <div class="space-y-3">
        <p class="text-sm font-semibold text-slate-700 dark:text-slate-300">Mạng xã hội</p>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">

          <div
            v-for="social in socialFields"
            :key="social.key"
            class="flex items-center gap-2 px-3 py-2 bg-slate-50 dark:bg-slate-800 rounded-lg border border-slate-200 dark:border-slate-700 focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all"
          >
            <span class="material-symbols-outlined text-slate-400 shrink-0">link</span>
            <input
              :value="socialValues[social.key]"
              type="url"
              class="bg-transparent border-none text-xs focus:ring-0 p-0 w-full outline-none"
              :placeholder="social.placeholder"
              @input="onSocialInput(social.key, ($event.target as HTMLInputElement).value)"
            />
          </div>

        </div>
      </div>

    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import { useLocationStore } from '@/stores/location.store'

const locationStore = useLocationStore()

const locationOptions = computed(() => {
  return locationStore.locations.map(l => ({ id: l.id.toString(), name: l.name }))
})

onMounted(() => {
  if (locationStore.locations.length === 0) {
    locationStore.fetchLocations({ size: 100 })
  }
})

type SocialKey = 'linkedin' | 'twitter' | 'facebook'

const props = defineProps<{
  email: string
  phone: string
  website: string
  provinceId: number | ''
  address: string
  linkedin: string
  twitter: string
  facebook: string
  errors?: Record<string, string>
}>()

const emit = defineEmits<{
  'update:email':      [value: string]
  'update:phone':      [value: string]
  'update:website':    [value: string]
  'update:provinceId': [value: number | '']
  'update:address':    [value: string]
  'update:linkedin':   [value: string]
  'update:twitter':    [value: string]
  'update:facebook':   [value: string]
}>()

const socialFields: { key: SocialKey; placeholder: string }[] = [
  { key: 'linkedin', placeholder: 'LinkedIn URL' },
  { key: 'twitter',  placeholder: 'Twitter (X) URL' },
  { key: 'facebook', placeholder: 'Facebook URL' },
]

// Dùng computed để reactive khi props thay đổi
const socialValues = computed<Record<SocialKey, string>>(() => ({
  linkedin: props.linkedin,
  twitter:  props.twitter,
  facebook: props.facebook,
}))

// Hàm emit với kiểu rõ ràng, tránh dynamic string emit
function onSocialInput(key: SocialKey, value: string) {
  if (key === 'linkedin') emit('update:linkedin', value)
  else if (key === 'twitter') emit('update:twitter', value)
  else if (key === 'facebook') emit('update:facebook', value)
}
</script>