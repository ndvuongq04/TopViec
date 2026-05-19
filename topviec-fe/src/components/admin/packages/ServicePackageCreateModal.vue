<template>
  <GlobalModal
    :visible="visible"
    title="Thêm gói dịch vụ mới"
    subtitle="Chọn loại gói và cấu hình thông tin chi tiết."
    icon="add_box"
    confirm-text="Tạo gói dịch vụ"
    loading-text="Đang tạo..."
    :loading="submitting"
    form-id="create-service-package-form"
    max-width="2xl"
    variant="danger"
    @close="$emit('close')"
  >
    <form id="create-service-package-form" class="space-y-6" @submit.prevent="handleSubmit">

      <!-- Tên gói & Chu kỳ -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="pkg-tier">
            Tên gói <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <select
              id="pkg-tier"
              v-model="form.tier"
              class="w-full px-4 py-3 bg-white dark:bg-slate-900 border rounded-xl text-sm outline-none transition-all shadow-sm appearance-none bg-none cursor-pointer"
              :class="errors.tier
                ? 'border-red-400 focus:ring-4 focus:ring-red-400/10'
                : 'border-slate-200 dark:border-slate-700 focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131]'"
            >
              <option value="">-- Chọn loại gói --</option>
              <option v-for="opt in tierOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-[20px]">
              expand_more
            </span>
          </div>
          <p v-if="errors.tier" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">error</span>
            {{ errors.tier }}
          </p>
        </div>

        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="pkg-billing">
            Chu kỳ thanh toán <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <select
              id="pkg-billing"
              v-model="form.billingCycle"
              class="w-full px-4 py-3 bg-white dark:bg-slate-900 border rounded-xl text-sm outline-none transition-all shadow-sm appearance-none bg-none cursor-pointer"
              :class="errors.billingCycle
                ? 'border-red-400 focus:ring-4 focus:ring-red-400/10'
                : 'border-slate-200 dark:border-slate-700 focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131]'"
            >
              <option value="">-- Chọn chu kỳ --</option>
              <option v-for="opt in billingCycleOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-[20px]">
              expand_more
            </span>
          </div>
          <p v-if="errors.billingCycle" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">error</span>
            {{ errors.billingCycle }}
          </p>
        </div>
      </div>

      <!-- Mã gói (auto, disabled) & Giá -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="pkg-code">
            Mã gói
          </label>
          <div class="relative">
            <input
              id="pkg-code"
              :value="computedCode"
              type="text"
              disabled
              class="w-full px-4 py-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-sm font-mono text-slate-500 dark:text-slate-400 cursor-not-allowed shadow-sm"
              :placeholder="form.tier && form.billingCycle ? '' : 'Tự động từ tên & chu kỳ'"
            />
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-300 text-[18px]">
              lock
            </span>
          </div>
        </div>

        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="pkg-price">
            Giá (VNĐ) <span class="text-red-500">*</span>
          </label>
          <input
            id="pkg-price"
            v-model.number="form.price"
            type="number"
            min="0"
            placeholder="VD: 499000"
            class="w-full px-4 py-3 bg-white dark:bg-slate-900 border rounded-xl text-sm outline-none transition-all shadow-sm"
            :class="errors.price
              ? 'border-red-400 focus:ring-4 focus:ring-red-400/10'
              : 'border-slate-200 dark:border-slate-700 focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131]'"
          />
          <p v-if="errors.price" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">error</span>
            {{ errors.price }}
          </p>
        </div>
      </div>

      <!-- Danh sách dịch vụ (details) -->
      <div class="rounded-xl border border-slate-200 dark:border-slate-700 overflow-hidden">
        <div class="px-4 py-3 bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-700 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-[18px] text-slate-400">list_alt</span>
            <span class="text-sm font-bold text-slate-700 dark:text-slate-300">Dịch vụ trong gói</span>
          </div>
          <button
            type="button"
            class="flex items-center gap-1 text-xs font-semibold text-[#963131] hover:text-[#7a2828] transition-colors cursor-pointer"
            @click="addDetail"
          >
            <span class="material-symbols-outlined text-[16px]">add</span>
            Thêm dịch vụ
          </button>
        </div>

        <div class="p-4 space-y-3">
          <div v-if="form.details.length === 0" class="text-center py-6 text-slate-400 text-sm">
            Chưa có dịch vụ nào. Nhấn "Thêm dịch vụ" để bắt đầu.
          </div>

          <div
            v-for="(detail, idx) in form.details"
            :key="idx"
            class="flex items-center gap-3"
          >
            <!-- Dropdown chọn service -->
            <div class="relative flex-1">
              <select
                v-model="detail.serviceId"
                class="w-full px-3 py-2.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg text-sm outline-none focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] transition-all appearance-none bg-none cursor-pointer"
              >
                <option :value="null">-- Chọn dịch vụ --</option>
                <option v-for="svc in services" :key="svc.id" :value="svc.id">
                  {{ svc.name }}<template v-if="svc.unit"> ({{ svc.unit }})</template>
                </option>
              </select>
              <span class="material-symbols-outlined absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-[18px]">
                expand_more
              </span>
            </div>

            <!-- Số lượng -->
            <input
              v-model.number="detail.quantity"
              type="number"
              min="1"
              placeholder="SL"
              class="w-24 px-3 py-2.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg text-sm outline-none focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] transition-all"
            />

            <!-- Xóa row -->
            <button
              type="button"
              class="p-1.5 text-slate-400 hover:text-red-500 transition-colors shrink-0 cursor-pointer"
              @click="removeDetail(idx)"
            >
              <span class="material-symbols-outlined text-[18px]">delete</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Thứ tự & Trạng thái -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
        <div>
          <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="pkg-sort">
            Thứ tự hiển thị
          </label>
          <input
            id="pkg-sort"
            v-model.number="form.sortOrder"
            type="number"
            min="0"
            placeholder="VD: 1"
            class="w-full px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] outline-none transition-all shadow-sm"
          />
        </div>

        <div class="flex flex-col justify-end pb-0.5">
          <label class="flex items-center gap-3 cursor-pointer select-none">
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
            <span class="text-sm font-semibold text-slate-700 dark:text-slate-300">
              {{ form.isActive ? 'Đang hoạt động' : 'Tạm ẩn' }}
            </span>
          </label>
          <p class="text-xs text-slate-400 mt-1.5 ml-14">Gói sẽ hiển thị với nhà tuyển dụng</p>
        </div>
      </div>

      <!-- Mô tả -->
      <div>
        <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="pkg-desc">
          Mô tả gói
        </label>
        <textarea
          id="pkg-desc"
          v-model.trim="form.description"
          rows="3"
          placeholder="Mô tả ngắn về đặc quyền và tính năng của gói..."
          class="w-full px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] outline-none transition-all shadow-sm resize-none"
        />
      </div>

    </form>
  </GlobalModal>
</template>

<script setup lang="ts">
import { reactive, computed, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import {
  BillingCycle,
  BILLING_CYCLE_LABELS,
  ServicePackageTier,
  SERVICE_PACKAGE_TIER_LABELS,
} from '@/constants/servicePackage.constants'
import type { ReqServicePackageDTO, ReqServicePackageDetailItem } from '@/types/servicePackage.types'
import type { ResServiceDTO } from '@/types/serviceCatalog.types'

const props = defineProps<{
  visible:     boolean
  submitting?: boolean
  services:    ResServiceDTO[]
}>()

const emit = defineEmits<{
  close:  []
  submit: [payload: ReqServicePackageDTO]
}>()

const tierOptions = Object.values(ServicePackageTier).map(v => ({
  value: v,
  label: SERVICE_PACKAGE_TIER_LABELS[v],
}))

const billingCycleOptions = Object.values(BillingCycle).map(v => ({
  value: v,
  label: BILLING_CYCLE_LABELS[v],
}))

const computedCode = computed(() =>
  form.tier && form.billingCycle ? `${form.tier}_${form.billingCycle}` : ''
)

interface DetailRow {
  serviceId: number | null
  quantity:  number
}

const form = reactive({
  tier:         '' as ServicePackageTier | '',
  billingCycle: '' as BillingCycle | '',
  price:        null as number | null,
  details:      [] as DetailRow[],
  description:  '',
  isActive:     true,
  sortOrder:    null as number | null,
})

const errors = reactive({
  tier:         '',
  billingCycle: '',
  price:        '',
})

function addDetail() {
  form.details.push({ serviceId: null, quantity: 1 })
}

function removeDetail(idx: number) {
  form.details.splice(idx, 1)
}

function resetForm() {
  form.tier         = ''
  form.billingCycle = ''
  form.price        = null
  form.description  = ''
  form.isActive     = true
  form.sortOrder    = null
  form.details      = []
  errors.tier         = ''
  errors.billingCycle = ''
  errors.price        = ''
}

watch(() => props.visible, (val) => {
  if (val) resetForm()
})

function validate(): boolean {
  errors.tier         = ''
  errors.billingCycle = ''
  errors.price        = ''

  let valid = true

  if (!form.tier) {
    errors.tier = 'Vui lòng chọn loại gói'
    valid = false
  }

  if (!form.billingCycle) {
    errors.billingCycle = 'Vui lòng chọn chu kỳ thanh toán'
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

function handleSubmit() {
  if (!validate()) return

  const details: ReqServicePackageDetailItem[] = form.details
    .filter(d => d.serviceId !== null)
    .map(d => ({ serviceId: d.serviceId as number, quantity: d.quantity }))

  const payload: ReqServicePackageDTO = {
    name:         SERVICE_PACKAGE_TIER_LABELS[form.tier as ServicePackageTier],
    code:         computedCode.value,
    billingCycle: form.billingCycle as BillingCycle,
    price:        form.price as number,
    details:      details.length ? details : null,
    description:  form.description || null,
    isActive:     form.isActive,
    sortOrder:    form.sortOrder,
  }

  emit('submit', payload)
}
</script>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
