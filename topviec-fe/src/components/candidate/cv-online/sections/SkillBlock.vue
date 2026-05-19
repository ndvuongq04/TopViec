<template>
  <div class="section-block section-block--compact">
    <InlineField
      class="cv-item-title"
      :model-value="item.name"
      tag="h3"
      placeholder="Ten ky nang"
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
      :model-value="item.description"
      tag="div"
      placeholder="Mo ta ky nang"
      @update:model-value="emit('update', { description: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineSkillItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineSkillItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineSkillItem>]
}>()
</script>
