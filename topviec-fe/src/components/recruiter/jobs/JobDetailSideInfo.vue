<template>
  <div class="sidebar-sections">

    <!-- Thông tin chung -->
    <section class="side-card">
      <h3 class="side-title">Thông tin chung</h3>
      <div class="info-list">
        <div
          v-for="(item, i) in infoItems"
          :key="item.label"
          class="info-row"
          :class="{ 'no-border': i === infoItems.length - 1 }"
        >
          <div class="info-label">
            <span class="material-symbols-outlined info-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </div>
          <span
            class="info-value"
            :class="{ 'salary-value': item.highlight }"
          >{{ item.value }}</span>
        </div>
      </div>
    </section>

    <!-- Mốc thời gian -->
    <section class="side-card">
      <h3 class="side-title">Mốc thời gian</h3>
      <div class="timeline">
        <div
          v-for="(event, i) in timeline"
          :key="event.label"
          class="timeline-item"
        >
          <div
            class="timeline-dot"
            :class="{ 'dot-error': event.isDeadline }"
          ></div>
          <div class="timeline-body">
            <p class="timeline-label">{{ event.label }}</p>
            <p
              class="timeline-date"
              :class="{ 'date-error': event.isDeadline }"
            >{{ event.date }}</p>
          </div>
          <!-- Connector line (except last) -->
          <div v-if="i < timeline.length - 1" class="timeline-line"></div>
        </div>
      </div>
    </section>

    <!-- Chỉ số chuyển đổi -->
    <section class="conversion-card">
      <div class="conversion-header">
        <h3 class="conversion-title">Chỉ số chuyển đổi</h3>
        <span class="conversion-badge">3.4%</span>
      </div>
      <div class="progress-track">
        <div class="progress-fill" :style="{ width: `${conversionPercent}%` }"></div>
      </div>
      <p class="conversion-note">Đã đạt {{ conversionPercent }}/100 CV mục tiêu cho vị trí này.</p>
    </section>

  </div>
</template>

<script setup lang="ts">


interface InfoItem {
  label: string
  value: string
  icon: string
  highlight?: boolean
}
interface TimelineEvent {
  label: string
  date: string
  isDeadline?: boolean
}

withDefaults(defineProps<{
  infoItems?: InfoItem[]
  timeline?: TimelineEvent[]
  conversionPercent?: number
}>(), {
  infoItems: () => [
    { label: 'Ngành nghề', value: 'IT - Phần mềm',       icon: 'category' },
    { label: 'Kinh nghiệm', value: '4 năm trở lên',       icon: 'history' },
    { label: 'Mức lương',   value: '$1500 - $2500',       icon: 'monetization_on', highlight: true },
    { label: 'Hình thức',   value: 'Toàn thời gian',      icon: 'work' },
    { label: 'Số lượng',    value: '3 người',             icon: 'person_add' },
  ],
  timeline: () => [
    { label: 'Ngày tạo',  date: '15/10/2023 09:30' },
    { label: 'Ngày đăng', date: '16/10/2023 14:20' },
    { label: 'Hạn cuối',  date: '15/11/2023', isDeadline: true },
  ],
  conversionPercent: 42,
})
</script>

<style scoped>
.sidebar-sections {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* Shared card */
.side-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,.05);
}

.side-title {
  font-size: 1.125rem; /* Nâng từ 16px lên 18px để nổi bật heading thẻ con */
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 1.5rem;
}

/* Info list */
.info-list { display: flex; flex-direction: column; gap: 0; }

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}
.info-row.no-border {
  padding-bottom: 0;
  margin-bottom: 0;
  border-bottom: none;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem; /* 14px Data Cell */
  color: #64748b;
}
.info-icon { font-size: 1rem; }

.info-value {
  font-size: 0.875rem; /* 14px Data Cell */
  font-weight: 600;
  color: #0f172a;
}
.salary-value {
  color: #2563eb;
  font-weight: 700;
}

/* Timeline */
.timeline {
  display: flex;
  flex-direction: column;
  gap: 0;
  position: relative;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  position: relative;
}

.timeline-dot {
  width: 1rem;
  height: 1rem;
  border-radius: 50%;
  border: 3px solid #fff;
  background: #4B9AF6;
  box-shadow: 0 0 0 2px #4B9AF6;
  flex-shrink: 0;
  margin-top: 0.125rem;
  z-index: 1;
}
.dot-error {
  background: #ef4444;
  box-shadow: 0 0 0 2px #ef4444;
}

.timeline-body {
  padding-bottom: 1.5rem;
  flex: 1;
}
.timeline-item:last-child .timeline-body { padding-bottom: 0; }

.timeline-label {
  font-size: 0.75rem; /* 12px x uppercase */
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.25rem;
}

.timeline-date {
  font-size: 0.875rem; /* 14px Focus Target Data */
  font-weight: 500;
  color: #0f172a;
}
.date-error {
  color: #ef4444;
  font-weight: 700;
}

/* Vertical connector between dots */
.timeline-line {
  position: absolute;
  left: 0.4375rem; /* center of 1rem dot */
  top: 1.125rem;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
  z-index: 0;
}

/* Conversion card */
.conversion-card {
  background: #dbeafe;
  border: 1px solid rgba(75,154,246,.2);
  border-radius: 1rem;
  padding: 1.5rem;
}

.conversion-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.conversion-title {
  font-size: 1rem; /* Nâng từ 14px lên 16px để đồng bộ SubItem Title */
  font-weight: 700;
  color: #1e40af;
}

.conversion-badge {
  font-size: 0.75rem; /* 12px Tiny Tag */
  font-weight: 700;
  background: #fff;
  color: #4B9AF6;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
}

.progress-track {
  height: 0.5rem;
  width: 100%;
  background: rgba(255,255,255,.5);
  border-radius: 9999px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill {
  height: 100%;
  background: #4B9AF6;
  border-radius: 9999px;
  transition: width 0.6s ease;
}

.conversion-note {
  font-size: 0.75rem; /* 12px Ghi chú */
  color: rgba(30,64,175,.7);
  font-style: italic;
}
</style>
