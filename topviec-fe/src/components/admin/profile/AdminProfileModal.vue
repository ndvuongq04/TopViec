<template>
  <Teleport to="body">
    <div
      v-if="visible"
      class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6 bg-slate-900/60 backdrop-blur-sm"
      @click.self="$emit('close')"
    >
      <div class="bg-white w-full max-w-lg rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">

        <!-- Header -->
        <div class="px-8 py-6 border-b border-slate-200 flex items-center justify-between shrink-0" style="background: #963131;">
          <div class="flex items-center gap-3">
            <div class="size-10 rounded-xl bg-white/20 flex items-center justify-center shrink-0">
              <span class="material-symbols-outlined text-white text-xl">person_outline</span>
            </div>
            <div>
              <h3 class="text-xl font-bold text-white tracking-tight">Thông tin cá nhân</h3>
              <p class="text-sm text-white/70 mt-0.5">Thông tin tài khoản quản trị của bạn</p>
            </div>
          </div>
          <button
            class="p-2 text-white/60 hover:text-white rounded-xl hover:bg-white/10 transition-colors cursor-pointer"
            @click="$emit('close')"
          >
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <!-- Body -->
        <div class="p-8 overflow-y-auto flex-1">

          <!-- Loading -->
          <div v-if="loading" class="flex items-center justify-center py-12">
            <span class="material-symbols-outlined animate-spin text-4xl" style="color: #963131">progress_activity</span>
          </div>

          <!-- Error -->
          <div v-else-if="error" class="flex items-center gap-3 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
            <span class="material-symbols-outlined text-[18px]">error</span>
            {{ error }}
          </div>

          <!-- Content -->
          <div v-else-if="profile" class="space-y-6">

            <!-- Avatar + name block -->
            <div class="flex items-center gap-4 p-4 rounded-2xl" style="background: rgba(150,49,49,0.06); border: 1px solid rgba(150,49,49,0.15);">
              <div class="size-14 rounded-xl flex items-center justify-center shrink-0 text-white font-bold text-2xl" style="background: #963131;">
                {{ profile.fullName?.charAt(0)?.toUpperCase() ?? 'A' }}
              </div>
              <div>
                <p class="font-bold text-slate-900 text-base">{{ profile.fullName }}</p>
                <p class="text-sm text-slate-500">{{ profile.email }}</p>
                <span
                  class="inline-flex items-center mt-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold border"
                  :class="roleStyle.bg + ' ' + roleStyle.text + ' ' + roleStyle.border"
                >
                  {{ roleLabel }}
                </span>
              </div>
            </div>

            <!-- Account info -->
            <div>
              <h4 class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">Tài khoản</h4>
              <div class="divide-y divide-slate-100">

                <div class="flex items-center gap-3 py-3">
                  <span class="material-symbols-outlined text-[18px] text-slate-400 shrink-0">email</span>
                  <span class="text-xs text-slate-400 w-36 shrink-0">Email</span>
                  <span class="text-sm font-medium text-slate-800">{{ profile.email }}</span>
                </div>

                <div class="flex items-center gap-3 py-3">
                  <span class="material-symbols-outlined text-[18px] text-slate-400 shrink-0">verified_user</span>
                  <span class="text-xs text-slate-400 w-36 shrink-0">Trạng thái</span>
                  <span
                    class="text-xs font-semibold px-2.5 py-0.5 rounded-full"
                    :class="profile.isActive ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50'"
                  >
                    {{ profile.isActive ? 'Đang hoạt động' : 'Bị khóa' }}
                  </span>
                </div>

                <div class="flex items-center gap-3 py-3">
                  <span class="material-symbols-outlined text-[18px] text-slate-400 shrink-0">badge</span>
                  <span class="text-xs text-slate-400 w-36 shrink-0">Vai trò</span>
                  <span class="text-sm font-medium text-slate-800">{{ roleLabel }}</span>
                </div>

                <div class="flex items-center gap-3 py-3">
                  <span class="material-symbols-outlined text-[18px] text-slate-400 shrink-0">corporate_fare</span>
                  <span class="text-xs text-slate-400 w-36 shrink-0">Phòng ban</span>
                  <span class="text-sm font-medium text-slate-800">{{ profile.department ?? '—' }}</span>
                </div>

                <div class="flex items-center gap-3 py-3">
                  <span class="material-symbols-outlined text-[18px] text-slate-400 shrink-0">calendar_today</span>
                  <span class="text-xs text-slate-400 w-36 shrink-0">Ngày tạo</span>
                  <span class="text-sm font-medium text-slate-800">{{ formatDate(profile.createdAt) }}</span>
                </div>

                <div v-if="profile.updatedAt" class="flex items-center gap-3 py-3">
                  <span class="material-symbols-outlined text-[18px] text-slate-400 shrink-0">update</span>
                  <span class="text-xs text-slate-400 w-36 shrink-0">Cập nhật lần cuối</span>
                  <span class="text-sm font-medium text-slate-800">{{ formatDate(profile.updatedAt) }}</span>
                </div>

              </div>
            </div>

          </div>
        </div>

        <!-- Footer -->
        <div class="px-8 py-5 border-t border-slate-200 bg-slate-50/50 flex justify-end shrink-0">
          <button
            class="px-6 py-2.5 rounded-xl font-bold text-sm text-white transition-colors cursor-pointer"
            style="background: #963131;"
            @mouseover="(e) => (e.target as HTMLElement).style.opacity = '0.9'"
            @mouseleave="(e) => (e.target as HTMLElement).style.opacity = '1'"
            @click="$emit('close')"
          >
            Đóng
          </button>
        </div>

      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { adminUserService } from '@/services/adminUser.service'
import type { ResAdminUser } from '@/types/adminUser.types'
import { ADMIN_ROLE_LABELS, ADMIN_ROLE_STYLES } from '@/types/adminUser.types'

const props = defineProps<{ visible: boolean }>()
defineEmits<{ close: [] }>()

const profile = ref<ResAdminUser | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const roleLabel = computed(() =>
  profile.value ? (ADMIN_ROLE_LABELS[profile.value.adminRole] ?? profile.value.adminRole) : ''
)

const roleStyle = computed(() =>
  profile.value
    ? (ADMIN_ROLE_STYLES[profile.value.adminRole] ?? { bg: 'bg-slate-100', text: 'text-slate-600', border: 'border-slate-200' })
    : { bg: 'bg-slate-100', text: 'text-slate-600', border: 'border-slate-200' }
)

function formatDate(iso: string) {
  return new Date(iso).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

async function fetchProfile() {
  loading.value = true
  error.value = null
  try {
    profile.value = await adminUserService.getMyProfile()
  } catch {
    error.value = 'Không thể tải thông tin. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val && !profile.value) fetchProfile()
})
</script>
