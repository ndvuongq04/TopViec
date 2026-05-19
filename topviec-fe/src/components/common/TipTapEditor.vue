<template>
  <div class="tiptap-wrapper" :class="{ 'tiptap-wrapper--error': hasError }">
    <!-- Toolbar -->
    <div v-if="editor" class="editor-toolbar">
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': editor.isActive('bold') }"
        title="Đậm"
        @click="editor.chain().focus().toggleBold().run()"
      >
        <span class="material-symbols-outlined">format_bold</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': editor.isActive('italic') }"
        title="Nghiêng"
        @click="editor.chain().focus().toggleItalic().run()"
      >
        <span class="material-symbols-outlined">format_italic</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': editor.isActive('strike') }"
        title="Gạch ngang"
        @click="editor.chain().focus().toggleStrike().run()"
      >
        <span class="material-symbols-outlined">strikethrough_s</span>
      </button>

      <div class="toolbar-divider"></div>

      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': editor.isActive('bulletList') }"
        title="Danh sách"
        @click="editor.chain().focus().toggleBulletList().run()"
      >
        <span class="material-symbols-outlined">format_list_bulleted</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': editor.isActive('orderedList') }"
        title="Danh sách số"
        @click="editor.chain().focus().toggleOrderedList().run()"
      >
        <span class="material-symbols-outlined">format_list_numbered</span>
      </button>

      <div class="toolbar-divider"></div>

      <button
        type="button"
        class="toolbar-btn"
        :disabled="!editor.can().undo()"
        title="Hoàn tác"
        @click="editor.chain().focus().undo().run()"
      >
        <span class="material-symbols-outlined">undo</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :disabled="!editor.can().redo()"
        title="Làm lại"
        @click="editor.chain().focus().redo().run()"
      >
        <span class="material-symbols-outlined">redo</span>
      </button>
    </div>

    <!-- Editor area -->
    <EditorContent :editor="editor" class="editor-body" />
  </div>
</template>

<script setup lang="ts">
import { watch, onBeforeUnmount } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Placeholder from '@tiptap/extension-placeholder'

const props = defineProps<{
  modelValue: string
  placeholder?: string
  hasError?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit,
    Placeholder.configure({
      placeholder: props.placeholder ?? 'Nhập nội dung...',
    }),
  ],
  onUpdate({ editor }) {
    emit('update:modelValue', editor.getHTML())
  },
})

// Sync khi giá trị thay đổi từ bên ngoài (EditJobPage load dữ liệu)
watch(
  () => props.modelValue,
  (val) => {
    if (!editor.value) return
    const current = editor.value.getHTML()
    if (current !== val) {
      editor.value.commands.setContent(val, { emitUpdate: false })
    }
  },
)

onBeforeUnmount(() => {
  editor.value?.destroy()
})
</script>

<style scoped>
.tiptap-wrapper {
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  overflow: hidden;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.tiptap-wrapper:focus-within {
  border-color: #4b9af6;
  box-shadow: 0 0 0 4px rgba(75, 154, 246, 0.1);
}
.tiptap-wrapper--error {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.08) !important;
}

/* Toolbar */
.editor-toolbar {
  background: #f8fafc;
  padding: 0.5rem 0.75rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  flex-wrap: wrap;
}
.toolbar-btn {
  padding: 0.3rem;
  border: none;
  background: none;
  cursor: pointer;
  color: #94a3b8;
  border-radius: 0.375rem;
  display: flex;
  align-items: center;
  transition: color 0.15s, background 0.15s;
}
.toolbar-btn:hover:not(:disabled) {
  color: #4b9af6;
  background: #eff6ff;
}
.toolbar-btn.is-active {
  color: #4b9af6;
  background: #dbeafe;
}
.toolbar-btn:disabled {
  opacity: 0.35;
  cursor: default;
}
.toolbar-btn .material-symbols-outlined {
  font-size: 1.125rem;
}
.toolbar-divider {
  width: 1px;
  height: 1.25rem;
  background: #e2e8f0;
  margin: 0 0.125rem;
}

/* Editor body */
.editor-body {
  min-height: 8rem;
}
</style>

<!-- Global styles cho ProseMirror (không dùng scoped) -->
<style>
.tiptap-wrapper .ProseMirror {
  padding: 0.875rem 1rem;
  outline: none;
  font-size: 0.9375rem;
  color: #374151;
  line-height: 1.7;
  min-height: 8rem;
}

.tiptap-wrapper .ProseMirror p {
  margin: 0 0 0.5rem;
}
.tiptap-wrapper .ProseMirror p:last-child {
  margin-bottom: 0;
}

.tiptap-wrapper .ProseMirror ul {
  list-style-type: disc;
  padding-left: 1.5rem;
  margin: 0.25rem 0 0.5rem;
}
.tiptap-wrapper .ProseMirror ol {
  list-style-type: decimal;
  padding-left: 1.5rem;
  margin: 0.25rem 0 0.5rem;
}
.tiptap-wrapper .ProseMirror li {
  display: list-item;
  margin-bottom: 0.25rem;
}

.tiptap-wrapper .ProseMirror strong {
  font-weight: 700;
}
.tiptap-wrapper .ProseMirror em {
  font-style: italic;
}
.tiptap-wrapper .ProseMirror s {
  text-decoration: line-through;
}

/* Placeholder */
.tiptap-wrapper .ProseMirror p.is-editor-empty:first-child::before {
  content: attr(data-placeholder);
  color: #94a3b8;
  pointer-events: none;
  float: left;
  height: 0;
}
</style>
