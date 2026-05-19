<template>
  <div class="section-block">
    <InlineField
      class="cv-item-title"
      :model-value="item.title"
      tag="h3"
      placeholder="Ten giai thuong / thanh tich"
      :single-line="true"
      @update:model-value="emit('update', { title: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.issuer"
      tag="p"
      placeholder="Don vi trao"
      :single-line="true"
      @update:model-value="emit('update', { issuer: $event })"
    />
    <InlineField
      class="cv-item-meta"
      :model-value="item.awardedAt"
      tag="div"
      placeholder="Thoi gian nhan"
      :single-line="true"
      @update:model-value="emit('update', { awardedAt: $event })"
    />
    <InlineField
      class="cv-item-description"
      :model-value="item.description"
      tag="div"
      placeholder="Mo ta"
      @update:model-value="emit('update', { description: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineAwardItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineAwardItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineAwardItem>]
}>()
</script>
