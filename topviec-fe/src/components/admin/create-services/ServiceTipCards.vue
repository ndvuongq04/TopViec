<template>
  <div class="tips-grid">
    <div class="tip-card tip-primary">
      <span class="material-symbols-outlined tip-icon">auto_awesome</span>
      <h4 class="tip-title">Mẹo cấu hình</h4>
      <p class="tip-desc">Sử dụng tên dịch vụ ngắn gọn và súc tích để tối ưu hóa hiển thị trên ứng dụng di động của khách hàng.</p>
    </div>
    <div class="tip-card tip-tertiary">
      <span class="material-symbols-outlined tip-icon">verified</span>
      <h4 class="tip-title">Tiêu chuẩn Premium</h4>
      <p class="tip-desc">Tất cả các dịch vụ mới sẽ được tự động áp dụng bộ lọc nhận diện thương hiệu cho gói Premium.</p>
    </div>
  </div>
</template>

<style scoped>
.tips-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  opacity: 0.65;
}
.tip-card {
  padding: 24px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.tip-primary {
  background: rgba(150, 49, 49, 0.04);
  border: 1px solid rgba(118, 25, 29, 0.1);
}
.tip-tertiary {
  background: rgba(30, 94, 78, 0.04);
  border: 1px solid rgba(0, 70, 56, 0.1);
}
.tip-icon { font-size: 22px; color: #963131; }
.tip-tertiary .tip-icon { color: #1e5e4e; }

.tip-title {
  font-size: 18px;
  font-weight: 700;
  color: #963131;
  margin: 0;
}
.tip-tertiary .tip-title { color: #1e5e4e; }

.tip-desc {
  font-size: 14px;
  color: #574240;
  line-height: 1.6;
  margin: 0;
}
</style>