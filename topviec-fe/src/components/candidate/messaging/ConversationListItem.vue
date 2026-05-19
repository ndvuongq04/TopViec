<template>
  <div class="conv-item" :class="{ 'is-active': isActive, 'is-unread': conversation.unread }" @click="$emit('click')">
    <div v-if="conversation.unread" class="unread-bar"></div>
    <div class="item-inner">
      <div class="avatar-wrap">
        <img
          class="avatar"
          :src="conversation.avatar"
          :alt="conversation.companyName"
          @error="(e) => ((e.target as HTMLImageElement).style.display = 'none')"
        />
        <div class="avatar-fallback">{{ conversation.companyName?.charAt(0) }}</div>
      </div>
      <div class="item-info">
        <div class="item-row">
          <span class="company-name" :class="{ bold: conversation.unread }">
            {{ conversation.companyName }}
          </span>
          <span class="time" :class="{ 'time-unread': conversation.unread }">
            {{ conversation.time }}
          </span>
        </div>
        <p class="position">{{ conversation.position }}</p>
        <div v-if="conversation.tag" class="tag-row">
          <span class="tag" :class="`tag-${conversation.tagColor}`">{{ conversation.tag }}</span>
        </div>
        <p class="preview" :class="{ 'preview-unread': conversation.unread }">
          {{ conversation.lastMessage }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  conversation: {
    id: number; companyName: string; position: string
    tag: string; tagColor: string; lastMessage: string
    time: string; unread: boolean; avatar: string
  }
  isActive: boolean
}>()

defineEmits<{ click: [] }>()
</script>

<style scoped>
.conv-item {
  position: relative;
  padding: 14px 12px;
  border-radius: 12px;
  border: 1.5px solid #e2e8f0;
  background: #fff;
  cursor: pointer;
  margin-bottom: 8px;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
}
.conv-item:hover {
  border-color: #bfdbfe;
  box-shadow: 0 2px 8px rgba(75,154,246,0.08);
}
.conv-item.is-active {
  background: rgba(75,154,246,0.06);
  border-color: #4B9AF6;
  box-shadow: 0 2px 12px rgba(75,154,246,0.12);
}
.conv-item.is-unread {
  border-color: #93c5fd;
}

.unread-bar {
  position: absolute;
  left: 0; top: 50%;
  transform: translateY(-50%);
  width: 3px; height: 32px;
  background: #4B9AF6;
  border-radius: 0 4px 4px 0;
}

.item-inner {
  display: flex;
  gap: 10px;
  padding-left: 4px;
}
.avatar-wrap {
  position: relative; width: 40px; height: 40px; flex-shrink: 0;
}
.avatar {
  width: 40px; height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e2e8f0;
  position: absolute; inset: 0;
}
.avatar-fallback {
  width: 40px; height: 40px;
  border-radius: 50%;
  background: #4B9AF6; color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 1rem; font-weight: 700;
}

.item-info { flex: 1; min-width: 0; }
.item-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2px;
}
.company-name {
  font-size: 1.125rem;
  color: #071b3b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 8px;
}
.company-name.bold { font-weight: 700; }

.time { font-size: 0.75rem; color: #404752; white-space: nowrap; }
.time.time-unread { color: #005ea4; font-weight: 500; }

.position {
  font-size: 0.875rem;
  color: #404752;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0 0 4px;
}

.tag-row { margin-bottom: 4px; }
.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 99px;
  font-size: 0.75rem;
  font-weight: 600;
}
.tag-green { background: rgba(29, 158, 117, 0.12); color: #1D9E75; }
.tag-blue { background: #d3e4ff; color: #004881; }

.preview {
  font-size: 0.875rem;
  color: #404752;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}
.preview.preview-unread { color: #071b3b; font-weight: 500; }
</style>