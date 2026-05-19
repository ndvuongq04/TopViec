<template>
  <component
    :is="tag"
    class="inline-field"
    :class="[
      `inline-field--${tag}`,
      {
        'inline-field--single-line': singleLine,
        'is-focused': isFocused,
        'is-hovered': isHovered,
      },
    ]"
    @mouseenter="isHovered = true"
    @mouseleave="isHovered = false"
  >
    <EditorContent :editor="editor" class="inline-field__content" />
  </component>
</template>

<script setup lang="ts">
import { inject, onBeforeUnmount, ref, watch, type Ref } from 'vue'
import { EditorContent, useEditor } from '@tiptap/vue-3'
import type { Editor } from '@tiptap/core'
import StarterKit from '@tiptap/starter-kit'
import Placeholder from '@tiptap/extension-placeholder'
import Underline from '@tiptap/extension-underline'
import Color from '@tiptap/extension-color'
import { TextStyle } from '@tiptap/extension-text-style'
import TextAlign from '@tiptap/extension-text-align'

const props = withDefaults(defineProps<{
  modelValue: string
  placeholder?: string
  singleLine?: boolean
  tag?: 'h1' | 'h2' | 'h3' | 'p' | 'span' | 'div'
}>(), {
  placeholder: '',
  singleLine: false,
  tag: 'div',
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const activeEditor = inject<Ref<Editor | null>>('cv-active-editor', ref(null))
const isFocused = ref(false)
const isHovered = ref(false)
let emitTimer: ReturnType<typeof setTimeout> | null = null
let blurTimer: ReturnType<typeof setTimeout> | null = null

function clearEmitTimer() {
  if (!emitTimer) return
  clearTimeout(emitTimer)
  emitTimer = null
}

function clearBlurTimer() {
  if (!blurTimer) return
  clearTimeout(blurTimer)
  blurTimer = null
}

function normalizedOutput(editor: Editor) {
  const html = editor.getHTML()
  if (html === '<p></p>' || editor.getText().trim() === '') return ''
  return html
}

function queueEmit(editor: Editor) {
  clearEmitTimer()
  emitTimer = setTimeout(() => {
    emit('update:modelValue', normalizedOutput(editor))
  }, 300)
}

function setActive(editor: Editor) {
  clearBlurTimer()
  activeEditor.value = editor
}

const editor = useEditor({
  content: props.modelValue || '',
  extensions: [
    StarterKit,
    TextStyle,
    Color,
    Underline,
    TextAlign.configure({
      types: ['heading', 'paragraph'],
    }),
    Placeholder.configure({
      placeholder: props.placeholder,
    }),
  ],
  editorProps: {
    attributes: {
      class: 'cv-inline-prosemirror',
    },
    handleKeyDown(_view, event) {
      if (!props.singleLine) return false
      if (event.key !== 'Enter') return false
      event.preventDefault()
      return true
    },
  },
  onFocus({ editor }) {
    isFocused.value = true
    setActive(editor)
  },
  onSelectionUpdate({ editor }) {
    setActive(editor)
  },
  onUpdate({ editor }) {
    setActive(editor)
    queueEmit(editor)
  },
  onBlur({ editor }) {
    isFocused.value = false
    blurTimer = setTimeout(() => {
      if (activeEditor.value === editor) {
        activeEditor.value = null
      }
    }, 180)
  },
})

watch(
  () => props.modelValue,
  (value) => {
    if (!editor.value) return
    const nextValue = value || ''
    if (normalizedOutput(editor.value) === nextValue) return
    editor.value.commands.setContent(nextValue, { emitUpdate: false })
  },
)

onBeforeUnmount(() => {
  clearEmitTimer()
  clearBlurTimer()
  editor.value?.destroy()
})
</script>

<style scoped>
.inline-field {
  display: block;
  min-height: 1.2em;
  border: 1px dashed transparent;
  border-radius: 4px;
  padding: 2px 6px;
  margin: 0;
  cursor: text;
  transition: border-color 0.18s ease, background-color 0.18s ease, box-shadow 0.18s ease;
}

.inline-field--span {
  display: inline-block;
}

.inline-field.is-hovered {
  border-color: #d1d5db;
}

.inline-field.is-focused {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.04);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
}

.inline-field :deep(.ProseMirror) {
  min-height: 1.2em;
  outline: none;
  color: inherit;
  font: inherit;
  line-height: inherit;
  letter-spacing: inherit;
  padding: 0;
}

.inline-field--single-line :deep(.ProseMirror) {
  white-space: nowrap;
}

.inline-field :deep(.ProseMirror p) {
  margin: 0;
}

.inline-field :deep(.ProseMirror ul),
.inline-field :deep(.ProseMirror ol) {
  margin: 0.25rem 0;
  padding-left: 1.3rem;
}

.inline-field :deep(.ProseMirror li) {
  margin: 0.125rem 0;
}

.inline-field :deep(.ProseMirror strong) {
  font-weight: 700;
}

.inline-field :deep(.ProseMirror em) {
  font-style: italic;
}

.inline-field :deep(.ProseMirror u) {
  text-decoration: underline;
}

.inline-field :deep(.ProseMirror p.is-editor-empty:first-child::before) {
  content: attr(data-placeholder);
  color: #94a3b8;
  pointer-events: none;
  float: left;
  height: 0;
}
</style>
