<template>
  <aside class="conv-list">
    <div class="list-header">
      <div class="header-left">
        <h1 class="list-title">Tin nhắn</h1>
        <span class="unread-badge">1 mới</span>
      </div>
      <button class="icon-btn">
        <span class="material-symbols-outlined">edit_square</span>
      </button>
    </div>

    <div class="search-wrap">
      <span class="material-symbols-outlined search-icon">search</span>
      <input class="search-input" type="text" placeholder="Tìm công ty, vị trí..." />
    </div>

    <div class="filter-row">
      <button
        v-for="f in filters"
        :key="f.value"
        class="filter-chip"
        :class="{ active: activeFilter === f.value }"
        @click="activeFilter = f.value"
      >
        {{ f.label }}
      </button>
    </div>

    <div class="conv-items">
      <ConversationListItem
        v-for="conv in filteredConversations"
        :key="conv.id"
        :conversation="conv"
        :is-active="conv.id === activeId"
        @click="$emit('select', conv.id)"
      />
      <div v-if="filteredConversations.length === 0" class="conv-empty">
        <span class="material-symbols-outlined conv-empty__icon">mark_chat_unread</span>
        <p>Không có tin nhắn nào</p>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import ConversationListItem from './ConversationListItem.vue'

const props = defineProps<{
  conversations: any[]
  activeId: number
}>()

defineEmits<{ select: [id: number] }>()

const activeFilter = ref('all')
const filters = [
  { value: 'all', label: 'Tất cả' },
  { value: 'unread', label: 'Chưa đọc' },
  { value: 'interview', label: 'Đang phỏng vấn' },
]

const filteredConversations = computed(() => {
  if (activeFilter.value === 'unread') return props.conversations.filter(c => c.unread)
  if (activeFilter.value === 'interview') return props.conversations.filter(c => c.tag === 'Mời phỏng vấn')
  return props.conversations
})
</script>

<style scoped>
.conv-list {
  width: 360px;
  flex-shrink: 0;
  background: #f6f7f8;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 20px 14px;
}
.header-left { display: flex; align-items: center; gap: 8px; }
.list-title { font-size: 1.125rem; font-weight: 700; color: #071b3b; margin: 0; }
.unread-badge {
  background: #ba1a1a;
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 99px;
}
.icon-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  background: none; border: none; cursor: pointer;
  border-radius: 8px; color: #404752;
  transition: color 0.15s, background 0.15s;
}
.icon-btn:hover { color: #4B9AF6; background: #e2e8f0; }

.search-wrap {
  position: relative;
  padding: 0 20px 14px;
}
.search-icon {
  position: absolute;
  left: 32px;
  top: 50%;
  transform: translateY(-65%);
  font-size: 18px;
  color: #404752;
  pointer-events: none;
}
.search-input {
  width: 100%;
  padding: 10px 14px 10px 40px;
  border-radius: 12px;
  border: 1px solid rgba(192, 199, 212, 0.4);
  background: #fff;
  font-size: 1rem;
  color: #071b3b;
  outline: none;
  box-sizing: border-box;
  font-family: inherit;
  transition: box-shadow 0.15s, border-color 0.15s;
  box-shadow: 0 2px 6px rgba(0, 94, 164, 0.04);
}
.search-input:focus {
  box-shadow: 0 0 0 2px rgba(75, 154, 246, 0.25);
  border-color: #4B9AF6;
}
.search-input::placeholder { color: rgba(64, 71, 82, 0.6); }

.filter-row {
  display: flex;
  gap: 8px;
  padding: 0 20px 12px;
  overflow-x: auto;
}
.filter-chip {
  padding: 6px 14px;
  border-radius: 99px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid rgba(192, 199, 212, 0.35);
  background: #fff;
  color: #404752;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
  font-family: inherit;
}
.filter-chip.active {
  background: #4B9AF6;
  color: #fff;
  border-color: transparent;
}
.filter-chip:hover:not(.active) { background: #e2e8f0; }

.conv-items {
  flex: 1;
  overflow-y: auto;
  padding: 4px 8px;
}

.conv-empty {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; gap: 8px;
  height: 100%; color: #94a3b8;
  font-size: 0.875rem; padding: 2rem;
}
.conv-empty__icon { font-size: 2.5rem; opacity: 0.4; }
.conv-empty p { margin: 0; }
</style>