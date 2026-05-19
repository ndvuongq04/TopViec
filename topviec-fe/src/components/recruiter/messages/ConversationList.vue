<template>
  <div :class="['conv-list', fullWidth && 'conv-list--full']">
    <div class="conv-list__header">
      <h2 class="conv-list__title">Tin nhắn</h2>
    </div>
    <div class="conv-list__search">
      <span class="material-symbols-outlined conv-list__search-icon">search</span>
      <input class="conv-list__search-input" placeholder="Tìm kiếm tin nhắn..." type="text" />
    </div>
    <div class="conv-list__tabs">
      <button
        v-for="tab in tabs" :key="tab.value"
        :class="['conv-list__tab', activeTab === tab.value && 'conv-list__tab--active']"
        @click="activeTab = tab.value"
      >{{ tab.label }}</button>
    </div>
    <div class="conv-list__items">
      <template v-if="filteredConversations.length > 0">
        <ConversationItem
          v-for="c in filteredConversations" :key="c.id"
          :conversation="c"
          :active="c.id === activeId"
          @click="$emit('select', c.id)"
        />
      </template>
      <div v-else class="conv-list__empty">
        <span class="material-symbols-outlined conv-list__empty-icon">mark_chat_unread</span>
        <p>Không có tin nhắn nào</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import ConversationItem from './ConversationItem.vue'

const props = defineProps<{
  conversations: any[]
  activeId: number | null
  fullWidth?: boolean
}>()
defineEmits(['select'])

const tabs = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Chưa đọc', value: 'unread' },
  { label: 'Đã đọc', value: 'read' },
]
const activeTab = ref('all')

const filteredConversations = computed(() => {
  if (activeTab.value === 'unread') return props.conversations.filter(c => c.unread > 0)
  if (activeTab.value === 'read')   return props.conversations.filter(c => c.unread === 0)
  return props.conversations
})
</script>

<style scoped>
.conv-list {
  width: 320px;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.conv-list__search {
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
  position: relative;
}
.conv-list__search-icon {
  position: absolute; left: 1.75rem; top: 50%; transform: translateY(-50%);
  font-size: 1.1rem; color: #64748b; pointer-events: none;
}
.conv-list__search-input {
  width: 100%; background: #f1f4f9;
  border: none; border-radius: 999px;
  padding: 8px 16px 8px 40px;
  font-size: 1rem; font-family: 'Manrope', sans-serif;
  color: #0f172a; outline: none; box-sizing: border-box;
}
.conv-list__search-input:focus { box-shadow: 0 0 0 2px rgba(75,154,246,0.25); }

.conv-list__tabs {
  display: flex;
  padding: 0.75rem 1rem;
  gap: 8px;
  flex-shrink: 0;
  border-bottom: 1px solid #e2e8f0;
}
.conv-list__tab {
  padding: 5px 14px;
  background: #f1f4f9; border: none;
  border-radius: 999px;
  font-size: 0.875rem; font-weight: 500;
  font-family: 'Manrope', sans-serif;
  color: #64748b; cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.conv-list__tab:hover { background: #e2e8f0; color: #0f172a; }
.conv-list__tab--active {
  background: #4B9AF6;
  color: #fff;
  font-weight: 600;
}

.conv-list__header {
  padding: 1.25rem 1rem 0.5rem;
}
.conv-list__title {
  font-size: 1.125rem; font-weight: 700; color: #0f172a; margin: 0;
}

.conv-list--full { width: 100%; }
.conv-list__items { flex: 1; overflow-y: auto; }

.conv-list__empty {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; gap: 8px;
  height: 100%; color: #94a3b8;
  font-size: 0.875rem;
}
.conv-list__empty-icon { font-size: 2.5rem; opacity: 0.4; }
.conv-list__empty p { margin: 0; }
</style>