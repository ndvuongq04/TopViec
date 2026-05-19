<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10">
    <div class="flex justify-between items-center mb-6">
      <h4 class="font-bold text-lg">Tăng trưởng người dùng</h4>
      <select
        v-model="period"
        class="text-xs border border-slate-200 dark:bg-white/10 dark:border-none rounded-lg px-2 py-1 outline-none focus:ring-2 focus:ring-[#963131]/20"
      >
        <option value="7">7 tháng qua</option>
        <option value="3">3 tháng qua</option>
      </select>
    </div>

    <div class="h-64 flex flex-col justify-between">
      <div class="relative flex-1">
        <svg class="w-full h-full" preserveAspectRatio="none" viewBox="0 0 400 100">
          <defs>
            <linearGradient id="adminChartGradient" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0%" stop-color="#963131" />
              <stop offset="100%" stop-color="#963131" stop-opacity="0" />
            </linearGradient>
          </defs>
          <path :d="areaPath" fill="url(#adminChartGradient)" opacity="0.15" />
          <path
            :d="linePath"
            fill="none"
            stroke="#963131"
            stroke-width="2.5"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <circle
            v-for="(pt, i) in points"
            :key="i"
            :cx="pt.x"
            :cy="pt.y"
            r="3"
            fill="#963131"
            class="opacity-0 hover:opacity-100 transition-opacity cursor-pointer"
          />
        </svg>
      </div>
      <div class="flex justify-between text-[10px] font-bold text-slate-400 mt-4 uppercase tracking-tighter">
        <span v-for="label in xLabels" :key="label">{{ label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const period = ref('7')

const data7 = [20, 35, 30, 50, 45, 70, 90]
const data3 = [40, 65, 90]

const chartData = computed(() => period.value === '7' ? data7 : data3)

const xLabels = computed(() =>
  period.value === '7'
    ? ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul']
    : ['Tháng 1', 'Tháng 2', 'Tháng 3']
)

const points = computed(() => {
  const d = chartData.value
  const max = Math.max(...d)
  return d.map((v, i) => ({
    x: (i / (d.length - 1)) * 400,
    y: 100 - (v / max) * 90,
  }))
})

const linePath = computed(() =>
  points.value.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ')
)

const areaPath = computed(() => {
  const pts = points.value
  if (pts.length === 0) return ''
  const line = pts.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ')
  const lastPoint = pts[pts.length - 1]
  if (!lastPoint) return ''
  return `${line} L${lastPoint.x},100 L0,100 Z`
})
</script>
