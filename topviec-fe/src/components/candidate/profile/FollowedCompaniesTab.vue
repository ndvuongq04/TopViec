<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white dark:bg-surface-dark p-6 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <div>
        <h1 class="text-2xl font-bold text-text-main dark:text-white">Công ty đang theo dõi</h1>
        <p class="text-text-muted text-sm mt-1">Danh sách các công ty bạn đang quan tâm và theo dõi.</p>
      </div>
      <div v-if="followStore.followedCompanies?.totalElements > 0" class="px-4 py-2 bg-slate-100 dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700">
        <span class="text-sm text-text-muted font-medium">Đang theo dõi: </span>
        <span class="text-sm font-bold text-primary">{{ followStore.followedCompanies?.totalElements }}</span>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="followStore.loading && !followStore.followedCompanies" class="flex justify-center py-12">
      <span class="material-symbols-outlined animate-spin text-primary text-4xl">progress_activity</span>
    </div>

    <!-- List -->
    <div v-else-if="companies.length > 0" class="space-y-4">
      <div
        v-for="item in companies"
        :key="item.id"
        class="group bg-white dark:bg-surface-dark p-5 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:border-primary/30 transition-all flex items-start gap-4"
      >
        <!-- Logo -->
        <div class="w-16 h-16 rounded-2xl border border-slate-100 dark:border-slate-800 overflow-hidden bg-white shrink-0">
          <img
            v-if="item.company?.logoUrl"
            :src="item.company?.logoUrl"
            :alt="item.company?.name"
            class="w-full h-full object-contain"
          />
          <div v-else class="w-full h-full flex items-center justify-center bg-slate-50 text-slate-300">
            <span class="material-symbols-outlined text-3xl">corporate_fare</span>
          </div>
        </div>

        <!-- Info -->
        <div class="flex-1 min-w-0">
          <h3 class="font-bold text-text-main dark:text-white group-hover:text-primary transition-colors truncate">
            {{ item.company?.name }}
          </h3>
          <p class="text-sm text-text-muted mt-0.5 truncate">{{ item.company?.industry || 'Chưa cập nhật ngành nghề' }}</p>
          
          <div class="flex items-center gap-3 mt-3">
             <div class="flex items-center gap-1 text-xs text-text-muted">
                <span class="material-symbols-outlined text-[16px]">location_on</span>
                {{ item.company?.location || 'Toàn quốc' }}
             </div>
          </div>
        </div>

        <!-- Action -->
        <button
          class="p-2 text-text-muted hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-xl transition-all cursor-pointer"
          title="Bỏ theo dõi"
          @click="handleUnfollow(item.company)"
        >
          <span class="material-symbols-outlined text-[20px]">person_remove</span>
        </button>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else class="bg-white dark:bg-surface-dark p-12 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col items-center justify-center text-center">
      <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center text-primary mb-4">
        <span class="material-symbols-outlined text-3xl">domain_disabled</span>
      </div>
      <h3 class="text-lg font-bold text-text-main dark:text-white mb-2">Chưa theo dõi công ty nào</h3>
      <p class="text-text-muted text-sm mb-6">Theo dõi công ty để nhận những tin tức và cơ hội việc làm mới nhất.</p>
      <router-link
        to="/companies"
        class="flex items-center gap-2 bg-primary hover:bg-primary-hover text-white px-5 py-2.5 rounded-2xl font-bold text-sm shadow-lg shadow-blue-500/30 transition-colors"
      >
        <span class="material-symbols-outlined text-[20px]">search</span>
        Khám phá các doanh nghiệp
      </router-link>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex items-center justify-center gap-4 mt-8">
      <button
        class="p-2 rounded-xl border border-slate-200 dark:border-slate-800 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
      >
        <span class="material-symbols-outlined">chevron_left</span>
      </button>
      
      <div class="flex items-center gap-2 overflow-x-auto pb-1 max-w-[200px] sm:max-w-none">
         <button 
           v-for="p in totalPages" 
           :key="p"
           class="w-10 h-10 rounded-xl text-sm font-bold transition-all shrink-0 cursor-pointer"
           :class="currentPage === p-1 
             ? 'bg-primary text-white shadow-lg shadow-primary/20' 
             : 'text-text-muted hover:bg-slate-50 dark:hover:bg-slate-800'"
           @click="changePage(p-1)"
         >
           {{ p }}
         </button>
      </div>

      <button
        class="p-2 rounded-xl border border-slate-200 dark:border-slate-800 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
        :disabled="currentPage === totalPages - 1"
        @click="changePage(currentPage + 1)"
      >
        <span class="material-symbols-outlined">chevron_right</span>
      </button>
    </div>

    <GlobalConfirmModal />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useCandidateCompanyFollowStore } from '@/stores/candidateCompanyFollow.store'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import GlobalConfirmModal from '@/components/ui/GlobalConfirmModal.vue'

const followStore = useCandidateCompanyFollowStore()
const toast = useToast()
const { confirm } = useConfirm()
const currentPage = ref(0)

const companies = computed(() => followStore.followedCompanies?.result ?? [])
const totalPages = computed(() => followStore.followedCompanies?.meta?.pages ?? 0)

async function fetchFollowedCompanies() {
  await followStore.getFollowedCompanies(currentPage.value)
}

async function handleUnfollow(company: any) {
  const ok = await confirm({
    title: 'Bỏ theo dõi?',
    message: `Bạn có chắc chắn muốn bỏ theo dõi "${company.name}"?`,
    confirmText: 'Bỏ theo dõi',
    cancelText: 'Hủy',
    confirmColor: 'red',
    icon: 'person_remove'
  })

  if (ok) {
    try {
      await followStore.unfollowCompany(company.id)
      toast.success('Thành công', `Đã bỏ theo dõi công ty ${company.name}`)
      
      // Refresh list
      // Nếu là item cuối cùng của trang và không phải trang đầu, lùi lại 1 trang
      if (companies.value.length === 1 && currentPage.value > 0) {
        currentPage.value--
      }
      await fetchFollowedCompanies()
    } catch (err) {
      toast.error('Lỗi', 'Có lỗi xảy ra khi bỏ theo dõi')
    }
  }
}

function changePage(page: number) {
  currentPage.value = page
  fetchFollowedCompanies()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchFollowedCompanies()
})
</script>
