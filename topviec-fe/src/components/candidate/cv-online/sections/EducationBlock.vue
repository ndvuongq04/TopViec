<template>
  <div class="section-block">
    <InlineField
      class="cv-item-title"
      :model-value="item.degree"
      tag="h3"
      placeholder="Bang cap"
      :single-line="true"
      @update:model-value="emit('update', { degree: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.school"
      tag="p"
      placeholder="Truong hoc"
      :single-line="true"
      @update:model-value="emit('update', { school: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.fieldOfStudy"
      tag="p"
      placeholder="Chuyen nganh"
      :single-line="true"
      @update:model-value="emit('update', { fieldOfStudy: $event })"
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
    </div>

    <InlineField
      class="cv-item-description"
      :model-value="item.description"
      tag="div"
      placeholder="Mo ta hoc van"
      @update:model-value="emit('update', { description: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineEducationItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineEducationItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineEducationItem>]
}>()
</script>
