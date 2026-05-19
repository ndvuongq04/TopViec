<template>
  <section class="cd-job">
    <div class="cd-job__section-title">
      <span class="material-symbols-outlined">work</span>
      <h3>Thông tin tin tuyển dụng</h3>
    </div>
    <div class="cd-job__body">
      <div class="cd-job__logo">
        <span class="material-symbols-outlined">code</span>
      </div>
      <div class="cd-job__info">
        <div class="cd-job__info-top">
          <div>
            <h4 class="cd-job__title">{{ job.title }}</h4>
            <p class="cd-job__meta">ID: {{ job.id }} • {{ job.company }}</p>
          </div>
          <span class="cd-job__status">{{ job.status }}</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{ job: { title: string; id: string; company: string; status: string } }>()
</script>

<style scoped>
.cd-job {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05); border: 1px solid rgba(226,232,240,0.2);
}

.cd-job__section-title {
  display: flex; align-items: center; gap: 8px;
  padding-bottom: 1rem; margin-bottom: 1rem;
  border-bottom: 1px solid #e2e7f0;
}
.cd-job__section-title .material-symbols-outlined { color: #4B9AF6; font-size: 20px; }
.cd-job__section-title h3 { font-size: 1.125rem; font-weight: 800; color: #0f172a; }

.cd-job__body { display: flex; align-items: flex-start; gap: 1rem; }
.cd-job__logo {
  width: 64px; height: 64px; border-radius: 0.5rem;
  background: #f1f4f9; border: 1px solid rgba(226,232,240,0.3);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.cd-job__logo .material-symbols-outlined { font-size: 32px; color: #64748b; }

.cd-job__info { flex: 1; min-width: 0; }
.cd-job__info-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }

.cd-job__title { font-size: 1.125rem; font-weight: 700; color: #0f172a; margin-bottom: 4px; }
.cd-job__meta { font-size: 0.8125rem; color: #64748b; font-weight: 500; }

.cd-job__status {
  flex-shrink: 0; padding: 4px 10px; border-radius: 6px;
  font-size: 0.75rem; font-weight: 700;
  background: #e0f2fe; color: #0369a1;
}
</style>