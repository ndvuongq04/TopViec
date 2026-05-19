<template>
  <GlobalModal
    :visible="visible"
    title="Thêm thành viên mới"
    subtitle="Mời thành viên mới gia nhập đội ngũ tuyển dụng của bạn."
    icon="person_add"
    confirm-text="Gửi lời mời"
    :loading="loading"
    @close="$emit('close')"
    @confirm="handleSubmit"
  >
    <div class="space-y-6">
      <!-- Email -->
      <div class="space-y-2">
        <label class="text-sm font-bold text-slate-700 dark:text-slate-300">Email thành viên</label>
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 material-symbols-outlined text-slate-400 text-lg">mail</span>
          <input
            v-model="form.email"
            type="email"
            class="w-full pl-10 pr-4 py-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:ring-2 focus:ring-primary focus:border-primary outline-none transition-all"
            placeholder="nguyenvanab@gmail.com"
          />
        </div>
      </div>

      <!-- Temporary Password -->
      <div class="space-y-2">
        <label class="text-sm font-bold text-slate-700 dark:text-slate-300">Mật khẩu tạm thời</label>
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 material-symbols-outlined text-slate-400 text-lg">lock</span>
          <input
            v-model="form.tempPassword"
            type="text"
            class="w-full pl-10 pr-4 py-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:ring-2 focus:ring-primary focus:border-primary outline-none transition-all"
            placeholder="Nhập mật khẩu cho thành viên"
          />
        </div>
        <p class="text-[11px] text-slate-500 italic">* Thành viên sẽ sử dụng mật khẩu này để đăng nhập lần đầu.</p>
      </div>

      <!-- Role Selection -->
      <div class="space-y-3">
        <label class="text-sm font-bold text-slate-700 dark:text-slate-300">Vai trò trong công ty</label>
        <div class="grid grid-cols-1 gap-3">
          <div
            v-for="option in roleOptions"
            :key="option.value"
            class="relative flex items-center p-4 border-2 rounded-xl cursor-pointer transition-all gap-4"
            :class="form.role === option.value
              ? 'border-primary bg-primary/5 ring-1 ring-primary'
              : 'border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50 hover:border-slate-200 dark:hover:border-slate-700'"
            @click="form.role = option.value"
          >
            <div class="size-6 rounded-full border-2 flex items-center justify-center shrink-0"
                 :class="form.role === option.value ? 'border-primary' : 'border-slate-300 dark:border-slate-600'">
              <div v-if="form.role === option.value" class="size-3 rounded-full bg-primary"></div>
            </div>
            <div>
              <p class="text-base font-bold capitalize">{{ option.label }}</p>
              <p class="text-sm text-slate-500">{{ option.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </GlobalModal>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { MEMBER_ROLE, type MemberRole } from '@/constants/companyMember.constants'
import { useToast } from '@/composables/useToast'

const props = defineProps<{
  visible: boolean
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  confirm: [data: { email: string; tempPassword: string; roleName: string }]
}>()

const toast = useToast()

const form = reactive({
  email: '',
  tempPassword: '',
  role: MEMBER_ROLE.RECRUITER as MemberRole,
})

watch(() => props.visible, (val) => {
  if (val) {
    form.email = ''
    form.tempPassword = ''
    form.role = MEMBER_ROLE.RECRUITER as MemberRole
  }
})

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
  if (!form.email || !form.tempPassword) {
    toast.error('Vui lòng điền đầy đủ thông tin')
    return
  }
  emit('confirm', { email: form.email, tempPassword: form.tempPassword, roleName: form.role })
}
</script>
