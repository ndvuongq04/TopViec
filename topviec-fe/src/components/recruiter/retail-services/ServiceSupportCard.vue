<template>
  <div class="support-card">
    <div class="support-icon">
      <span class="material-symbols-outlined">headset_mic</span>
    </div>
    <div>
      <p class="support-title">Cần hỗ trợ tư vấn?</p>
      <p class="support-phone">Gọi ngay: 1900 1234 (Miễn phí)</p>
    </div>
  </div>
</template>

<style scoped>
.support-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: #fff;
  border: 1.5px dashed #e2e8f0;
  border-radius: 16px;
}
.support-icon {
  width: 40px;
  height: 40px;
  background: #f1f5f9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.support-icon .material-symbols-outlined { font-size: 20px; color: #64748b; }
.support-title { font-size: 0.75rem; font-weight: 700; color: #0f172a; margin: 0; }
.support-phone { font-size: 0.75rem; color: #64748b; margin: 2px 0 0; }
</style>