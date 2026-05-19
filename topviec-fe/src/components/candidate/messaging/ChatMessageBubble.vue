<template>
  <div class="bubble-wrap" :class="message.type">
    <!-- Received -->
    <template v-if="message.type === 'received'">
      <div class="receiver-row">
        <img class="sender-avatar" :src="message.avatar || '/default-hr.png'" alt="HR" />
        <div class="bubble-col">
          <span class="sender-meta">{{ message.senderName }} • {{ message.time }}</span>
          <div class="bubble received-bubble">
            <p v-for="(line, i) in lines" :key="i" :class="{ 'mt-2': (i as number) > 0 }">{{ line }}</p>
          </div>
          <ChatInterviewCard v-if="message.interviewCard" :card="message.interviewCard" />
        </div>
      </div>
    </template>

    <!-- Sent -->
    <template v-else-if="message.type === 'sent'">
      <div class="sender-col">
        <div class="bubble sent-bubble">
          <p v-for="(line, i) in lines" :key="i" :class="{ 'mt-1': (i as number) > 0 }">{{ line }}</p>
        </div>
        <div class="sent-meta">
          <span class="time-text">{{ message.time }}</span>
          <span v-if="message.seen" class="material-symbols-outlined seen-icon">done_all</span>
          <span v-if="message.seen" class="seen-text">Đã xem</span>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ChatInterviewCard from './ChatInterviewCard.vue'

const props = defineProps<{ message: any }>()
const lines = computed(() => (props.message.text || '').split('\n').filter(Boolean))
</script>

<style scoped>
.bubble-wrap { display: flex; }
.bubble-wrap.received { justify-content: flex-start; }
.bubble-wrap.sent { justify-content: flex-end; }

.receiver-row { display: flex; align-items: flex-end; gap: 8px; max-width: 80%; }
.sender-avatar {
  width: 32px; height: 32px;
  border-radius: 50%; object-fit: cover;
  flex-shrink: 0; box-shadow: 0 2px 4px rgba(0,0,0,0.08);
}
.bubble-col { display: flex; flex-direction: column; gap: 4px; }
.sender-meta { font-size: 0.75rem; color: #404752; margin-left: 4px; }

.bubble {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 1rem;
  line-height: 1.6;
}
.bubble p { margin: 0; }

.received-bubble {
  background: #fff;
  color: #0f172a;
  border-bottom-left-radius: 4px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.sender-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  max-width: 80%;
}
.sent-bubble {
  background: #4B9AF6;
  color: #fff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(75, 154, 246, 0.25);
}

.sent-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  padding-right: 4px;
}
.time-text { font-size: 0.75rem; color: #404752; }
.seen-icon {
  font-size: 0.75rem; color: #1D9E75;
  font-variation-settings: 'FILL' 1;
}
.seen-text { font-size: 0.75rem; color: #404752; }
.mt-1 { margin-top: 4px !important; }
.mt-2 { margin-top: 8px !important; }
</style>