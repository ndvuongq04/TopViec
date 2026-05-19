<template>
  <div class="section-block section-block--compact">
    <InlineField
      class="cv-item-title"
      :model-value="item.name"
      tag="h3"
      placeholder="Ngon ngu"
      :single-line="true"
      @update:model-value="emit('update', { name: $event })"
    />
    <InlineField
      class="cv-item-meta"
      :model-value="item.level"
      tag="div"
      placeholder="Trinh do"
      :single-line="true"
      @update:model-value="emit('update', { level: $event })"
    />
    <InlineField
      class="cv-item-description"
      :model-value="item.certificate"
      tag="div"
      placeholder="Chung chi"
      :single-line="true"
      @update:model-value="emit('update', { certificate: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineLanguageItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineLanguageItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineLanguageItem>]
}>()
</script>
