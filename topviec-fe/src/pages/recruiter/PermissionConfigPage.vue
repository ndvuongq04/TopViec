<template>
  <div class="pcp-page">
    <div class="pcp-header">
      <div>
        <h2 class="pcp-title">Phân quyền thành viên</h2>
        <p class="pcp-desc">Cấu hình chi tiết quyền truy cập theo từng thành viên trong hệ thống.</p>
      </div>
    </div>

    <div class="pcp-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['pcp-tab', activeTab === tab.key ? 'active' : '']"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="pcp-content">
      <div v-if="activeTab === 'member'" class="pcp-matrix-wrap">
        <PermMemberMatrix :members="members" />
      </div>
      <div v-else-if="activeTab === 'my'" class="pcp-my-wrap">
        <PermMyPermissions />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import PermMemberMatrix from '@/components/recruiter/permissions/PermMemberMatrix.vue'
import PermMyPermissions from '@/components/recruiter/permissions/PermMyPermissions.vue'
import { useEmployerMemberStore } from '@/stores/employerMember.store'

const tabs = [
  { key: 'member', label: 'Theo thành viên' },
  { key: 'my', label: 'Quyền của tôi' },
]

const activeTab = ref('member')

const memberStore = useEmployerMemberStore()
onMounted(() => memberStore.getMembers({ page: 0, size: 50 }))

const members = computed(() => memberStore.members?.result ?? [])
</script>

<style scoped>
.pcp-page {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding-top: 1.5rem;
}

.pcp-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 16px;
}

.pcp-title {
  margin-bottom: 4px;
  color: #0f172a;
  font-size: 1.875rem;
  font-weight: 800;
}

.pcp-desc {
  color: #64748b;
  font-size: 0.875rem;
}

.pcp-tabs {
  display: flex;
  gap: 24px;
  flex-shrink: 0;
  margin-bottom: 0;
  border-bottom: 1px solid #e2e8f0;
}

.pcp-tab {
  padding: 0 0 12px;
  border: none;
  border-bottom: 2px solid transparent;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
  white-space: nowrap;
}

.pcp-tab:hover {
  color: #0f172a;
}

.pcp-tab.active {
  border-bottom-color: #4b9af6;
  color: #4b9af6;
  font-weight: 700;
}

.pcp-content {
  margin-top: 0;
  padding-bottom: 2rem;
}

.pcp-matrix-wrap,
.pcp-function-wrap {
  margin-top: 16px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pcp-my-wrap {
  margin-top: 16px;
}
</style>
