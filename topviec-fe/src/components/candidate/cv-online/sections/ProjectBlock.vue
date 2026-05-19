<template>
  <div class="section-block">
    <InlineField
      class="cv-item-title"
      :model-value="item.name"
      tag="h3"
      placeholder="Ten du an"
      :single-line="true"
      @update:model-value="emit('update', { name: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.role"
      tag="p"
      placeholder="Vai tro"
      :single-line="true"
      @update:model-value="emit('update', { role: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.organization"
      tag="p"
      placeholder="To chuc / khach hang"
      :single-line="true"
      @update:model-value="emit('update', { organization: $event })"
    />

    <div class="cv-item-meta">
      <InlineField
        :model-value="item.startDate"
        tag="div"
        placeholder="Bat dau"
        :single-line="true"
        @update:model-value="emit('update', { startDate: $event })"
      />
      <span class="meta-separator">-</span>
      <InlineField
        :model-value="item.endDate"
        tag="div"
        placeholder="Ket thuc"
        :single-line="true"
        @update:model-value="emit('update', { endDate: $event })"
      />
      <InlineField
        :model-value="item.projectUrl"
        tag="div"
        placeholder="Project URL"
        :single-line="true"
        @update:model-value="emit('update', { projectUrl: $event })"
      />
    </div>

    <InlineField
      class="cv-item-description"
      :model-value="item.description"
      tag="div"
      placeholder="Mo ta du an"
      @update:model-value="emit('update', { description: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineProjectItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineProjectItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineProjectItem>]
}>()
</script>
