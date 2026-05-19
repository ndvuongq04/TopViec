<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm hover:shadow-md transition-shadow">
    <div class="flex items-center gap-2 mb-6">
      <span class="material-symbols-outlined text-primary">person_outline</span>
      <h3 class="text-xs font-extrabold text-slate-400 uppercase tracking-widest">Người thực hiện</h3>
    </div>

    <div class="flex items-start gap-5">
      <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-primary/20 to-blue-500/10 flex items-center justify-center border-2 border-white dark:border-slate-800 shadow-inner shrink-0 overflow-hidden">
        <img v-if="actor.avatar" :src="actor.avatar" :alt="actor.name" class="w-full h-full object-cover" />
        <span v-else class="text-primary font-black text-2xl">{{ actor.initials || actor.name.charAt(0) }}</span>
      </div>
      
      <div class="flex-1 space-y-4">
        <div>
          <h4 class="text-lg font-extrabold text-slate-900 dark:text-white leading-none">{{ actor.name }}</h4>
          <div class="flex items-center gap-2 mt-2">
            <span class="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-500 text-[10px] font-bold rounded uppercase tracking-wider">
              {{ actor.role }}
            </span>
            <span class="text-slate-300 text-xs">•</span>
            <span class="text-xs text-slate-400 font-medium">{{ actor.time }}</span>
          </div>
        </div>

        <div v-if="actor.ip || actor.userAgent" class="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4 border-t border-slate-100 dark:border-slate-800">
          <div v-if="actor.ip" class="space-y-1">
            <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Địa chỉ IP</p>
            <div class="flex items-center gap-2 text-sm font-bold text-slate-700 dark:text-slate-300">
              <span class="material-symbols-outlined text-base text-slate-400">public</span>
              {{ actor.ip }}
              <button 
                class="ml-1 p-1 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 hover:text-primary transition-all cursor-pointer"
                title="Sao chép IP"
                @click="copyToClipboard(actor.ip, 'Địa chỉ IP')"
              >
                <span class="material-symbols-outlined text-sm">content_copy</span>
              </button>
            </div>
          </div>
          <div v-if="actor.userAgent" class="space-y-1">
            <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Thiết bị / Trình duyệt</p>
            <div class="flex items-center gap-2 text-sm font-bold text-slate-700 dark:text-slate-300 min-w-0">
              <span class="material-symbols-outlined text-base text-slate-400 shrink-0">devices</span>
              <span class="truncate">{{ actor.userAgent }}</span>
              <button 
                class="ml-1 p-1 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 hover:text-primary transition-all cursor-pointer shrink-0"
                title="Sao chép User Agent"
                @click="copyToClipboard(actor.userAgent, 'Thông tin thiết bị')"
              >
                <span class="material-symbols-outlined text-sm">content_copy</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useToast } from '@/composables/useToast'

defineProps<{
  actor: { 
    name: string; 
    role: string; 
    avatar: string; 
    initials?: string; 
    time: string;
    ip: string;
    userAgent: string;
  }
}>()

const toast = useToast()

const copyToClipboard = (text: string, label: string) => {
  if (!text) return
  navigator.clipboard.writeText(text)
  toast.success('Đã sao chép', `${label} đã được lưu vào bộ nhớ tạm.`)
}
</script>