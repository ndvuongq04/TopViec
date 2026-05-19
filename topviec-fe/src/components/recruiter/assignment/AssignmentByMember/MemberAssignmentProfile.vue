<template>
  <div class="member-profile">
    <div class="member-profile__deco"></div>

    <div class="member-profile__left">
      <img v-if="member.avatarLarge || member.avatar" :src="member.avatarLarge || member.avatar" :alt="member.name" class="member-profile__avatar" />
      <div v-else class="member-profile__avatar-initials">{{ member.initials }}</div>

      <div class="member-profile__info">
        <h2 class="member-profile__name">{{ member.name }}</h2>
        <div class="member-profile__meta">
          <span v-if="member.email" class="member-profile__meta-item">
            <span class="material-symbols-outlined">mail</span>{{ member.email }}
          </span>
          <span class="member-profile__meta-sep" v-if="member.email"></span>
          <span class="member-profile__meta-item member-profile__meta-item--primary">
            <span class="material-symbols-outlined">badge</span>{{ member.role }}
          </span>
        </div>
        <div class="member-profile__job-count">
          Đang phụ trách: <strong>{{ member.jobCount }} jobs</strong>
        </div>
      </div>
    </div>

    <button class="member-profile__assign-btn" @click="$emit('assign')">
      <span class="material-symbols-outlined">add_task</span>
      Giao thêm việc
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ member: any }>()
defineEmits(['assign'])
</script>

<style scoped>
.member-profile {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(226,232,240,0.3);
  display: flex; align-items: center; justify-content: space-between;
  position: relative; overflow: hidden; flex-shrink: 0; gap: 1rem;
}
.member-profile__deco {
  position: absolute; top: 0; right: 0;
  width: 200px; height: 200px;
  background: radial-gradient(circle, rgba(191,219,254,0.2), transparent 70%);
  border-radius: 50%; transform: translate(30%, -30%);
  pointer-events: none;
}

.member-profile__left { display: flex; align-items: center; gap: 1.5rem; position: relative; z-index: 1; }

.member-profile__avatar {
  width: 80px; height: 80px; border-radius: 50%; object-fit: cover;
  border: 4px solid #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.12); flex-shrink: 0;
}
.member-profile__avatar-initials {
  width: 80px; height: 80px; border-radius: 50%; flex-shrink: 0;
  background: #f1f5f9; color: #334155;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.5rem; font-weight: 700;
  border: 4px solid #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.member-profile__name {
  font-size: 1.875rem; font-weight: 800; color: #0f172a;
  letter-spacing: -0.02em; margin-bottom: 6px;
}
.member-profile__meta { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.member-profile__meta-item {
  display: flex; align-items: center; gap: 4px;
  font-size: 0.875rem; color: #64748b;
}
.member-profile__meta-item .material-symbols-outlined { font-size: 16px; }
.member-profile__meta-item--primary { color: #4B9AF6; font-weight: 600; }
.member-profile__meta-sep { width: 4px; height: 4px; border-radius: 50%; background: #cbd5e1; }

.member-profile__job-count {
  display: inline-flex; align-items: center; gap: 4px;
  margin-top: 10px; padding: 4px 12px;
  background: #f8fafd; border: 1px solid #e2e8f0;
  border-radius: 999px; font-size: 0.875rem; color: #64748b; font-weight: 500;
}
.member-profile__job-count strong { color: #4B9AF6; font-weight: 700; font-size: 1rem; }

.member-profile__assign-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 20px; background: #4B9AF6; color: #fff;
  border: none; border-radius: 0.5rem;
  font-size: 0.875rem; font-weight: 600; cursor: pointer;
  font-family: 'Manrope', sans-serif;
  box-shadow: 0 2px 8px rgba(75,154,246,0.25);
  transition: all 0.15s; flex-shrink: 0; position: relative; z-index: 1;
}
.member-profile__assign-btn:hover { background: #2563eb; }
.member-profile__assign-btn:active { transform: scale(0.97); }
.member-profile__assign-btn .material-symbols-outlined { font-size: 18px; }
</style>