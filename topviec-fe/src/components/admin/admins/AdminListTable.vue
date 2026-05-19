<template>
  <div>
    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr
            class="bg-slate-50/50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800"
          >
            <th
              class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider"
            >
              MÃ ID
            </th>
            <th
              class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider"
            >
              HỌ TÊN &amp; EMAIL
            </th>
            <th
              class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider"
            >
              VAI TRÒ
            </th>
            <th
              class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider"
            >
              TRẠNG THÁI
            </th>
            <th
              class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider"
            >
              NGÀY TẠO
            </th>
            <th
              class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right"
            >
              HÀNH ĐỘNG
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
          <tr
            v-for="admin in admins"
            :key="admin.adminUsersId"
            class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors"
          >
            <!-- ID -->
            <td class="px-6 py-4 text-sm font-mono text-slate-400">
              #AD-{{ admin.adminUsersId }}
            </td>

            <!-- Name & Email -->
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div
                  class="size-9 rounded-full flex items-center justify-center text-sm font-bold shrink-0"
                  :class="
                    admin.isActive
                      ? 'bg-[#963131]/10 text-[#963131]'
                      : 'bg-slate-200 dark:bg-slate-700 text-slate-400'
                  "
                >
                  {{ initials(admin.fullName) }}
                </div>
                <div>
                  <p
                    class="text-sm font-bold text-slate-900 dark:text-slate-100 leading-none"
                  >
                    {{ admin.fullName }}
                  </p>
                  <p class="text-xs text-slate-500 mt-1">{{ admin.email }}</p>
                </div>
              </div>
            </td>

            <!-- Role -->
            <td class="px-6 py-4">
              <span
                class="px-2.5 py-1 text-[10px] font-black uppercase rounded-full border"
                :class="[
                  roleStyle(admin.adminRole).bg,
                  roleStyle(admin.adminRole).text,
                  roleStyle(admin.adminRole).border,
                ]"
              >
                {{ roleLabel(admin.adminRole) }}
              </span>
            </td>

            <!-- Status -->
            <td class="px-6 py-4">
              <span
                class="flex items-center gap-1.5 text-xs font-semibold"
                :class="
                  admin.isActive
                    ? 'text-green-600 dark:text-green-400'
                    : 'text-red-600 dark:text-red-400'
                "
              >
                <span
                  class="w-1.5 h-1.5 rounded-full"
                  :class="admin.isActive ? 'bg-green-500' : 'bg-red-500'"
                ></span>
                {{ admin.isActive ? "Đang hoạt động" : "Bị khóa" }}
              </span>
            </td>

            <!-- Created Date -->
            <td class="px-6 py-4 text-sm text-slate-500">
              {{ formatDate(admin.createdAt) }}
            </td>

            <!-- Actions -->
            <td class="px-6 py-4 text-right">
              <div class="flex items-center justify-end gap-1">
                <button
                  class="p-1.5 text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
                  title="Xem chi tiết"
                  @click="$emit('view', admin)"
                >
                  <span class="material-symbols-outlined text-lg"
                    >visibility</span
                  >
                </button>
                <button
                  v-if="can('admin.update')"
                  class="p-1.5 text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
                  title="Chỉnh sửa"
                  @click="$emit('edit', admin)"
                >
                  <span class="material-symbols-outlined text-lg">edit</span>
                </button>
                <button
                  v-if="can('admin.toggle-active')"
                  class="p-1.5 transition-colors cursor-pointer"
                  :class="admin.isActive
                    ? 'text-slate-400 hover:text-orange-600'
                    : 'text-green-600 hover:bg-green-50 rounded'"
                  :title="admin.isActive ? 'Khóa tài khoản' : 'Mở khóa tài khoản'"
                  @click="$emit('toggle-lock', admin)"
                >
                  <span class="material-symbols-outlined text-lg">
                    {{ admin.isActive ? 'lock' : 'lock_open' }}
                  </span>
                </button>
                <button
                  v-if="can('admin.delete')"
                  class="p-1.5 text-slate-400 hover:text-red-600 transition-colors cursor-pointer"
                  title="Xóa"
                  @click="$emit('delete', admin)"
                >
                  <span class="material-symbols-outlined text-lg">delete</span>
                </button>
              </div>
            </td>
          </tr>

          <!-- Empty state -->
          <tr v-if="admins.length === 0">
            <td
              colspan="6"
              class="px-6 py-12 text-center text-slate-400 text-sm"
            >
              <span class="material-symbols-outlined text-4xl block mb-2"
                >manage_accounts</span
              >
              Không tìm thấy admin nào
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div
      class="p-6 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between"
    >
      <p class="text-xs text-slate-500 font-medium">
        Hiển thị
        <span class="text-slate-900 dark:text-white"
          >{{ rangeStart }} - {{ rangeEnd }}</span
        >
        trong số
        <span class="text-slate-900 dark:text-white">{{ total }}</span>
        quản trị viên
      </p>
      <div class="flex items-center gap-2">
        <button
          class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 disabled:opacity-50 cursor-pointer disabled:cursor-not-allowed"
          :disabled="currentPage <= 1"
          @click="$emit('page-change', currentPage - 1)"
        >
          <span class="material-symbols-outlined text-xl leading-none"
            >chevron_left</span
          >
        </button>

        <template v-for="page in visiblePages" :key="page">
          <span v-if="page === '...'" class="px-1 text-slate-400 text-sm"
            >...</span
          >
          <button
            v-else
            class="size-8 flex items-center justify-center rounded-lg text-xs font-bold transition-colors cursor-pointer"
            :class="
              page === currentPage
                ? 'bg-[#963131] text-white shadow-sm'
                : 'border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'
            "
            @click="$emit('page-change', page as number)"
          >
            {{ page }}
          </button>
        </template>

        <button
          class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer disabled:cursor-not-allowed"
          :disabled="currentPage >= totalPages"
          @click="$emit('page-change', currentPage + 1)"
        >
          <span class="material-symbols-outlined text-xl leading-none"
            >chevron_right</span
          >
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import dayjs from "dayjs";
import type { ResAdminUser, AdminRole } from "@/types/adminUser.types";
import { ADMIN_ROLE_LABELS, ADMIN_ROLE_STYLES } from "@/types/adminUser.types";
import { useAdminPermission } from "@/composables/useAdminPermission"

const { can } = useAdminPermission()

const props = defineProps<{
  admins: ResAdminUser[];
  total: number;
  currentPage: number;
  pageSize: number;
}>();

defineEmits<{
  view: [admin: ResAdminUser];
  edit: [admin: ResAdminUser];
  "toggle-lock": [admin: ResAdminUser];
  delete: [admin: ResAdminUser];
  "page-change": [page: number];
}>();

// ─── Helpers ─────────────────────────────────────────────────────────────────
function initials(name: string) {
  return name
    .split(" ")
    .slice(-2)
    .map((w) => w[0])
    .join("")
    .toUpperCase();
}

function roleLabel(role: AdminRole) {
  return ADMIN_ROLE_LABELS[role] ?? role;
}

function roleStyle(role: AdminRole) {
  return (
    ADMIN_ROLE_STYLES[role] ?? {
      bg: "bg-slate-100",
      text: "text-slate-700",
      border: "border-slate-200",
    }
  );
}

function formatDate(date: string) {
  return date ? dayjs(date).format("DD/MM/YYYY") : "—";
}

// ─── Pagination ─────────────────────────────────────────────────────────────
const totalPages = computed(() =>
  Math.max(1, Math.ceil(props.total / props.pageSize)),
);
const rangeStart = computed(() =>
  props.total === 0 ? 0 : (props.currentPage - 1) * props.pageSize + 1,
);
const rangeEnd = computed(() =>
  Math.min(props.currentPage * props.pageSize, props.total),
);

const visiblePages = computed(() => {
  const pages: (number | string)[] = [];
  const total = totalPages.value;
  const cur = props.currentPage;
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i);
    return pages;
  }
  pages.push(1);
  if (cur > 3) pages.push("...");
  for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++)
    pages.push(i);
  if (cur < total - 2) pages.push("...");
  pages.push(total);
  return pages;
});
</script>
