<template>
  <footer class="pricing-footer">
    <p class="pricing-footer__note">
      Giá trên chưa bao gồm thuế VAT (10%). <br/>
      Bạn có thể nâng cấp hoặc hạ cấp gói dịch vụ bất kỳ lúc nào. Việc huỷ gia hạn cần được thực hiện ít nhất 24 giờ trước khi chu kỳ thanh toán tiếp theo bắt đầu.
    </p>
    <div class="pricing-footer__payments">
      <img v-for="pm in paymentMethods" :key="pm.alt" :alt="pm.alt" :src="pm.src" class="pricing-footer__payment-logo" />
    </div>
  </footer>
</template>

<script setup lang="ts">
const paymentMethods = [
  { alt: 'Visa', src: 'https://upload.wikimedia.org/wikipedia/commons/5/5e/Visa_Inc._logo.svg' },
  { alt: 'Mastercard', src: 'https://upload.wikimedia.org/wikipedia/commons/2/2a/Mastercard-logo.svg' },
  { alt: 'PayPal', src: 'https://upload.wikimedia.org/wikipedia/commons/b/b5/PayPal.svg' },
]
</script>

<style scoped>
.pricing-footer {
  text-align: center;
  padding-bottom: 3rem;
}
.pricing-footer__note {
  font-size: 0.75rem;
  color: #64748b;
  line-height: 1.6;
}
.pricing-footer__payments {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
}
.pricing-footer__payment-logo {
  height: 24px;
  filter: grayscale(1);
  opacity: 0.5;
}
</style>