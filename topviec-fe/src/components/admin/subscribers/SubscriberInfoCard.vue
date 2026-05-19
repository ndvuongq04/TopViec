<script setup lang="ts">
const company = {
  name: 'ABC Corp',
  email: 'contact@abccorp.vn',
  phone: '0987 654 321',
  registeredAt: '01/01/2024',
  totalOrders: '12 đơn',
  totalSpent: '14.256.000 đ',
}

const account = {
  status: 'Đang hoạt động',
  lastLogin: '13/04/2025 · 14:32',
}
</script>

<template>
  <section class="info-card">
    <div class="info-left">
      <label class="section-label">THÔNG TIN CÔNG TY</label>
      <div class="info-grid">
        <div class="info-item">
          <p class="item-label">Tên công ty</p>
          <p class="item-value item-value--bold">{{ company.name }}</p>
        </div>
        <div class="info-item">
          <p class="item-label">Email</p>
          <p class="item-value">{{ company.email }}</p>
        </div>
        <div class="info-item">
          <p class="item-label">Số điện thoại</p>
          <p class="item-value">{{ company.phone }}</p>
        </div>
        <div class="info-item">
          <p class="item-label">Ngày đăng ký</p>
          <p class="item-value">{{ company.registeredAt }}</p>
        </div>
        <div class="info-item">
          <p class="item-label">Tổng đơn hàng</p>
          <p class="item-value item-value--bold">{{ company.totalOrders }}</p>
        </div>
        <div class="info-item">
          <p class="item-label">Tổng chi tiêu</p>
          <p class="item-value item-value--primary">{{ company.totalSpent }}</p>
        </div>
      </div>
    </div>
    <div class="info-right">
      <label class="section-label">TRẠNG THÁI TÀI KHOẢN</label>
      <div class="status-badge-wrap">
        <span class="status-badge">
          <span class="status-dot"></span>
          {{ account.status }}
        </span>
      </div>
      <p class="last-login">
        Lần đăng nhập cuối:<br />
        <span class="last-login-value">{{ account.lastLogin }}</span>
      </p>
    </div>
  </section>
</template>

<style scoped>
.info-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  display: flex;
  box-shadow: 0 12px 32px -4px rgba(87, 66, 64, 0.06);
}

.info-left {
  flex: 1;
  padding-right: 48px;
}

.info-right {
  width: 320px;
  padding-left: 48px;
  border-left: 1px solid rgba(221, 192, 190, 0.2);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.section-label {
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  color: #574240;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 24px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px 48px;
}

.item-label {
  font-size: 0.75rem;
  color: rgba(87, 66, 64, 0.7);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-weight: 600;
  margin: 0 0 4px;
}

.item-value {
  font-size: 0.875rem;
  font-weight: 500;
  color: #1b1c18;
  margin: 0;
}

.item-value--bold {
  font-weight: 700;
}

.item-value--primary {
  font-size: 1rem;
  font-weight: 700;
  color: #963131;
}

.status-badge-wrap {
  margin-bottom: 16px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 16px;
  border-radius: 9999px;
  background: #b0efdb;
  color: #095041;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.03em;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #004638;
  border-radius: 50%;
  margin-right: 8px;
}

.last-login {
  font-size: 0.875rem;
  color: #574240;
  font-weight: 500;
  line-height: 1.6;
  margin: 0;
}

.last-login-value {
  color: #1b1c18;
}

@media (max-width: 1024px) {
  .info-card {
    flex-direction: column;
  }
  .info-left {
    padding-right: 0;
    margin-bottom: 24px;
  }
  .info-right {
    width: 100%;
    padding-left: 0;
    border-left: none;
    border-top: 1px solid rgba(221, 192, 190, 0.2);
    padding-top: 24px;
  }
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>