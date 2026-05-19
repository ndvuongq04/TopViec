<template>
  <div class="result-layout">
    <main class="result-main">
      <div class="result-card" v-if="!loading">
        <!-- Success State -->
        <div v-if="isSuccess" class="result-content">
          <div class="result-icon result-icon--success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </div>
          <h1 class="result-title">Thanh toán thành công!</h1>
          <p class="result-desc">
            Cảm ơn bạn đã tin dùng dịch vụ của TopViec. Đơn hàng <strong>{{ orderCode || order?.orderCode }}</strong> của bạn đã được thanh toán thành công.
          </p>
        </div>

        <!-- Error State -->
        <div v-else class="result-content">
          <div class="result-icon result-icon--error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </div>
          <h1 class="result-title">Thanh toán thất bại</h1>
          <p class="result-desc">
            Rất tiếc, đã có lỗi xảy ra trong quá trình xử lý thanh toán cho đơn hàng <strong>{{ orderCode || order?.orderCode }}</strong>.
          </p>
          <div v-if="errorMessage" class="result-error-box">
            {{ errorMessage }}
          </div>
        </div>

        <div class="result-details" v-if="order">
          <div class="detail-row">
            <span class="detail-label">Mã đơn hàng:</span>
            <span class="detail-value">{{ order.orderCode }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Tổng thanh toán:</span>
            <span class="detail-value detail-value--amount">{{ formatCurrency(order.totalAmount) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Thời gian:</span>
            <span class="detail-value">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Phương thức:</span>
            <span class="detail-value">VNPay</span>
          </div>
        </div>

        <div class="result-actions">
          <router-link v-if="isSuccess" to="/recruiter/services" class="btn btn--primary">
            Sử dụng dịch vụ ngay
          </router-link>
          <router-link v-else to="/recruiter/pricing" class="btn btn--primary">
            Thử lại
          </router-link>
          <router-link to="/recruiter/billing" class="btn btn--secondary">
            Xem lịch sử đơn hàng
          </router-link>
        </div>
      </div>

      <!-- Loading State -->
      <div v-else class="result-loading">
        <div class="spinner"></div>
        <p>Đang kiểm tra kết quả thanh toán...</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { employerOrderService } from '@/services/order.service'
import type { ResOrderDTO } from '@/types/order.types'

const route = useRoute()
const loading = ref(true)
const order = ref<ResOrderDTO | null>(null)

// Query parameters from VNPay/BE
const orderId = computed(() => route.query.orderId ? Number(route.query.orderId) : null)
const orderCode = computed(() => route.query.orderCode as string)
const statusParam = computed(() => route.query.status as string)
const responseCode = computed(() => route.query.responseCode as string)
const errorMessage = computed(() => route.query.message as string)

const isSuccess = computed(() => {
  // Common VNPay success code is '00'
  const isVnpaySuccess = responseCode.value === '00'
  const isStatusPaid = statusParam.value?.toLowerCase() === 'paid'
  return isVnpaySuccess || isStatusPaid
})

async function fetchOrderData() {
  if (!orderId.value) {
    loading.value = false
    return
  }

  try {
    const data = await employerOrderService.getById(orderId.value)
    order.value = data
  } catch (error) {
    console.error('Failed to fetch order details:', error)
  } finally {
    loading.value = false
  }
}

function formatCurrency(amount: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('vi-VN')
}

onMounted(() => {
  fetchOrderData()
})
</script>

<style scoped>
.result-layout {
  min-height: 100vh;
  background: #f8fafc;
  color: #1e293b;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  display: flex;
  flex-direction: column;
}

/* Main */
.result-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.result-card {
  background: #fff;
  width: 100%;
  max-width: 540px;
  border-radius: 24px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  padding: 3.5rem 2.5rem;
  text-align: center;
}

.result-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
}
.result-icon svg { width: 40px; height: 40px; }

.result-icon--success {
  background: #ecfdf5;
  color: #10b981;
}
.result-icon--error {
  background: #fef2f2;
  color: #ef4444;
}

.result-title {
  font-family: 'Manrope', sans-serif;
  font-size: 1.875rem;
  font-weight: 800;
  margin-bottom: 1rem;
  letter-spacing: -0.025em;
  color: #0f172a;
}

.result-desc {
  font-size: 1rem;
  font-weight: 400;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.result-error-box {
  background: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 12px;
  padding: 1rem;
  color: #b91c1c;
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 2rem;
}

.result-details {
  background: #f8fafc;
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 2.5rem;
  text-align: left;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}
.detail-row:last-child { margin-bottom: 0; }

.detail-label { 
  color: #64748b; 
  font-size: 0.875rem; 
  font-weight: 500;
}
.detail-value { 
  font-weight: 700; 
  color: #1e293b; 
  font-size: 0.875rem; 
}
.detail-value--amount { 
  color: #4B9AF6; 
  font-size: 1.125rem; 
  font-weight: 800;
}

.result-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.btn {
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  padding: 0.875rem 1.5rem;
  border-radius: 12px;
  font-weight: 800;
  text-decoration: none;
  transition: all 0.2s;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn--primary {
  background: #4B9AF6;
  color: #fff;
  border: none;
}
.btn--primary:hover {
  background: #3b82f6;
  transform: translateY(-1px);
}

.btn--secondary {
  background: #fff;
  color: #64748b;
  border: 1px solid #e2e8f0;
}
.btn--secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

/* Loading */
.result-loading {
  text-align: center;
  color: #64748b;
}
.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top-color: #4B9AF6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 640px) {
  .result-card { padding: 2.5rem 1.5rem; }
  .result-title { font-size: 1.5rem; }
}
</style>
