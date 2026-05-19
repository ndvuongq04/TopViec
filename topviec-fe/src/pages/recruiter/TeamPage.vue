<template>
  <div class="space-y-8 pt-6" style="font-family: 'Manrope', sans-serif;">

    <!-- Title -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Quản lý Thành viên</h2>
        <p class="text-slate-500 mt-1 text-base">Quản lý và phân quyền cho đội ngũ tuyển dụng của bạn.</p>
      </div>
      <button
        class="inline-flex items-center gap-2 bg-primary hover:bg-primary/90 text-white px-5 py-2.5 rounded-xl font-extrabold text-base transition-all shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-95 cursor-pointer"
        @click="openInviteModal"
      >
        <span class="material-symbols-outlined text-sm">person_add</span>
        Thêm thành viên
      </button>
    </div>

    <!-- Stats -->
    <TeamStats :stats="stats" />

    <TeamTable
      :members="filteredMembers"
      :total="totalMembers"
      :current-page="currentPage"
      :page-size="pageSize"
      @edit="onEdit"
      @delete="onDelete"
      @search="onSearch"
      @filter="onFilter"
      @page-change="onPageChange"
    />

    <!-- Modals -->
    <InviteMemberModal
      :visible="isInviteModalOpen"
      :loading="memberStore.loading"
      @close="isInviteModalOpen = false"
      @confirm="handleInvite"
    />

    <UpdatePermissionModal
      :visible="isUpdateModalOpen"
      :member="selectedMember"
      :loading="memberStore.loading"
      @close="isUpdateModalOpen = false"
      @confirm="handleUpdatePermission"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import TeamStats from '@/components/recruiter/team/TeamStats.vue'
import TeamTable from '@/components/recruiter/team/TeamTable.vue'
import InviteMemberModal from '@/components/recruiter/team/InviteMemberModal.vue'
import UpdatePermissionModal from '@/components/recruiter/team/UpdatePermissionModal.vue'
import { useEmployerMemberStore } from '@/stores/employerMember.store'
import { useRoleStore } from '@/stores/role.store'
import { useConfirm } from '@/composables/useConfirm'
import type { ResCompanyMember } from '@/types/companyMember.types'
import { MEMBER_ROLE, MEMBER_STATUS } from '@/constants/companyMember.constants'
import { useToast } from '@/composables/useToast'

// ─── State ───────────────────────────────────────────────────────────────────
const memberStore = useEmployerMemberStore()
const roleStore = useRoleStore()
const toast = useToast()

const searchQuery = ref('')
const filterRole   = ref('')
const filterStatus = ref('')
const currentPage  = ref(1) // 1-based for UI
const pageSize     = ref(10)

const isInviteModalOpen = ref(false)
const isUpdateModalOpen = ref(false)
const selectedMember    = ref<any>(null)

const { confirm } = useConfirm()

onMounted(() => {
  fetchMembers()
  memberStore.fetchStatistics()
  roleStore.fetchEmployerRoles()
})

async function fetchMembers() {
  await memberStore.getMembers({
    page: currentPage.value - 1, // 0-based for BE
    size: pageSize.value,
    keyword: searchQuery.value || undefined,
    role: filterRole.value || undefined,
    status: filterStatus.value || undefined
  })
}

// Watchers for automatic refetch
watch([currentPage, searchQuery, filterRole, filterStatus], () => {
  fetchMembers()
})

const totalMembers = computed(() => memberStore.members?.meta.totals ?? 0)

const stats = computed(() => {
  const s = memberStore.memberStatistics
  return [
    {
      icon: 'groups',
      label: 'Tổng số thành viên',
      value: s?.totalMembers ?? 0,
      trend: '+0%',
      trendUp: true,
      trendNote: 'cập nhật mới nhất',
      iconBg: 'bg-primary/10',
      iconColor: 'text-primary',
    },
    {
      icon: 'check_circle',
      label: 'Đang hoạt động',
      value: s?.activeMembers ?? 0,
      trend: '0%',
      trendUp: null,
      trendNote: 'hiện tại',
      iconBg: 'bg-emerald-100',
      iconColor: 'text-emerald-600',
    },
    {
      icon: 'hourglass_empty',
      label: 'Chờ xác nhận',
      value: s?.pendingMembers ?? 0,
      trend: '0%',
      trendUp: false,
      trendNote: 'đang chờ',
      iconBg: 'bg-amber-100',
      iconColor: 'text-amber-600',
    },
    {
      icon: 'lock',
      label: 'Bị khóa',
      value: s?.lockedMembers ?? 0,
      trend: '0%',
      trendUp: false,
      trendNote: 'ngừng hoạt động',
      iconBg: 'bg-rose-100',
      iconColor: 'text-rose-600',
    },
  ]
})

// Mapping backend Response to TeamTable's expected TeamMember interface
const tableMembers = computed(() => {
  return (memberStore.members?.result ?? []).map((m: ResCompanyMember) => ({
    id: m.userId, // Using userId for identification
    name: m.email.split('@')[0], // Fallback if name is missing
    email: m.email,
    role: m.roleName as any, // Changed to use roleName
    status: m.status as any,
    joinedAt: new Date(m.createdAt).toLocaleDateString('vi-VN'),
    actions: m.actions || {}
  }))
})

// filteredMembers computed is no longer needed as search is done on BE
const filteredMembers = computed(() => tableMembers.value)

// ─── Actions ─────────────────────────────────────────────────────────────────
function openInviteModal() {
  isInviteModalOpen.value = true
}

async function handleInvite(data: { email: string; tempPassword: string; roleName: string }) {
  const roleId = roleStore.getEmployerRoleId(data.roleName)
  if (!roleId) {
    toast.error('Không tìm thấy vai trò. Vui lòng thử lại.')
    return
  }
  try {
    await memberStore.addMember({ email: data.email, tempPassword: data.tempPassword, roleId })
    toast.success('Đã gửi lời mời thành công')
    isInviteModalOpen.value = false
    fetchMembers()
  } catch (err) {
    toast.error(memberStore.error || 'Có lỗi xảy ra')
  }
}

function onEdit(member: any) {
  selectedMember.value = member
  isUpdateModalOpen.value = true
}

async function handleUpdatePermission(data: { roleName: string; reason?: string }) {
  if (!selectedMember.value) return
  const roleId = roleStore.getEmployerRoleId(data.roleName)
  if (!roleId) {
    toast.error('Không tìm thấy vai trò. Vui lòng thử lại.')
    return
  }
  try {
    await memberStore.updateMemberPermission(selectedMember.value.id, { roleId, reason: data.reason })
    toast.success('Đã cập nhật quyền thành công')
    isUpdateModalOpen.value = false
    fetchMembers()
  } catch (err) {
    toast.error(memberStore.error || 'Có lỗi xảy ra')
  }
}

async function onDelete(member: any) {
  selectedMember.value = member
  const ok = await confirm({
    title: 'Xóa thành viên',
    message: `Bạn có chắc chắn muốn xóa thành viên ${member.email} khỏi công ty? Hành động này không thể hoàn tác.`,
    confirmText: 'Xóa thành viên',
    confirmColor: 'red',
    icon: 'delete_forever'
  })

  if (ok) {
    try {
      await memberStore.removeMember(member.id)
      toast.success('Đã xóa thành viên thành công')
      fetchMembers()
    } catch (err) {
      toast.error(memberStore.error || 'Có lỗi xảy ra')
    }
  }
}

function onSearch(query: string) {
  searchQuery.value = query
  currentPage.value = 1
}

function onFilter(filters: { role: string, status: string }) {
  filterRole.value = filters.role
  filterStatus.value = filters.status
  currentPage.value = 1
}

function onPageChange(page: number) {
  currentPage.value = page
}
</script>