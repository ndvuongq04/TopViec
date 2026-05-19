<template>
  <section class="service-panel">
    <!-- Header -->
    <div class="service-panel__header">
      <div class="service-panel__header-left">
        <h3 class="service-panel__title">Dịch vụ đang áp dụng</h3>
        <span class="material-symbols-outlined service-panel__info-icon" title="Dịch vụ lẻ áp dụng cho tin tuyển dụng này">info</span>
      </div>
      <router-link :to="{ name: 'recruiter-service-shop' }" class="service-panel__add-btn">
        <span class="material-symbols-outlined">add</span>
        Mua thêm
      </router-link>
    </div>

    <div class="service-panel__body">

      <!-- TODO: Hiển thị dịch vụ đang chạy trên tin này sau khi BE bổ sung -->
      <!-- GET /employer/services/job-posts/{jobPostingId}/addons -->

      <!-- Loading -->
      <div v-if="store.loading" class="service-panel__loading">
        <div class="service-panel__skeleton" v-for="n in 2" :key="n" />
      </div>

      <template v-else>
        <!-- Không có addon nào thuộc nhóm JOB_POSTING -->
        <div v-if="!availableAddons.length && !depletedAddons.length" class="service-panel__empty">
          <span class="material-symbols-outlined">inventory_2</span>
          <p>Bạn chưa có dịch vụ lẻ nào cho tin tuyển dụng.</p>
          <router-link :to="{ name: 'recruiter-service-shop' }" class="service-panel__empty-link">
            Mua ngay
          </router-link>
        </div>

        <!-- Banner xanh: addon còn lượt — mỗi addon 1 banner -->
        <div
          v-for="addon in availableAddons"
          :key="addon.id"
          class="service-banner service-banner--blue"
        >
          <div class="service-banner__left">
            <div class="service-banner__icon">{{ ADDON_CODE_EMOJI[addon.addonCode ?? ''] ?? '✨' }}</div>
            <div>
              <h4 class="service-banner__title">Tăng hiệu quả tuyển dụng</h4>
              <p class="service-banner__desc">
                Bạn còn
                <strong class="service-banner__highlight">{{ addon.quantityRemaining }} lượt {{ addon.addonName }}</strong>
                <span v-if="addon.expiredAt"> • Hết hạn {{ formatDate(addon.expiredAt) }}</span>
              </p>
            </div>
          </div>
          <div class="service-banner__actions">
            <button
              class="service-banner__btn service-banner__btn--primary"
              :disabled="applyingIds.has(addon.id)"
              @click="applyAddon(addon.id)"
            >
              <span v-if="applyingIds.has(addon.id)" class="service-banner__spinner" />
              <span v-else>Áp dụng ngay</span>
            </button>
            <router-link :to="{ name: 'recruiter-services' }" class="service-banner__btn service-banner__btn--ghost">
              Xem tất cả
            </router-link>
          </div>
        </div>

        <!-- Banner cam: addon hết lượt -->
        <div v-if="depletedAddons.length" class="service-banner service-banner--amber">
          <div class="service-banner__left">
            <div class="service-banner__icon">⚠️</div>
            <div>
              <h4 class="service-banner__title service-banner__title--amber">
                Hết lượt: {{ depletedAddons.map(a => a.addonName).join(', ') }}
              </h4>
              <p class="service-banner__desc service-banner__desc--amber">
                Nâng cấp gói hoặc mua lẻ để tiếp tục tiếp cận ứng viên tiềm năng.
              </p>
            </div>
          </div>
          <router-link :to="{ name: 'recruiter-service-shop' }" class="service-banner__btn service-banner__btn--amber">
            Mua thêm dịch vụ lẻ
          </router-link>
        </div>
      </template>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { useEmployerServiceManagementStore } from '@/stores/employerServiceManagement.store'
import { useToast } from '@/composables/useToast'
import { SubscriptionStatus } from '@/constants/servicePackage.constants'
import { ServiceCategory } from '@/constants/serviceCatalog.constants'

const props = defineProps<{ jobPostingId: number }>()

const store = useEmployerServiceManagementStore()
const toast = useToast()

// ─── Emoji map theo addonCode ─────────────────────────────────────────────────
const ADDON_CODE_EMOJI: Record<string, string> = {
    HOT_JOB_7D:     '🔥',
    URGENT_JOB_7D:  '⚡',
    EXTEND_JOB_7D:  '📅',
    EXTEND_JOB_14D: '📅',
    CV_ACCESS:      '🔓',
    PROFILE_BOOST:  '🚀',
    BRAND_BANNER:   '🏆',
    BRAND_POST:     '📝',
}

// ─── Chỉ lấy addon thuộc nhóm JOB_POSTING ────────────────────────────────────
const jobPostingAddons = computed(() =>
    store.addons.filter(a => a.serviceCategory === ServiceCategory.JOB_POSTING),
)

// Còn lượt: ACTIVE + quantityRemaining > 0 + chưa hết hạn
const availableAddons = computed(() =>
    jobPostingAddons.value.filter(a =>
        a.status === SubscriptionStatus.ACTIVE &&
        a.quantityRemaining > 0 &&
        (!a.expiredAt || dayjs(a.expiredAt).isAfter(dayjs())),
    ),
)

// Hết lượt hoặc hết hạn
const depletedAddons = computed(() =>
    jobPostingAddons.value.filter(a =>
        a.quantityRemaining === 0 || a.status !== SubscriptionStatus.ACTIVE,
    ),
)

// ─── Apply addon ──────────────────────────────────────────────────────────────
const applyingIds = ref<Set<number>>(new Set())

async function applyAddon(companyAddonId: number) {
    applyingIds.value = new Set([...applyingIds.value, companyAddonId])
    try {
        await store.applyAddonToJobPost(props.jobPostingId, { companyAddonId })
        toast.success('Áp dụng thành công', 'Dịch vụ đã được kích hoạt cho tin tuyển dụng này.')
    } catch {
        toast.error('Áp dụng thất bại', store.error ?? 'Vui lòng thử lại.')
    } finally {
        applyingIds.value = new Set([...applyingIds.value].filter(id => id !== companyAddonId))
    }
}

function formatDate(date: string) {
    return dayjs(date).format('DD/MM/YYYY')
}

onMounted(() => {
    if (!store.addons.length) {
        store.fetchMyAddons()
    }
})
</script>

<style scoped>
.service-panel {
  background: #fff;
  border-radius: 0.75rem;
  border: 1px solid #f8fafc;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  overflow: hidden;
}

/* Header */
.service-panel__header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
}
.service-panel__header-left { display: flex; align-items: center; gap: 8px; }
.service-panel__title { font-size: 0.95rem; font-weight: 800; color: #0f172a; }
.service-panel__info-icon { font-size: 1rem; color: #94a3b8; cursor: help; }

.service-panel__add-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 14px;
  border: 1px solid #4B9AF6; border-radius: 0.625rem;
  color: #4B9AF6; font-size: 0.8125rem; font-weight: 700;
  text-decoration: none; transition: background 0.2s;
}
.service-panel__add-btn:hover { background: rgba(219,234,254,0.3); }
.service-panel__add-btn .material-symbols-outlined { font-size: 0.9rem; }

/* Body */
.service-panel__body { padding: 1.25rem 1.5rem; display: flex; flex-direction: column; gap: 1rem; }

/* Loading */
.service-panel__loading { display: flex; flex-direction: column; gap: 12px; }
.service-panel__skeleton {
  height: 88px; border-radius: 0.75rem;
  background: #f1f5f9;
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.5; }
}

/* Empty */
.service-panel__empty {
  display: flex; flex-direction: column; align-items: center;
  gap: 8px; padding: 2rem 1rem; text-align: center;
}
.service-panel__empty .material-symbols-outlined { font-size: 2rem; color: #cbd5e1; }
.service-panel__empty p { font-size: 0.875rem; color: #94a3b8; font-weight: 500; }
.service-panel__empty-link {
  font-size: 0.875rem; font-weight: 700; color: #4B9AF6; text-decoration: none;
}

/* Banner */
.service-banner {
  display: flex; align-items: center; justify-content: space-between;
  gap: 1rem; padding: 1.25rem;
  border-radius: 0.875rem; flex-wrap: wrap;
}
.service-banner--blue  { background: #f8f7ff; border: 1px solid rgba(75,154,246,0.12); }
.service-banner--amber { background: #fffbeb; border: 1px solid #fde68a; }

.service-banner__left  { display: flex; align-items: center; gap: 1rem; flex: 1; min-width: 0; }
.service-banner__icon  {
  width: 44px; height: 44px; flex-shrink: 0;
  background: #fff; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.375rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.service-banner__title { font-weight: 700; color: #0f172a; font-size: 0.875rem; }
.service-banner__title--amber { color: #78350f; }
.service-banner__desc { font-size: 0.8125rem; color: #64748b; margin-top: 2px; }
.service-banner__desc--amber { color: rgba(120,53,15,0.7); }
.service-banner__highlight { color: #4B9AF6; font-weight: 700; }

.service-banner__actions { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }

/* Buttons */
.service-banner__btn {
  padding: 8px 18px; border-radius: 0.625rem;
  font-size: 0.8125rem; font-weight: 700;
  cursor: pointer; border: none; transition: all 0.15s;
  text-decoration: none; display: inline-flex; align-items: center; gap: 6px;
}
.service-banner__btn--primary {
  background: #4B9AF6; color: #fff;
  box-shadow: 0 4px 12px rgba(75,154,246,0.25);
  min-width: 110px; justify-content: center;
}
.service-banner__btn--primary:hover:not(:disabled) { transform: scale(0.97); }
.service-banner__btn--primary:disabled { opacity: 0.6; cursor: not-allowed; }
.service-banner__btn--ghost  { background: none; color: #475569; }
.service-banner__btn--ghost:hover  { background: rgba(0,0,0,0.04); }
.service-banner__btn--amber  { background: #fef3c7; color: #b45309; }
.service-banner__btn--amber:hover  { background: #fde68a; }

/* Spinner */
.service-banner__spinner {
  width: 14px; height: 14px;
  border: 2px solid rgba(255,255,255,0.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  display: inline-block;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
