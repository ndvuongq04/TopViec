<template>
  <div class="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50 flex flex-col">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h4 class="font-bold text-lg">Hiệu suất tuyển dụng</h4>
        <p class="text-sm text-slate-500">Thống kê ứng tuyển {{ periodLabel }}</p>
      </div>
      <select
        v-model="selectedPeriod"
        class="bg-slate-50 dark:bg-slate-900 border-none rounded-lg text-sm focus:ring-primary outline-none px-3 py-1.5"
      >
        <option value="week">Tuần này</option>
        <option value="month">Tháng này</option>
      </select>
    </div>

    <div class="flex-1 min-h-[250px] relative">
      <svg class="w-full h-full" preserveAspectRatio="none" viewBox="0 0 500 200">
        <defs>
          <linearGradient id="chartGradient" x1="0" x2="0" y1="0" y2="1">
            <stop offset="0%" stop-color="#4B9AF6" stop-opacity="0.2" />
            <stop offset="100%" stop-color="#4B9AF6" stop-opacity="0" />
          </linearGradient>
        </defs>
        <path
          :d="areaPath"
          fill="url(#chartGradient)"
        />
        <path
          :d="linePath"
          fill="none"
          stroke="#4B9AF6"
          stroke-width="3"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        <!-- Data points -->
        <circle
          v-for="(point, i) in chartPoints"
          :key="i"
          :cx="point.x"
          :cy="point.y"
          r="4"
          fill="#4B9AF6"
          class="opacity-0 hover:opacity-100 transition-opacity cursor-pointer"
        />
      </svg>

      <!-- X axis labels -->
      <div class="absolute bottom-0 left-0 w-full flex justify-between text-xs text-slate-400 font-bold px-2">
        <span v-for="label in xLabels" :key="label">{{ label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const selectedPeriod = ref<'week' | 'month'>('week')

const periodLabel = computed(() =>
  selectedPeriod.value === 'week' ? '7 ngày qua' : '30 ngày qua'
)

const xLabels = computed(() =>
  selectedPeriod.value === 'week'
    ? ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'CN']
    : ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12']
)

// Dữ liệu mẫu — sau này thay bằng props hoặc API
const weekData  = [20, 50, 40, 120, 100, 160, 140]
const monthData = [80, 60, 90, 110, 70, 130, 100, 140, 120, 160, 130, 150]

const chartData = computed(() =>
  selectedPeriod.value === 'week' ? weekData : monthData
)

const chartPoints = computed(() => {
  const data = chartData.value
  const w = 500
  const h = 180 // leave 20px bottom for labels
  const maxVal = Math.max(...data)
  const denominator = Math.max(data.length - 1, 1)
  return data.map((v, i) => ({
    x: (i / denominator) * w,
    y: h - (v / maxVal) * h,
  }))
})

const linePath = computed(() => {
  const pts = chartPoints.value
  return pts.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ')
})

const areaPath = computed(() => {
  const pts = chartPoints.value
  if (pts.length === 0) return ''
  const line = pts.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ')
  const lastPoint = pts[pts.length - 1]
  if (!lastPoint) return ''
  return `${line} L${lastPoint.x},200 L0,200 Z`
})
</script>
