<template>
  <GlobalModal
    :visible="visible"
    title="Lưu vào Talent Pool"
    :subtitle="candidateName ? `Ứng viên: ${candidateName}` : undefined"
    icon="group_add"
    variant="primary"
    max-width="md"
    cancel-text="Hủy"
    confirm-text="Lưu vào Talent Pool"
    confirm-icon="group_add"
    :loading="loading"
    loading-text="Đang lưu..."
    @close="emit('close')"
    @confirm="onConfirm"
  >
    <div class="space-y-4">
      <p class="text-sm text-slate-500">
        Ứng viên sẽ được thêm vào danh sách tiềm năng. Bạn có thể để lại ghi chú để dễ nhớ sau này.
      </p>
      <div>
        <label class="block text-sm font-bold text-slate-700 mb-1.5">
          Ghi chú <span class="text-slate-400 font-normal">(tuỳ chọn)</span>
        </label>
        <textarea
          v-model="note"
          rows="4"
          placeholder="VD: Ứng viên có kinh nghiệm React tốt, phù hợp cho dự án X..."
          class="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-800 placeholder:text-slate-400 focus:border-primary focus:ring-2 focus:ring-primary/20 focus:bg-white outline-none resize-none transition-all"
        />
      </div>
    </div>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const props = defineProps<{
  visible: boolean
  candidateName?: string
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  confirm: [note: string]
}>()

const note = ref('')

watch(() => props.visible, (v) => {
  if (v) note.value = ''
})

function onConfirm() {
  emit('confirm', note.value.trim())
}
</script>
