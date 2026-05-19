<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
    <!-- Left Column: Description and Gallery -->
    <div class="lg:col-span-2 space-y-8">
      
      <!-- Company Description -->
      <section class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
          <span class="material-symbols-outlined text-[#963131]">description</span>
          Mô tả công ty
        </h3>
        <div
          v-if="company.description"
          class="prose prose-slate dark:prose-invert max-w-none text-sm leading-relaxed tiptap-content"
          v-html="company.description"
        />
        <p v-else class="italic text-slate-400 text-sm">Chưa cập nhật mô tả công ty.</p>
      </section>

      <!-- Cover Image -->
      <section class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
          <span class="material-symbols-outlined text-[#963131]">image</span>
          Ảnh bìa công ty
        </h3>
        <div v-if="company.coverUrl" class="w-full aspect-[21/9] rounded-xl overflow-hidden bg-slate-100 border border-slate-200 dark:border-slate-800">
          <img
            :src="company.coverUrl"
            :alt="`Cover image of ${company.name}`"
            class="w-full h-full object-cover"
            @error="handleImageError"
          />
        </div>
        <div v-else class="text-sm text-slate-400 italic">
          Chưa cập nhật ảnh bìa.
        </div>
      </section>

      <!-- Culture -->
      <section class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
          <span class="material-symbols-outlined text-[#963131]">diversity_3</span>
          Văn hóa & Môi trường làm việc
        </h3>
        <div
          v-if="company.culture"
          class="prose prose-slate dark:prose-invert max-w-none text-sm leading-relaxed tiptap-content"
          v-html="company.culture"
        />
        <p v-else class="italic text-slate-400 text-sm">Chưa cập nhật thông tin văn hóa doanh nghiệp.</p>
      </section>

      <!-- Social Links (Optional if placed here) -->
      <section v-if="parsedSocialLinks.length > 0" class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
          <span class="material-symbols-outlined text-[#963131]">public</span>
          Mạng xã hội & Liên kết
        </h3>
        <div class="flex flex-wrap gap-3">
          <a
            v-for="(link, idx) in parsedSocialLinks"
            :key="idx"
            :href="link.url"
            target="_blank"
            class="flex items-center gap-2 px-4 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg text-sm font-medium hover:border-[#963131] hover:text-[#963131] transition-colors"
          >
            <span class="material-symbols-outlined text-base">{{ link.icon || 'link' }}</span>
            {{ link.name || formatUrl(link.url) }}
          </a>
        </div>
      </section>
    </div>

    <!-- Right Column: General Info and Benefits -->
    <div class="space-y-8">
      
      <!-- General Information -->
      <section class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <h3 class="text-lg font-bold mb-6 flex items-center gap-2">
          <span class="material-symbols-outlined text-[#963131]">info</span>
          Thông tin chung
        </h3>
        <div class="space-y-4">
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Mã số thuế</p>
            <p class="text-sm font-medium">{{ company.taxCode || 'Chưa cập nhật' }}</p>
          </div>
          <div class="h-px bg-slate-100 dark:bg-slate-800"></div>
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Quy mô công ty</p>
            <p class="text-sm font-medium">{{ company.companySize || 'Chưa cập nhật' }}</p>
          </div>
          <div class="h-px bg-slate-100 dark:bg-slate-800"></div>
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Ngành nghề</p>
            <div v-if="companyIndustryLabel" class="flex flex-wrap gap-2 mt-2">
              <span class="px-2 py-1 bg-slate-100 dark:bg-slate-800 rounded text-xs">{{ companyIndustryLabel }}</span>
            </div>
            <p v-else class="text-sm text-slate-400 italic mt-1">Chưa cập nhật</p>
          </div>
          <div class="h-px bg-slate-100 dark:bg-slate-800"></div>
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Email liên hệ</p>
            <a v-if="company.email" :href="`mailto:${company.email}`" class="text-sm font-medium text-[#963131] hover:underline">
              {{ company.email }}
            </a>
            <p v-else class="text-sm text-slate-400 italic">Chưa cập nhật</p>
          </div>
          <div class="h-px bg-slate-100 dark:bg-slate-800"></div>
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Số điện thoại</p>
            <a v-if="company.phone" :href="`tel:${company.phone}`" class="text-sm font-medium text-[#963131] hover:underline">
              {{ company.phone }}
            </a>
            <p v-else class="text-sm text-slate-400 italic">Chưa cập nhật</p>
          </div>
          <div class="h-px bg-slate-100 dark:bg-slate-800"></div>
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Website</p>
            <a v-if="company.website" :href="company.website" target="_blank" class="text-sm font-medium text-[#963131] hover:underline">
              {{ formatUrl(company.website) }}
            </a>
            <p v-else class="text-sm text-slate-400 italic">Chưa cập nhật</p>
          </div>
          <div class="h-px bg-slate-100 dark:bg-slate-800"></div>
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Năm thành lập</p>
            <p class="text-sm font-medium">{{ company.foundedYear || 'Chưa cập nhật' }}</p>
          </div>
          <div class="h-px bg-slate-100 dark:bg-slate-800"></div>
          <div>
            <p class="text-xs text-slate-400 uppercase font-bold tracking-widest mb-1">Địa chỉ trụ sở</p>
            <p class="text-sm font-medium leading-relaxed">{{ company.address || 'Chưa cập nhật' }}</p>
          </div>
        </div>
      </section>

      <!-- Culture & Benefits -->
      <!-- <section class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <h3 class="text-lg font-bold mb-6 flex items-center gap-2">
          <span class="material-symbols-outlined text-[#963131]">card_giftcard</span>
          Văn hóa & Phúc lợi
        </h3>
        <div v-if="parsedBenefits.length > 0" class="space-y-4">
          <div v-for="(benefit, idx) in parsedBenefits" :key="idx" class="flex items-start gap-4">
            <div class="w-10 h-10 rounded-lg bg-green-50 dark:bg-green-900/20 text-green-600 flex items-center justify-center shrink-0">
              <span class="material-symbols-outlined">{{ getBenefitIcon(idx) }}</span>
            </div>
            <div>
              <p class="text-sm font-bold">{{ benefit.title || benefit }}</p>
              <p v-if="benefit.description" class="text-xs text-slate-500 mt-0.5">{{ benefit.description }}</p>
            </div>
          </div>
        </div>
        <div v-else class="text-sm text-slate-400 italic">
          Chưa cập nhật phúc lợi.
        </div>
      </section> -->
      
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ResCompanyDTO } from '@/types/company.types'

const props = defineProps<{
  company: ResCompanyDTO
}>()

// ─── Constants for UI Mapping ────────────────────────────────────────────────────
const companyIndustryLabel = computed(() => {
  return props.company.industryName || null
})

// ─── Format Helpers ──────────────────────────────────────────────────────────────
function formatUrl(url: string) {
  try {
    const obj = new URL(url)
    return obj.hostname.replace('www.', '')
  } catch (e) {
    return url
  }
}

function handleImageError(event: Event) {
  const target = event.target as HTMLImageElement
  target.src = 'https://placehold.co/1200x500/e2e8f0/94a3b8?text=Image+Not+Found'
}

// ─── Social Links Processing ───────────────────────────────────────────────────
const parsedSocialLinks = computed<any[]>(() => {
  if (!props.company.socialLinks) return []
  try {
    const parsed = JSON.parse(props.company.socialLinks)
    if (Array.isArray(parsed)) {
      return parsed.map(link => {
        // Basic icon guessing based on URL
        let icon = 'link'
        const urlStr = link.url?.toLowerCase() || ''
        if (urlStr.includes('facebook.com')) icon = 'facebook'
        if (urlStr.includes('linkedin.com')) icon = 'work'
        if (urlStr.includes('youtube.com')) icon = 'video_library'
        if (urlStr.includes('twitter.com') || urlStr.includes('x.com')) icon = 'forum'
        
        return {
          name: link.name || formatUrl(link.url),
          url: link.url,
          icon: link.icon || icon
        }
      })
    }
  } catch (e) {
    // If it's just a raw single URL instead of a JSON array
    if (props.company.socialLinks.trim().startsWith('http')) {
       return [{ name: formatUrl(props.company.socialLinks), url: props.company.socialLinks, icon: 'link' }]
    }
  }
  return []
})

// ─── Benefits Processing ─────────────────────────────────────────────────────────
const parsedBenefits = computed<any[]>(() => {
  if (!props.company.benefits) return []
  try {
    const parsed = JSON.parse(props.company.benefits)
    if (Array.isArray(parsed) && parsed.length > 0) {
      // Logic mapping if parsed is just strings
      return parsed.map((item) => {
        if (typeof item === 'string') {
          return { title: item, description: '' }
        }
        return item
      })
    }
  } catch (e) {
    return [{ title: props.company.benefits, description: '' }]
  }

  return []
})

function getBenefitIcon(idx: number) {
  const icons = ['health_and_safety', 'flight_takeoff', 'payments', 'school', 'redeem', 'favorite']
  return icons[idx % icons.length]
}
</script>
