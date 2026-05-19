<template>
  <GlobalModal
    :visible="visible"
    title="Chỉnh sửa ghi chú"
    :subtitle="candidateName"
    icon="edit_note"
    variant="primary"
    max-width="md"
    cancel-text="Hủy"
    confirm-text="Lưu ghi chú"
    confirm-icon="save"
    :loading="loading"
    loading-text="Đang lưu..."
    @close="emit('close')"
    @confirm="emit('confirm', note)"
  >
    <div class="space-y-3">
      <label class="block text-sm font-bold text-slate-700">
        Ghi chú <span class="text-slate-400 font-normal">(tuỳ chọn)</span>
      </label>
      <textarea
        v-model="note"
        rows="5"
        placeholder="VD: Ứng viên có kinh nghiệm React tốt, phù hợp cho dự án X..."
        class="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-800 placeholder:text-slate-400 focus:border-primary focus:ring-2 focus:ring-primary/20 focus:bg-white outline-none resize-none transition-all"
      />
    </div>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const props = defineProps<{
  visible: boolean
  candidateName?: string
  initialNote?: string
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  confirm: [note: string]
}>()

const note = ref('')

watch(() => props.visible, (v) => {
  if (v) note.value = props.initialNote ?? ''
})
</script>
