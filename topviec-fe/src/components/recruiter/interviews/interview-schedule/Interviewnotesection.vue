<template>
  <section class="section">
    <div class="section__header">
      <h3 class="section__title">
        <span class="material-symbols-outlined">description</span>
        Ghi chú gửi ứng viên
      </h3>
    </div>

    <div class="section__body">
      <textarea
        class="note-textarea"
        rows="4"
        placeholder="Nhập lời chào hoặc các lưu ý chuẩn bị cho ứng viên trước buổi phỏng vấn..."
        :value="modelValue"
        @input="emit('update:modelValue', ($event.target as HTMLTextAreaElement).value)"
      />
      <div class="note-hint">
        <span class="material-symbols-outlined">info</span>
        Nội dung này sẽ được hiển thị trực tiếp trong email mời phỏng vấn.
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: string): void
}>()
</script>

<style scoped>
.section {
  background: #fff;
  border-radius: 0.75rem;
  border: 1px solid rgba(203, 213, 225, 0.3);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  overflow: hidden;
}

.section__header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f8fafc;
  background: rgba(248,250,252,0.5);
}

.section__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.section__title .material-symbols-outlined {
  color: #4B9AF6;
  font-size: 1.2rem;
}

.section__body {
  padding: 1.5rem;
}

.note-textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  background: #f1f4f9;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-family: inherit;
  color: #0f172a;
  resize: none;
  outline: none;
  box-sizing: border-box;
  transition: box-shadow 0.18s;
  line-height: 1.6;
}

.note-textarea:focus {
  box-shadow: 0 0 0 2px rgba(75,154,246,0.2);
}

.note-textarea::placeholder {
  color: #94a3b8;
}

.note-hint {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin-top: 0.75rem;
  font-size: 0.75rem;
  color: #94a3b8;
  font-style: italic;
}

.note-hint .material-symbols-outlined {
  font-size: 0.9rem;
}
</style>