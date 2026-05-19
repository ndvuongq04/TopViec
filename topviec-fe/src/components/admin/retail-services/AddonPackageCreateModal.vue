<template>
  <GlobalModal
    :visible="visible"
    title="Thêm dịch vụ lẻ mới"
    subtitle="Chọn danh mục, chọn dịch vụ cơ sở rồi điền thông tin chi tiết."
    icon="add_box"
    confirm-text="Tạo dịch vụ"
    loading-text="Đang tạo..."
    :loading="submitting"
    form-id="create-addon-service-form"
    max-width="2xl"
    variant="danger"
    @close="$emit('close')"
  >
    <form id="create-addon-service-form" class="space-y-6" @submit.prevent="handleSubmit">

      <!-- Danh mục & Dịch vụ cơ sở -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">

        <!-- Danh mục (filter, tùy chọn) -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-category">
            Danh mục
          </label>
          <div class="relative">
            <select
              id="addon-category"
              v-model="form.category"
              class="w-full px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm outline-none transition-all shadow-sm appearance-none bg-none cursor-pointer focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131]"
              @change="onCategoryChange"
            >
              <option value="">-- Tất cả danh mục --</option>
              <option v-for="opt in categoryOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-[20px]">expand_more</span>
          </div>
        </div>

        <!-- Dịch vụ cơ sở (bắt buộc) -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-service">
            Dịch vụ cơ sở <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <select
              id="addon-service"
              v-model="form.serviceId"
              class="w-full px-4 py-3 border rounded-xl text-sm outline-none transition-all shadow-sm appearance-none bg-none cursor-pointer"
              :class="errors.serviceId
                ? 'bg-white dark:bg-slate-900 border-red-400 focus:ring-4 focus:ring-red-400/10'
                : 'bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700 focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131]'"
              @change="errors.serviceId = ''"
            >
              <option :value="null">-- Chọn dịch vụ cơ sở --</option>
              <option v-for="svc in filteredServices" :key="svc.id" :value="svc.id">
                {{ svc.name }}
              </option>
            </select>
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-[20px]">expand_more</span>
          </div>
          <p v-if="errors.serviceId" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">error</span>
            {{ errors.serviceId }}
          </p>
        </div>
      </div>

      <!-- Tên & Mã (auto-fill từ dịch vụ cơ sở, disabled) -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">

        <!-- Tên dịch vụ lẻ -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-name">
            Tên dịch vụ lẻ
          </label>
          <div class="relative">
            <input
              id="addon-name"
              :value="selectedService?.name ?? ''"
              type="text"
              disabled
              placeholder="Tự động điền từ dịch vụ cơ sở"
              class="w-full px-4 py-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-sm text-slate-500 dark:text-slate-400 cursor-not-allowed shadow-sm"
            />
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-300 text-[18px]">lock</span>
          </div>
        </div>

        <!-- Mã dịch vụ lẻ -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-code">
            Mã dịch vụ lẻ
          </label>
          <div class="relative">
            <input
              id="addon-code"
              :value="selectedService?.code ?? ''"
              type="text"
              disabled
              placeholder="Tự động điền từ dịch vụ cơ sở"
              class="w-full px-4 py-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-sm font-mono text-slate-500 dark:text-slate-400 cursor-not-allowed shadow-sm"
            />
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-300 text-[18px]">lock</span>
          </div>
        </div>
      </div>

      <!-- Số lượng, Giá, Thời hạn -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-5">

        <!-- Số lượng -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-quantity">
            Số lượng <span class="text-red-500">*</span>
          </label>
          <input
            id="addon-quantity"
            v-model.number="form.quantity"
            type="number"
            min="1"
            placeholder="VD: 1"
            class="w-full px-4 py-3 bg-white dark:bg-slate-900 border rounded-xl text-sm outline-none transition-all shadow-sm"
            :class="errors.quantity
              ? 'border-red-400 focus:ring-4 focus:ring-red-400/10'
              : 'border-slate-200 dark:border-slate-700 focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131]'"
            @input="errors.quantity = ''"
          />
          <p v-if="errors.quantity" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">error</span>
            {{ errors.quantity }}
          </p>
        </div>

        <!-- Giá -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-price">
            Giá (VNĐ) <span class="text-red-500">*</span>
          </label>
          <input
            id="addon-price"
            v-model.number="form.price"
            type="number"
            min="0"
            placeholder="VD: 99000"
            class="w-full px-4 py-3 bg-white dark:bg-slate-900 border rounded-xl text-sm outline-none transition-all shadow-sm"
            :class="errors.price
              ? 'border-red-400 focus:ring-4 focus:ring-red-400/10'
              : 'border-slate-200 dark:border-slate-700 focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131]'"
            @input="errors.price = ''"
          />
          <p v-if="errors.price" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">error</span>
            {{ errors.price }}
          </p>
        </div>

        <!-- Thời hạn -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-duration">
            Thời hạn (ngày)
          </label>
          <input
            id="addon-duration"
            v-model.number="form.durationDays"
            type="number"
            min="1"
            placeholder="Để trống = Vô thời hạn"
            class="w-full px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] outline-none transition-all shadow-sm"
          />
          <p class="mt-1.5 text-xs text-slate-400">Để trống nếu dịch vụ không có thời hạn</p>
        </div>
      </div>

      <!-- Trạng thái -->
      <div class="flex items-center gap-3">
        <button
          type="button"
          role="switch"
          :aria-checked="form.isActive"
          class="relative w-11 h-6 rounded-full transition-colors duration-200 shrink-0 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#963131] cursor-pointer"
          :class="form.isActive ? 'bg-[#963131]' : 'bg-slate-200 dark:bg-slate-700'"
          @click="form.isActive = !form.isActive"
        >
          <span
            class="absolute top-0.5 left-0.5 size-5 bg-white rounded-full shadow transition-transform duration-200"
            :class="form.isActive ? 'translate-x-5' : 'translate-x-0'"
          />
        </button>
        <div>
          <p class="text-sm font-semibold text-slate-700 dark:text-slate-300">
            {{ form.isActive ? 'Đang hoạt động' : 'Tạm ẩn' }}
          </p>
          <p class="text-xs text-slate-400">Dịch vụ sẽ hiển thị để nhà tuyển dụng có thể mua</p>
        </div>
      </div>

      <!-- Mô tả -->
      <div>
        <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="addon-desc">
          Mô tả dịch vụ
        </label>
        <textarea
          id="addon-desc"
          v-model.trim="form.description"
          rows="3"
          placeholder="Mô tả ngắn về dịch vụ và lợi ích mang lại..."
          class="w-full px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] outline-none transition-all shadow-sm resize-none"
        />
      </div>

    </form>
  </GlobalModal>
</template>

<script setup lang="ts">
import { reactive, computed, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { ServiceCategory, SERVICE_CATEGORY_LABELS } from '@/constants/serviceCatalog.constants'
import type { ResServiceDTO, ReqAddonServiceDTO } from '@/types/serviceCatalog.types'

const props = defineProps<{
  visible:     boolean
  submitting?: boolean
  services:    ResServiceDTO[]
}>()

const emit = defineEmits<{
  close:  []
  submit: [payload: ReqAddonServiceDTO]
}>()

// ─── Options ──────────────────────────────────────────────────────────────────
const categoryOptions = Object.values(ServiceCategory).map(v => ({
  value: v,
  label: SERVICE_CATEGORY_LABELS[v],
}))

const filteredServices = computed(() =>
  form.category
    ? props.services.filter(s => s.category === form.category && s.isActive)
    : props.services.filter(s => s.isActive)
)

// Dịch vụ cơ sở đang được chọn → auto-fill tên và mã
const selectedService = computed(() =>
  props.services.find(s => s.id === form.serviceId) ?? null
)

// ─── Form state ───────────────────────────────────────────────────────────────
const form = reactive({
  category:     '' as ServiceCategory | '',
  serviceId:    null as number | null,
  quantity:     null as number | null,
  price:        null as number | null,
  durationDays: null as number | null,
  description:  '',
  isActive:     true,
})

const errors = reactive({
  serviceId: '',
  quantity:  '',
  price:     '',
})

// ─── Khi đổi danh mục → reset service đã chọn ────────────────────────────────
function onCategoryChange() {
  form.serviceId   = null
  errors.serviceId = ''
}

// ─── Reset form khi mở modal ─────────────────────────────────────────────────
function resetForm() {
  form.category     = ''
  form.serviceId    = null
  form.quantity     = null
  form.price        = null
  form.durationDays = null
  form.description  = ''
  form.isActive     = true
  errors.serviceId  = ''
  errors.quantity   = ''
  errors.price      = ''
}

watch(() => props.visible, (visible) => {
  if (visible) resetForm()
})

// ─── Validation ───────────────────────────────────────────────────────────────
function validate(): boolean {
  errors.serviceId = ''
  errors.quantity  = ''
  errors.price     = ''
  let valid = true

  if (!form.serviceId || !selectedService.value) {
    errors.serviceId = 'Vui lòng chọn dịch vụ cơ sở'
    valid = false
  }
  if (form.quantity === null || form.quantity === undefined) {
    errors.quantity = 'Số lượng không được để trống'
    valid = false
  } else if (form.quantity < 1) {
    errors.quantity = 'Số lượng phải ít nhất là 1'
    valid = false
  }
  if (form.price === null || form.price === undefined) {
    errors.price = 'Giá không được để trống'
    valid = false
  } else if (form.price < 0) {
    errors.price = 'Giá không được âm'
    valid = false
  }

  return valid
}

// ─── Submit ───────────────────────────────────────────────────────────────────
function handleSubmit() {
  if (!validate()) return

  const svc = selectedService.value!
  const payload: ReqAddonServiceDTO = {
    serviceId:    svc.id,
    name:         svc.name,
    code:         svc.code,
    quantity:     form.quantity as number,
    durationDays: form.durationDays || null,
    price:        form.price as number,
    description:  form.description || null,
    isActive:     form.isActive,
  }

  emit('submit', payload)
}
</script>
