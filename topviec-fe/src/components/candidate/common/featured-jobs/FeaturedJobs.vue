<template>
  <div class="featured-jobs">
    <FeaturedJobsHeader
      title="Việc làm từ Thương hiệu nổi bật"
      subtitle="Việc làm từ các Thương hiệu hàng đầu phù hợp với bạn."
    />

    <div class="featured-jobs__body">
      <div class="featured-jobs__grid">
        <FeaturedJobCard
          v-for="job in jobs"
          :key="job.id"
          :job="job"
          @click="handleJobClick(job.id)"
        />
      </div>

      <FeaturedJobsPagination
        :current="currentPage"
        :total="totalPages"
        @prev="loadPage(currentPage - 1)"
        @next="loadPage(currentPage + 1)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePublicJobPostingStore } from '@/stores/publicJobPosting.store'
import FeaturedJobsHeader from './FeaturedJobsHeader.vue'
import FeaturedJobCard from './FeaturedJobCard.vue'
import FeaturedJobsPagination from './FeaturedJobsPagination.vue'

const PAGE_SIZE = 9

const router = useRouter()
const store = usePublicJobPostingStore()

const currentPage = computed(() => store.meta.page)
const totalPages = computed(() => store.meta.pages)

const jobs = computed(() =>
  store.jobs.map(job => ({
    id: job.id,
    title: job.title,
    company: job.company.name,
    salary: formatSalary(job),
    isHot: job.isHot,
    isUrgent: job.isUrgent,
    location: job.locations?.map(l => l.name).join(', ') ?? '',
    logoBg: '#f1f3ff',
    logoText: job.company.name.slice(0, 3).toUpperCase(),
    logoTextColor: '#005ea4',
    logoUrl: job.company.logoUrl,
    isNew: isNewJob(job.publishedAt),
  }))
)

function formatSalary(job: typeof store.jobs[0]): string {
  if (job.salaryNegotiable) return 'Thoả thuận'
  if (job.salaryMin && job.salaryMax) return `${(job.salaryMin / 1_000_000).toFixed(0)} - ${(job.salaryMax / 1_000_000).toFixed(0)} triệu`
  if (job.salaryMin) return `Từ ${(job.salaryMin / 1_000_000).toFixed(0)} triệu`
  if (job.salaryMax) return `Đến ${(job.salaryMax / 1_000_000).toFixed(0)} triệu`
  return 'Thoả thuận'
}

function isNewJob(publishedAt?: string): boolean {
  if (!publishedAt) return false
  return Date.now() - new Date(publishedAt).getTime() < 7 * 24 * 60 * 60 * 1000
}

async function loadPage(page: number) {
  await store.fetchJobs({ isHot: true, page, size: PAGE_SIZE })
}

function handleJobClick(id: number) {
  router.push(`/jobs/${id}`)
}

onMounted(() => loadPage(0))
</script>

<style scoped>
.featured-jobs {
  background: #fff;
  border-radius: 0.75rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  border: 1px solid rgba(192,199,212,0.2);
  overflow: hidden;
}
.featured-jobs__body { padding: 1.5rem; }
.featured-jobs__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}
@media (max-width: 1024px) { .featured-jobs__grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px) { .featured-jobs__grid { grid-template-columns: 1fr; } }
</style>