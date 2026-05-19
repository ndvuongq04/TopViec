<template>
  <div class="header-wrap">
    <!-- Breadcrumb -->
    <nav class="breadcrumb">
      <router-link to="/recruiter/jobs" class="breadcrumb-link">Quản lý tin tuyển dụng</router-link>
      <span class="material-symbols-outlined breadcrumb-sep">chevron_right</span>
      <span class="breadcrumb-current">Chi tiết tin</span>
    </nav>

    <!-- Title row -->
    <header class="header-body">
      <div class="header-left">
        <div class="title-row">
          <h1 class="page-title">{{ title }}</h1>
          <span class="status-badge" :class="statusBadgeClass">
            <span class="status-dot" :class="statusDotClass"></span>
            {{ statusLabel }}
          </span>
        </div>
        <p class="header-location">
          <span class="material-symbols-outlined loc-icon">location_on</span>
          {{ location }}
        </p>
      </div>

    </header>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const STATUS_LABELS: Record<string, string> = {
  draft:            'Bản nháp',
  pending_approval: 'Chờ duyệt',
  published:        'Đang tuyển',
  hidden:           'Đã ẩn',
  paused:           'Tạm dừng',
  closed:           'Đã đóng',
  expired:          'Hết hạn',
  rejected:         'Bị từ chối',
  renewed:          'Đã gia hạn',
  scheduled:        'Đã lên lịch',
  interviewing:     'Đang phỏng vấn',
  completed:        'Đã hoàn thành',
}

const props = withDefaults(defineProps<{
  title?: string
  location?: string
  status?: string
}>(), {
  title: '',
  location: '',
  status: '',
})

const statusLabel = computed(() => STATUS_LABELS[props.status ?? ''] ?? props.status ?? '')

const statusBadgeClass = computed(() => ({
  'status-badge--active':       ['published', 'renewed'].includes(props.status ?? ''),
  'status-badge--pending':      props.status === 'pending_approval',
  'status-badge--draft':        props.status === 'draft',
  'status-badge--hidden':       props.status === 'hidden',
  'status-badge--paused':       props.status === 'paused',
  'status-badge--closed':       ['closed', 'expired'].includes(props.status ?? ''),
  'status-badge--rejected':     props.status === 'rejected',
  'status-badge--interviewing': props.status === 'interviewing',
  'status-badge--completed':    props.status === 'completed',
}))

const statusDotClass = computed(() => ({
  'status-dot--pulse': ['published', 'renewed', 'interviewing'].includes(props.status ?? ''),
}))
</script>

<style scoped>
.header-wrap {
  margin-bottom: 2.5rem;
}

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem; /* Tăng từ 12px lên 14px */
  color: #64748b;
  margin-bottom: 1.5rem;
}
.breadcrumb-link {
  color: inherit;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.15s;
}
.breadcrumb-link:hover { color: #4B9AF6; }
.breadcrumb-sep { font-size: 0.75rem; } /* Tăng từ 10px lên 12px */
.breadcrumb-current { color: #0f172a; font-weight: 500; }

/* Header body */
.header-body {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
@media (min-width: 768px) {
  .header-body {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }
}

.header-left { display: flex; flex-direction: column; gap: 0.5rem; }

.title-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.page-title {
  font-size: 1.875rem; /* Chuẩn 30px */
  font-weight: 800;
  letter-spacing: -0.025em;
  color: #0f172a;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 9999px;
  background: #f1f5f9;
  color: #64748b;
}
.status-badge--active       { background: #dbeafe; color: #2563eb; }
.status-badge--pending      { background: #fef3c7; color: #b45309; }
.status-badge--draft        { background: #f1f5f9; color: #64748b; }
.status-badge--hidden       { background: #fef3c7; color: #92400e; }
.status-badge--paused       { background: #ffedd5; color: #c2410c; }
.status-badge--closed       { background: #f1f5f9; color: #94a3b8; }
.status-badge--rejected     { background: #ffe4e6; color: #be123c; }
.status-badge--interviewing { background: #e0f2fe; color: #0369a1; }
.status-badge--completed    { background: #e0e7ff; color: #4338ca; }

.status-dot {
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 50%;
  background: currentColor;
}
.status-dot--pulse { animation: pulse 2s infinite; }
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.header-location {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.875rem; /* 14px */
  color: #64748b;
}
.loc-icon { font-size: 1rem; }

/* Actions */
.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn-outline,
.btn-danger {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-size: 1rem; /* Tăng từ 14px lên 16px để đồng bộ action button cấp 1 */
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s, opacity 0.15s;
}
.btn-outline .material-symbols-outlined,
.btn-danger .material-symbols-outlined {
  font-size: 1.125rem;
}
.btn-outline {
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #0f172a;
}
.btn-outline:hover { background: #f1f5f9; }

.btn-danger {
  border: none;
  background: #fee2e2;
  color: #b91c1c;
}
.btn-danger:hover { opacity: 0.85; }
</style>
