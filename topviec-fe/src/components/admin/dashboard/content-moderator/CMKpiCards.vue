<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
    <AdminKpiCard
      icon="pending_actions"
      label="Tin chờ duyệt"
      :value="pendingApprovalJobs"
      badge="Pending"
      badge-type="warning"
      note="Số tin đang chờ được phê duyệt"
    />
    <AdminKpiCard
      icon="verified"
      label="Công ty chờ xét duyệt"
      :value="pendingVerifyCompanies"
      badge="Verify"
      badge-type="info"
      note="Số công ty đang chờ xác thực"
    />
    <AdminKpiCard
      icon="block"
      label="Tin bị từ chối"
      :value="rejectedJobsThisMonth"
      badge="Tháng này"
      badge-type="warning"
      note="Số tin bị từ chối trong tháng"
    />
  </div>
</template>

<script setup lang="ts">
import AdminKpiCard from '@/components/admin/dashboard/AdminKpiCard.vue';

defineProps<{
  pendingApprovalJobs: number;
  pendingVerifyCompanies: number;
  rejectedJobsThisMonth: number;
}>();
</script>
