<template>
  <GlobalModal
    :visible="visible"
    :title="`Chỉnh sửa vai trò`"
    :subtitle="`Cập nhật vai trò cho thành viên ${member?.email}`"
    icon="security"
    confirm-text="Lưu thay đổi"
    :loading="loading"
    @close="$emit('close')"
    @confirm="handleSubmit"
  >
    <div class="space-y-6">
      <!-- Role Selection -->
      <div class="space-y-3">
        <label class="text-sm font-bold text-slate-700 dark:text-slate-300">Vai trò mới</label>
        <div class="grid grid-cols-1 gap-3">
          <div
            v-for="option in roleOptions"
            :key="option.value"
            class="relative flex items-center p-4 border-2 rounded-xl cursor-pointer transition-all gap-4"
            :class="selectedRole === option.value
              ? 'border-primary bg-primary/5 ring-1 ring-primary'
              : 'border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50 hover:border-slate-200 dark:hover:border-slate-700'"
            @click="selectedRole = option.value"
          >
            <div class="size-6 rounded-full border-2 flex items-center justify-center shrink-0"
                 :class="selectedRole === option.value ? 'border-primary' : 'border-slate-300 dark:border-slate-600'">
              <div v-if="selectedRole === option.value" class="size-3 rounded-full bg-primary"></div>
            </div>
            <div>
              <p class="text-base font-bold capitalize">{{ option.label }}</p>
              <p class="text-sm text-slate-500">{{ option.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Reason -->
      <div class="space-y-2">
        <label class="text-sm font-bold text-slate-700 dark:text-slate-300">Lý do thay đổi (tùy chọn)</label>
        <textarea
          v-model="reason"
          rows="3"
          class="w-full px-4 py-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:ring-2 focus:ring-primary focus:border-primary outline-none transition-all resize-none text-sm"
          placeholder="Nhập lý do thay đổi vai trò nếu cần..."
        ></textarea>
      </div>
    </div>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { MEMBER_ROLE, type MemberRole } from '@/constants/companyMember.constants'
import type { TeamMember } from './TeamTable.vue'

const props = defineProps<{
  visible: boolean
  loading?: boolean
  member: TeamMember | null
}>()

const emit = defineEmits<{
  close: []
  confirm: [data: { roleName: string; reason?: string }]
}>()

const selectedRole = ref<MemberRole>(MEMBER_ROLE.VIEWER)
const reason = ref('')

watch(() => props.member, (newVal) => {
  if (!newVal) return
  selectedRole.value = (newVal.role || MEMBER_ROLE.VIEWER) as MemberRole
  reason.value = ''
}, { immediate: true })

const roleOptions = [
  {
    value: MEMBER_ROLE.MANAGER,
    label: 'Quản lý',
    description: 'Toàn quyền quản lý tin tuyển dụng và thành viên.'
  },
  {
    value: MEMBER_ROLE.RECRUITER,
    label: 'Nhà tuyển dụng',
    description: 'Đăng tin, quản lý ứng viên và tương tác.'
  },
  {
    value: MEMBER_ROLE.VIEWER,
    label: 'Người xem',
    description: 'Chỉ xem dữ liệu, không có quyền thao tác.'
  }
]

function handleSubmit() {
  emit('confirm', { roleName: selectedRole.value, reason: reason.value || undefined })
}
</script>
