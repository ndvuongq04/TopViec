<template>
  <div class="overflow-x-auto">
    <table class="w-full text-left border-collapse">
      <thead>
        <tr class="bg-slate-50/50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
          <th v-for="col in columns" :key="col.key"
            class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap"
            :class="{ 'text-right': col.key === 'actions' }"
          >
            {{ col.label }}
          </th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
        <tr
          v-for="pkg in packages"
          :key="pkg.id"
          class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors"
          :class="{ 'opacity-50': !pkg.isActive }"
        >
          <!-- Tên gói -->
          <td class="px-6 py-4">
            <span
              class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold"
              :style="tierBadgeStyle(pkg.code)"
            >
              {{ pkg.name }}
            </span>
          </td>

          <!-- Mã gói -->
          <td class="px-6 py-4">
            <span class="font-mono text-xs text-slate-500 dark:text-slate-400 bg-slate-100 dark:bg-slate-800 px-2 py-1 rounded-md">
              {{ pkg.code }}
            </span>
          </td>

          <!-- Chu kỳ -->
          <td class="px-6 py-4">
            <span class="text-sm text-slate-600 dark:text-slate-300">
              {{ BILLING_CYCLE_LABELS[pkg.billingCycle] ?? pkg.billingCycle }}
            </span>
          </td>

          <!-- Giá -->
          <td class="px-6 py-4 text-sm font-semibold text-slate-900 dark:text-slate-100 whitespace-nowrap">
            {{ formatPrice(pkg.price) }}
          </td>

          <!-- Tính năng -->
          <td class="px-6 py-4">
            <div v-if="pkg.details && pkg.details.length" class="flex flex-col gap-1">
              <span
                v-for="detail in pkg.details"
                :key="detail.id"
                class="text-xs text-slate-600 dark:text-slate-300 whitespace-nowrap"
              >
                {{ detail.serviceName }}: {{ detail.quantity }}<template v-if="detail.serviceUnit"> {{ detail.serviceUnit }}</template>
              </span>
            </div>
            <span v-else class="text-sm text-slate-400">—</span>
          </td>

          <!-- Trạng thái -->
          <td class="px-6 py-4">
            <div class="flex items-center gap-2.5">
              <button
                v-if="can('package.update')"
                type="button"
                role="switch"
                :aria-checked="pkg.isActive"
                :disabled="togglingId === pkg.id"
                class="relative inline-flex h-5 w-9 shrink-0 rounded-full border-2 border-transparent transition-colors duration-200 disabled:opacity-60 disabled:cursor-not-allowed cursor-pointer"
                :class="pkg.isActive ? 'bg-green-500' : 'bg-slate-200 dark:bg-slate-700'"
                @click="$emit('toggle', pkg)"
              >
                <span
                  class="pointer-events-none inline-block size-4 transform rounded-full bg-white shadow transition duration-200"
                  :class="pkg.isActive ? 'translate-x-4' : 'translate-x-0'"
                />
              </button>
              <span
                class="text-xs font-semibold whitespace-nowrap"
                :class="pkg.isActive ? 'text-green-600' : 'text-slate-400'"
              >
                {{ pkg.isActive ? 'Đang bật' : 'Đã tắt' }}
              </span>
            </div>
          </td>

          <!-- Thao tác -->
          <td class="px-6 py-4 text-right">
            <button
              v-if="can('package.update')"
              class="p-1.5 text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
              title="Chỉnh sửa"
              @click="$emit('edit', pkg)"
            >
              <span class="material-symbols-outlined text-lg">edit</span>
            </button>
          </td>
        </tr>

        <!-- Empty state -->
        <tr v-if="packages.length === 0">
          <td colspan="7" class="px-6 py-12 text-center text-slate-400 text-sm">
            <span class="material-symbols-outlined text-4xl block mb-2">inventory_2</span>
            Không tìm thấy gói dịch vụ nào
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { BILLING_CYCLE_LABELS } from '@/constants/servicePackage.constants'
import type { ResServicePackageDTO } from '@/types/servicePackage.types'
import { useAdminPermission } from '@/composables/useAdminPermission'

const { can } = useAdminPermission()

defineProps<{
  packages:   ResServicePackageDTO[]
  togglingId?: number | null
}>()
defineEmits<{
  edit:   [ResServicePackageDTO]
  toggle: [ResServicePackageDTO]
}>()

const columns = [
  { key: 'name',     label: 'Tên gói' },
  { key: 'code',     label: 'Mã gói' },
  { key: 'cycle',    label: 'Chu kỳ' },
  { key: 'price',    label: 'Giá' },
  { key: 'feature',  label: 'Tính năng nổi bật' },
  { key: 'status',   label: 'Trạng thái' },
  { key: 'actions',  label: 'Thao tác' },
]

// Badge màu theo tier từ code
const TIER_BADGE: Record<string, { bg: string; color: string }> = {
  FREE:       { bg: '#F1EFE8', color: '#444441' },
  BASIC:      { bg: '#E1F5EE', color: '#085041' },
  PRO:        { bg: '#FAEEDA', color: '#633806' },
  PREMIUM:    { bg: '#FAECE7', color: '#712B13' },
  ENTERPRISE: { bg: '#EEEDFE', color: '#3C3489' },
}

function tierBadgeStyle(code: string) {
  const tier = Object.keys(TIER_BADGE).find(t => code.startsWith(t)) ?? ''
  const style = TIER_BADGE[tier] ?? { bg: '#F1F5F9', color: '#475569' }
  return { background: style.bg, color: style.color }
}

function formatPrice(price: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

</script>
