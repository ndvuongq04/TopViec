<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10">
    <div class="flex justify-between items-center mb-6">
      <h4 class="font-bold text-lg">Doanh thu theo gói dịch vụ</h4>
      <span class="text-xs font-bold text-[#963131]">Tổng: 1.2B VNĐ</span>
    </div>

    <div class="h-64 flex items-end justify-around gap-3 pb-6">
      <div
        v-for="bar in bars"
        :key="bar.label"
        class="flex flex-col items-center flex-1 gap-2"
      >
        <span class="text-xs font-bold text-slate-600 dark:text-slate-300">{{ bar.amount }}</span>
        <div
          class="w-full rounded-t-lg transition-all cursor-pointer hover:brightness-110"
          :style="{ height: bar.height, backgroundColor: bar.color }"
          :title="bar.label"
        ></div>
        <span class="text-[10px] font-bold text-slate-500 uppercase tracking-tight">{{ bar.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const bars = [
  { label: 'Basic',      height: '30%', amount: '180M', color: 'rgba(150,49,49,0.2)' },
  { label: 'Pro',        height: '55%', amount: '330M', color: 'rgba(150,49,49,0.4)' },
  { label: 'Premium',    height: '90%', amount: '540M', color: '#963131'              },
  { label: 'Enterprise', height: '70%', amount: '420M', color: 'rgba(150,49,49,0.6)' },
]
</script>