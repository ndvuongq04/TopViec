<template>
  <div class="ald-hero">
    <!-- Admin -->
    <div class="ald-hero__section">
      <p class="ald-hero__label">Người thực hiện</p>
      <div class="ald-hero__admin">
        <div class="ald-hero__admin-avatar">{{ log.admin.initials }}</div>
        <div>
          <p class="ald-hero__admin-name">{{ log.admin.name }}</p>
          <p class="ald-hero__admin-email">{{ log.admin.email }}</p>
        </div>
      </div>
      <span v-if="log.admin.role" class="ald-hero__tag ald-hero__tag--default">{{ log.admin.role }}</span>
      <span v-else class="ald-hero__tag ald-hero__tag--default italic opacity-60">N/A</span>
    </div>

    <!-- Action -->
    <div class="ald-hero__section ald-hero__section--bordered">
      <p class="ald-hero__label">Hành động</p>
      <div>
        <span class="ald-hero__action-code">{{ log.action }}</span>
        <p class="ald-hero__category">
          <span class="material-symbols-outlined">category</span>
          {{ log.category }}
        </p>
      </div>
      <span v-if="type === 'audit'" :class="['ald-hero__tag', `ald-hero__tag--${log.severity.toLowerCase()}`]">{{ log.severityLabel || log.severity }}</span>
    </div>

    <!-- Resource -->
    <div class="ald-hero__section ald-hero__section--bordered">
      <p class="ald-hero__label">Đối tượng</p>
      <div>
        <p class="ald-hero__resource-type">
          <span class="material-symbols-outlined">business</span>
          {{ log.resource.type }}
        </p>
        <p class="ald-hero__resource-name">{{ log.resource.name }}</p>
      </div>
      <span class="ald-hero__resource-id">ID: {{ log.resource.id }}</span>
    </div>

    <!-- Result -->
    <div class="ald-hero__section ald-hero__section--bordered">
      <p class="ald-hero__label">Kết quả</p>
      <span :class="['ald-hero__result-badge', `ald-hero__result-badge--${log.result.status}`]">
        <span class="material-symbols-outlined">check_circle</span>
        {{ log.result.status === 'success' ? 'Success' : 'Failed' }}
      </span>
      <div class="ald-hero__result-note">
        <span class="ald-hero__result-quote material-symbols-outlined">format_quote</span>
        "{{ log.result.note }}"
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  log: {
    admin: { name: string; email: string; initials: string; role: string }
    action: string; category: string; severity: string; severityLabel?: string
    resource: { type: string; name: string; id: string }
    result: { status: string; note: string }
  },
  type: 'audit' | 'business'
}>()
</script>

<style scoped>
.ald-hero {
  background: #fff; border-radius: 1rem; padding: 2rem;
  margin-bottom: 2.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(228,226,220,0.5);
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 2rem;
}
@media (max-width: 1024px) { .ald-hero { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px)  { .ald-hero { grid-template-columns: 1fr; } }

.ald-hero__section { display: flex; flex-direction: column; gap: 1rem; }
.ald-hero__section--bordered { padding-left: 2rem; border-left: 1px solid rgba(228,226,220,0.5); }
@media (max-width: 640px) { .ald-hero__section--bordered { padding-left: 0; border-left: none; border-top: 1px solid rgba(228,226,220,0.5); padding-top: 1rem; } }

.ald-hero__label {
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.1em; color: #574240;
}

/* Admin */
.ald-hero__admin { display: flex; align-items: center; gap: 1rem; }
.ald-hero__admin-avatar {
  width: 48px; height: 48px; border-radius: 50%; flex-shrink: 0;
  background: rgba(150,49,49,0.1); color: #76191d;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 1rem;
}
.ald-hero__admin-name  { font-size: 1rem; font-weight: 700; color: #1b1c18; }
.ald-hero__admin-email { font-size: 0.875rem; color: #574240; margin-top: 2px; }

/* Action */
.ald-hero__action-code {
  display: inline-block; font-family: 'JetBrains Mono', monospace;
  font-size: 0.875rem; font-weight: 700; color: #1b1c18;
  background: #f6f4ec; padding: 4px 8px; border-radius: 6px;
}
.ald-hero__category {
  display: flex; align-items: center; gap: 6px;
  font-size: 0.875rem; color: #574240; margin-top: 8px;
}
.ald-hero__category .material-symbols-outlined { font-size: 16px; }

/* Resource */
.ald-hero__resource-type {
  display: flex; align-items: center; gap: 6px;
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #574240; margin-bottom: 6px;
}
.ald-hero__resource-type .material-symbols-outlined { font-size: 16px; }
.ald-hero__resource-name { font-size: 1rem; font-weight: 700; color: #1b1c18; }
.ald-hero__resource-id {
  font-family: 'JetBrains Mono', monospace; font-size: 0.75rem;
  background: #f6f4ec; padding: 4px 8px; border-radius: 6px;
  color: #574240; display: inline-block;
}

/* Tags */
.ald-hero__tag {
  display: inline-block; padding: 4px 12px; border-radius: 999px;
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em;
}
.ald-hero__tag--default  { background: #eae8e1; color: #574240; }
.ald-hero__tag--high     { background: #ffdad6; color: #ba1a1a; }
.ald-hero__tag--critical { background: rgba(186,26,26,0.1); color: #ba1a1a; }
.ald-hero__tag--medium   { background: #faeeda; color: #633806; }
.ald-hero__tag--low      { background: #eae8e1; color: #574240; }

/* Result */
.ald-hero__result-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 4px 12px; border-radius: 999px;
  font-size: 0.75rem; font-weight: 700;
}
.ald-hero__result-badge--success { background: rgba(0,70,56,0.1); color: #004638; }
.ald-hero__result-badge .material-symbols-outlined { font-size: 14px; }

.ald-hero__result-note {
  font-size: 0.875rem; color: #574240; font-style: italic;
  background: #f6f4ec; padding: 14px; border-radius: 0.75rem;
  border: 1px solid rgba(228,226,220,0.3); position: relative;
  line-height: 1.6;
}
.ald-hero__result-quote {
  position: absolute; top: -10px; left: -8px;
  font-size: 20px; color: rgba(228,226,220,0.5);
  transform: rotate(180deg);
}
</style>