<template>
  <div class="global-dropdown" ref="dropdownRef">
    <!-- Trigger Slot -->
    <slot name="trigger" :toggle="toggle" :is-open="isOpen">
      <button
        class="btn-dots"
        :class="{ 'btn-dots--active': isOpen }"
        @click.stop="toggle"
        aria-label="Hành động"
      >
        <span class="dots-icon">
          <span></span><span></span><span></span>
        </span>
      </button>
    </slot>

    <!-- Menu Content (Teleported) -->
    <Teleport to="body">
      <Transition name="dropdown">
        <div
          v-if="isOpen"
          ref="menuEl"
          class="dropdown-menu"
          :style="menuStyle"
          @click.stop
        >
          <slot :close="closeMenu"></slot>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

const props = withDefaults(defineProps<{
  align?: 'left' | 'right'
  offset?: number
  direction?: 'up' | 'down'
}>(), {
  align: 'right',
  offset: 8,
  direction: 'down'
})

const isOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)
const menuEl     = ref<HTMLElement | null>(null)
const menuStyle  = ref<Record<string, string>>({})

function toggle() {
  const willOpen = !isOpen.value
  if (willOpen) {
    window.dispatchEvent(new CustomEvent('topviec-close-dropdowns'))
  }
  isOpen.value = willOpen
  if (isOpen.value) {
    nextTick(positionMenu)
  }
}

function closeMenu() {
  isOpen.value = false
}

function positionMenu() {
  if (!dropdownRef.value) return
  const rect = dropdownRef.value.getBoundingClientRect()

  const style: Record<string, string> = {
    position: 'fixed',
    zIndex: '10000',
  }

  // Horizontal
  if (props.align === 'right') {
    style.right = `${window.innerWidth - rect.right}px`
  } else {
    style.left = `${rect.left}px`
  }

  // Vertical: auto-detect — mở lên khi không đủ chỗ bên dưới
  const menuHeight = menuEl.value?.offsetHeight ?? 320
  const spaceBelow = window.innerHeight - rect.bottom - props.offset
  const spaceAbove = rect.top - props.offset
  const openUp = spaceBelow < menuHeight && spaceAbove >= menuHeight

  if (openUp) {
    style.bottom = `${window.innerHeight - rect.top + props.offset}px`
  } else {
    style.top = `${rect.bottom + props.offset}px`
  }

  menuStyle.value = style
}

function handleOutsideClick(e: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target as Node)) {
    closeMenu()
  }
}

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') closeMenu()
}

onMounted(() => {
  document.addEventListener('click', handleOutsideClick)
  document.addEventListener('keydown', handleKeydown)
  window.addEventListener('topviec-close-dropdowns', closeMenu)
  window.addEventListener('resize', positionMenu)
  window.addEventListener('scroll', positionMenu, true) // Capture scroll
})

onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick)
  document.removeEventListener('keydown', handleKeydown)
  window.removeEventListener('topviec-close-dropdowns', closeMenu)
  window.removeEventListener('resize', positionMenu)
  window.removeEventListener('scroll', positionMenu, true)
})
</script>

<style scoped>
.global-dropdown {
  display: inline-flex;
}

/* Default 3-dot trigger button */
.btn-dots {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 0.5rem;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #94a3b8;
  transition: all 0.15s;
}

.btn-dots:hover,
.btn-dots--active {
  background: #f1f5f9;
  color: #4B9AF6;
}

.dots-icon {
  display: flex;
  gap: 3px;
  align-items: center;
}
.dots-icon span {
  display: block;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: currentColor;
}
</style>

<style>
/* Global styles for the teleported menu */
.dropdown-menu {
  min-width: 14rem;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.12), 0 4px 6px rgba(15, 23, 42, 0.04);
  padding: 0.375rem;
  overflow: visible; /* Important for tooltips */
}

/* Transition */
.dropdown-enter-active { transition: opacity 0.15s ease, transform 0.15s ease; }
.dropdown-leave-active { transition: opacity 0.1s ease,  transform 0.1s ease; }

/* Mặc định mở xuống: trượt từ trên xuống */
.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}

/* Trường hợp mở lên (CSS selector cho phần tử có style bottom) */
div[style*="bottom"].dropdown-enter-from,
div[style*="bottom"].dropdown-leave-to {
  transform: translateY(8px) scale(0.95);
}
</style>
