<template>
  <div class="space-y-6">
    <nav class="flex items-center gap-2 text-sm">
      <router-link
        :to="{ name: 'admin-employers' }"
        class="text-slate-500 transition-colors hover:text-[#963131]"
      >
        Quản lý Nhà Tuyển Dụng
      </router-link>
      <span class="material-symbols-outlined text-xs text-slate-400">chevron_right</span>
      <span class="font-medium text-slate-900 dark:text-slate-100">Chi tiết NTD</span>
    </nav>

    <EmployerProfileHeader
      v-if="store.selectedCompany"
      :company="store.selectedCompany"
      @reset-password="onResetPassword"
      @warn="onWarn"
      @suspend="onSuspend"
    />

    <EmployerStatsCards :stats="stats" />

    <EmployerDetailTabs
      v-model:active-tab="activeTab"
      :tabs="tabs"
    />

    <EmployerProfileTab
      v-if="activeTab === 'profile' && store.selectedCompany"
      :company="store.selectedCompany"
    />

    <EmployerLicensePanel
      v-else-if="activeTab === 'license' && store.selectedCompany"
      :company="store.selectedCompany"
      @approve="onApprove"
      @reject="onReject"
      @request-supplement="onRequestSupplement"
    />

    <EmployerViolationScorePanel
      v-else-if="activeTab === 'violation'"
      :employer-id="employerId"
    />

    <div v-else-if="activeTab === 'services'" class="flex flex-col gap-8">
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <div class="lg:col-span-4">
          <SubscriberCurrentPlan />
        </div>
        <div class="lg:col-span-8">
          <SubscriberQuotas />
        </div>
      </div>
      <SubscriberAddons />
    </div>

    <div v-else-if="activeTab === 'orders'">
      <SubscriberOrderHistory />
    </div>

    <div v-else-if="activeTab === 'renewals'">
      <SubscriberRenewalTimeline />
    </div>

    <div
      v-else-if="!store.selectedCompany"
      class="rounded-xl border border-slate-200 bg-white p-12 text-center text-slate-400 dark:border-slate-800 dark:bg-slate-900"
    >
      <p class="text-sm">Đang tải dữ liệu...</p>
    </div>

    <div
      v-else
      class="rounded-xl border border-slate-200 bg-white p-12 text-center text-slate-400 dark:border-slate-800 dark:bg-slate-900"
    >
      <span class="material-symbols-outlined mb-2 block text-4xl">construction</span>
      <p class="text-sm">
        Nội dung tab "{{ tabs.find((t) => t.key === activeTab)?.label }}" đang được phát triển...
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EmployerDetailTabs from '@/components/admin/employers/EmployerDetailTabs.vue'
import EmployerLicensePanel from '@/components/admin/employers/EmployerLicensePanel.vue'
import EmployerProfileHeader from '@/components/admin/employers/EmployerProfileHeader.vue'
import EmployerProfileTab from '@/components/admin/employers/EmployerProfileTab.vue'
import EmployerStatsCards from '@/components/admin/employers/EmployerStatsCards.vue'
import EmployerViolationScorePanel from '@/components/admin/employers/EmployerViolationScorePanel.vue'
import SubscriberCurrentPlan from '@/components/admin/subscribers/SubscriberCurrentPlan.vue'
import SubscriberQuotas from '@/components/admin/subscribers/SubscriberQuotas.vue'
import SubscriberAddons from '@/components/admin/subscribers/SubscriberAddons.vue'
import SubscriberOrderHistory from '@/components/admin/subscribers/SubscriberOrderHistory.vue'
import SubscriberRenewalTimeline from '@/components/admin/subscribers/SubscriberRenewalTimeline.vue'
import { useToast } from '@/composables/useToast'
import { useAdminCompanyStore } from '@/stores/adminCompany.store'
import { CompanyStatus } from '@/constants/company.constants'
import { useAdminPermission } from '@/composables/useAdminPermission'
import type { StatItem } from '@/components/admin/employers/EmployerStatsCards.vue'

const route = useRoute()
const router = useRouter()
const store = useAdminCompanyStore()
const toast = useToast()
const { can, canAccessRoute } = useAdminPermission()

const companyId = Number(route.params.id)
const employerId = computed(() => store.selectedCompany?.createdBy ?? null)

onMounted(async () => {
  if (!companyId) return
  await Promise.all([
    store.fetchById(companyId),
    store.fetchStatistics(companyId)
  ])
  
  if (store.error) {
    toast.error('Lỗi', 'Không tìm thấy công ty này.')
    router.push({ name: 'admin-employers' })
  }
})

const stats = computed<StatItem[]>(() => {
  const s = store.companyStatistics
  
  return [
    {
      label: 'Tin đã đăng',
      value: s?.totalJobPostings ?? 0,
      icon: 'post_add',
      iconBg: 'bg-blue-50 dark:bg-blue-900/20',
      iconColor: 'text-blue-600',
    },
    {
      label: 'CV đã nhận',
      value: s?.totalApplicationsReceived?.toLocaleString() ?? 0,
      icon: 'description',
      iconBg: 'bg-green-50 dark:bg-green-900/20',
      iconColor: 'text-green-600',
    },
    {
      label: 'Gói dịch vụ',
      value: s?.activeSubscriptions?.[0]?.packageName ?? 'Chưa đăng ký',
      icon: 'diamond',
      iconBg: 'bg-[#963131]/10',
      iconColor: 'text-[#963131]',
    },
  ]
})

const activeTab = ref('profile')
const tabs = computed(() => {
  const allTabs = [
    { key: 'profile', label: 'Hồ sơ công ty' },
    { key: 'services', label: 'Dịch vụ' },
    { key: 'orders', label: 'Lịch sử đơn hàng' },
    { key: 'renewals', label: 'Lịch sử gia hạn' },
    { key: 'license', label: 'Giấy phép & Xác thực' },
    { key: 'violation', label: 'Điểm vi phạm' },
  ]

  return allTabs.filter(tab => {
    if (tab.key === 'orders') return canAccessRoute('admin-orders')
    if (tab.key === 'violation') return can('violation.view-score')
    return true
  })
})

watch(activeTab, async (newTab) => {
  if (!companyId) return
  
  try {
    if (newTab === 'services') {
      await store.fetchCompanyPlan(companyId)
    } else if (newTab === 'orders') {
      await store.fetchCompanyOrders(companyId)
    } else if (newTab === 'renewals') {
      await store.fetchCompanySubscriptions(companyId)
    }
  } catch (err) {
    toast.error('Lỗi', store.error || 'Không thể tải dữ liệu tab này')
  }
}, { immediate: true })

function onResetPassword() {
  toast.info('Tính năng', 'Reset mật khẩu đang được phát triển.')
}

function onWarn() {
  toast.info('Tính năng', 'Gửi cảnh báo đang được phát triển.')
}

async function onSuspend() {
  if (!store.selectedCompany) return
  try {
    if (store.selectedCompany.status === CompanyStatus.SUSPENDED) {
      await store.unsuspendCompany(store.selectedCompany.id)
      toast.success('Thành công', 'Đã mở khóa công ty.')
    } else {
      await store.suspendCompany(store.selectedCompany.id, 'Admin khóa tài khoản')
      toast.success('Thành công', 'Đã khóa công ty.')
    }
  } catch {
    toast.error('Thất bại', store.error || 'Có lỗi xảy ra.')
  }
}

async function onApprove() {
  if (!store.selectedCompany) return
  try {
    await store.verifyCompany(store.selectedCompany.id, true)
    toast.success('Thành công', 'Công ty đã được xác thực.')
  } catch {
    toast.error('Thất bại', store.error || 'Không thể phê duyệt.')
  }
}

async function onReject(reason: string) {
  if (!store.selectedCompany) return
  if (!reason.trim()) {
    toast.warning('Chú ý', 'Vui lòng nhập lý do từ chối.')
    return
  }

  try {
    await store.verifyCompany(store.selectedCompany.id, false, reason)
    toast.success('Thành công', 'Đã từ chối xác thực công ty.')
  } catch {
    toast.error('Thất bại', store.error || 'Không thể từ chối.')
  }
}

function onRequestSupplement() {
  toast.info('Tính năng', 'Yêu cầu bổ sung hồ sơ đang được phát triển.')
}
</script>
