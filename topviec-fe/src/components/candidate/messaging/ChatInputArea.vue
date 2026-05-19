<template>
  <div class="input-area">
    <!-- Quick Replies -->
    <div class="quick-replies">
      <ChatQuickReply
        v-for="reply in quickReplies"
        :key="reply"
        :text="reply"
        @click="handleQuickReply(reply)"
      />
    </div>

    <!-- Input Row -->
    <div class="input-row">
      <button class="attach-btn">
        <span class="material-symbols-outlined">attach_file</span>
      </button>
      <input
        v-model="inputText"
        class="input-field"
        placeholder="Nhập tin nhắn..."
        type="text"
        @keydown.enter.exact.prevent="handleSend"
      />
      <button class="send-btn" @click="handleSend">
        <span class="material-symbols-outlined">send</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ChatQuickReply from './ChatQuickReply.vue'

const emit = defineEmits<{ send: [text: string] }>()

const inputText = ref('')

const quickReplies = [
  'Cảm ơn đã liên hệ!',
  'Tôi rất quan tâm vị trí này',
  'Tôi có thể phỏng vấn tuần tới',
  'Vui lòng cho biết thêm về công việc',
]

function handleSend() {
  if (!inputText.value.trim()) return
  emit('send', inputText.value)
  inputText.value = ''
}

function handleQuickReply(text: string) {
  inputText.value = text
}
</script>

<style scoped>
.input-area {
  padding: 1rem;
  background: #fff;
  border-top: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.quick-replies {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 10px;
}
.quick-replies::-webkit-scrollbar { display: none; }

.input-row { display: flex; align-items: center; gap: 12px; }

.attach-btn {
  background: none; border: none; cursor: pointer;
  color: #64748b; padding: 8px;
  transition: color 0.15s;
}
.attach-btn:hover { color: #4B9AF6; }
.attach-btn .material-symbols-outlined { font-size: 1.5rem; }

.input-field {
  flex: 1;
  background: #f1f4f9; border: none;
  border-radius: 999px;
  padding: 12px 20px;
  font-size: 1rem; font-family: inherit;
  color: #0f172a; outline: none;
  transition: box-shadow 0.2s;
}
.input-field:focus { box-shadow: 0 0 0 2px rgba(75,154,246,0.25); }

.send-btn {
  width: 48px; height: 48px; flex-shrink: 0;
  background: #4B9AF6; color: #fff;
  border: none; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(75,154,246,0.3);
  transition: transform 0.15s, background 0.15s;
  font-variation-settings: 'FILL' 1;
}
.send-btn:hover { background: #2563eb; }
.send-btn:active { transform: scale(0.93); }
.send-btn .material-symbols-outlined { font-size: 1.25rem; }
</style>