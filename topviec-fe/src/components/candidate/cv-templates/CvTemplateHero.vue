<template>
  <section class="hero-section">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div class="hero-deco-blob"></div>

      <div class="hero-text">
        <h1 class="hero-title">
          Mẫu CV xin việc
          <span class="hero-highlight">tiếng Việt</span>
          chuẩn 2026
        </h1>
        <p class="hero-desc">
          Tuyển chọn các mẫu CV tiếng Việt có thiết kế đơn giản, ưu tiên tính dễ đọc và dễ sử dụng.
          Dành cho ứng viên muốn tập trung vào khả năng truyền tải thông tin một cách đầy đủ và rõ ràng.
        </p>
      </div>

      <div class="hero-illustration">
        <span class="material-symbols-outlined">smart_toy</span>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

/* Hero Banner */
.hero-banner {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f0ff 100%);
  border-radius: 16px;
  padding: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  border: 1px solid rgba(19, 127, 236, 0.1);
}
.hero-deco-blob {
  position: absolute;
  top: 0; right: 0;
  width: 260px; height: 260px;
  background: #137fec;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.12;
  transform: translate(30%, -50%);
  pointer-events: none;
}

.hero-text { position: relative; z-index: 1; max-width: 600px; }
.hero-title {
  font-size: clamp(1.875rem, 4vw, 2.5rem);
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
  line-height: 1.2;
  margin: 0 0 16px;
}
.hero-highlight { color: #137fec; }
.hero-desc {
  font-size: 1rem;
  color: #475569;
  line-height: 1.65;
  margin: 0;
}

.hero-illustration {
  position: relative;
  z-index: 1;
  width: 140px; height: 140px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 32px rgba(19, 127, 236, 0.15);
  flex-shrink: 0;
}
.hero-illustration .material-symbols-outlined {
  font-size: 64px;
  color: #137fec;
}

@media (max-width: 720px) {
  .hero-illustration { display: none; }
  .hero-banner { padding: 32px 24px; }
}
</style>