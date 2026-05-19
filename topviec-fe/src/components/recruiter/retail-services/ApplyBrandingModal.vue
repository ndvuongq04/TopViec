<template>
  <Teleport to="body">
    <div v-if="visible" class="backdrop" @click.self="$emit('close')">
      <div class="modal" role="dialog" aria-modal="true">
        <!-- Header -->
        <div class="modal-header">
          <div class="header-icon">
            <span class="material-symbols-outlined">verified</span>
          </div>
          <div>
            <h3 class="modal-title">Áp dụng dịch vụ đang chạy</h3>
            <p class="modal-sub">Chọn một dịch vụ branding đang hoạt động để áp dụng cho công ty</p>
          </div>
          <button class="close-btn" @click="$emit('close')">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <!-- Body -->
        <div class="modal-body">
          <div v-if="serviceStore.loading" class="state-center">
            <span class="material-symbols-outlined spin">progress_activity</span>
            <span>Đang tải danh sách dịch vụ...</span>
          </div>

          <div v-else-if="eligibleAddons.length === 0" class="state-center empty">
            <span class="material-symbols-outlined">inventory_2</span>
            <span>Bạn không có dịch vụ branding nào đang hoạt động.</span>
          </div>

          <div v-else class="addon-list">
            <label
              v-for="addon in eligibleAddons"
              :key="addon.id"
              class="addon-item"
              :class="{ selected: selectedId === addon.id, depleted: addon.quantityRemaining <= 0 }"
            >
              <input
                type="radio"
                :value="addon.id"
                v-model="selectedId"
                :disabled="addon.quantityRemaining <= 0"
                class="sr-only"
              />
              <div class="addon-check">
                <span v-if="selectedId === addon.id" class="material-symbols-outlined check-icon">check_circle</span>
                <span v-else class="material-symbols-outlined check-icon unset">radio_button_unchecked</span>
              </div>
              <div class="addon-info">
                <span class="addon-name">{{ addon.addonName ?? addon.addonCode }}</span>
                <div class="addon-meta">
                  <span class="badge" :class="addon.quantityRemaining > 0 ? 'badge-green' : 'badge-red'">
                    Còn {{ addon.quantityRemaining }} lượt
                  </span>
                  <span v-if="addon.expiredAt" class="meta-date">
                    · HSD: {{ formatDate(addon.expiredAt) }}
                  </span>
                </div>
              </div>
              <span v-if="addon.quantityRemaining <= 0" class="depleted-label">Hết lượt</span>
            </label>
          </div>
        </div>

        <!-- Footer -->
        <div class="modal-footer">
          <button class="btn-cancel" @click="$emit('close')">Hủy</button>
          <button
            class="btn-apply"
            :disabled="!selectedId || applying"
            @click="handleApply"
          >
            <span class="material-symbols-outlined spin-icon" v-if="applying">progress_activity</span>
            <span class="material-symbols-outlined" v-else>check</span>
            {{ applying ? 'Đang áp dụng...' : 'Áp dụng' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import dayjs from 'dayjs'
import { useEmployerServiceManagementStore } from '@/stores/employerServiceManagement.store'
import { useToast } from '@/composables/useToast'
import { ServiceCategory } from '@/constants/serviceCatalog.constants'
import { SubscriptionStatus } from '@/constants/servicePackage.constants'

const props = defineProps<{ visible: boolean }>()
const emit  = defineEmits<{ close: []; applied: [] }>()

const serviceStore = useEmployerServiceManagementStore()
const toast        = useToast()

const selectedId = ref<number | null>(null)
const applying   = ref(false)

const eligibleAddons = computed(() =>
  serviceStore.addons.filter(
    a => a.serviceCategory !== ServiceCategory.JOB_POSTING
      && a.status === SubscriptionStatus.ACTIVE,
  )
)

watch(
  () => props.visible,
  (val) => {
    if (val) {
      selectedId.value = null
      if (serviceStore.addons.length === 0) {
        serviceStore.fetchMyAddons()
      }
    }
  },
)

function formatDate(iso: string) {
  return dayjs(iso).format('DD/MM/YYYY')
}

async function handleApply() {
  if (!selectedId.value) return
  applying.value = true
  try {
    await serviceStore.applyBrandingToCompany({ companyAddonId: selectedId.value })
    toast.success('Áp dụng dịch vụ thành công!')
    emit('applied')
    emit('close')
  } catch {
    toast.error(serviceStore.error ?? 'Áp dụng thất bại. Vui lòng thử lại.')
  } finally {
    applying.value = false
  }
}
</script>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(2px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.modal {
  background: #fff;
  border-radius: 16px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 20px 20px 16px;
  border-bottom: 1px solid #f1f5f9;
}
.header-icon {
  width: 40px; height: 40px;
  border-radius: 10px;
  background: #fdf4ff;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.header-icon .material-symbols-outlined { font-size: 22px; color: #9333ea; }
.modal-title { font-size: 1rem; font-weight: 700; color: #0f172a; margin: 0 0 2px; }
.modal-sub   { font-size: 0.8rem; color: #64748b; margin: 0; line-height: 1.4; }
.close-btn {
  margin-left: auto;
  border: none; background: transparent; cursor: pointer;
  padding: 4px; border-radius: 6px; flex-shrink: 0;
  display: flex; align-items: center; color: #94a3b8;
  transition: background 0.15s, color 0.15s;
}
.close-btn:hover { background: #f1f5f9; color: #334155; }
.close-btn .material-symbols-outlined { font-size: 20px; }

.modal-body {
  padding: 16px 20px;
  max-height: 320px;
  overflow-y: auto;
}

.state-center {
  display: flex; flex-direction: column; align-items: center;
  gap: 8px; padding: 32px 0;
  font-size: 0.875rem; color: #94a3b8;
}
.state-center .material-symbols-outlined { font-size: 32px; }
.state-center.empty .material-symbols-outlined { color: #cbd5e1; }

.addon-list { display: flex; flex-direction: column; gap: 8px; }

.addon-item {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 14px;
  border-radius: 10px; border: 1.5px solid #e2e8f0;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
  user-select: none;
}
.addon-item:hover:not(.depleted) { border-color: #9333ea; background: #fdf4ff; }
.addon-item.selected  { border-color: #9333ea; background: #fdf4ff; }
.addon-item.depleted  { opacity: 0.5; cursor: not-allowed; }

.sr-only { position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(0,0,0,0); }

.addon-check { flex-shrink: 0; }
.check-icon       { font-size: 20px; color: #9333ea; }
.check-icon.unset { color: #cbd5e1; }

.addon-info { flex: 1; min-width: 0; }
.addon-name { font-size: 0.9rem; font-weight: 600; color: #0f172a; display: block; }
.addon-meta { display: flex; align-items: center; gap: 4px; margin-top: 4px; flex-wrap: wrap; }
.badge { font-size: 0.7rem; font-weight: 600; padding: 2px 7px; border-radius: 99px; }
.badge-green { background: #dcfce7; color: #16a34a; }
.badge-red   { background: #fee2e2; color: #dc2626; }
.meta-date   { font-size: 0.75rem; color: #94a3b8; }
.depleted-label { font-size: 0.7rem; color: #ef4444; font-weight: 600; flex-shrink: 0; }

.modal-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
}
.btn-cancel {
  padding: 8px 18px;
  border: 1.5px solid #e2e8f0; background: #fff; color: #64748b;
  font-size: 0.85rem; font-weight: 600; border-radius: 8px; cursor: pointer;
  transition: border-color 0.15s, color 0.15s;
}
.btn-cancel:hover { border-color: #94a3b8; color: #334155; }

.btn-apply {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 18px;
  border: none; background: #9333ea; color: #fff;
  font-size: 0.85rem; font-weight: 700; border-radius: 8px; cursor: pointer;
  transition: background 0.15s, opacity 0.15s;
}
.btn-apply:hover:not(:disabled) { background: #7e22ce; }
.btn-apply:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-apply .material-symbols-outlined { font-size: 16px; }

.spin-icon {
  animation: spin 0.8s linear infinite;
  display: inline-block;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
