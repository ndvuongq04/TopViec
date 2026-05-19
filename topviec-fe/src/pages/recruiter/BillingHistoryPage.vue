<template>
  <div class="billing-page">
    <div class="billing-page__header">
      <div>
        <h1 class="billing-page__title">Lịch sử & Hóa đơn</h1>
        <p class="billing-page__subtitle">Tra cứu đơn hàng và lịch sử sử dụng dịch vụ của doanh nghiệp</p>
      </div>
      <button class="billing-page__export-btn">
        <span class="material-symbols-outlined">download</span>
        Xuất CSV
      </button>
    </div>

    <BillingTabs v-model="activeTab" />
    <BillingFilters @filter="onFilter" />
    <BillingSummaryBar
      :total-amount="totalAmountFormatted"
      :total-orders="store.meta.totals"
      :paid-orders="paidOrdersCount"
    />

    <div v-if="store.loading" class="billing-page__loading">
      <span class="material-symbols-outlined billing-page__spinner">progress_activity</span>
      Đang tải dữ liệu...
    </div>
    <div v-else-if="store.error" class="billing-page__error">
      <span class="material-symbols-outlined">error</span>
      {{ store.error }}
    </div>
    <BillingTable
      v-else
      :orders="store.orders"
      :meta="store.meta"
      @page-change="onPageChange"
      @view="openDetail"
      @pay="handlePay"
      @refund="handleRefund"
    />

    <BillingPromoSection />

    <OrderDetailModal
      :visible="detailVisible"
      :order-id="selectedOrderId"
      @close="detailVisible = false"
    />

    <!-- Refund Modal -->
    <GlobalModal
      :visible="refundVisible"
      title="Yêu cầu hoàn tiền"
      subtitle="Vui lòng cung cấp lý do để chúng tôi xem xét yêu cầu của bạn."
      icon="assignment_return"
      confirm-text="Gửi yêu cầu"
      :loading="store.loading"
      @close="refundVisible = false"
      @confirm="submitRefund"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-bold text-slate-700 mb-2">Lý do hoàn tiền <span class="text-red-500">*</span></label>
          <textarea
            v-model="refundReason"
            rows="3"
            class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary focus:ring-2 focus:ring-primary/10 transition-all outline-none resize-none text-sm"
            placeholder="Ví dụ: Tôi mua nhầm gói dịch vụ này..."
          ></textarea>
        </div>
        <div class="p-4 bg-amber-50 rounded-xl border border-amber-100">
          <p class="text-[13px] text-amber-700 leading-relaxed">
            <strong>Lưu ý:</strong> Yêu cầu hoàn tiền sẽ được ban quản trị xem xét trong vòng 24-48 giờ làm việc. Kết quả sẽ được cập nhật trực tiếp tại đây.
          </p>
        </div>
      </div>
    </GlobalModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import BillingTabs from '@/components/recruiter/billing/BillingTabs.vue'
import BillingFilters from '@/components/recruiter/billing/BillingFilters.vue'
import BillingSummaryBar from '@/components/recruiter/billing/BillingSummaryBar.vue'
import BillingTable from '@/components/recruiter/billing/BillingTable.vue'
import BillingPromoSection from '@/components/recruiter/billing/BillingPromoSection.vue'
import OrderDetailModal from '@/components/recruiter/billing/OrderDetailModal.vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { useEmployerOrderStore } from '@/stores/order.store'
import { useToast } from '@/composables/useToast'
import { OrderStatus } from '@/constants/servicePackage.constants'
import type { EmployerOrderQueryParams } from '@/types/order.types'

const store        = useEmployerOrderStore()
const activeTab    = ref<'orders' | 'history'>('orders')
const filterParams = ref<EmployerOrderQueryParams>({})

const paidOrdersCount = computed(() =>
  store.orders.filter(o => o.status === OrderStatus.PAID).length
)

const totalAmountFormatted = computed(() => {
  const sum = store.orders.reduce((acc, o) => acc + o.totalAmount, 0)
  return sum.toLocaleString('vi-VN') + ' đ'
})

const detailVisible   = ref(false)
const selectedOrderId = ref<number | null>(null)
const toast = useToast()

// Refund modal state
const refundVisible = ref(false)
const refundReason  = ref('')
const refundOrderId = ref<number | null>(null)

async function handlePay(id: number) {
  try {
    const url = await store.fetchPaymentUrl(id)
    if (url) {
      window.location.href = url
    }
  } catch (err) {
    // Error is already handled in store
  }
}

async function handleRefund(id: number) {
  refundOrderId.value = id
  refundReason.value  = 'Mua nhầm gói dịch vụ'
  refundVisible.value = true
}

async function submitRefund() {
  if (!refundOrderId.value) return
  
  if (!refundReason.value.trim()) {
    toast.warning('Cảnh báo', 'Vui lòng nhập lý do hoàn tiền')
    return
  }

  try {
    await store.requestRefund(refundOrderId.value, { reason: refundReason.value })
    toast.success('Thành công', 'Đã gửi yêu cầu hoàn tiền')
    refundVisible.value = false
    // Tự động reload list
    store.fetchMyOrders({ ...filterParams.value, page: store.meta.page })
  } catch (err) {
    // Error is handled in store
  }
}

function openDetail(id: number) {
  selectedOrderId.value = id
  detailVisible.value   = true
}

function onPageChange(page: number) {
  store.fetchMyOrders({ ...filterParams.value, page })
}

function onFilter(params: EmployerOrderQueryParams) {
  filterParams.value = params
  store.fetchMyOrders({ ...params, page: 0 })
}

onMounted(() => {
  store.fetchMyOrders({ page: 0 })
})
</script>

<style scoped>


.billing-page__header {
  display: flex; align-items: flex-end; justify-content: space-between;
  gap: 1rem; flex-wrap: wrap;
}
.billing-page__title { font-size: 1.875rem; font-weight: 800; color: #0f172a; letter-spacing: -0.02em; }
.billing-page__subtitle { font-size: 0.875rem; color: #64748b; margin-top: 4px; }

.billing-page__export-btn {
  display: flex; align-items: center; gap: 8px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  padding: 10px 16px;
  font-size: 0.875rem; font-weight: 700; color: #334155;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: background 0.2s, border-color 0.2s;
}
.billing-page__export-btn:hover { background: #f8fafc; border-color: #cbd5e1; }
.billing-page__export-btn .material-symbols-outlined { font-size: 20px; }

.billing-page__loading,
.billing-page__error {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  margin-top: 1.5rem; padding: 3rem;
  background: #fff; border-radius: 1rem;
  border: 1px solid #f8fafc;
  font-size: 0.875rem; font-weight: 500; color: #64748b;
}
.billing-page__error { color: #ef4444; }
@keyframes spin { to { transform: rotate(360deg); } }
.billing-page__spinner { animation: spin 1s linear infinite; }
</style>