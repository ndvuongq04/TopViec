<template>
  <div class="promo-banner">
    <div class="promo-content">
      <h4 class="promo-title">Ưu đãi Combo Tiết Kiệm</h4>
      <p class="promo-desc">Tiết kiệm đến 30% khi mua gói dịch vụ tổng hợp. Phù hợp cho nhu cầu tuyển dụng quy mô lớn.</p>
      <button class="promo-btn">Xem các gói Combo</button>
    </div>
    <span class="promo-icon material-symbols-outlined">rocket_launch</span>
  </div>
</template>

<style scoped>
.promo-banner {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
  background: linear-gradient(135deg, #4B9AF6, #2563eb);
  padding: 32px;
  color: #fff;
}
.promo-content { position: relative; z-index: 1; max-width: 360px; }
.promo-title { font-size: 1.125rem; font-weight: 800; margin: 0 0 8px; }
.promo-desc { font-size: 0.875rem; color: rgba(255,255,255,0.8); margin: 0 0 24px; line-height: 1.6; }
.promo-btn {
  padding: 12px 24px;
  background: #fff;
  color: #2563eb;
  font-size: 1rem;
  font-weight: 700;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s;
}
.promo-btn:hover { background: #eff6ff; }
.promo-icon {
  position: absolute;
  right: -24px;
  bottom: -24px;
  font-size: 160px;
  color: rgba(255,255,255,0.08);
  transform: rotate(12deg);
  user-select: none;
}
</style>