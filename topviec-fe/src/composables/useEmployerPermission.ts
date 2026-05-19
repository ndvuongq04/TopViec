import { ref, computed } from 'vue';
import { employerProfileService } from '@/services/employerProfile.service';
import type { ResEmployerProfileDTO } from '@/types/companyMember.types';
import type { MemberRole } from '@/constants/companyMember.constants';

const profile = ref<ResEmployerProfileDTO | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

export function useEmployerPermission() {
  async function init() {
    if (profile.value) return;
    
    loading.value = true;
    error.value = null;
    try {
      profile.value = await employerProfileService.getMyProfile();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch employer profile';
    } finally {
      loading.value = false;
    }
  }

  const memberRole = computed(() => profile.value?.roleName || null);
  const isOwner = computed(() => memberRole.value === 'owner');
  const isManager = computed(() => memberRole.value === 'manager');
  const isRecruiter = computed(() => memberRole.value === 'recruiter');
  const isViewer = computed(() => memberRole.value === 'viewer');

  return {
    profile,
    memberRole,
    loading,
    error,
    isOwner,
    isManager,
    isRecruiter,
    isViewer,
    init
  };
}
