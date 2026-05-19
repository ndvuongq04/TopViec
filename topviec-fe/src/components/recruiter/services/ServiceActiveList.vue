<template>
  <section class="sal-section">
    <div class="sal-header">
      <h3 class="sal-title">Dịch vụ lẻ đang chạy</h3>
      <span class="sal-count">{{ services.length }}</span>
    </div>
    <div class="sal-list">
      <div v-for="svc in services" :key="svc.id" class="sal-item">
        <div class="sal-item-left">
          <div class="sal-svc-info">
            <div
              class="sal-icon"
              :style="{ background: svc.iconBg, color: svc.iconColor }"
            >
              <span class="material-symbols-outlined">{{ svc.icon }}</span>
            </div>
            <div>
              <h5 class="sal-name">{{ svc.name }}</h5>
              <p v-if="svc.description" class="sal-desc">{{ svc.description }}</p>
              <p v-else class="sal-desc sal-desc--empty">Chưa có mô tả</p>
            </div>
          </div>
          <div class="sal-divider" />
          <div class="sal-meta">
            <span class="sal-meta-label">Tình trạng</span>
            <span class="sal-meta-val">{{ svc.remaining }}</span>
          </div>
          <div class="sal-meta">
            <span class="sal-meta-label">Hết hạn</span>
            <span class="sal-meta-val">{{ svc.expireDate ?? '—' }}</span>
          </div>
        </div>

        <div class="sal-item-right">
          <span class="sal-status" :class="svc.status">
            {{ svc.status === 'expired' ? 'Hết hạn' : 'Đang chạy' }}
          </span>
          <button
            v-if="svc.status === 'active'"
            class="sal-apply-btn"
            @click="$emit('apply', svc)"
          >
            <span class="material-symbols-outlined">bolt</span>
            {{ svc.category === 'JOB_POSTING' ? 'Áp dụng cho tin' : 'Áp dụng ngay' }}
          </button>
        </div>
      </div>

      <router-link :to="{ name: 'recruiter-service-shop' }" class="sal-cta">
        <span class="material-symbols-outlined">add_circle</span>
        Mua thêm dịch vụ lẻ
      </router-link>
    </div>
  </section>
</template>

<script setup lang="ts">
export interface ActiveService {
  id:          number
  name:        string
  description: string | null
  icon:        string
  iconBg:      string
  iconColor:   string
  remaining:   string
  expireDate:  string | null
  status:      'active' | 'expired'
  category:    string
}

defineProps<{ services: ActiveService[] }>()
defineEmits<{ apply: [service: ActiveService] }>()
</script>

<style scoped>
.sal-section { display: flex; flex-direction: column; gap: 16px; }
.sal-header { display: flex; align-items: center; gap: 12px; }
.sal-title { font-size: 0.75rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.08em; color: #0f172a; }
.sal-count {
  width: 24px; height: 24px; border-radius: 9999px;
  background: rgba(75,154,246,0.1); color: #4B9AF6;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 700;
}
.sal-list { display: flex; flex-direction: column; gap: 12px; }
.sal-item {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #f8fafc;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: box-shadow 0.2s;
}
.sal-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.sal-item-left { display: flex; align-items: center; gap: 24px; }
.sal-svc-info { display: flex; align-items: center; gap: 16px; }
.sal-icon {
  width: 40px; height: 40px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.sal-name { font-size: 14px; font-weight: 700; color: #0f172a; }
.sal-desc { font-size: 12px; color: #64748b; margin-top: 2px; }
.sal-desc--empty { font-style: italic; color: #cbd5e1; }
.sal-divider { width: 1px; height: 32px; background: #e2e8f0; flex-shrink: 0; }
.sal-meta { display: flex; flex-direction: column; }
.sal-meta-label { font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em; color: #64748b; }
.sal-meta-val { font-size: 0.875rem; font-weight: 700; color: #0f172a; }

.sal-item-right { display: flex; align-items: center; gap: 12px; }
.sal-status {
  padding: 6px 16px;
  font-size: 12px; font-weight: 700;
  border-radius: 9999px;
}
.sal-status.active  { background: #ecfdf5; color: #059669; }
.sal-status.expired { background: #fee2e2; color: #dc2626; }

.sal-apply-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 7px 14px;
  border: none;
  background: #f0fdf4;
  color: #16a34a;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s, color 0.15s, transform 0.1s;
}
.sal-apply-btn:hover  { background: #16a34a; color: #fff; }
.sal-apply-btn:active { transform: scale(0.96); }
.sal-apply-btn .material-symbols-outlined { font-size: 15px; }

.sal-cta {
  width: 100%;
  padding: 16px;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  background: transparent;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  color: #64748b; font-size: 1rem; font-weight: 700;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  transition: all 0.2s;
  text-decoration: none;
}
.sal-cta:hover { color: #4B9AF6; border-color: #4B9AF6; background: rgba(75,154,246,0.05); }
</style>
