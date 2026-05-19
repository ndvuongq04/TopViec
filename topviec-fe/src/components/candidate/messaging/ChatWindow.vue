<template>
  <section class="chat-window">
    <ChatHeader :conversation="conversation" />
    <ChatMessageArea :messages="messages" />
    <ChatInputArea @send="$emit('send', $event)" />
  </section>
</template>

<script setup lang="ts">
import ChatHeader from './ChatHeader.vue'
import ChatMessageArea from './ChatMessageArea.vue'
import ChatInputArea from './ChatInputArea.vue'

defineProps<{ conversation: any; messages: any[] }>()
defineEmits<{ send: [text: string] }>()
</script>

<style scoped>
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f6f7f8;
  height: 100%;
  overflow: hidden;
}
</style>