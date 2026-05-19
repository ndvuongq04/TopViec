<template>
  <div class="section-block">
    <InlineField
      class="cv-item-title"
      :model-value="item.name"
      tag="h3"
      placeholder="Ten chung chi"
      :single-line="true"
      @update:model-value="emit('update', { name: $event })"
    />
    <InlineField
      class="cv-item-subtitle"
      :model-value="item.issuer"
      tag="p"
      placeholder="Don vi cap"
      :single-line="true"
      @update:model-value="emit('update', { issuer: $event })"
    />

    <div class="cv-item-meta">
      <InlineField
        :model-value="item.issuedAt"
        tag="div"
        placeholder="Ngay cap"
        :single-line="true"
        @update:model-value="emit('update', { issuedAt: $event })"
      />
      <span class="meta-separator">-</span>
      <InlineField
        :model-value="item.expiresAt"
        tag="div"
        placeholder="Het han"
        :single-line="true"
        @update:model-value="emit('update', { expiresAt: $event })"
      />
    </div>

    <div class="cv-item-meta">
      <InlineField
        :model-value="item.credentialId"
        tag="div"
        placeholder="Credential ID"
        :single-line="true"
        @update:model-value="emit('update', { credentialId: $event })"
      />
      <InlineField
        :model-value="item.credentialUrl"
        tag="div"
        placeholder="Credential URL"
        :single-line="true"
        @update:model-value="emit('update', { credentialUrl: $event })"
      />
    </div>

    <InlineField
      class="cv-item-description"
      :model-value="item.description"
      tag="div"
      placeholder="Mo ta chung chi"
      @update:model-value="emit('update', { description: $event })"
    />
  </div>
</template>

<script setup lang="ts">
import InlineField from '../InlineField.vue'
import type { CvOnlineCertificationItem } from '@/types/cvOnline.types'

defineProps<{
  item: CvOnlineCertificationItem
}>()

const emit = defineEmits<{
  update: [patch: Partial<CvOnlineCertificationItem>]
}>()
</script>
