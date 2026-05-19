<template>
  <div class="info-card">
    <!-- Avatar + contact buttons -->
    <div class="info-card__top">
      <div class="info-card__avatar">
        <img :src="candidate.avatarUrl" :alt="candidate.name" />
      </div>
      <div class="info-card__contact">
        <h4 class="info-card__contact-title">Thông tin liên hệ</h4>
        <div class="info-card__contact-actions">
          <button class="contact-btn" title="Email">
            <span class="material-symbols-outlined">mail</span>
          </button>
          <button class="contact-btn" title="Gọi điện">
            <span class="material-symbols-outlined">call</span>
          </button>
          <button class="contact-btn" title="LinkedIn">
            <span class="material-symbols-outlined">link</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Fields -->
    <div class="info-card__fields">
      <div class="info-field">
        <label class="info-field__label">Email</label>
        <p class="info-field__value">{{ candidate.email }}</p>
      </div>
      <div class="info-field">
        <label class="info-field__label">Số điện thoại</label>
        <p class="info-field__value">{{ candidate.phone }}</p>
      </div>
      <div class="info-field">
        <label class="info-field__label">Tóm tắt hồ sơ</label>
        <p class="info-field__summary">{{ candidate.summary }}</p>
      </div>

      <!-- CV download -->
      <div class="info-card__cv">
        <div class="info-card__cv-left">
          <div class="info-card__cv-icon">
            <span class="material-symbols-outlined">picture_as_pdf</span>
          </div>
          <div>
            <p class="info-card__cv-name">{{ candidate.cvFileName }}</p>
            <p class="info-card__cv-meta">{{ candidate.cvSize }} • {{ candidate.cvUpdated }}</p>
          </div>
        </div>
        <button class="info-card__cv-download" title="Tải xuống">
          <span class="material-symbols-outlined">download</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  candidate: {
    name: string
    avatarUrl: string
    email: string
    phone: string
    summary: string
    cvFileName: string
    cvSize: string
    cvUpdated: string
  }
}>()
</script>

<style scoped>
.info-card {
  background: #fff;
  border-radius: 0.75rem;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  border: 1px solid rgba(203,213,225,0.3);
}

/* Top row */
.info-card__top {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.info-card__avatar {
  width: 6rem;
  height: 6rem;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.info-card__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-card__contact-title {
  font-size: 1.125rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 0.75rem;
}

.info-card__contact-actions {
  display: flex;
  gap: 0.5rem;
}

.contact-btn {
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  border: none;
  background: #f8fafc;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}

.contact-btn .material-symbols-outlined {
  font-size: 0.95rem;
}

.contact-btn:hover {
  background: #e0f2fe;
  color: #4B9AF6;
}

/* Fields */
.info-card__fields {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.info-field__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #64748b;
  margin-bottom: 0.2rem;
}

.info-field__value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.info-field__summary {
  font-size: 0.875rem;
  color: #64748b;
  line-height: 1.65;
  margin: 0;
}

/* CV row */
.info-card__cv {
  padding-top: 1.25rem;
  border-top: 1px solid rgba(203,213,225,0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-card__cv-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.info-card__cv-icon {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 0.5rem;
  background: #fef2f2;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ef4444;
  flex-shrink: 0;
}

.info-card__cv-icon .material-symbols-outlined {
  font-size: 1.2rem;
}

.info-card__cv-name {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 0.15rem;
}

.info-card__cv-meta {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0;
}

.info-card__cv-download {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  border: none;
  background: #f1f4f9;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: background 0.18s, color 0.18s;
}

.info-card__cv-download .material-symbols-outlined {
  font-size: 0.95rem;
}

.info-card__cv-download:hover {
  background: #4B9AF6;
  color: #fff;
}
</style>