<template>
  <div class="table-card">
    <div class="table-header">
      <h3 class="table-title">Danh sách dịch vụ trong nhóm</h3>
      <p class="table-desc">Quản lý và điều chỉnh trạng thái các dịch vụ đang hiển thị.</p>
    </div>

    <div v-if="$slots.filter" class="table-filter">
      <slot name="filter"></slot>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th class="th col-name">Tên dịch vụ</th>
            <th class="th col-group">Nhóm</th>
            <th class="th col-status">Trạng thái</th>
            <th class="th col-action">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <ServiceListTableRow
            v-for="item in items"
            :key="item.id"
            :item="item"
            @toggle="$emit('toggle', item.id)"
            @edit="$emit('edit', item.id)"
            @view="$emit('view', item.id)"
          />
        </tbody>
      </table>
    </div>

    <div class="table-footer">
      <span class="result-count">Hiển thị {{ items.length }} kết quả</span>
      <div class="pagination">
        <button class="page-btn" disabled>
          <span class="material-symbols-outlined">chevron_left</span>
        </button>
        <button class="page-btn">
          <span class="material-symbols-outlined">chevron_right</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import ServiceListTableRow from './ServiceListTableRow.vue'

defineProps<{
  items: Array<{
    id: number
    name: string
    group: string
    groupIcon: string
    active: boolean
  }>
}>()

defineEmits<{
  toggle: [id: number]
  edit: [id: number]
  view: [id: number]
}>()
</script>

<style scoped>
.table-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 32px -4px rgba(87, 66, 64, 0.06);
  overflow: hidden;
}

.table-header {
  padding: 32px 40px 16px;
}
.table-filter {
  padding: 0 40px 20px;
}
.table-title {
  font-size: 18px;
  font-weight: 700;
  color: #1b1c18;
  margin: 0 0 4px;
  letter-spacing: -0.01em;
}
.table-desc { font-size: 16px; color: #574240; margin: 0; }

.table-wrap { overflow-x: auto; }

table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}
.th {
  padding: 14px 24px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #574240;
  background: #fff;
  border-bottom: 2px solid #e2e8f0;
}
.col-name { padding-left: 40px; }
.col-action { text-align: right; padding-right: 40px; }

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 40px;
  background: #fff;
  border-top: 1px solid rgba(228, 226, 220, 0.3);
}
.result-count { font-size: 14px; color: #574240; font-weight: 500; }
.pagination { display: flex; gap: 6px; }
.page-btn {
  padding: 4px;
  border: 1px solid #ddc0be;
  border-radius: 6px;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background 0.15s;
}
.page-btn:hover:not(:disabled) { background: #f8fafc; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-btn .material-symbols-outlined { font-size: 16px; color: #574240; }
</style>