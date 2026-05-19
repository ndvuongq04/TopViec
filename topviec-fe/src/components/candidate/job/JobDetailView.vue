<script setup lang="ts">
import { onMounted, computed, watch, ref } from "vue";
import { RouterLink } from "vue-router";
import { usePublicJobPostingStore } from "@/stores/publicJobPosting.store";
import { useCandidateCompanyFollowStore } from "@/stores/candidateCompanyFollow.store";
import { useSavedJobStore } from "@/stores/savedJob.store";
import { useAuthStore } from "@/stores/auth.store";
import { useApplicationStore } from "@/stores/application.store";
import { useToast } from "@/composables/useToast";
import ApplyJobModal from "@/components/candidate/job/ApplyJobModal.vue";
import ComplaintModal from "@/components/candidate/job/ComplaintModal.vue";
import { useQuickApply } from "@/composables/useQuickApply";
import { APPLY_METHOD } from "@/constants/application.constants";
import { formatSalary, formatWorkType } from "@/types/jobPosting.types";

interface Props {
  id: number | string;
}
const props = defineProps<Props>();

const jobStore = usePublicJobPostingStore();
const followStore = useCandidateCompanyFollowStore();
const savedJobStore = useSavedJobStore();
const authStore = useAuthStore();
const applicationStore = useApplicationStore();
const toast = useToast();
const { handleQuickApply } = useQuickApply();

const job = computed(() => jobStore.selectedJob);
const showApplyModal     = ref(false);
const showComplaintModal = ref(false);

const handleApplyConfirm = async (cvId: number) => {
  if (!authStore.isAuthenticated) {
    toast.info("Vui lòng đăng nhập để ứng tuyển");
    return;
  }

  const jobId = Number(props.id);
  try {
    await applicationStore.apply(jobId, {
      cvId,
      applyMethod: APPLY_METHOD.NORMAL
    });
    
    toast.success('Thành công', `Hồ sơ của bạn đã được gửi tới nhà tuyển dụng thành công!`);
    showApplyModal.value = false;
  } catch (err: any) {
    const message = err?.response?.data?.message || "Có lỗi xảy ra khi nộp hồ sơ. Vui lòng thử lại.";
    toast.error('Lỗi', message);
  }
};

// Helper to handle image URLs
function getLogoUrl(url?: string | null) {
  if (!url) return "https://via.placeholder.com/150";
  if (url.startsWith("http")) return url;
  // If absolute path from root, remove leading slash if base URL has trailing slash
  const baseUrl = import.meta.env.VITE_API_URL?.replace(/\/api\/v1\/?$/, "") || "";
  const cleanUrl = url.startsWith("/") ? url : `/${url}`;
  return `${baseUrl}${cleanUrl}`;
}

const formattedSalary = computed(() => job.value ? formatSalary(job.value) : '');

const formattedLocation = computed(() => {
  if (!job.value?.locations?.length) return '';
  return job.value.locations
    .map(l => l.isRemote ? 'Remote' : l.name)
    .filter(Boolean)
    .join(', ');
});

function formatRelativeTime(dateStr?: string) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const dateOnly = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const todayOnly = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const diffDays = Math.round((todayOnly.getTime() - dateOnly.getTime()) / 86_400_000);

  if (diffDays === 0) return 'Hôm nay';
  if (diffDays === 1) return 'Hôm qua';
  if (diffDays < 30) return `${diffDays} ngày trước`;
  return date.toLocaleDateString('vi-VN');
}

const postedAt = computed(() => formatRelativeTime(job.value?.publishedAt || job.value?.createdAt));

const isSaved = computed(() => {
  const jobId = Number(props.id);
  return savedJobStore.isSavedMap[jobId] || false;
});

async function toggleSave() {
  if (!authStore.isAuthenticated) {
    toast.info("Vui lòng đăng nhập để lưu việc làm");
    return;
  }

  const jobId = Number(props.id);
  try {
    if (isSaved.value) {
      await savedJobStore.unsave(jobId);
      toast.info("Đã bỏ lưu việc làm");
    } else {
      await savedJobStore.toggleSave(jobId);
      toast.success("Đã lưu việc làm thành công");
    }
  } catch (err) {
    toast.error("Thao tác thất bại");
  }
}

async function fetchJobData() {
  const jobId = Number(props.id);
  await jobStore.fetchJobById(jobId);
  if (job.value?.company?.id) {
    followStore.checkFollowStatus(job.value.company.id);
  }
  if (authStore.isAuthenticated) {
    savedJobStore.checkIsSaved(jobId);
  }
}

onMounted(() => {
  fetchJobData();
});

// Re-fetch when ID changes
watch(() => props.id, () => {
  fetchJobData();
});

const isCompanyFollowing = computed(() => {
  if (!job.value?.company?.id) return false;
  return followStore.followMap[job.value.company.id] || false;
});

async function toggleCompanyFollow() {
  if (!job.value?.company?.id) return;
  const companyId = job.value.company.id;
  try {
    if (isCompanyFollowing.value) {
      await followStore.unfollowCompany(companyId);
      toast.success("Đã bỏ theo dõi công ty");
    } else {
      await followStore.followCompany(companyId);
      toast.success("Đã theo dõi công ty");
    }
  } catch (err) {
    toast.error("Thao tác thất bại");
  }
}

</script>

<template>
  <main class="flex flex-col flex-1 gap-6 min-w-0 pb-20 max-w-[1440px] mx-auto w-full px-4 md:px-10 py-6">
    <div v-if="jobStore.loading" class="flex items-center justify-center py-20">
      <div
        class="animate-spin rounded-full h-12 w-12 border-4 border-primary border-t-transparent"
      ></div>
    </div>

    <div
      v-else-if="!job"
    class="flex flex-col items-center justify-center py-20 text-text-muted"
  >
    <span class="material-symbols-outlined text-6xl mb-4">error</span>
    <p>Không tìm thấy tin tuyển dụng này.</p>
    <RouterLink to="/" class="mt-4 text-primary font-bold hover:underline"
      >Quay lại trang chủ</RouterLink
    >
  </div>

  <div v-else class="flex flex-col gap-6 pb-16 lg:pb-0">


    <!-- Header Card -->
    <div
      class="bg-white rounded-2xl p-6 md:p-8 shadow-sm border border-slate-100 relative overflow-hidden"
    >
      <!-- Decorative gradient -->
      <div
        class="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-primary/5 to-transparent rounded-bl-full -mr-16 -mt-16 pointer-events-none"
      />

      <div
        class="flex flex-col md:flex-row justify-between items-start md:items-center gap-6 relative z-10"
      >
        <!-- Company + Title -->
        <div class="flex gap-5 items-start">
          <div
            class="w-20 h-20 rounded-xl bg-white border border-slate-100 shadow-sm flex items-center justify-center p-2 shrink-0 overflow-hidden"
          >
            <img
              :alt="job.company.name"
              :src="getLogoUrl(job.company.logoUrl)"
              class="w-full h-full object-contain"
            />
          </div>
          <div>
            <h1
              class="text-text-main text-3xl md:text-4xl font-extrabold leading-tight mb-2 tracking-tight"
            >
              {{ job.title }}
            </h1>
            <div
              class="flex flex-wrap items-center gap-y-1.5 gap-x-4 text-text-secondary text-base"
            >
              <span class="flex items-center gap-1">
                <span class="font-bold text-text-main text-lg">{{ job.company.name }}</span>
                <div v-if="job.company.isBrandVerified" class="relative flex items-center group/verified">
                  <span class="material-symbols-outlined text-blue-600 text-[18px] cursor-default" style="font-variation-settings:'FILL' 1">verified</span>
                  <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified:opacity-100 transition-opacity z-10">
                    Công ty đã xác minh
                    <span class="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-blue-800"></span>
                  </span>
                </div>
              </span>
              <span class="w-1 h-1 bg-slate-300 rounded-full hidden sm:block" />
              <div class="flex items-center gap-1">
                <span class="material-symbols-outlined text-[18px]"
                  >location_on</span
                >
                {{ formattedLocation }}
              </div>
              <span class="w-1 h-1 bg-slate-300 rounded-full hidden sm:block" />
              <div class="flex items-center gap-1">
                <span class="material-symbols-outlined text-[18px]">schedule</span>
                {{ postedAt }}
              </div>
            </div>
          </div>
        </div>

        <!-- Action buttons -->
        <div class="flex gap-3 w-full md:w-auto shrink-0">
          <button
            class="flex-1 md:flex-none h-12 px-5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-text-main font-bold text-base flex items-center justify-center gap-2 transition-all cursor-pointer"
            :class="{ 'border-primary text-primary bg-primary/5': isSaved }"
            @click="toggleSave"
          >
            <span
              class="material-symbols-outlined"
              :style="isSaved ? 'font-variation-settings: \'FILL\' 1' : ''"
              >bookmark</span
            >
            <span>{{ isSaved ? "Đã lưu" : "Lưu" }}</span>
          </button>
          <button
            class="flex-1 md:flex-none h-12 px-5 rounded-xl border border-primary/20 bg-white hover:bg-primary/5 text-primary font-bold text-base flex items-center justify-center gap-2 transition-all cursor-pointer"
            @click="showComplaintModal = true"
          >
            <span class="material-symbols-outlined text-[20px]">flag</span>
            <span>Khiếu nại</span>
          </button>
          <button
            @click="handleQuickApply(props.id, job.title)"
            class="hidden md:flex flex-1 md:flex-none h-12 px-6 rounded-xl border border-orange-200 bg-orange-50 hover:bg-orange-100 text-orange-600 font-bold text-base items-center justify-center gap-2 transition-all cursor-pointer"
            title="Ứng tuyển nhanh bằng CV mặc định"
          >
            <span class="material-symbols-outlined text-[20px]">bolt</span>
            <span>Ứng tuyển nhanh</span>
          </button>
          <button
            @click="showApplyModal = true"
            class="flex-1 md:flex-none h-12 px-8 rounded-xl bg-primary hover:bg-primary-dark text-white font-bold text-base shadow-lg shadow-primary/25 flex items-center justify-center gap-2 transition-all hover:-translate-y-0.5 cursor-pointer"
          >
            <span>Ứng tuyển</span>
            <span class="material-symbols-outlined text-[20px]"
              >arrow_forward</span
            >
          </button>
        </div>
      </div>

      <!-- Tags row -->
      <div class="mt-8 pt-6 border-t border-slate-100 flex flex-wrap gap-3">
        <div
          class="px-4 py-2 bg-slate-50 rounded-lg text-base font-medium text-text-secondary flex items-center gap-2"
        >
          <span class="material-symbols-outlined text-primary text-[20px]"
            >work</span
          >
          {{ formatWorkType(job.workType) }}
        </div>
        <div
          class="px-4 py-2 bg-slate-50 rounded-lg text-base font-medium text-text-secondary flex items-center gap-2"
        >
          <span class="material-symbols-outlined text-green-500 text-[20px]"
            >payments</span
          >
          {{ formattedSalary }}
        </div>
        <div
          class="px-4 py-2 bg-slate-50 rounded-lg text-base font-medium text-text-secondary flex items-center gap-2"
        >
          <span class="material-symbols-outlined text-orange-500 text-[20px]"
            >stars</span
          >
          {{ job.level.name }}
        </div>
        <div
          class="px-4 py-2 bg-slate-50 rounded-lg text-base font-medium text-text-secondary flex items-center gap-2"
        >
          <span class="material-symbols-outlined text-blue-500 text-[20px]"
            >group</span
          >
          {{ job.headcount }} người
        </div>
      </div>
    </div>

    <!-- 2-column grid -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
      <!-- Left: Job content -->
      <div class="lg:col-span-8 flex flex-col gap-6">
        <!-- Description -->
        <div
          class="bg-white rounded-2xl p-6 md:p-8 shadow-sm border border-slate-100"
        >
          <h2
            class="text-lg font-extrabold text-text-main mb-6 flex items-center gap-3"
          >
            <span
              class="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary shrink-0"
            >
              <span class="material-symbols-outlined text-[20px]"
                >description</span
              >
            </span>
            Mô tả công việc
          </h2>

          <div
            class="text-text-secondary leading-relaxed prose prose-slate max-w-none text-sm md:text-base"
            v-html="job.description"
          ></div>

          <h3 class="text-lg font-bold text-text-main mt-8 mb-4">
            Yêu cầu công việc
          </h3>
          <div
            class="text-text-secondary leading-relaxed prose prose-slate max-w-none text-sm md:text-base"
            v-html="job.requirements"
          ></div>

          <template v-if="job.benefits">
            <h3 class="text-lg font-bold text-text-main mt-8 mb-4">Phúc lợi</h3>
            <div
              class="text-text-secondary leading-relaxed prose prose-slate max-w-none text-base"
              v-html="job.benefits"
            ></div>
          </template>

          <template v-if="job.skills?.length">
            <h3 class="text-lg font-bold text-text-main mt-8 mb-4">Kỹ năng</h3>
            <div class="flex flex-wrap gap-2">
              <span
                v-for="skill in job.skills"
                :key="skill.id"
                class="px-3 py-1.5 bg-slate-100 dark:bg-slate-800 rounded-lg text-sm font-medium text-text-main"
              >
                {{ skill.skillName }}
                <span
                  v-if="skill.proficiencyMin"
                  class="text-xs text-text-muted"
                  >(Lvl {{ skill.proficiencyMin }})</span
                >
              </span>
            </div>
          </template>
        </div>
      </div>

      <!-- Right: Sidebar -->
      <div class="lg:col-span-4 flex flex-col gap-6">
        <!-- Company info -->
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
          <h3 class="text-lg font-bold text-text-main mb-4">Về công ty</h3>
          <div class="flex items-start justify-between mb-5">
            <div class="flex items-center gap-4">
              <img
                :alt="job.company.name"
                :src="getLogoUrl(job.company.logoUrl)"
                class="w-12 h-12 rounded-lg object-contain border border-slate-100 p-1"
              />
              <div>
                <div class="flex items-center gap-1">
                  <h4 class="text-lg font-bold text-text-main">{{ job.company.name }}</h4>
                  <div v-if="job.company.isBrandVerified" class="relative flex items-center group/verified2">
                    <span class="material-symbols-outlined text-blue-600 text-[16px] cursor-default" style="font-variation-settings:'FILL' 1">verified</span>
                    <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified2:opacity-100 transition-opacity z-10">
                      Công ty đã xác minh
                      <span class="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-blue-800"></span>
                    </span>
                  </div>
                </div>
                <RouterLink
                  v-if="job.company.slug"
                  :to="`/companies/${job.company.slug}`"
                  class="text-primary text-base font-medium hover:underline"
                  >Xem hồ sơ</RouterLink
                >
                <span v-else class="text-text-muted text-base">Chưa có hồ sơ</span>
              </div>
            </div>
            <button
              @click="toggleCompanyFollow"
              class="h-8 px-3 rounded-md text-sm font-bold transition-colors shrink-0 cursor-pointer"
              :class="
                isCompanyFollowing
                  ? 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                  : 'bg-primary/10 text-primary hover:bg-primary hover:text-white'
              "
            >
              {{ isCompanyFollowing ? "Đang theo dõi" : "+ Theo dõi" }}
            </button>
          </div>
          <div class="space-y-3 text-sm">
            <div
              class="flex justify-between items-center py-2 border-b border-slate-50"
            >
              <span class="text-text-secondary text-sm">Lĩnh vực</span>
              <span class="font-medium text-text-main text-sm">{{
                job.industry.name
              }}</span>
            </div>
            <div
              class="flex justify-between items-center py-2 border-b border-slate-50"
            >
              <span class="text-text-secondary text-sm">Trụ sở</span>
              <span class="font-medium text-text-main text-sm truncate ml-4">{{
                formattedLocation || 'Chưa cập nhật'
              }}</span>
            </div>
          </div>
        </div>

        <!-- Meta info -->
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
          <h3 class="text-lg font-bold text-text-main mb-4">Thông tin thêm</h3>
          <div class="space-y-4 text-sm">
            <div class="flex items-start gap-3">
              <span class="material-symbols-outlined text-slate-400"
                >calendar_today</span
              >
              <div>
                <p class="text-text-secondary text-sm">Hạn ứng tuyển</p>
                <p class="font-bold text-text-main text-base">
                  {{ new Date(job.deadline).toLocaleDateString("vi-VN") }}
                </p>
              </div>
            </div>
            <div class="flex items-start gap-3">
              <span class="material-symbols-outlined text-slate-400"
                >visibility</span
              >
              <div>
                <p class="text-text-secondary text-sm">Lượt xem</p>
                <p class="font-bold text-text-main text-base">{{ job.viewCount }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Mobile sticky apply bar -->
    <div
      class="lg:hidden fixed bottom-4 left-4 right-4 bg-white p-4 rounded-xl shadow-2xl border border-slate-200 z-50 flex gap-3 items-center"
    >
      <div class="flex-1 min-w-0">
        <p class="font-bold text-text-main truncate text-sm">{{ job.title }}</p>
        <p class="text-xs text-text-secondary">{{ job.company.name }}</p>
      </div>
      <button
        @click="handleQuickApply(props.id, job.title)"
        class="h-10 w-10 rounded-lg bg-orange-50 text-orange-500 flex items-center justify-center shrink-0 border border-orange-100 shadow-sm"
        title="Ứng tuyển nhanh"
      >
        <span class="material-symbols-outlined">bolt</span>
      </button>
      <button
        @click="showApplyModal = true"
        class="h-10 px-5 rounded-lg bg-primary hover:bg-primary-dark text-white font-bold text-sm shadow-md shrink-0 transition-colors"
      >
        Ứng tuyển
      </button>
    </div>

    <!-- Apply Modal -->
    <ApplyJobModal
      :show="showApplyModal"
      :job-title="job.title"
      :company-name="job.company.name"
      :company-logo="getLogoUrl(job.company.logoUrl)"
      @close="showApplyModal = false"
      @confirm="handleApplyConfirm"
    />

    <!-- Complaint Modal -->
    <ComplaintModal
      :show="showComplaintModal"
      :job-post-id="Number(props.id)"
      :job-title="job.title"
      :company-name="job.company.name"
      :company-logo="getLogoUrl(job.company.logoUrl)"
      @close="showComplaintModal = false"
      @submitted="showComplaintModal = false"
    />
    </div>
  </main>
</template>
