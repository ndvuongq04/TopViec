<template>
  <BubbleMenu
    v-if="activeEditor"
    :editor="activeEditor"
    :options="{ placement: 'top', offset: 8 }"
  >
    <div class="floating-toolbar">
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': activeEditor.isActive('bold') }"
        title="Dam"
        @click="activeEditor.chain().focus().toggleBold().run()"
      >
        <span class="material-symbols-outlined">format_bold</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': activeEditor.isActive('italic') }"
        title="Nghieng"
        @click="activeEditor.chain().focus().toggleItalic().run()"
      >
        <span class="material-symbols-outlined">format_italic</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': activeEditor.isActive('underline') }"
        title="Gach chan"
        @click="activeEditor.chain().focus().toggleUnderline().run()"
      >
        <span class="material-symbols-outlined">format_underlined</span>
      </button>

      <div class="toolbar-divider" />

      <label class="color-picker" title="Doi mau">
        <span class="material-symbols-outlined">palette</span>
        <input type="color" value="#0f172a" @input="setColor" />
      </label>
      <button
        type="button"
        class="toolbar-btn"
        title="Bo mau"
        @click="activeEditor.chain().focus().unsetColor().run()"
      >
        <span class="material-symbols-outlined">format_color_reset</span>
      </button>

      <div class="toolbar-divider" />

      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': activeEditor.isActive({ textAlign: 'left' }) }"
        title="Can trai"
        @click="activeEditor.chain().focus().setTextAlign('left').run()"
      >
        <span class="material-symbols-outlined">format_align_left</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': activeEditor.isActive({ textAlign: 'center' }) }"
        title="Can giua"
        @click="activeEditor.chain().focus().setTextAlign('center').run()"
      >
        <span class="material-symbols-outlined">format_align_center</span>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ 'is-active': activeEditor.isActive({ textAlign: 'right' }) }"
        title="Can phai"
        @click="activeEditor.chain().focus().setTextAlign('right').run()"
      >
        <span class="material-symbols-outlined">format_align_right</span>
      </button>
    </div>
  </BubbleMenu>
</template>

<script setup lang="ts">
import { inject, ref, type Ref } from 'vue'
import { BubbleMenu } from '@tiptap/vue-3/menus'
import type { Editor } from '@tiptap/core'

const activeEditor = inject<Ref<Editor | null>>('cv-active-editor', ref(null))

function setColor(event: Event) {
  const color = (event.target as HTMLInputElement).value
  if (!color || !activeEditor.value) return
  activeEditor.value.chain().focus().setColor(color).run()
}
</script>

<style scoped>
.floating-toolbar {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  border: 1px solid rgba(15, 23, 42, 0.1);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.94);
  padding: 0.35rem;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.16);
  backdrop-filter: blur(12px);
}

.toolbar-btn,
.color-picker {
  display: inline-flex;
  width: 2rem;
  height: 2rem;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #475569;
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.toolbar-btn:hover,
.color-picker:hover,
.toolbar-btn.is-active {
  background: #e0f2fe;
  color: #0369a1;
}

.toolbar-btn .material-symbols-outlined,
.color-picker .material-symbols-outlined {
  font-size: 1.2rem;
}

.toolbar-divider {
  width: 1px;
  height: 1.35rem;
  background: #e2e8f0;
  margin: 0 0.15rem;
}

.color-picker {
  position: relative;
  overflow: hidden;
}

.color-picker input {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
  opacity: 0;
}
</style>
