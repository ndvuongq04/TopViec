<template>
  <div class="flex flex-wrap items-center gap-4 rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
    <div class="relative min-w-[300px] flex-1">
      <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-xl text-slate-400">search</span>
      <input
        type="text"
        placeholder="Nhap tu khoa, nhan Enter de tim..."
        class="w-full rounded-lg border border-[#963131]/5 bg-slate-50 py-2.5 pl-10 pr-4 text-base text-slate-700 outline-none transition-all placeholder:text-slate-400 focus:ring-2 focus:ring-[#963131]/20"
        :value="searchDraft"
        @input="searchDraft = ($event.target as HTMLInputElement).value"
        @keydown.enter.prevent="commitSearch"
      />
    </div>

    <select
      class="shrink-0 rounded-lg border border-[#963131]/5 bg-slate-50 py-2.5 pl-4 pr-8 text-base text-slate-700 outline-none focus:ring-2 focus:ring-[#963131]/20"
      :value="modelValue.status"
      @change="update('status', ($event.target as HTMLSelectElement).value)"
    >
      <option value="">Tat ca trang thai</option>
      <option value="active">Dang hoat dong</option>
      <option value="draft">Ban nhap</option>
      <option value="archived">Da luu tru</option>
    </select>

    <select
      class="shrink-0 rounded-lg border border-[#963131]/5 bg-slate-50 py-2.5 pl-4 pr-8 text-base text-slate-700 outline-none focus:ring-2 focus:ring-[#963131]/20"
      :value="modelValue.tier"
      @change="update('tier', ($event.target as HTMLSelectElement).value)"
    >
      <option value="">Tat ca hang</option>
      <option value="free">Mien phi</option>
      <option value="pro">Pro</option>
      <option value="premium">Premium</option>
    </select>

    <select
      class="shrink-0 rounded-lg border border-[#963131]/5 bg-slate-50 py-2.5 pl-4 pr-8 text-base text-slate-700 outline-none focus:ring-2 focus:ring-[#963131]/20"
      :value="modelValue.sort"
      @change="update('sort', ($event.target as HTMLSelectElement).value)"
    >
      <option value="createdAt,desc">Moi nhat</option>
      <option value="createdAt,asc">Cu nhat</option>
      <option value="updatedAt,desc">Vua cap nhat</option>
      <option value="name,asc">Ten A-Z</option>
      <option value="name,desc">Ten Z-A</option>
    </select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: { search: string; status: string; tier: string; sort: string }
}>()

const emit = defineEmits<{
  'update:modelValue': [value: typeof props.modelValue]
}>()

const searchDraft = ref(props.modelValue.search)

watch(
  () => props.modelValue.search,
  (value) => {
    if (value !== searchDraft.value) {
      searchDraft.value = value
    }
  },
)

function commitSearch() {
  const keyword = searchDraft.value.trim()
  if (keyword === props.modelValue.search) return
  update('search', keyword)
}

function update(key: keyof typeof props.modelValue, value: string) {
  emit('update:modelValue', { ...props.modelValue, [key]: value })
}
</script>
