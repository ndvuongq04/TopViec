<template>
  <div class="rounded-[28px] border border-slate-200 bg-white p-3 shadow-[0_24px_60px_rgba(15,23,42,0.08)]">
    <iframe
      class="h-[980px] w-full rounded-[20px] bg-white"
      :srcdoc="document"
      sandbox=""
      title="CV live preview"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { buildCvPreviewDocument } from '@/utils/cvOnlineRenderer'
import type { CvOnlineExtraData } from '@/types/cvOnline.types'

const props = defineProps<{
  html: string
  css: string
  data: CvOnlineExtraData
}>()

const document = computed(() => buildCvPreviewDocument(props.html, props.css, props.data))
</script>
