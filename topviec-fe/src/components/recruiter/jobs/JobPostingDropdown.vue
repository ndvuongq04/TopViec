<template>
  <div class="dropdown" ref="dropdownRef">
    <button class="btn-action" @click.stop="toggle">
      <span class="material-symbols-outlined icon-xl">more_vert</span>
    </button>
    <Teleport to="body">
      <div
        v-if="isOpen"
        class="dropdown-menu"
        :style="menuStyle"
        @click.stop
      >
        <slot />
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

const isOpen = ref(false)
const dropdownRef = ref<HTMLElement>()
const menuStyle = ref({})

const toggle = async () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    await nextTick()
    positionMenu()
  }
}

const positionMenu = () => {
  if (!dropdownRef.value) return
  const rect = dropdownRef.value.getBoundingClientRect()
  menuStyle.value = {
    position: 'fixed',
    top: `${rect.bottom + 4}px`,
    right: `${window.innerWidth - rect.right}px`,
    zIndex: 9999,
  }
}

const onClickOutside = (e: MouseEvent) => {
  if (!dropdownRef.value?.contains(e.target as Node)) {
    isOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', onClickOutside))
onUnmounted(() => document.removeEventListener('click', onClickOutside))
</script>

<style scoped>
.dropdown { position: relative; display: inline-block; }

.btn-action {
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  border: none;
  background: none;
  color: var(--color-on-surface-muted);
  cursor: pointer;
  transition: background 0.15s;
}
.btn-action:hover { background: #f1f5f9; }
.icon-xl { font-size: 1.25rem !important; }
</style>

<!-- dropdown-menu style phải là non-scoped vì dùng Teleport to body -->
<style>
.dropdown-menu {
  width: 14rem;
  background: #fff;
  border-radius: 0.5rem;
  box-shadow: 0 10px 25px rgba(0,0,0,0.12), 0 2px 6px rgba(0,0,0,0.06);
  border: 1px solid #f1f5f9;
  padding: 0.5rem 0;
  overflow: hidden;
}
.dropdown-menu__item {
  width: 100%;
  padding: 0.625rem 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1rem;
  font-weight: 500;
  font-family: inherit;
  color: #0f172a;
  background: none;
  border: none;
  cursor: pointer;
  text-align: left;
  transition: background 0.15s;
  box-sizing: border-box;
}
.dropdown-menu__item:hover { background: #f8fafc; }
.dropdown-menu__item--danger { color: #ef4444; }
.dropdown-menu__item--danger:hover { background: rgba(239,68,68,0.05); }
.dropdown-menu__item-icon { color: #4B9AF6; font-size: 1.25rem !important; }
.dropdown-menu__item--danger .dropdown-menu__item-icon { color: #ef4444; }
.dropdown-menu__divider { height: 1px; background: #f1f5f9; margin: 0.25rem 0; }
</style>
