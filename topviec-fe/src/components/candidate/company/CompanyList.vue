<script setup lang="ts">
// CompanyList: Danh sách công ty scroll ngang
import { onMounted, watch } from "vue";
import CompanyCard from "@/components/candidate/company/CompanyCard.vue";
import { useCandidateCompanyStore } from "@/stores/candidateCompany.store";
import { useCandidateCompanyFollowStore } from "@/stores/candidateCompanyFollow.store";
import { useToast } from "@/composables/useToast";

const companyStore = useCandidateCompanyStore();
const followStore = useCandidateCompanyFollowStore();
const toast = useToast();

onMounted(() => {
  companyStore.fetchPublicCompanies({ size: 10, isBanner: true });
});

watch(
  () => companyStore.companies,
  (newCompanies) => {
    newCompanies.forEach((company) => {
      followStore.checkFollowStatus(company.id);
    });
  },
  { immediate: true }
);

async function handleFollow(id: number) {
  const isFollowing = followStore.followMap[id];
  try {
    if (isFollowing) {
      await followStore.unfollowCompany(id);
      toast.success("Đã bỏ theo dõi công ty");
    } else {
      await followStore.followCompany(id);
      toast.success("Đã theo dõi công ty");
    }
  } catch (err) {
    toast.error(followStore.error || "Thao tác thất bại");
  }
}
</script>

<template>
  <div class="flex flex-col gap-4 mt-4">
    <!-- Header -->
    <h2 class="text-3xl font-extrabold text-text-main dark:text-white tracking-tight">
      Công ty đang tuyển dụng
    </h2>

    <!-- Scroll ngang -->
    <div
      v-if="companyStore.loading && companyStore.companies.length === 0"
      class="flex gap-4 overflow-x-auto pb-4 scrollbar-hide"
    >
      <div
        v-for="i in 4"
        :key="i"
        class="min-w-[200px] h-48 bg-slate-100 dark:bg-slate-800 animate-pulse rounded-3xl"
      ></div>
    </div>

    <div
      v-else-if="companyStore.error"
      class="p-4 rounded-xl bg-red-50 text-red-500 text-base"
    >
      {{ companyStore.error }}
    </div>

    <div v-else class="flex gap-4 overflow-x-auto pb-4 scrollbar-hide">
      <CompanyCard
        v-for="company in companyStore.companies"
        :key="company.id"
        :id="company.id"
        :slug="company.slug"
        :name="company.name"
        :logo-url="company.logoUrl || ''"
        :open-jobs="0"
        :is-following="followStore.followMap[company.id] || false"
        @follow="handleFollow"
      />
      <div
        v-if="companyStore.companies.length === 0"
        class="text-slate-400 text-base py-8"
      >
        Chưa có công ty nào nổi bật.
      </div>
    </div>
  </div>
</template>
