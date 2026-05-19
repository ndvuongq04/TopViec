<template>
  <div class="section-block section-block--compact">
    <InlineField
      class="cv-item-title"
      :model-value="item.name"
      tag="h3"
      placeholder="So thich"
      :single-line="true"
      @update:model-value="emit('update', { name: $event })"
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
import type { CvOnlineHobbyItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineHobbyItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineHobbyItem>]
}>()
</script>
