<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
    <AdminKpiCard
      icon="report_problem"
      label="Khiếu nại chờ xử lý"
      :value="pendingComplaints"
      badge="Urgent"
      badge-type="warning"
      note="Tổng khiếu nại Pending + Processing"
    />
    <AdminKpiCard
      icon="gavel"
      label="Kháng cáo chờ duyệt"
      :value="pendingAppeals"
      badge="Pending"
      badge-type="info"
      note="Kháng cáo từ Nhà tuyển dụng"
    />
    <AdminKpiCard
      icon="person_off"
      label="NTD bị hạn chế"
      :value="restrictedEmployers"
      badge="Score≥20"
      badge-type="warning"
      note="Nhà tuyển dụng có điểm vi phạm cao"
    />
  </div>
</template>

<script setup lang="ts">
import AdminKpiCard from '@/components/admin/dashboard/AdminKpiCard.vue';

defineProps<{
  pendingComplaints: number;
  pendingAppeals: number;
  restrictedEmployers: number;
}>();
</script>
