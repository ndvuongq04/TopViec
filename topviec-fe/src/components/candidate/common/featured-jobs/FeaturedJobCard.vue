<template>
  <div class="job-card" @click="$emit('click')">
    <!-- Logo -->
    <div class="logo-wrap" :style="{ background: job.logoUrl ? '#fff' : job.logoBg }">
      <img v-if="job.logoUrl" :src="job.logoUrl" :alt="job.company" class="logo-img" />
      <span v-else class="logo-text" :style="{ color: job.logoTextColor }">{{ job.logoText }}</span>
    </div>

    <!-- Info -->
    <div class="job-info">
      <h3 class="job-title">{{ job.title }}</h3>

      <div class="meta-row">
        <span v-if="job.isUrgent" class="badge badge-urgent">GẤP</span>
        <span v-else-if="job.isHot" class="badge badge-hot">HOT</span>
        <span class="company-name">{{ job.company }}</span>
      </div>

      <div class="tags-row">
        <span class="tag salary-tag">{{ job.salary }}</span>
        <span v-if="job.location" class="tag location-tag">
          <span class="material-symbols-outlined" style="font-size:13px;vertical-align:middle;">location_on</span>
          {{ job.location }}
        </span>
        <span v-if="job.isNew" class="tag new-tag">Mới</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  job: {
    id: number
    title: string
    company: string
    salary: string
    logoBg: string
    logoText: string
    logoTextColor: string
    logoUrl?: string
    isNew: boolean
    isHot: boolean
    isUrgent: boolean
    location: string
  }
}>()

defineEmits<{ click: [] }>()
</script>

<style scoped>
.job-card {
  display: flex;
  gap: 14px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(192, 199, 212, 0.3);
  cursor: pointer;
  transition: transform 200ms ease, box-shadow 200ms ease, border-color 200ms ease;
}
.job-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 94, 164, 0.08);
  border-color: #005ea4;
}
.job-card:hover .job-title { color: #005ea4; }

.logo-wrap {
  width: 56px;
  height: 56px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(192, 199, 212, 0.25);
  padding: 6px;
  box-sizing: border-box;
  overflow: hidden;
}
.logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.logo-text {
  font-size: 0.75rem;
  font-weight: 800;
  text-align: center;
  line-height: 1.3;
  word-break: break-word;
}

.job-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.job-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #071b3b;
  margin: 0 0 6px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.15s;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
}
.badge {
  font-size: 0.75rem;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 99px;
  flex-shrink: 0;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}
.badge-urgent {
  background: #fffbeb;
  color: #b45309;
  border: 1px solid #fcd34d;
}
.badge-hot {
  background: #fff1f2;
  color: #be123c;
  border: 1px solid #fecdd3;
}
.company-name {
  font-size: 0.75rem;
  color: #404752;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tags-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: auto;
  flex-wrap: wrap;
}
.tag {
  font-size: 0.75rem;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 6px;
  white-space: nowrap;
}
.salary-tag {
  background: #fff7ed;
  color: #ea580c;
  border: 1px solid #fed7aa;
}
.location-tag {
  background: #e9edff;
  color: #404752;
  border: 1px solid #c0c7d4;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
}
.new-tag {
  background: #dcfce7;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}
</style>