<template>
  <div class="proposal-table">

    <!-- Toolbar -->
    <div class="proposal-table__toolbar">
      <div class="search-wrap">
        <span class="search-icon material-symbols-outlined">search</span>
        <input
          class="search-input"
          type="text"
          placeholder="Tìm tên ứng viên, email... (Enter để tìm)"
          :value="searchValue"
          @input="$emit('update:searchValue', ($event.target as HTMLInputElement).value)"
          @keyup.enter="$emit('search')"
        />
        <button
          v-if="searchValue"
          class="search-clear"
          @click="$emit('update:searchValue', ''); $emit('search')"
        >
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>
    </div>

    <!-- Table -->
    <table class="table">
      <thead>
        <tr>
          <th>Ứng viên</th>
          <th>Vòng PV</th>
          <th>Slots đã gửi cho UV</th>
          <th>Slot UV đã xác nhận</th>
          <th>Trạng thái</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <ProposalTableRow
          v-for="item in proposals"
          :key="item.id"
          :proposal="item"
          @extend-deadline="$emit('extendDeadline', item)"
          @force-schedule="$emit('forceSchedule', item)"
        />

        <!-- Empty state -->
        <tr v-if="!proposals.length">
          <td colspan="5" class="table__empty">
            <span class="material-symbols-outlined table__empty-icon">inbox</span>
            <p>{{ searchValue ? 'Không tìm thấy kết quả phù hợp.' : 'Chưa có đề xuất nào.' }}</p>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Footer: count only -->
    <div class="proposal-table__footer">
      <span class="proposal-table__count">
        Hiển thị {{ proposals.length }} trong tổng số {{ total }} đề xuất
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import ProposalTableRow from './ProposalTableRow.vue'

defineProps<{
  proposals:   any[]
  total:       number
  searchValue: string
}>()

defineEmits<{
  'update:searchValue': [value: string]
  'search':             []
  'extendDeadline':     [item: any]
  'forceSchedule':      [item: any]
}>()
</script>

<style scoped>
.proposal-table {
  background: #fff;
  border-radius: 0.75rem;
  box-shadow: 0 1px 3px rgb(0 0 0 / 0.04);
  overflow: hidden;
  border: 1px solid #f1f5f9;
}

/* ── Toolbar ── */
.proposal-table__toolbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0.875rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
  background: #fff;
}

/* ── Search ── */
.search-wrap {
  position: relative;
  width: 22rem;
}

.search-icon {
  position: absolute;
  top: 50%;
  left: 0.75rem;
  transform: translateY(-50%);
  font-size: 1.1rem !important;
  color: #94a3b8;
  pointer-events: none;
  user-select: none;
}

.search-input {
  width: 100%;
  height: 2.25rem;
  padding: 0 2.25rem 0 2.375rem;
  border-radius: 9999px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  font-size: 0.875rem;
  font-family: 'Manrope', sans-serif;
  color: #0f172a;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
  box-sizing: border-box;
}

.search-input::placeholder { color: #94a3b8; }

.search-input:focus {
  border-color: #4b9af6;
  box-shadow: 0 0 0 3px rgba(75, 154, 246, 0.1);
  background: #fff;
}

.search-clear {
  position: absolute;
  top: 50%;
  right: 0.5rem;
  transform: translateY(-50%);
  width: 1.25rem;
  height: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: #e2e8f0;
  border-radius: 9999px;
  cursor: pointer;
  color: #64748b;
  transition: background 0.15s, color 0.15s;
  padding: 0;
}

.search-clear:hover { background: #cbd5e1; color: #0f172a; }
.search-clear .material-symbols-outlined { font-size: 0.75rem !important; }

/* ── Table ── */
.table {
  width: 100%;
  text-align: left;
  border-collapse: collapse;
}

.table thead { background: rgb(248 250 252 / 0.5); }

.table th {
  padding: 1rem 1.5rem;
  font-size: 0.6875rem;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.table__empty {
  padding: 3rem 1.5rem;
  text-align: center;
  color: #94a3b8;
  font-size: 0.875rem;
}

.table__empty-icon {
  font-size: 2rem !important;
  display: block;
  margin-bottom: 0.5rem;
  color: #cbd5e1;
}

.table__empty p { margin: 0; }

/* ── Footer ── */
.proposal-table__footer {
  padding: 0.875rem 1.5rem;
  background: rgb(248 250 252 / 0.3);
  border-top: 1px solid #f1f5f9;
}

.proposal-table__count {
  font-size: 0.75rem;
  font-weight: 500;
  color: #64748b;
}
</style>
