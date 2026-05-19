<script setup lang="ts">
import { useAuthStore } from "@/stores/auth.store";
import { useRouter } from "vue-router";
import { ref, computed, onMounted, onUnmounted } from "vue";

const props = defineProps<{
  unfixed?: boolean;
}>();

const authStore = useAuthStore();
const router = useRouter();

const isDropdownOpen = ref(false);
const dropdownRef = ref<HTMLElement | null>(null);

// Derived user display values
const displayName = computed(() => {
  if (!authStore.user?.email) return "Người dùng";
  return authStore.user.email.split("@")[0];
});

const avatarInitial = computed(() => {
  return displayName.value.charAt(0).toUpperCase();
});

const ROLE_LABELS: Record<string, string> = {
  CANDIDATE: 'Ứng viên',
  EMPLOYER:  'Nhà tuyển dụng',
  ADMIN:     'Quản trị viên',
}

const roleLabel = computed(() =>
  authStore.user?.role ? (ROLE_LABELS[authStore.user.role] ?? authStore.user.role) : 'Ứng viên'
)

function toggleDropdown() {
  isDropdownOpen.value = !isDropdownOpen.value;
}

function closeDropdown() {
  isDropdownOpen.value = false;
}

function handleClickOutside(event: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    closeDropdown();
  }
}

onMounted(() => document.addEventListener("click", handleClickOutside));
onUnmounted(() => document.removeEventListener("click", handleClickOutside));

async function handleLogout() {
  closeDropdown();
  await authStore.logout();
}
</script>

<template>
  <header
    class="flex items-center justify-between whitespace-nowrap border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-surface-dark px-10 py-3 shadow-sm"
    :class="props.unfixed ? 'relative z-50' : 'fixed top-0 left-0 right-0 z-50'"
  >
    <!-- Logo -->
    <router-link to="/" class="flex items-center">
      <img src="@/assets/images/logo.png" alt="TopViec" class="h-14 w-auto object-contain" />
    </router-link>

    <!-- Right side -->
    <div class="flex flex-1 justify-end gap-6 items-center">

      <!-- === CHƯA ĐĂNG NHẬP === -->
      <template v-if="!authStore.isAuthenticated">
        <!-- Link nhà tuyển dụng -->
        <router-link
          to="/recruiter/register"
          class="hidden lg:flex items-center gap-1 text-primary hover:text-blue-700 text-base font-bold transition-colors"
        >
          <span class="material-symbols-outlined text-xl">corporate_fare</span>
          <span>Nhà tuyển dụng</span>
        </router-link>

        <!-- Đăng nhập -->
        <router-link
          to="/login"
          class="flex items-center justify-center rounded-3xl h-10 px-5 border border-primary text-primary hover:bg-primary/5 text-base font-bold transition-colors"
        >
          Đăng nhập
        </router-link>

        <!-- Đăng ký -->
        <router-link
          to="/register"
          class="flex items-center justify-center rounded-3xl h-10 px-5 bg-primary hover:bg-blue-700 text-white text-base font-bold transition-colors shadow-lg shadow-blue-500/30"
        >
          Đăng ký ngay
        </router-link>
      </template>

      <!-- === ĐÃ ĐĂNG NHẬP === -->
      <template v-else>
        <!-- Link nhà tuyển dụng -->
        <router-link
          to="/recruiter/register"
          class="hidden lg:flex items-center gap-1 text-primary hover:text-blue-700 text-base font-bold transition-colors"
        >
          <span class="material-symbols-outlined text-xl">corporate_fare</span>
          <span>Nhà tuyển dụng</span>
        </router-link>

        <!-- CV Online button -->
        <button
          class="flex items-center justify-center rounded-3xl h-10 px-5 bg-primary hover:bg-blue-600 text-white text-base font-bold transition-colors shadow-lg shadow-blue-500/30 cursor-pointer"
          @click="router.push('/cv-templates')"
        >
          CV online
        </button>

        <!-- Avatar + Tên + Dropdown -->
        <div class="relative" ref="dropdownRef">
          <!-- Trigger -->
          <div
            class="flex items-center gap-2.5 cursor-pointer group"
            @click="toggleDropdown"
          >
            <!-- Avatar -->
            <div
              class="h-9 w-9 rounded-full border-2 border-slate-200 dark:border-slate-700 flex items-center justify-center bg-primary text-white text-sm font-bold uppercase shadow-sm group-hover:border-primary transition-colors shrink-0"
            >
              {{ avatarInitial }}
            </div>

            <!-- Tên người dùng -->
            <div class="hidden md:flex flex-col leading-tight">
              <span class="text-base font-semibold text-text-main dark:text-white truncate max-w-[120px]">
                {{ displayName }}
              </span>
              <span class="text-xs text-slate-400 dark:text-slate-500 uppercase tracking-wide">
                {{ roleLabel }}
              </span>
            </div>

            <span
              class="material-symbols-outlined text-slate-400 text-[18px] transition-transform duration-200"
              :class="{ 'rotate-180': isDropdownOpen }"
            >
              expand_more
            </span>
          </div>

          <!-- Dropdown menu -->
          <Transition
            enter-active-class="transition-all duration-200"
            enter-from-class="opacity-0 translate-y-2 scale-95"
            enter-to-class="opacity-100 translate-y-0 scale-100"
            leave-active-class="transition-all duration-150"
            leave-from-class="opacity-100 translate-y-0 scale-100"
            leave-to-class="opacity-0 translate-y-2 scale-95"
          >
            <div
              v-if="isDropdownOpen"
              class="absolute right-0 mt-3 w-60 bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-800 rounded-2xl shadow-xl py-2 z-[60] origin-top-right"
            >
              <!-- User info header -->
              <div class="px-4 py-3 border-b border-slate-100 dark:border-slate-800">
                <div class="flex items-center gap-3">
                  <div class="h-10 w-10 rounded-full bg-primary flex items-center justify-center text-white font-bold text-sm shrink-0">
                    {{ avatarInitial }}
                  </div>
                  <div class="min-w-0">
                    <p class="text-sm font-bold text-text-main dark:text-white truncate">{{ displayName }}</p>
                    <p class="text-xs text-slate-400 truncate">{{ authStore.user?.email }}</p>
                  </div>
                </div>
              </div>

              <!-- Navigation links -->
              <div class="py-1">
                <router-link
                  to="/profile"
                  class="flex items-center gap-3 px-4 py-2 text-base text-text-main dark:text-gray-200 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer"
                  @click="closeDropdown"
                >
                  <span class="material-symbols-outlined text-lg opacity-70">person</span>
                  Thông tin cá nhân
                </router-link>

                <router-link
                  to="/change-password"
                  class="flex items-center gap-3 px-4 py-2 text-base text-text-main dark:text-gray-200 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer"
                  @click="closeDropdown"
                >
                  <span class="material-symbols-outlined text-lg opacity-70">lock</span>
                  Đổi mật khẩu
                </router-link>
              </div>

              <div class="h-px bg-slate-100 dark:bg-slate-800 mx-3" />

              <div class="py-1">
                <router-link
                  to="/saved-jobs"
                  class="flex items-center gap-3 px-4 py-2 text-base text-text-main dark:text-gray-200 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer"
                  @click="closeDropdown"
                >
                  <span class="material-symbols-outlined text-lg opacity-70">bookmark</span>
                  Việc đã lưu
                </router-link>

                <router-link
                  to="/applied-jobs"
                  class="flex items-center gap-3 px-4 py-2 text-base text-text-main dark:text-gray-200 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer"
                  @click="closeDropdown"
                >
                  <span class="material-symbols-outlined text-lg opacity-70">send</span>
                  Việc đã ứng tuyển
                </router-link>

                <router-link
                  to="/interviews"
                  class="flex items-center gap-3 px-4 py-2 text-base text-text-main dark:text-gray-200 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer"
                  @click="closeDropdown"
                >
                  <span class="material-symbols-outlined text-lg opacity-70">chat_bubble</span>
                  Lịch phỏng vấn
                </router-link>

                <router-link
                  to="/messages"
                  class="flex items-center gap-3 px-4 py-2 text-base text-text-main dark:text-gray-200 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer"
                  @click="closeDropdown"
                >
                  <span class="material-symbols-outlined text-lg opacity-70">chat</span>
                  Tin nhắn
                </router-link>

                <router-link
                  to="/my-complaints"
                  class="flex items-center gap-3 px-4 py-2 text-base text-text-main dark:text-gray-200 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer"
                  @click="closeDropdown"
                >
                  <span class="material-symbols-outlined text-lg opacity-70">flag</span>
                  Khiếu nại của tôi
                </router-link>
              </div>

              <div class="h-px bg-slate-100 dark:bg-slate-800 mx-3" />

              <div class="py-1">
                <button
                  class="w-full flex items-center gap-3 px-4 py-2 text-base text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 transition-colors font-medium cursor-pointer"
                  @click="handleLogout"
                >
                  <span class="material-symbols-outlined text-lg">logout</span>
                  Đăng xuất
                </button>
              </div>
            </div>
          </Transition>
        </div>
      </template>

    </div>
  </header>
</template>
