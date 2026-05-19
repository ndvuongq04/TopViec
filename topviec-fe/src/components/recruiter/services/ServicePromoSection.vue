<template>
  <section class="sps-grid">
    <div class="sps-ai-card">
      <div class="sps-ai-content">
        <div>
          <h4 class="sps-ai-title">Mở khóa AI Recruitment</h4>
          <p class="sps-ai-desc">Tự động lọc 90% ứng viên không phù hợp bằng hệ thống đánh giá thông minh.</p>
        </div>
        <button class="sps-ai-btn">Tìm hiểu ngay</button>
      </div>
      <span class="material-symbols-outlined sps-ai-bg-icon">psychology</span>
    </div>

    <div class="sps-report-card">
      <div class="sps-report-img-wrap">
        <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuAIUF4QvsRB9gbS5jbIdna14EusqP7CrcKuU1BjOD61Df_JTCJWDP_4KnkMFaYFyGJU_u2cb57GIlJDD5hdtfHU943q0s6VcU1OKL_2iT7SxyoslHETxTeaH6QmsE-pplel_wecFJsUo5tbclBNeK8W5fu7VFv6trMykOaCkbVrhWGxm3iUstyfAN0QHoZTHnw3I_V3_ZXgzLDuXi1CqSl-mOoMkbcxyXA9Bg"
          alt="Báo cáo phân tích" class="sps-report-img" />
      </div>
      <div class="sps-report-info">
        <span class="sps-premium-badge">Premium Only</span>
        <h4 class="sps-report-title">Báo cáo phân tích chuyên sâu</h4>
        <p class="sps-report-desc">Xem chi tiết hành vi ứng viên và tối ưu hóa chi phí tuyển dụng theo tháng.</p>
        <a href="#" class="sps-report-link">
          Nâng cấp ngay
          <span class="material-symbols-outlined">arrow_forward</span>
        </a>
      </div>
    </div>
  </section>
</template>

<style scoped>
.sps-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; padding-top: 16px; }
@media (max-width: 768px) { .sps-grid { grid-template-columns: 1fr; } }

/* AI Card */
.sps-ai-card {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border-radius: 12px; padding: 32px;
  color: #fff; position: relative; overflow: hidden;
}
.sps-ai-content { position: relative; z-index: 1; height: 100%; display: flex; flex-direction: column; justify-content: space-between; }
.sps-ai-title { font-size: 1.125rem; font-weight: 800; line-height: 1.3; margin: 0 0 8px; }
.sps-ai-desc { color: rgba(219,234,254,0.9); font-size: 14px; max-width: 280px; margin: 0; }
.sps-ai-btn {
  margin-top: 24px;
  background: #fff; color: #2563eb;
  padding: 10px 24px; border-radius: 12px;
  font-size: 1rem; font-weight: 800;
  border: none; cursor: pointer;
  width: fit-content;
  font-family: 'Manrope', sans-serif;
  box-shadow: 0 4px 12px rgba(30,58,138,0.2);
  transition: transform 0.15s;
}
.sps-ai-btn:hover { transform: scale(1.05); }
.sps-ai-bg-icon {
  position: absolute; bottom: -40px; right: -40px;
  font-size: 200px; opacity: 0.1;
  transition: transform 0.5s;
}
.sps-ai-card:hover .sps-ai-bg-icon { transform: scale(1.1); }

/* Report Card */
.sps-report-card {
  background: #f8fafd;
  border-radius: 12px; padding: 32px;
  border: 1px solid #e2e8f0;
  display: flex; align-items: center; gap: 32px;
}
.sps-report-img-wrap {
  width: 128px; height: 128px; flex-shrink: 0;
  background: #fff; border-radius: 16px;
  box-shadow: 0 8px 24px rgba(148,163,184,0.3);
  padding: 8px; transform: rotate(-3deg);
  overflow: hidden;
}
.sps-report-img { width: 100%; height: 100%; object-fit: cover; border-radius: 10px; }
.sps-report-info { display: flex; flex-direction: column; gap: 12px; }
.sps-premium-badge {
  display: inline-block;
  padding: 4px 8px;
  background: #fef3c7; color: #b45309;
  font-size: 0.75rem; font-weight: 800;
  text-transform: uppercase; letter-spacing: 0.08em;
  border-radius: 4px; width: fit-content;
}
.sps-report-title { font-size: 18px; font-weight: 800; color: #0f172a; margin: 0; }
.sps-report-desc { font-size: 14px; color: #64748b; margin: 0; }
.sps-report-link {
  display: inline-flex; align-items: center; gap: 4px;
  color: #4B9AF6; font-size: 14px; font-weight: 700;
  text-decoration: none;
}
.sps-report-link:hover { text-decoration: underline; }
.sps-report-link .material-symbols-outlined { font-size: 16px; }
</style>