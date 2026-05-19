<template>
  <div class="section-block">
    <InlineField
      class="cv-item-title"
      :model-value="item.jobTitle"
      tag="h3"
      placeholder="Chuc danh"
      :single-line="true"
      @update:model-value="emit('update', { jobTitle: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.company"
      tag="p"
      placeholder="Cong ty"
      :single-line="true"
      @update:model-value="emit('update', { company: $event })"
    />

    <div class="cv-item-meta">
      <InlineField
        :model-value="item.location"
        tag="div"
        placeholder="Dia diem"
        :single-line="true"
        @update:model-value="emit('update', { location: $event })"
      />
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
      <label class="inline-check">
        <input
          type="checkbox"
          :checked="item.isCurrent"
          @change="emit('update', { isCurrent: ($event.target as HTMLInputElement).checked })"
        >
        Hien tai
      </label>
    </div>

    <InlineField
      class="cv-item-description"
      :model-value="item.description"
      tag="div"
      placeholder="Mo ta cong viec"
      @update:model-value="emit('update', { description: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineExperienceItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineExperienceItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineExperienceItem>]
}>()
</script>
