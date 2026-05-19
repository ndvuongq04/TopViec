<template>
  <section class="section-card">
    <div class="section-header">
      <div class="icon-wrap icon-white">
        <span class="material-symbols-outlined">auto_awesome</span>
      </div>
      <h3 class="section-title">Tùy chọn nâng cao</h3>
    </div>

    <div class="advanced-grid">
      <div class="toggles">
        <!-- Featured -->
        <div class="toggle-row">
          <div class="toggle-info">
            <p class="toggle-title">Tin nổi bật</p>
            <p class="toggle-desc">Hiển thị ở vị trí ưu tiên trên trang chủ và kết quả tìm kiếm</p>
          </div>
          <label class="toggle-switch">
            <input v-model="form.isFeatured" type="checkbox" />
            <span class="toggle-track"></span>
          </label>
        </div>

        <div class="divider"></div>

        <!-- Urgent -->
        <div class="toggle-row">
          <div class="toggle-info">
            <p class="toggle-title">Tin khẩn cấp</p>
            <p class="toggle-desc">Gắn nhãn "Tuyển gấp" để thu hút ứng viên nộp hồ sơ nhanh</p>
          </div>
          <label class="toggle-switch urgent">
            <input v-model="form.isUrgent" type="checkbox" />
            <span class="toggle-track"></span>
          </label>
        </div>
      </div>

      <!-- Tip box -->
      <div class="tip-box">
        <span class="material-symbols-outlined tip-icon">tips_and_updates</span>
        <div>
          <p class="tip-title">Mẹo tuyển dụng</p>
          <p class="tip-desc">
            Tin được gắn nhãn <span class="highlight-amber">Nổi bật</span> và
            <span class="highlight-red">Tuyển gấp</span> giúp tăng tỷ lệ tiếp cận ứng viên
            chất lượng lên đến <span class="highlight-primary">300%</span> so với tin thường.
          </p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { inject } from 'vue'
import { CREATE_JOB_FORM_KEY } from '@/composables/useCreateJobForm'

const form = inject(CREATE_JOB_FORM_KEY)!
</script>

<style scoped>
.section-card {
  background: linear-gradient(135deg, #eff6ff 0%, rgba(219,234,254,.6) 100%);
  border-radius: 1.5rem;
  padding: 2rem;
  border: 1px solid #bfdbfe;
}
.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}
.icon-wrap {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-white {
  background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,.08);
  color: #4B9AF6;
}
.section-title { font-size: 1.125rem; font-weight: 700; color: #0f172a; }

.advanced-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  align-items: start;
}

/* Toggles */
.toggles { display: flex; flex-direction: column; gap: 1.25rem; }
.toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}
.toggle-info { flex: 1; }
.toggle-title { font-size: 1rem; font-weight: 700; color: #1e293b; }
.toggle-desc { font-size: 0.875rem; color: #64748b; margin-top: 0.125rem; }

.toggle-switch { position: relative; display: inline-flex; align-items: center; cursor: pointer; flex-shrink: 0; margin-top: 0.125rem; }
.toggle-switch input { position: absolute; opacity: 0; width: 0; height: 0; }

.toggle-track {
  display: block;
  width: 2.75rem;
  height: 1.5rem;
  background: #cbd5e1;
  border-radius: 9999px;
  transition: background 0.2s;
  position: relative;
}
.toggle-track::after {
  content: '';
  position: absolute;
  top: 0.125rem;
  left: 0.125rem;
  width: 1.25rem;
  height: 1.25rem;
  background: #fff;
  border-radius: 50%;
  transition: transform 0.2s;
  box-shadow: 0 1px 3px rgba(0,0,0,.2);
}
.toggle-switch input:checked + .toggle-track { background: #4B9AF6; }
.toggle-switch input:checked + .toggle-track::after { transform: translateX(1.25rem); }

/* Urgent variant — red */
.toggle-switch.urgent input:checked + .toggle-track { background: #ef4444; }

.divider { height: 1px; background: rgba(147,197,253,.5); }

/* Tip box */
.tip-box {
  display: flex;
  gap: 0.75rem;
  padding: 1.25rem;
  background: rgba(255,255,255,.6);
  border-radius: 1rem;
  border: 1px solid rgba(255,255,255,.8);
}
.tip-icon { color: #4B9AF6; font-size: 1.5rem; flex-shrink: 0; margin-top: 0.125rem; }
.tip-title { font-weight: 700; color: #0f172a; font-size: 1rem; }
.tip-desc { font-size: 0.875rem; color: #64748b; margin-top: 0.375rem; line-height: 1.6; }
.highlight-amber { color: #f59e0b; font-weight: 600; }
.highlight-red { color: #ef4444; font-weight: 600; }
.highlight-primary { color: #4B9AF6; font-weight: 700; }

@media (max-width: 768px) {
  .advanced-grid { grid-template-columns: 1fr; }
}
</style>
