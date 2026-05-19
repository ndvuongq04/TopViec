<script setup lang="ts">
import { onMounted, computed, watch, ref } from "vue";
import { useRoute } from "vue-router";
import { useCandidateCompanyStore } from "@/stores/candidateCompany.store";
import { useCandidateCompanyFollowStore } from "@/stores/candidateCompanyFollow.store";
import { parseBenefits, parseSocialLinks } from "@/types/company.types";
import { useToast } from "@/composables/useToast";
import { publicJobPostingService } from "@/services/jobPosting.service";
import type { ResJobPostingSummary, PaginationMeta } from "@/types/jobPosting.types";
import { formatSalary } from "@/types/jobPosting.types";
import { useSavedJobStore } from "@/stores/savedJob.store";

const route = useRoute();
const companyStore = useCandidateCompanyStore();
const followStore = useCandidateCompanyFollowStore();
const savedJobStore = useSavedJobStore();
const toast = useToast();
const slug = route.params.slug as string;

// ─── Job postings state ───────────────────────────────────────────────────────
const jobs = ref<ResJobPostingSummary[]>([]);
const jobsMeta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });
const jobsLoading = ref(false);
const currentPage = ref(0);
const keyword = ref("");
const searchInput = ref("");

async function fetchCompanyJobs(companyId: number, page = 0) {
  jobsLoading.value = true;
  try {
    const data = await publicJobPostingService.getByCompany(companyId, {
      page,
      size: 10,
      keyword: keyword.value || undefined,
    });
    jobs.value = data.result;
    jobsMeta.value = data.meta;
    currentPage.value = page;
    data.result.forEach(job => savedJobStore.checkIsSaved(job.id));
  } catch {
    // silent
  } finally {
    jobsLoading.value = false;
  }
}

function handleSearch() {
  if (!company.value?.id) return;
  keyword.value = searchInput.value.trim();
  fetchCompanyJobs(company.value.id, 0);
}

function goToPage(page: number) {
  if (!company.value?.id) return;
  fetchCompanyJobs(company.value.id, page);
}

onMounted(() => {
  if (slug) {
    companyStore.fetchBySlug(slug);
  }
});

const company = computed(() => companyStore.selectedCompany);
const loading = computed(() => companyStore.loading);
const error = computed(() => companyStore.error);

const parsedBenefits = computed(() => {
  if (!company.value || !company.value.benefits) return [];
  return parseBenefits(company.value.benefits);
});

const parsedSocial = computed(() => {
  if (!company.value || !company.value.socialLinks) return null;
  return parseSocialLinks(company.value.socialLinks);
});

watch(company, (newCompany) => {
  if (newCompany?.id) {
    followStore.checkFollowStatus(newCompany.id);
    fetchCompanyJobs(newCompany.id, 0);
  }
}, { immediate: true });

const isFollowing = computed(() => {
  if (!company.value?.id) return false;
  return followStore.followMap[company.value.id] || false;
});

// ─── Pagination computed ──────────────────────────────────────────────────────
const totalPages = computed(() => jobsMeta.value.pages);
const canGoPrev = computed(() => currentPage.value > 0);
const canGoNext = computed(() => currentPage.value < totalPages.value - 1);
const rangeStart = computed(() =>
  jobsMeta.value.totals > 0 ? currentPage.value * jobsMeta.value.pageSize + 1 : 0
);
const rangeEnd = computed(() =>
  Math.min((currentPage.value + 1) * jobsMeta.value.pageSize, jobsMeta.value.totals)
);

const visiblePages = computed(() => {
  const total = totalPages.value;
  const cur = currentPage.value;
  if (total <= 7) return Array.from({ length: total }, (_, i) => i);
  const pages: (number | "...")[] = [];
  if (cur <= 3) {
    pages.push(0, 1, 2, 3, 4, "...", total - 1);
  } else if (cur >= total - 4) {
    pages.push(0, "...", total - 5, total - 4, total - 3, total - 2, total - 1);
  } else {
    pages.push(0, "...", cur - 1, cur, cur + 1, "...", total - 1);
  }
  return pages;
});

// ─── Helpers ──────────────────────────────────────────────────────────────────

function formatDaysLeft(deadline: string): string {
  const diff = Math.ceil((new Date(deadline).getTime() - Date.now()) / 86_400_000);
  if (diff < 0) return "Đã hết hạn";
  if (diff === 0) return "Hết hạn hôm nay";
  return `Còn ${diff} ngày để ứng tuyển`;
}

function isDaysLeftWarning(deadline: string): boolean {
  const diff = Math.ceil((new Date(deadline).getTime() - Date.now()) / 86_400_000);
  return diff >= 0 && diff <= 7;
}

async function handleToggleSave(jobId: number) {
  try {
    const isSaved = await savedJobStore.toggleSave(jobId);
    toast.success(isSaved ? "Đã lưu việc làm" : "Đã bỏ lưu việc làm");
  } catch {
    toast.error("Vui lòng đăng nhập để lưu việc làm");
  }
}

async function toggleFollow() {
  if (!company.value?.id) return;
  try {
    if (isFollowing.value) {
      await followStore.unfollowCompany(company.value.id);
      toast.success("Đã bỏ theo dõi công ty");
    } else {
      await followStore.followCompany(company.value.id);
      toast.success("Đã theo dõi công ty");
    }
  } catch (err) {
    toast.error(followStore.error || "Thao tác thất bại");
  }
}
</script>

<template>
  <main class="flex flex-col flex-1 gap-6 min-w-0 pb-20 max-w-[1440px] mx-auto w-full px-4 md:px-10 py-6">
    <div v-if="loading" class="flex flex-col items-center justify-center py-20 gap-4">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      <p class="text-text-muted">Đang tải thông tin công ty...</p>
    </div>

    <div v-else-if="error" class="bg-red-50 text-red-500 p-6 rounded-2xl text-center">
      <span class="material-symbols-outlined text-4xl mb-2">error</span>
      <p class="font-bold">Lỗi khi tải dữ liệu</p>
      <p class="text-sm">{{ error }}</p>
    </div>

    <!-- Hero Section -->
    <div v-else-if="company" class="relative rounded-3xl flex-shrink-0 overflow-hidden bg-surface-light dark:bg-surface-dark shadow-sm border border-gray-100 dark:border-gray-800">
      <!-- Cover Image -->
      <div
        class="h-48 md:h-64 lg:h-80 w-full bg-cover bg-center relative group"
        :style="{ backgroundImage: `url('${company.coverUrl || 'https://lh3.googleusercontent.com/aida-public/AB6AXuAJYGefPbGq9DE3gYEwJLaXILq11eQMb86gZ3Kb52SAECvRpXCUMIw28o3e6CxrmuQA5fVCqBbLKRbxGADTqomM29QAGdEIA4QHNR9a-TJYvRwE43ME2honHZD4Giy7byZr0nEGR1bSbcT9-W8kVtBSqinWGCqZOs8NPpkF3EaEXXl-NssEYWvYFwIHj1tS6zNiKrJbHWyEzOXNz1lxelC_OtIgt5yj8GUAKQeeYVPtO_vLudBBlpdLDN2ziTelIbbCokE6RRlkIUft'}')` }">
        <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
      </div>

      <!-- Company Info Overlay/Block -->
      <div class="relative px-6 pb-6 pt-0 -mt-16 sm:-mt-20 md:px-10 flex flex-col sm:flex-row items-end sm:items-end gap-6">
        <!-- Logo Box -->
        <div class="size-32 rounded-2xl bg-white p-2 shadow-lg ring-4 ring-white dark:ring-surface-dark dark:bg-surface-dark shrink-0 flex items-center justify-center">
          <div class="flex items-center justify-center size-full rounded-xl bg-gray-50 dark:bg-gray-800 overflow-hidden">
            <img v-if="company.logoUrl" :src="company.logoUrl" :alt="company.name" class="w-full h-full object-contain" />
            <span v-else class="material-symbols-outlined text-primary text-5xl">deployed_code</span>
          </div>
        </div>

        <!-- Text Info -->
        <div class="flex-1 flex flex-col gap-1 pb-2">
          <h1 class="text-3xl font-extrabold sm:text-5xl text-white drop-shadow-[0_4px_8px_rgba(0,0,0,0.8)] tracking-tight">
            {{ company.name }}
          </h1>
          <div class="flex flex-wrap gap-4 mt-2 text-base text-gray-200 sm:text-gray-300">
            <div class="flex items-center gap-1.5" v-if="company.address">
              <span class="material-symbols-outlined text-[20px]">location_on</span>
              <span>{{ company.address }}</span>
            </div>
            <div class="flex items-center gap-1.5" v-if="company.companySize">
              <span class="material-symbols-outlined text-[20px]">group</span>
              <span>Quy mô: {{ company.companySize }}</span>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex gap-3 w-full sm:w-auto mt-4 sm:mt-0 pb-2">
          <button
            @click="toggleFollow"
            class="flex-1 sm:flex-none items-center justify-center rounded-xl h-12 px-6 text-base font-bold transition-all flex gap-2 cursor-pointer"
            :class="isFollowing ? 'bg-slate-200 text-slate-700 hover:bg-slate-300' : 'bg-primary text-white shadow-lg shadow-primary/20 hover:bg-primary-dark'"
          >
            <span class="material-symbols-outlined">{{ isFollowing ? 'check' : 'add' }}</span>
            <span>{{ isFollowing ? 'Đang theo dõi' : 'Theo dõi' }}</span>
          </button>
          <button class="items-center justify-center rounded-xl bg-surface-light dark:bg-surface-dark border border-gray-200 dark:border-gray-700 size-12 text-text-main dark:text-white hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors hidden sm:flex cursor-pointer">
            <span class="material-symbols-outlined">share</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Navigation Tabs -->
    <div v-if="company" class="sticky top-16 z-40 bg-background-light/95 dark:bg-background-dark/95 backdrop-blur-sm border-b border-gray-200 dark:border-gray-800">
      <div class="flex gap-8 overflow-x-auto no-scrollbar">
        <a class="flex items-center gap-2 border-b-[3px] border-primary py-4 text-primary" href="#">
          <span class="text-base font-bold whitespace-nowrap">TỔNG QUAN</span>
        </a>
      </div>
    </div>

    <!-- Content Grid -->
    <div v-if="company" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Left Column: Main Info -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Dashboard/Who We Are -->
        <section class="rounded-2xl bg-surface-light dark:bg-surface-dark border border-gray-100 dark:border-gray-800 p-6 sm:p-8 shadow-sm">
          <h2 class="text-3xl font-extrabold text-text-main dark:text-white mb-6 tracking-tight">Giới thiệu công ty</h2>
          <div
            v-if="company.description"
            class="prose prose-slate dark:prose-invert max-w-none text-text-muted dark:text-gray-300 text-base leading-relaxed tiptap-content"
            v-html="company.description"
          />
          <p v-else class="text-text-muted dark:text-gray-400">Chưa có thông tin giới thiệu chi tiết.</p>
        </section>

        <!-- Culture -->
        <section v-if="company.culture" class="rounded-2xl bg-surface-light dark:bg-surface-dark border border-gray-100 dark:border-gray-800 p-6 sm:p-8 shadow-sm">
          <h2 class="text-3xl font-extrabold text-text-main dark:text-white mb-6 tracking-tight">Văn hóa và môi trường làm việc</h2>
          <div
            class="prose prose-slate dark:prose-invert max-w-none text-text-muted dark:text-gray-300 text-base leading-relaxed tiptap-content"
            v-html="company.culture"
          />
        </section>

        <!-- Benefits -->
        <section v-if="parsedBenefits.length > 0" class="rounded-2xl bg-surface-light dark:bg-surface-dark border border-gray-100 dark:border-gray-800 p-6 sm:p-8 shadow-sm">
          <h2 class="text-3xl font-extrabold text-text-main dark:text-white mb-6 tracking-tight">Phúc lợi</h2>
          <ul class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <li v-for="(benefit, index) in parsedBenefits" :key="index" class="flex items-start gap-3">
              <div class="mt-1 flex items-center justify-center size-6 rounded-full bg-emerald-100 text-emerald-600 shrink-0">
                <span class="material-symbols-outlined text-sm font-bold">check</span>
              </div>
              <span class="text-text-muted dark:text-gray-300 leading-relaxed">{{ benefit }}</span>
            </li>
          </ul>
        </section>

        <!-- Job Openings -->
        <section class="rounded-2xl bg-surface-light dark:bg-surface-dark border border-gray-100 dark:border-gray-800 p-6 sm:p-8 shadow-sm">
          <!-- Title -->
          <h2 class="text-2xl font-extrabold text-text-main dark:text-white tracking-tight mb-5">
            Tuyển dụng
            <span v-if="jobsMeta.totals > 0" class="ml-2 text-base font-semibold text-text-muted">({{ jobsMeta.totals }})</span>
          </h2>

          <!-- Search bar -->
          <form @submit.prevent="handleSearch" class="flex gap-0 mb-5 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden shadow-sm">
            <div class="flex items-center flex-1 px-3 gap-2 bg-background-light dark:bg-background-dark">
              <span class="material-symbols-outlined text-text-muted text-[20px] shrink-0">search</span>
              <input
                v-model="searchInput"
                type="text"
                placeholder="Tên công việc, vị trí ứng tuyển..."
                class="flex-1 h-11 bg-transparent text-sm text-text-main dark:text-white placeholder-text-muted focus:outline-none"
              />
            </div>
            <button
              type="submit"
              class="h-11 px-5 bg-primary text-white text-sm font-bold hover:bg-primary-dark transition-colors flex items-center gap-1.5 shrink-0"
            >
              <span class="material-symbols-outlined text-[18px]">search</span>
              Tìm kiếm
            </button>
          </form>

          <!-- Loading -->
          <div v-if="jobsLoading" class="flex justify-center py-10">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>

          <!-- Empty -->
          <div v-else-if="jobs.length === 0" class="text-text-muted dark:text-gray-400 text-center py-10">
            <span class="material-symbols-outlined text-4xl mb-2 block">search_off</span>
            <p>{{ keyword ? `Không tìm thấy kết quả cho "${keyword}"` : 'Công ty hiện chưa có tin tuyển dụng nào.' }}</p>
          </div>

          <!-- List -->
          <ul v-else class="space-y-3">
            <li
              v-for="job in jobs"
              :key="job.id"
              class="border border-gray-200 dark:border-gray-700 rounded-xl p-4 bg-gray-50 dark:bg-gray-800/60 hover:border-primary/50 hover:bg-white dark:hover:bg-gray-800 hover:shadow-sm transition-all"
            >
              <div class="flex items-start gap-4">
                <!-- Logo -->
                <router-link :to="`/jobs/${job.id}`" class="shrink-0">
                  <div class="size-14 rounded-xl border border-gray-100 dark:border-gray-700 bg-white dark:bg-gray-800 flex items-center justify-center overflow-hidden">
                    <img
                      v-if="company?.logoUrl"
                      :src="company.logoUrl"
                      :alt="company?.name"
                      class="w-full h-full object-contain p-1"
                    />
                    <span v-else class="material-symbols-outlined text-primary text-2xl">deployed_code</span>
                  </div>
                </router-link>

                <!-- Content -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between gap-3">
                    <!-- Left: title + company + tags -->
                    <div class="flex-1 min-w-0">
                      <router-link :to="`/jobs/${job.id}`">
                        <h3 class="text-sm font-bold text-text-main dark:text-white hover:text-primary transition-colors line-clamp-2 leading-snug">
                          {{ job.title }}
                          <span v-if="job.isHot" class="ml-1.5 text-xs font-bold bg-red-100 text-red-500 dark:bg-red-900/30 dark:text-red-400 px-1.5 py-0.5 rounded align-middle">HOT</span>
                          <span v-if="job.isUrgent" class="ml-1 text-xs font-bold bg-orange-100 text-orange-500 dark:bg-orange-900/30 dark:text-orange-400 px-1.5 py-0.5 rounded align-middle">URGENT</span>
                        </h3>
                      </router-link>
                      <div class="flex items-center gap-1 mt-1">
                        <!-- Top badge — đồng bộ FeaturedBrandCard -->
                        <span
                          v-if="job.company?.isTopEmployer"
                          class="shrink-0 text-[11px] font-semibold px-2 py-0.5 rounded-full border"
                          style="background:#fff7ed; color:#b45309; border-color:#fcd34d"
                        >Top</span>
                        <span class="text-xs text-text-muted dark:text-gray-400 truncate">{{ company?.name }}</span>
                        <!-- Verified icon — đồng bộ FeaturedBrandCard -->
                        <div v-if="job.company?.isBrandVerified" class="relative flex items-center group/verified shrink-0">
                          <span
                            class="material-symbols-outlined text-blue-600 text-[16px] cursor-default"
                            style="font-variation-settings: 'FILL' 1"
                          >verified</span>
                          <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified:opacity-100 transition-opacity z-10">
                            Công ty đã xác minh
                            <span class="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-blue-800"></span>
                          </span>
                        </div>
                      </div>
                      <!-- Bottom tags -->
                      <div class="flex flex-wrap items-center gap-2 mt-2">
                        <span v-if="job.locations && job.locations.length > 0" class="text-xs text-text-muted dark:text-gray-400 bg-gray-100 dark:bg-gray-800 px-2 py-0.5 rounded-full">
                          {{ job.locations.map(l => l.name).join(', ') }}
                        </span>
                        <span
                          class="text-xs px-2 py-0.5 rounded-full font-medium"
                          :class="isDaysLeftWarning(job.deadline)
                            ? 'bg-orange-100 text-orange-600 dark:bg-orange-900/30 dark:text-orange-400'
                            : 'bg-emerald-50 text-emerald-600 dark:bg-emerald-900/20 dark:text-emerald-400'"
                        >
                          {{ formatDaysLeft(job.deadline) }}
                        </span>
                      </div>
                    </div>

                    <!-- Right: salary + actions -->
                    <div class="flex flex-col items-end gap-3 shrink-0">
                      <span class="flex items-center gap-1 text-sm font-semibold text-primary">
                        <span class="material-symbols-outlined text-[16px]">monetization_on</span>
                        {{ formatSalary(job) }}
                      </span>
                      <div class="flex items-center gap-2">
                        <router-link
                          :to="`/jobs/${job.id}`"
                          class="h-8 px-3.5 rounded-lg bg-primary text-white text-xs font-bold hover:bg-primary-dark transition-colors flex items-center"
                        >
                          Ứng tuyển
                        </router-link>
                        <button
                          @click.prevent="handleToggleSave(job.id)"
                          class="size-8 rounded-lg border flex items-center justify-center transition-colors"
                          :class="savedJobStore.isSavedMap[job.id]
                            ? 'border-primary bg-primary/10 text-primary'
                            : 'border-gray-200 dark:border-gray-700 text-text-muted hover:border-primary hover:text-primary hover:bg-gray-50 dark:hover:bg-gray-800'"
                          :title="savedJobStore.isSavedMap[job.id] ? 'Bỏ lưu' : 'Lưu việc làm'"
                        >
                          <span class="material-symbols-outlined text-[18px]">
                            {{ savedJobStore.isSavedMap[job.id] ? 'bookmark' : 'bookmark_border' }}
                          </span>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </li>
          </ul>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="mt-6 flex flex-col items-center gap-3">
            <p class="text-sm text-text-muted">
              Hiển thị {{ rangeStart }}–{{ rangeEnd }} trong số {{ jobsMeta.totals }} tin
            </p>
            <div class="flex items-center gap-1">
              <button
                @click="goToPage(currentPage - 1)"
                :disabled="!canGoPrev"
                class="h-9 px-3 rounded-lg border border-gray-200 dark:border-gray-700 text-sm font-medium disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
              >
                <span class="material-symbols-outlined text-[18px] align-middle">chevron_left</span>
              </button>
              <template v-for="(p, i) in visiblePages" :key="i">
                <span v-if="p === '...'" class="h-9 px-2 flex items-center text-text-muted text-sm select-none">…</span>
                <button
                  v-else
                  @click="goToPage(p as number)"
                  class="h-9 min-w-9 px-2 rounded-lg border text-sm font-medium transition-colors"
                  :class="(p as number) === currentPage
                    ? 'bg-primary text-white border-primary'
                    : 'border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 text-text-main dark:text-white'"
                >
                  {{ (p as number) + 1 }}
                </button>
              </template>
              <button
                @click="goToPage(currentPage + 1)"
                :disabled="!canGoNext"
                class="h-9 px-3 rounded-lg border border-gray-200 dark:border-gray-700 text-sm font-medium disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
              >
                <span class="material-symbols-outlined text-[18px] align-middle">chevron_right</span>
              </button>
            </div>
          </div>
        </section>
      </div>

      <!-- Right Column: Sidebar -->
      <div class="lg:col-span-1 space-y-6">
        <!-- Quick Facts Card -->
        <div class="rounded-2xl bg-surface-light dark:bg-surface-dark border border-gray-100 dark:border-gray-800 p-6 shadow-sm">
          <h3 class="text-lg font-bold text-text-main dark:text-white mb-4">Thông tin công ty</h3>
          <div class="space-y-4">
            <div class="flex items-start gap-3" v-if="company.website">
              <span class="material-symbols-outlined text-text-muted mt-0.5">language</span>
              <div>
                <p class="text-sm font-bold text-text-muted uppercase tracking-wider">Website</p>
                <a :href="company.website" target="_blank" class="text-base font-bold text-primary hover:underline break-all">{{ company.website }}</a>
              </div>
            </div>

            <div class="flex items-start gap-3" v-if="company.foundedYear">
              <span class="material-symbols-outlined text-text-muted mt-0.5">calendar_month</span>
              <div>
                <p class="text-sm font-bold text-text-muted uppercase tracking-wider">Năm thành lập</p>
                <p class="text-base font-bold text-text-main dark:text-white">{{ company.foundedYear }}</p>
              </div>
            </div>

            <div class="flex items-start gap-3" v-if="company.companySize">
              <span class="material-symbols-outlined text-text-muted mt-0.5">group</span>
              <div>
                <p class="text-sm font-bold text-text-muted uppercase tracking-wider">Quy mô</p>
                <p class="text-base font-bold text-text-main dark:text-white">{{ company.companySize }}</p>
              </div>
            </div>

            <div class="flex items-start gap-3" v-if="company.address">
              <span class="material-symbols-outlined text-text-muted mt-0.5">location_on</span>
              <div>
                <p class="text-sm font-bold text-text-muted uppercase tracking-wider">Địa chỉ</p>
                <p class="text-base font-bold text-text-main dark:text-white">{{ company.address }}</p>
              </div>
            </div>

            <div class="flex items-start gap-3" v-if="company.email">
              <span class="material-symbols-outlined text-text-muted mt-0.5">email</span>
              <div>
                <p class="text-sm font-bold text-text-muted uppercase tracking-wider">Email</p>
                <a :href="'mailto:' + company.email" class="text-base font-bold text-primary hover:underline break-all">{{ company.email }}</a>
              </div>
            </div>

            <div class="flex items-start gap-3" v-if="company.phone">
              <span class="material-symbols-outlined text-text-muted mt-0.5">call</span>
              <div>
                <p class="text-sm font-bold text-text-muted uppercase tracking-wider">Hotline</p>
                <a :href="'tel:' + company.phone" class="text-base font-bold text-text-main dark:text-white hover:underline">{{ company.phone }}</a>
              </div>
            </div>

            <!-- Social Links -->
            <div class="pt-4 border-t border-gray-100 dark:border-gray-800" v-if="parsedSocial && (parsedSocial.facebook || parsedSocial.linkedin || parsedSocial.twitter)">
              <p class="text-xs font-semibold text-text-muted uppercase tracking-wider mb-4">Mạng xã hội</p>
              <div class="flex items-center gap-3 w-full">
                <a
                  v-if="parsedSocial.linkedin"
                  :href="parsedSocial.linkedin"
                  target="_blank"
                  class="flex-1 flex items-center justify-center h-10 border border-slate-200 dark:border-slate-700 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <img src="https://upload.wikimedia.org/wikipedia/commons/8/81/LinkedIn_icon.svg" alt="LinkedIn" class="w-5 h-5" />
                </a>
                <a
                  v-if="parsedSocial.twitter"
                  :href="parsedSocial.twitter"
                  target="_blank"
                  class="flex-1 flex items-center justify-center h-10 border border-slate-200 dark:border-slate-700 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <img src="https://upload.wikimedia.org/wikipedia/commons/c/ce/X_logo_2023.svg" alt="Twitter" class="w-4 h-4 dark:invert" />
                </a>
                <a
                  v-if="parsedSocial.facebook"
                  :href="parsedSocial.facebook"
                  target="_blank"
                  class="flex-1 flex items-center justify-center h-10 border border-slate-200 dark:border-slate-700 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <img src="https://upload.wikimedia.org/wikipedia/commons/b/b8/2021_Facebook_icon.svg" alt="Facebook" class="w-5 h-5" />
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>
