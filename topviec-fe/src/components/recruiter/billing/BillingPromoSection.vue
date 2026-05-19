<template>
  <div class="promo">
    <!-- App Download Banner -->
    <div class="promo__app">
      <div class="promo__app-content">
        <h3 class="promo__app-title">Tải ứng dụng quản lý</h3>
        <p class="promo__app-desc">Theo dõi đơn hàng và duyệt hồ sơ ứng viên ngay trên điện thoại của bạn.</p>
        <div class="promo__app-btns">
          <button class="promo__store-btn">
            <span class="material-symbols-outlined">ios</span>
            <div>
              <p class="promo__store-sub">Download on</p>
              <p class="promo__store-name">App Store</p>
            </div>
          </button>
          <button class="promo__store-btn">
            <span class="material-symbols-outlined">play_store_installed</span>
            <div>
              <p class="promo__store-sub">Get it on</p>
              <p class="promo__store-name">Google Play</p>
            </div>
          </button>
        </div>
      </div>
      <div class="promo__app-blur"></div>
    </div>

    <!-- VAT Card -->
    <div class="promo__vat">
      <div>
        <div class="promo__vat-icon">
          <span class="material-symbols-outlined">workspace_premium</span>
        </div>
        <h4 class="promo__vat-title">Cần hóa đơn VAT?</h4>
        <p class="promo__vat-desc">Tất cả hóa đơn điện tử sẽ được gửi tự động qua email của bạn sau khi thanh toán thành công.</p>
      </div>
      <a href="#" class="promo__vat-link">
        <span>Liên hệ kế toán</span>
        <span class="material-symbols-outlined">arrow_forward</span>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts" />

<style scoped>
.promo {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 1.5rem;
  margin-top: 3rem;
}
@media (max-width: 768px) { .promo { grid-template-columns: 1fr; } }

/* App banner */
.promo__app {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  border-radius: 1.5rem;
  padding: 2rem;
  color: #fff;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 8px 32px rgba(59,130,246,0.25);
  position: relative; overflow: hidden;
}
.promo__app-content { position: relative; z-index: 1; }
.promo__app-title { font-size: 1.5rem; font-weight: 800; margin-bottom: 8px; }
.promo__app-desc { font-size: 0.875rem; color: rgba(255,255,255,0.8); max-width: 280px; margin-bottom: 1.5rem; line-height: 1.5; }
.promo__app-btns { display: flex; gap: 1rem; flex-wrap: wrap; }

.promo__store-btn {
  display: flex; align-items: center; gap: 12px;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 0.75rem; padding: 10px 20px;
  color: #fff; cursor: pointer;
  transition: background 0.2s;
  backdrop-filter: blur(8px);
}
.promo__store-btn:hover { background: rgba(255,255,255,0.2); }
.promo__store-sub { font-size: 0.75rem; opacity: 0.7; line-height: 1; }
.promo__store-name { font-size: 0.875rem; font-weight: 700; line-height: 1; margin-top: 2px; }

.promo__app-blur {
  position: absolute; top: -40px; right: -40px;
  width: 200px; height: 200px;
  background: rgba(255,255,255,0.08);
  border-radius: 50%; filter: blur(40px);
}

/* VAT card */
.promo__vat {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2rem;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  display: flex; flex-direction: column; justify-content: space-between;
}
.promo__vat-icon {
  width: 48px; height: 48px;
  background: #fffbeb; border-radius: 1rem;
  display: flex; align-items: center; justify-content: center;
  color: #f59e0b; margin-bottom: 1.5rem;
  font-variation-settings: 'FILL' 1;
}
.promo__vat-title { font-size: 1.125rem; font-weight: 800; color: #0f172a; margin-bottom: 8px; }
.promo__vat-desc { font-size: 0.875rem; color: #64748b; line-height: 1.6; }

.promo__vat-link {
  display: flex; align-items: center; gap: 8px;
  font-size: 0.875rem; font-weight: 700; color: #4B9AF6;
  text-decoration: none; margin-top: 1.5rem;
  transition: gap 0.2s;
}
.promo__vat-link:hover { gap: 12px; }
.promo__vat-link .material-symbols-outlined { font-size: 16px; }
</style>