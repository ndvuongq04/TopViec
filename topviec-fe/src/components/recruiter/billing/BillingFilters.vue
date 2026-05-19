<template>
  <div class="filters">
    <div class="filters__grid">

      <!-- Search (Enter mới gọi API) -->
      <div class="filters__field">
        <label class="filters__label">Tìm kiếm mã đơn</label>
        <div class="filters__input-wrap">
          <span class="material-symbols-outlined filters__input-icon">search</span>
          <input
            v-model="search"
            class="filters__input"
            placeholder="VD: ORD-00247 (Enter để tìm)"
            type="text"
            @keyup.enter="emitFilter"
          />
        </div>
      </div>

      <!-- Loại giao dịch -->
      <div class="filters__field">
        <label class="filters__label">Loại giao dịch</label>
        <select v-model="orderType" class="filters__select">
          <option value="">Tất cả</option>
          <option :value="OrderType.SUBSCRIPTION">Gói đăng ký</option>
          <option :value="OrderType.ADDON">Dịch vụ lẻ</option>
        </select>
      </div>

      <!-- Trạng thái -->
      <div class="filters__field">
        <label class="filters__label">Trạng thái</label>
        <select v-model="status" class="filters__select">
          <option value="">Tất cả trạng thái</option>
          <option :value="OrderStatus.PAID">Đã thanh toán</option>
          <option :value="OrderStatus.PENDING">Chờ thanh toán</option>
          <option :value="OrderStatus.FAILED">Thất bại</option>
          <option :value="OrderStatus.CANCELLED">Đã huỷ</option>
        </select>
      </div>

      <!-- Khoảng thời gian -->
      <div class="filters__field">
        <label class="filters__label">Khoảng thời gian</label>
        <div class="filters__daterange">
          <div class="filters__daterange-group">
            <span class="filters__daterange-label">Từ</span>
            <input v-model="startDate" type="date" class="filters__date-input" />
          </div>
          <span class="filters__daterange-sep">—</span>
          <div class="filters__daterange-group">
            <span class="filters__daterange-label">Đến</span>
            <input v-model="endDate" type="date" class="filters__date-input" />
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="filters__actions">
        <button class="filters__btn-submit" @click="emitFilter">Lọc</button>
        <button class="filters__btn-clear" @click="clearFilters">Xoá</button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { OrderType, OrderStatus } from '@/constants/servicePackage.constants'
import type { EmployerOrderQueryParams } from '@/types/order.types'

const search    = ref('')
const orderType = ref<OrderType | ''>('')
const status    = ref<OrderStatus | ''>('')
const startDate = ref('')
const endDate   = ref('')

const emit = defineEmits<{
  filter: [params: EmployerOrderQueryParams]
}>()

function emitFilter() {
  const params: EmployerOrderQueryParams = {}
  if (search.value)    params.search    = search.value
  if (orderType.value) params.type      = orderType.value.toUpperCase()
  if (status.value)    params.status    = status.value.toUpperCase()
  if (startDate.value) params.startDate = startDate.value
  if (endDate.value)   params.endDate   = endDate.value
  emit('filter', params)
}

function clearFilters() {
  search.value    = ''
  orderType.value = ''
  status.value    = ''
  startDate.value = ''
  endDate.value   = ''
  emit('filter', {})
}
</script>

<style scoped>
.filters {
  margin-top: 2rem;
  background: #fff;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #f8fafc;
}
.filters__grid {
  display: grid;
  grid-template-columns: 3fr 2fr 2fr 3fr 2fr;
  gap: 1rem;
  align-items: end;
}
@media (max-width: 1024px) {
  .filters__grid { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 640px) {
  .filters__grid { grid-template-columns: 1fr; }
}

.filters__label {
  display: block;
  font-size: 0.75rem; font-weight: 800;
  color: #94a3b8; text-transform: uppercase;
  letter-spacing: 0.05em; margin-bottom: 8px;
}
.filters__input-wrap { position: relative; }
.filters__input-icon {
  position: absolute; left: 12px; top: 50%; transform: translateY(-50%);
  font-size: 20px; color: #94a3b8; pointer-events: none;
}
.filters__input,
.filters__select {
  width: 100%; background: #f8fafc; border: none;
  border-radius: 0.75rem; padding: 10px 16px;
  font-size: 1rem; font-family: 'Manrope', sans-serif;
  color: #0f172a; outline: none; box-sizing: border-box;
  transition: box-shadow 0.2s;
}
.filters__input { padding-left: 40px; }
.filters__input:focus,
.filters__select:focus { box-shadow: 0 0 0 2px rgba(75,154,246,0.2); }

.filters__actions {
  display: flex; align-items: center; gap: 0.5rem;
  height: 45px;
}
.filters__btn-submit {
  flex: 1; background: #0f172a; color: #fff;
  font-weight: 700; font-size: 0.875rem;
  padding: 10px 0; border: none; border-radius: 0.75rem;
  cursor: pointer; box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  transition: background 0.2s;
}
.filters__btn-submit:hover { background: #1e293b; }
.filters__btn-clear {
  flex: 0 0 auto; background: transparent; color: #94a3b8;
  font-weight: 700; font-size: 0.75rem;
  padding: 10px 12px; border: 1px solid #e2e8f0; border-radius: 0.75rem;
  cursor: pointer; transition: color 0.2s, border-color 0.2s;
  white-space: nowrap;
}
.filters__btn-clear:hover { color: #ef4444; border-color: #fecaca; }

.filters__daterange {
  display: flex; align-items: center; gap: 8px;
  background: #f8fafc; border-radius: 0.75rem;
  padding: 0 16px; height: 45px;
  transition: box-shadow 0.2s;
}
.filters__daterange:focus-within { box-shadow: 0 0 0 2px rgba(75,154,246,0.2); }
.filters__daterange-group {
  display: flex; align-items: center; gap: 6px; flex: 1;
}
.filters__daterange-label {
  font-size: 0.75rem; font-weight: 800;
  color: #94a3b8; text-transform: uppercase;
  letter-spacing: 0.05em; white-space: nowrap;
}
.filters__daterange-sep {
  color: #cbd5e1; font-size: 0.875rem; flex-shrink: 0;
}
.filters__date-input {
  background: transparent; border: none; outline: none;
  font-size: 0.875rem; font-family: 'Manrope', sans-serif;
  color: #0f172a; width: 100%; cursor: pointer;
}
</style>
