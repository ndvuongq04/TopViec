<template>
  <Teleport to="body">
    <transition name="modal">
      <div v-if="visible" class="modal-backdrop" @click="$emit('close')">
        <div class="modal-content relative" @click.stop>
          <button class="modal-close" @click="$emit('close')">
            <span class="material-symbols-outlined">close</span>
          </button>

          <div v-if="store.loading" class="modal-loading">Đang tải...</div>
          <InterviewTimeline v-else :items="timelineItems" />
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import InterviewTimeline from '@/components/recruiter/interviews/candidate-detail/InterviewTimeline.vue'
import { usePublicInterviewStore } from '@/stores/publicInterview.store'
import type { RoundHistory } from '@/types/interview.types'

const props = defineProps<{
  visible: boolean
  process: any
}>()

defineEmits<{ close: [] }>()

const store = usePublicInterviewStore()

watch(
  () => props.visible,
  (val) => {
    if (val && props.process?.id) {
      store.fetchMyInterviewHistory(props.process.id)
    }
  }
)

function formatScheduledAt(raw: string | undefined): string | undefined {
  if (!raw) return undefined
  const d = new Date(raw)
  const time = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  const date = `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
  return `${time} · ${date}`
}

function mapRoundStatus(round: RoundHistory): 'passed' | 'upcoming' | 'pending' {
  if (round.result) return 'passed'
  if (round.scheduleStatus && ['scheduled', 'confirmed'].includes(round.scheduleStatus)) return 'upcoming'
  return 'pending'
}

const timelineItems = computed(() => {
  if (!store.interviewHistory) return []
  return store.interviewHistory.rounds.map((r) => ({
    roundLabel: `Vòng ${r.roundNumber}: ${r.roundName}`,
    status: mapRoundStatus(r),
    rating: r.rating,
    scheduledAt: formatScheduledAt(r.scheduledAt),
    note: r.note,
    countdown: mapRoundStatus(r) === 'upcoming' ? 'Sắp diễn ra' : undefined,
  }))
})
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 100;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.modal-content {
  width: 100%;
  max-width: 680px;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 0.75rem;
  background: transparent;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  font-family: 'Manrope', sans-serif;
}

.modal-content::-webkit-scrollbar {
  width: 6px;
}
.modal-content::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.5);
  border-radius: 4px;
}

.modal-close {
  position: absolute;
  top: 1.25rem;
  right: 1.25rem;
  z-index: 50;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-close span {
  font-size: 1.25rem;
}

.modal-close:hover {
  background: #e2e8f0;
  color: #0f172a;
  transform: rotate(90deg);
}

.modal-loading {
  background: #fff;
  border-radius: 0.75rem;
  padding: 2rem;
  color: #707783;
  font-size: 0.875rem;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-active .modal-content,
.modal-leave-active .modal-content {
  transition: transform 0.2s ease;
}
.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: translateY(10px) scale(0.98);
}
</style>
