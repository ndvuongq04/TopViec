<template>
  <div :class="['conv-item', active && 'conv-item--active']" @click="$emit('click')">
    <div class="conv-item__avatar-wrap">
      <img :alt="conversation.name" :src="conversation.avatar" class="conv-item__avatar" />
      <span :class="['conv-item__dot', conversation.online ? 'conv-item__dot--online' : 'conv-item__dot--offline']"></span>
    </div>
    <div class="conv-item__body">
      <div class="conv-item__top">
        <h4 :class="['conv-item__name', active && 'conv-item__name--active']">{{ conversation.name }}</h4>
        <span :class="['conv-item__time', active && 'conv-item__time--active']">{{ conversation.time }}</span>
      </div>
      <p :class="['conv-item__role', active && 'conv-item__role--active']">{{ conversation.role }}</p>
      <p :class="['conv-item__last', active && 'conv-item__last--active']">{{ conversation.lastMsg }}</p>
    </div>
    <div v-if="conversation.unread" class="conv-item__badge">{{ conversation.unread }}</div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ conversation: any; active: boolean }>()
defineEmits(['click'])
</script>

<style scoped>
.conv-item {
  display: flex; align-items: flex-start; gap: 12px;
  padding: 0.875rem 1rem;
  margin: 6px 8px;
  cursor: pointer;
  border: 1.5px solid #e2e8f0;
  border-radius: 12px;
  border-left: 4px solid transparent;
  background: #fff;
  transition: border-color 0.15s, background 0.15s, box-shadow 0.15s;
}
.conv-item:hover {
  background: #f8fafd;
  border-color: #bfdbfe;
  box-shadow: 0 2px 8px rgba(75,154,246,0.08);
}
.conv-item--active {
  background: rgba(75,154,246,0.06);
  border-color: #4B9AF6;
  border-left-color: #4B9AF6;
  box-shadow: 0 2px 12px rgba(75,154,246,0.12);
}

.conv-item__avatar-wrap { position: relative; flex-shrink: 0; }
.conv-item__avatar { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; }
.conv-item__dot {
  position: absolute; bottom: 0; right: 0;
  width: 12px; height: 12px; border-radius: 50%;
  border: 2px solid #fff;
}
.conv-item__dot--online  { background: #10b981; }
.conv-item__dot--offline { background: #cbd5e1; }

.conv-item__body { flex: 1; min-width: 0; }
.conv-item__top  { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 2px; }

.conv-item__name { font-size: 1.125rem; font-weight: 600; color: #0f172a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.conv-item__name--active { font-weight: 700; }
.conv-item__time { font-size: 0.75rem; color: #64748b; flex-shrink: 0; }
.conv-item__time--active { color: #4B9AF6; font-weight: 600; }

.conv-item__role { font-size: 0.875rem; color: #64748b; margin-bottom: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.conv-item__role--active { color: #4B9AF6; font-weight: 500; }

.conv-item__last { font-size: 0.875rem; color: #64748b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.conv-item__last--active { color: #0f172a; font-weight: 500; }

.conv-item__badge {
  width: 20px; height: 20px; flex-shrink: 0;
  background: #4B9AF6; color: #fff;
  border-radius: 50%; font-size: 0.75rem; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
</style>