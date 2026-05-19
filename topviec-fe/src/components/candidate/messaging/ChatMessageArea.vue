<template>
  <div class="message-area" ref="scrollRef">
    <template v-for="msg in messages" :key="msg.id">
      <ChatSystemEvent v-if="msg.type === 'system'" :text="msg.text" :icon="msg.icon" />
      <ChatMessageBubble v-else :message="msg" />
    </template>
    <div class="scroll-anchor" ref="anchor"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import ChatSystemEvent from './ChatSystemEvent.vue'
import ChatMessageBubble from './ChatMessageBubble.vue'

const props = defineProps<{ messages: any[] }>()
const anchor = ref<HTMLElement>()

watch(() => props.messages.length, async () => {
  await nextTick()
  anchor.value?.scrollIntoView({ behavior: 'smooth' })
})
</script>

<style scoped>
.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>