<template>
  <div class="chat-input">
    <div class="chat-input__quick-replies">
      <button
        v-for="q in quickReplies" :key="q.label"
        :class="['quick-reply', q.primary && 'quick-reply--primary']"
        @click="inputText = q.label"
      >{{ q.label }}</button>
    </div>

    <div class="chat-input__row">
      <button class="chat-input__attach">
        <span class="material-symbols-outlined">attach_file</span>
      </button>
      <input
        v-model="inputText"
        class="chat-input__field"
        placeholder="Nhập tin nhắn..."
        type="text"
        @keydown.enter.exact.prevent="handleSend"
      />
      <button class="chat-input__send" @click="handleSend">
        <span class="material-symbols-outlined">send</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{ send: [text: string] }>()
const inputText = ref('')

const quickReplies = [
  { label: 'Mời phỏng vấn', primary: true },
  { label: 'Yêu cầu thêm thông tin', primary: false },
  { label: 'Cảm ơn', primary: false },
]

function handleSend() {
  if (!inputText.value.trim()) return
  emit('send', inputText.value)
  inputText.value = ''
}
</script>

<style scoped>
.chat-input {
  padding: 1rem;
  background: #fff;
  border-top: 1px solid #e2e8f0;
  flex-shrink: 0;
}

/* Quick replies */
.chat-input__quick-replies {
  display: flex; gap: 8px; margin-bottom: 12px;
  overflow-x: auto; padding-bottom: 2px;
}
.chat-input__quick-replies::-webkit-scrollbar { display: none; }

.quick-reply {
  white-space: nowrap;
  padding: 6px 16px;
  border-radius: 999px;
  font-size: 0.75rem; font-weight: 500;
  cursor: pointer; border: 1px solid #e2e8f0;
  background: #f1f4f9; color: #64748b;
  transition: background 0.15s;
  font-family: 'Manrope', sans-serif;
}
.quick-reply:hover { background: #e2e8f0; }
.quick-reply--primary {
  background: #e0f2fe; color: #4B9AF6;
  border-color: rgba(75,154,246,0.2);
  font-weight: 600;
}
.quick-reply--primary:hover { background: #bfdbfe; }

/* Input row */
.chat-input__row { display: flex; align-items: center; gap: 12px; }

.chat-input__attach {
  background: none; border: none; cursor: pointer;
  color: #64748b; padding: 8px;
  transition: color 0.15s;
}
.chat-input__attach:hover { color: #4B9AF6; }
.chat-input__attach .material-symbols-outlined { font-size: 1.5rem; }

.chat-input__field {
  flex: 1;
  background: #f1f4f9; border: none;
  border-radius: 999px;
  padding: 12px 20px;
  font-size: 1rem; font-family: 'Manrope', sans-serif;
  color: #0f172a; outline: none;
  transition: box-shadow 0.2s;
}
.chat-input__field:focus { box-shadow: 0 0 0 2px rgba(75,154,246,0.25); }

.chat-input__send {
  width: 48px; height: 48px; flex-shrink: 0;
  background: #4B9AF6; color: #fff;
  border: none; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(75,154,246,0.3);
  transition: transform 0.15s, background 0.15s;
  font-variation-settings: 'FILL' 1;
}
.chat-input__send:hover { background: #2563eb; }
.chat-input__send:active { transform: scale(0.93); }
.chat-input__send .material-symbols-outlined { font-size: 1.25rem; }
</style>