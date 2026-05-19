<template>
  <div class="overflow-x-auto">
    <table class="w-full text-left border-collapse">
      <thead>
        <tr class="bg-slate-50/50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
          <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Tên dịch vụ</th>
          <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Giá niêm yết</th>
          <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Thời hạn</th>
          <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Đơn vị tính</th>
          <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-center whitespace-nowrap">Trạng thái</th>
          <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right whitespace-nowrap">Thao tác</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
        <tr
          v-for="svc in services"
          :key="svc.id"
          class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors"
          :class="{ 'opacity-50': !svc.active }"
        >
          <!-- Tên + mô tả -->
          <td class="px-6 py-4">
            <p class="text-sm font-bold text-slate-900 dark:text-slate-100 leading-none">{{ svc.name }}</p>
            <p class="text-xs text-slate-500 mt-1">{{ svc.description }}</p>
          </td>

          <!-- Giá -->
          <td class="px-6 py-4 text-sm font-semibold text-slate-900 dark:text-slate-100 whitespace-nowrap">
            {{ svc.price }}
          </td>

          <!-- Thời hạn -->
          <td class="px-6 py-4 text-sm text-slate-500 whitespace-nowrap">
            {{ svc.duration }}
          </td>

          <!-- Đơn vị -->
          <td class="px-6 py-4 text-sm text-slate-500 whitespace-nowrap">
            {{ svc.unit }}
          </td>

          <!-- Toggle trạng thái -->
          <td class="px-6 py-4">
            <div class="flex items-center justify-center gap-2">
              <button
                v-if="can('addon.update')"
                type="button"
                role="switch"
                :aria-checked="svc.active"
                :disabled="togglingId === svc.id"
                class="relative inline-flex h-5 w-9 shrink-0 rounded-full border-2 border-transparent transition-colors duration-200 disabled:opacity-60 disabled:cursor-not-allowed cursor-pointer"
                :class="svc.active ? 'bg-green-500' : 'bg-slate-200 dark:bg-slate-700'"
                @click="$emit('toggle', svc)"
              >
                <span
                  class="pointer-events-none inline-block h-4 w-4 transform rounded-full bg-white shadow ring-0 transition duration-200"
                  :class="svc.active ? 'translate-x-4' : 'translate-x-0'"
                ></span>
              </button>
              <span
                class="text-xs font-semibold whitespace-nowrap"
                :class="svc.active ? 'text-green-600' : 'text-slate-400'"
              >
                {{ svc.active ? 'Đang bật' : 'Đã tắt' }}
              </span>
            </div>
          </td>

          <!-- Thao tác -->
          <td class="px-6 py-4 text-right">
            <button
              v-if="can('addon.update')"
              class="p-1.5 text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
              title="Chỉnh sửa"
              @click="$emit('edit', svc)"
            >
              <span class="material-symbols-outlined text-lg">edit</span>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { useAdminPermission } from '@/composables/useAdminPermission'

export interface RetailService {
  id: number
  name: string
  description: string
  price: string
  duration: string
  unit: string
  active: boolean
}

const { can } = useAdminPermission()

defineProps<{ services: RetailService[]; togglingId?: number | null }>()
defineEmits<{ edit: [RetailService]; toggle: [RetailService] }>()
</script>
