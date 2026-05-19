<template>
  <div class="chat-messages">
    <div class="chat-messages__date-divider">
      <span class="chat-messages__date-label">HÔM NAY</span>
    </div>

    <template v-for="msg in messages" :key="msg.id">
      <div :class="['chat-bubble', msg.mine ? 'chat-bubble--mine' : 'chat-bubble--theirs']">
        <img v-if="!msg.mine" :src="msg.avatar" alt="avatar" class="chat-bubble__avatar" />
        <div>
          <div :class="['chat-bubble__text', msg.mine ? 'chat-bubble__text--mine' : 'chat-bubble__text--theirs']">
            {{ msg.text }}
          </div>
          <span :class="['chat-bubble__time', msg.mine && 'chat-bubble__time--right']">{{ msg.time }}</span>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  messages: { id: number; mine: boolean; text: string; time: string; avatar: string }[]
}>()
</script>

<style scoped>
.chat-messages {
  flex: 1; overflow-y: auto;
  padding: 1.5rem; display: flex;
  flex-direction: column; gap: 1.5rem;
}

/* Custom scrollbar */
.chat-messages::-webkit-scrollbar { width: 4px; }
.chat-messages::-webkit-scrollbar-track { background: transparent; }
.chat-messages::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 999px; }

.chat-messages__date-divider { text-align: center; }
.chat-messages__date-label {
  display: inline-block;
  padding: 4px 12px;
  background: #fff; border: 1px solid #e2e8f0;
  border-radius: 999px;
  font-size: 0.75rem; color: #64748b; font-weight: 500; letter-spacing: 0.05em;
}

/* Bubble */
.chat-bubble { display: flex; gap: 1rem; max-width: 42rem; }
.chat-bubble--mine { margin-left: auto; flex-direction: row-reverse; }
.chat-bubble--theirs { margin-right: auto; }

.chat-bubble__avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; flex-shrink: 0; align-self: flex-end; }

.chat-bubble__text {
  padding: 1rem; border-radius: 1rem;
  font-size: 1rem; line-height: 1.5;
}
.chat-bubble__text--theirs {
  background: #fff; border: 1px solid #e2e8f0;
  color: #0f172a; border-radius: 1rem 1rem 1rem 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.chat-bubble__text--mine {
  background: #4B9AF6; color: #fff;
  border-radius: 1rem 1rem 4px 1rem;
  box-shadow: 0 2px 8px rgba(75,154,246,0.25);
}

.chat-bubble__time {
  display: block; font-size: 0.75rem;
  color: #94a3b8; margin-top: 4px; padding-left: 4px;
}
.chat-bubble__time--right { text-align: right; padding-right: 4px; padding-left: 0; }
</style>