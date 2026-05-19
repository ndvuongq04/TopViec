<template>
  <div class="section-block">
    <InlineField
      class="cv-item-title"
      :model-value="item.sectionTitle"
      tag="h3"
      placeholder="Ten section"
      :single-line="true"
      @update:model-value="emit('update', { sectionTitle: $event })"
    />
    <InlineField
      class="cv-item-title"
      :model-value="item.itemTitle"
      tag="h3"
      placeholder="Tieu de item"
      :single-line="true"
      @update:model-value="emit('update', { itemTitle: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.itemSubtitle"
      tag="p"
      placeholder="Phu de"
      :single-line="true"
      @update:model-value="emit('update', { itemSubtitle: $event })"
    />
    <InlineField
      class="cv-item-meta"
      :model-value="item.itemMeta"
      tag="div"
      placeholder="Meta / thoi gian"
      :single-line="true"
      @update:model-value="emit('update', { itemMeta: $event })"
    />
    <InlineField
      class="cv-item-description"
      :model-value="item.description"
      tag="div"
      placeholder="Noi dung"
      @update:model-value="emit('update', { description: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineCustomSectionItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineCustomSectionItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineCustomSectionItem>]
}>()
</script>
