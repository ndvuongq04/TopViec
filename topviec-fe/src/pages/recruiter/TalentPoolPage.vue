<template>
  <div class="talent-pool">
    <TalentPoolHeader v-model:tab="activeTab" :total="savedTotal" />
    <div class="talent-pool__content">
      <SavedCandidatesTab v-if="activeTab === 'saved'" v-model:total="savedTotal" />
      <CandidateSearchTab v-else />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import TalentPoolHeader from '@/components/recruiter/talent-pool/TalentPoolHeader.vue'
import SavedCandidatesTab from '@/components/recruiter/talent-pool/saved/SavedCandidatesTab.vue'
import CandidateSearchTab from '@/components/recruiter/talent-pool/search/CandidateSearchTab.vue'

const activeTab  = ref<'saved' | 'search'>('saved')
const savedTotal = ref(0)
</script>

<style scoped>
.talent-pool { display: flex; flex-direction: column; height: 100%; }
.talent-pool__content { flex: 1; overflow-y: auto; }
</style>
