<template>
  <div class="overdue-table">
    <!-- Loading overlay -->
    <div v-if="loading" class="overdue-table__loading">
      <div class="overdue-table__spinner" />
    </div>

    <!-- Table header -->
    <div class="overdue-table__header">
      <h3 class="overdue-table__header-title">Danh sách ứng viên</h3>
    </div>

    <div class="overdue-table__scroll">
      <table class="overdue-table__el">
        <thead>
          <tr class="overdue-table__thead-row">
            <th class="overdue-table__th">Ứng viên &amp; Thông tin</th>
            <th class="overdue-table__th overdue-table__th--center">Số lần nhắc</th>
            <th class="overdue-table__th">Tình trạng</th>
            <th class="overdue-table__th">Hạn phản hồi</th>
            <th class="overdue-table__th overdue-table__th--right">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <!-- Data rows -->
          <tr
            v-for="app in items"
            :key="app.applicationId"
            class="overdue-table__row"
          >
            <!-- Ứng viên -->
            <td class="overdue-table__td">
              <div class="overdue-table__candidate">
                <img
                  :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(app.candidateName)}&background=random`"
                  :alt="app.candidateName"
                  class="overdue-table__avatar"
                />
                <div>
                  <p class="overdue-table__candidate-name">{{ app.candidateName }}</p>
                  <p class="overdue-table__candidate-email">{{ app.candidateEmail }}</p>
                </div>
              </div>
            </td>

            <!-- Số lần nhắc -->
            <td class="overdue-table__td overdue-table__td--center">
              <span class="overdue-table__reminder-badge">{{ app.reminderCount }} lần</span>
            </td>

            <!-- Tình trạng -->
            <td class="overdue-table__td">
              <div class="overdue-table__status">
                <p class="overdue-table__status-text">{{ formatOverdueText(app.reminderDeadline) }}</p>
                <div class="overdue-table__progress">
                  <div class="overdue-table__progress-bar" style="width: 100%" />
                </div>
              </div>
            </td>

            <!-- Hạn phản hồi -->
            <td class="overdue-table__td overdue-table__td--date">
              {{ new Date(app.reminderDeadline).toLocaleString('vi-VN') }}
            </td>

            <!-- Thao tác -->
            <td class="overdue-table__td">
              <div class="overdue-table__actions">
                <button
                  class="overdue-table__action-btn overdue-table__action-btn--extend"
                  title="Gia hạn thêm thời gian"
                  @click="$emit('extend', app.applicationId)"
                >
                  <span class="material-symbols-outlined">more_time</span>
                </button>
                <button
                  class="overdue-table__action-btn overdue-table__action-btn--schedule"
                  title="Đặt lịch hộ ứng viên"
                  @click="$emit('force-schedule', app.applicationId)"
                >
                  <span class="material-symbols-outlined">event_available</span>
                </button>
              </div>
            </td>
          </tr>

          <!-- Empty state -->
          <tr v-if="!items.length && !loading">
            <td colspan="5" class="overdue-table__empty">
              <span class="material-symbols-outlined overdue-table__empty-icon">check_circle</span>
              <p>Tuyệt vời! Hiện không có ứng viên nào bị quá hạn phản hồi.</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  items: {
    applicationId: number
    candidateName: string
    candidateEmail: string
    reminderCount: number
    reminderDeadline: string
  }[]
  loading: boolean
}>()

defineEmits<{
  (e: 'extend', id: number): void
  (e: 'force-schedule', id: number): void
}>()

function formatOverdueText(deadlineStr: string) {
  const deadline = new Date(deadlineStr)
  const now = new Date()
  const diffDays = Math.ceil((now.getTime() - deadline.getTime()) / (1000 * 60 * 60 * 24))
  return diffDays > 0 ? `Quá hạn ${diffDays} ngày` : 'Sắp hết hạn'
}
</script>

<style scoped>
.overdue-table {
  background: #fff;
  border-radius: 1rem;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  overflow: hidden;
  position: relative;
  min-height: 18rem;
}

/* Loading overlay */
.overdue-table__loading {
  position: absolute;
  inset: 0;
  z-index: 10;
  background: rgba(255,255,255,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(1px);
}

.overdue-table__spinner {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  border: 2.5px solid transparent;
  border-bottom-color: #4B9AF6;
  animation: spin 0.7s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Header row */
.overdue-table__header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid rgba(241,245,249,0.5);
  background: rgba(248,250,252,0.3);
}

.overdue-table__header-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

/* Scroll wrapper */
.overdue-table__scroll {
  overflow-x: auto;
}

/* Table */
.overdue-table__el {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

/* Thead */
.overdue-table__thead-row {
  background: rgba(248,250,252,0.5);
}

.overdue-table__th {
  padding: 1rem 1.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #94a3b8;
  white-space: nowrap;
}

.overdue-table__th--center { text-align: center; }
.overdue-table__th--right  { text-align: right; }

/* Body rows */
.overdue-table__row {
  border-top: 1px solid rgba(241,245,249,0.5);
  transition: background 0.15s;
}

.overdue-table__row:hover {
  background: rgba(248,250,252,0.8);
}

/* Actions — visible on hover */
.overdue-table__row .overdue-table__actions {
  opacity: 1;
}

.overdue-table__td {
  padding: 1.25rem 1.5rem;
  vertical-align: middle;
}

.overdue-table__td--center { text-align: center; }

.overdue-table__td--date {
  font-size: 0.875rem;
  font-weight: 500;
  color: #64748b;
  white-space: nowrap;
}

/* Candidate cell */
.overdue-table__candidate {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.overdue-table__avatar {
  width: 2.75rem;
  height: 2.75rem;
  border-radius: 50%;
  object-fit: cover;
  background: #f1f5f9;
  flex-shrink: 0;
}

.overdue-table__candidate-name {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 0.15rem;
}

.overdue-table__candidate-email {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0;
}

/* Reminder badge */
.overdue-table__reminder-badge {
  display: inline-block;
  padding: 0.2rem 0.75rem;
  border-radius: 9999px;
  background: #f1f5f9;
  color: #475569;
  font-size: 0.875rem;
  font-weight: 700;
}

/* Status */
.overdue-table__status {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  min-width: 7rem;
}

.overdue-table__status-text {
  font-size: 0.875rem;
  font-weight: 700;
  color: #ef4444;
  margin: 0;
}

.overdue-table__progress {
  width: 100%;
  height: 0.375rem;
  background: #f1f5f9;
  border-radius: 9999px;
  overflow: hidden;
}

.overdue-table__progress-bar {
  height: 100%;
  background: #ef4444;
  border-radius: 9999px;
  transition: width 0.4s;
}

/* Actions */
.overdue-table__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.25rem;
}

.overdue-table__action-btn {
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 0.625rem;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}

.overdue-table__action-btn .material-symbols-outlined {
  font-size: 1.2rem;
}

.overdue-table__action-btn--extend {
  background: transparent;
  color: #4B9AF6;
}

.overdue-table__action-btn--extend:hover {
  background: rgba(75,154,246,0.1);
}

.overdue-table__action-btn--schedule {
  background: transparent;
  color: #10b981;
}

.overdue-table__action-btn--schedule:hover {
  background: rgba(16,185,129,0.08);
}

/* Empty state */
.overdue-table__empty {
  padding: 5rem 1.5rem;
  text-align: center;
  color: #94a3b8;
}

.overdue-table__empty-icon {
  font-size: 2.5rem;
  display: block;
  margin-bottom: 0.5rem;
}

.overdue-table__empty p {
  font-size: 0.875rem;
  margin: 0;
}
</style>