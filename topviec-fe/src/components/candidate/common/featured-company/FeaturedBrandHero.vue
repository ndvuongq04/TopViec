<template>
  <div class="brand-hero">
    <div class="brand-hero__logo-wrap">
      <span class="material-symbols-outlined brand-hero__logo-icon">payments</span>
      <span class="brand-hero__logo-sub">Shinhan Finance</span>
    </div>
    <h2 class="brand-hero__name">{{ company.name }}</h2>
    <p class="brand-hero__industry">{{ company.industry }}</p>
    <div class="brand-hero__jobs">
      <span class="material-symbols-outlined">work</span>
      <span>{{ company.jobCount }} việc làm</span>
    </div>
    <div class="brand-hero__pro-badge">Top Company</div>
    <button class="brand-hero__follow-btn">
      <span class="material-symbols-outlined">add</span>
      Theo dõi
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ company: { name: string; industry: string; jobCount: number } }>()
</script>

<style scoped>
.brand-hero {
  border-radius: 0.75rem;
  background: linear-gradient(to bottom, #60a5fa, #2563eb);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 1.5rem; text-align: center; color: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  min-height: 280px;
}

.brand-hero__logo-wrap {
  width: 80px; height: 80px;
  background: #fff; border-radius: 0.75rem;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  margin-bottom: 1rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  padding: 6px;
}
.brand-hero__logo-icon { font-size: 2rem; color: #005ea4; }
.brand-hero__logo-sub { font-size: 0.75rem; font-weight: 700; color: #005ea4; margin-top: 2px; }

.brand-hero__name { font-size: 1.125rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.04em; margin-bottom: 4px; }
.brand-hero__industry { font-size: 0.75rem; opacity: 0.8; margin-bottom: 1rem; }

.brand-hero__jobs {
  display: flex; align-items: center; justify-content: center; gap: 6px;
  background: rgba(0,0,0,0.2);
  border-radius: 999px; padding: 6px 16px;
  font-size: 0.75rem; font-weight: 600;
  margin-bottom: 1rem; width: 75%;
  backdrop-filter: blur(4px);
}
.brand-hero__jobs .material-symbols-outlined { font-size: 16px; }

.brand-hero__pro-badge {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 999px; padding: 6px 20px;
  font-size: 0.75rem; font-weight: 700;
  margin-bottom: 1.25rem; width: 75%; text-align: center;
}

.brand-hero__follow-btn {
  display: flex; align-items: center; justify-content: center; gap: 4px;
  background: #fff; color: #005ea4;
  border: none; border-radius: 999px;
  padding: 8px 24px;
  font-size: 0.75rem; font-weight: 700;
  cursor: pointer; width: 50%;
  font-family: 'Manrope', sans-serif;
  transition: background 0.15s;
}
.brand-hero__follow-btn:hover { background: #e9edff; }
.brand-hero__follow-btn .material-symbols-outlined { font-size: 16px; }
</style>