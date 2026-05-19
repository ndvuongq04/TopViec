<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10">
    <div class="flex justify-between items-center mb-6">
      <h4 class="font-bold text-lg">Tăng trưởng người dùng (30 ngày)</h4>
      <div class="flex items-center gap-4">
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full bg-[#963131]"></span>
          <span class="text-xs text-slate-500">Ứng viên</span>
        </div>
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full bg-emerald-500"></span>
          <span class="text-xs text-slate-500">Nhà tuyển dụng</span>
        </div>
      </div>
    </div>

    <div class="h-64 flex flex-col justify-between">
      <div class="relative flex-1">
        <svg class="w-full h-full" preserveAspectRatio="none" viewBox="0 0 400 100">
          <!-- Gradients -->
          <defs>
            <linearGradient id="candidateGradient" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0%" stop-color="#963131" stop-opacity="0.2" />
              <stop offset="100%" stop-color="#963131" stop-opacity="0" />
            </linearGradient>
            <linearGradient id="employerGradient" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0%" stop-color="#10b981" stop-opacity="0.2" />
              <stop offset="100%" stop-color="#10b981" stop-opacity="0" />
            </linearGradient>
          </defs>
          
          <!-- Candidate Path -->
          <path :d="candidateAreaPath" fill="url(#candidateGradient)" />
          <path :d="candidateLinePath" fill="none" stroke="#963131" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
          
          <!-- Employer Path -->
          <path :d="employerAreaPath" fill="url(#employerGradient)" />
          <path :d="employerLinePath" fill="none" stroke="#10b981" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
      
      <!-- x-axis labels (first, middle, last) -->
      <div class="flex justify-between text-[10px] font-bold text-slate-400 mt-4 uppercase">
        <span>{{ formatDate(data[0]?.date) }}</span>
        <span>{{ formatDate(data[Math.floor(data.length / 2)]?.date) }}</span>
        <span>{{ formatDate(data[data.length - 1]?.date) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { DailyUserGrowth } from '@/types/dashboard.types';

const props = defineProps<{
  data: DailyUserGrowth[];
}>();

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getDate()}/${date.getMonth() + 1}`;
};

const maxVal = computed(() => {
  const allVals = props.data.flatMap(d => [d.candidateCount, d.employerCount]);
  return Math.max(...allVals, 10);
});

const getPoints = (key: 'candidateCount' | 'employerCount') => {
  if (props.data.length === 0) return [];
  const denominator = Math.max(props.data.length - 1, 1);
  return props.data.map((d, i) => ({
    x: (i / denominator) * 400,
    y: 100 - (d[key] / maxVal.value) * 90,
  }));
};

const candidatePoints = computed(() => getPoints('candidateCount'));
const employerPoints = computed(() => getPoints('employerCount'));

const getLinePath = (pts: {x: number, y: number}[]) => 
  pts.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ');

const getAreaPath = (pts: {x: number, y: number}[]) => {
  if (pts.length === 0) return '';
  const line = getLinePath(pts);
  const lastPoint = pts[pts.length - 1];
  if (!lastPoint) return '';
  return `${line} L${lastPoint.x},100 L0,100 Z`;
};

const candidateLinePath = computed(() => getLinePath(candidatePoints.value));
const candidateAreaPath = computed(() => getAreaPath(candidatePoints.value));

const employerLinePath = computed(() => getLinePath(employerPoints.value));
const employerAreaPath = computed(() => getAreaPath(employerPoints.value));
</script>
