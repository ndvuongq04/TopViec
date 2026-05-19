<template>
  <div class="summary">
    <div class="summary__rows">
      <div class="summary__row">
        <span class="summary__label">Tạm tính</span>
        <span class="summary__value">{{ format(subtotal) }}</span>
      </div>
    </div>

    <div class="summary__total">
      <span class="summary__total-label">Tổng cộng</span>
      <div class="summary__total-right">
        <span class="summary__total-amount">{{ format(total) }}</span>
        <p class="summary__total-vat">Đã bao gồm 10% thuế giá trị gia tăng (VAT)</p>
      </div>
    </div>

    <div class="summary__actions">
      <button class="summary__btn-submit" @click="handleCheckout" :disabled="isProcessing">
        <span v-if="!isProcessing">Tiến hành thanh toán</span>
        <span v-else>Đang xử lý...</span>
      </button>
      <a href="#" class="summary__btn-back" @click.prevent="handleCancel">Quay lại</a>
    </div>

    <div class="summary__security">
      <span class="material-symbols-outlined summary__security-icon">security</span>
      <p class="summary__security-text">
        Thông tin thanh toán của bạn được mã hóa và bảo mật tuyệt đối theo tiêu chuẩn quốc tế PCI DSS.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useEmployerOrderStore } from '@/stores/order.store'
import { useToast } from '@/composables/useToast'
import { OrderType, PaymentMethod } from '@/constants/servicePackage.constants'

interface Props {
  subtotal: number
  total: number
  paymentMethod: string
}

const props = defineProps<Props>()

const store = useEmployerOrderStore()
const router = useRouter()
const toast = useToast()
const isProcessing = ref(false)

function format(n: number) {
  return n.toLocaleString('vi-VN') + 'đ'
}

function handleCancel() {
  router.back()
}

async function handleCheckout() {
  isProcessing.value = true
  try {
    const context = store.checkoutContext
    if (!context) {
      toast.error('Lỗi', 'Không tìm thấy thông tin đơn hàng')
      return
    }

    // Cast string to enum
    const paymentMethodEnum = props.paymentMethod.toUpperCase() as PaymentMethod

    if (context.type === OrderType.SUBSCRIPTION) {
      // Tạo đơn hàng subscription (single item – dùng legacy fields)
      if (!context.packageId || !context.billingCycle) {
        toast.error('Lỗi', 'Thông tin gói không hợp lệ')
        return
      }
      
      const order = await store.createOrder({
        type: OrderType.SUBSCRIPTION,
        packageId: context.packageId,
        quantity: 1,
        paymentMethod: paymentMethodEnum,
        payNow: true,
      })

      if (order.paymentUrl) {
        window.location.href = order.paymentUrl
        return
      }
    } else {
      // Tạo 1 đơn hàng addon chứa tất cả dịch vụ lẻ trong giỏ hàng
      if (!store.cartItems.length) {
        toast.error('Lỗi', 'Giỏ hàng trống')
        return
      }

      const order = await store.createOrder({
        type: OrderType.ADDON,
        items: store.cartItems.map(item => ({
          packageId: item.addonServiceId,
          quantity: item.qty,
        })),
        paymentMethod: paymentMethodEnum,
        payNow: true,
      })

      if (order.paymentUrl) {
        window.location.href = order.paymentUrl
        return
      }
    }

    // Clear cart và hiển thị thành công (nếu không redirect)
    store.clearCart()
    toast.success('Thành công', 'Đơn hàng đã được tạo')
    
    // Điều hướng đến trang xác nhận
    router.push('/recruiter/checkout/confirmation')
  } catch (err) {
    toast.error('Lỗi', 'Không thể xử lý thanh toán. Vui lòng thử lại.')
  } finally {
    isProcessing.value = false
  }
}
</script>

<style scoped>
.summary { display: flex; flex-direction: column; gap: 1.5rem; }

.summary__rows {
  display: flex; flex-direction: column; gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
}
.summary__row { display: flex; justify-content: space-between; align-items: center; font-size: 0.875rem; }
.summary__label { color: #64748b; font-weight: 500; }
.summary__value { font-weight: 700; color: #0f172a; }
.summary__value--discount { color: #059669; }

.summary__total {
  display: flex; justify-content: space-between; align-items: center;
  padding-top: 1rem;
  border-top: 1px dashed #e2e8f0;
}
.summary__total-label { font-size: 1.125rem; font-weight: 800; }
.summary__total-right { text-align: right; }
.summary__total-amount { font-size: 1.5rem; font-weight: 800; color: #4B9AF6; }
.summary__total-vat { font-size: 0.75rem; color: #64748b; margin-top: 4px; }

.summary__actions { display: flex; flex-direction: column; gap: 1rem; }
.summary__btn-submit {
  width: 100%;
  background: #4B9AF6;
  color: #fff;
  font-weight: 800;
  font-size: 1rem;
  padding: 1rem 0;
  border: none;
  border-radius: 0.75rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(75,154,246,0.25);
  transition: background 0.2s, transform 0.1s;
}
.summary__btn-submit:hover:not(:disabled) { background: #2563eb; }
.summary__btn-submit:active:not(:disabled) { transform: scale(0.98); }
.summary__btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }

.summary__btn-back {
  display: block;
  text-align: center;
  font-size: 0.875rem;
  font-weight: 700;
  color: #64748b;
  text-decoration: none;
  transition: color 0.2s;
}
.summary__btn-back:hover { color: #4B9AF6; }

.summary__security {
  display: flex; align-items: flex-start; gap: 12px;
  padding: 1rem;
  background: #f8fafd;
  border-radius: 0.75rem;
}
.summary__security-icon { color: #64748b; font-size: 1rem; flex-shrink: 0; }
.summary__security-text { font-size: 0.75rem; line-height: 1.6; color: #64748b; }
</style>