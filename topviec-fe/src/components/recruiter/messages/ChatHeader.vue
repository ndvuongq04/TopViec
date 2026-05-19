<template>
  <div class="chat-header">
    <div class="chat-header__left">
      <div class="chat-header__avatar-wrap">
        <img :alt="contact.name" :src="contact.avatar" class="chat-header__avatar" />
        <span v-if="contact.online" class="chat-header__dot"></span>
      </div>
      <div>
        <h3 class="chat-header__name">{{ contact.name }}</h3>
        <p class="chat-header__role">Ứng tuyển: {{ contact.role }}</p>
      </div>
    </div>
    <div class="chat-header__actions">
      <button class="chat-header__profile-btn">
        <span class="material-symbols-outlined">contact_page</span>
        Xem hồ sơ
      </button>
      <button class="chat-header__close-btn" @click="$emit('close')">
        <span class="material-symbols-outlined">close</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ contact: any }>()
defineEmits(['close'])
</script>

<style scoped>
.chat-header {
  height: 80px; flex-shrink: 0;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.chat-header__left { display: flex; align-items: center; gap: 1rem; }
.chat-header__avatar-wrap { position: relative; }
.chat-header__avatar { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; }
.chat-header__dot {
  position: absolute; bottom: 0; right: 0;
  width: 12px; height: 12px; border-radius: 50%;
  background: #10b981; border: 2px solid #fff;
}
.chat-header__name { font-size: 1.125rem; font-weight: 800; color: #0f172a; }
.chat-header__role { font-size: 0.875rem; color: #4B9AF6; font-weight: 500; }

.chat-header__actions { display: flex; align-items: center; gap: 8px; }

.chat-header__profile-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 16px;
  background: #fff; border: 1px solid #e2e8f0;
  border-radius: 0.5rem; color: #4B9AF6;
  font-size: 0.875rem; font-weight: 600; cursor: pointer;
  transition: background 0.15s;
}
.chat-header__profile-btn:hover { background: #f1f4f9; }
.chat-header__profile-btn .material-symbols-outlined { font-size: 18px; }

.chat-header__close-btn {
  display: flex; align-items: center; justify-content: center;
  width: 36px; height: 36px;
  background: #f1f4f9; border: none; border-radius: 50%;
  color: #64748b; cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.chat-header__close-btn:hover { background: #fee2e2; color: #ef4444; }
.chat-header__close-btn .material-symbols-outlined { font-size: 18px; }
</style>