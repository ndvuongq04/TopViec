<template>
  <div class="space-y-8 pt-6">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <nav class="flex items-center gap-2 text-xs text-slate-400 mb-2 uppercase tracking-widest font-bold">
          <router-link to="/recruiter/jobs" class="hover:text-primary transition-colors">Tuyển dụng</router-link>
          <span class="material-symbols-outlined text-[12px]">chevron_right</span>
          <span class="text-slate-500">Senior UI/UX Designer</span>
          <span class="material-symbols-outlined text-[12px]">chevron_right</span>
          <span class="text-primary">Offer & Hoàn thành</span>
        </nav>
        <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Quản lý Offer</h2>
        <p class="text-slate-500 mt-1">Danh sách ứng viên đã vượt qua tất cả các vòng phỏng vấn chuyên môn.</p>
      </div>
      <div class="flex items-center gap-3">
        <div class="bg-white dark:bg-slate-900 px-4 py-2.5 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
          <span class="text-sm font-bold text-slate-900 dark:text-white">12 Ứng viên PASS</span>
        </div>
      </div>
    </div>

    <!-- Main Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
      <!-- Candidate List -->
      <div class="lg:col-span-8 space-y-4">
        <!-- Card 1 — Awaiting Offer -->
        <div class="bg-white dark:bg-slate-900 rounded-2xl shadow-sm p-6 border border-transparent hover:border-primary/30 transition-all group">
          <div class="flex items-start gap-4">
            <img src="https://i.pravatar.cc/150?u=minh" class="w-16 h-16 rounded-2xl object-cover bg-slate-100" />
            <div class="flex-1">
              <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                <div>
                  <h3 class="font-bold text-lg text-slate-900 dark:text-white">Nguyễn Thị Minh Anh</h3>
                  <p class="text-sm text-slate-500">Senior Product Designer • 5 năm kinh nghiệm</p>
                </div>
                <div class="flex gap-2">
                  <span class="px-3 py-1 bg-primary/10 text-primary text-[10px] font-extrabold uppercase tracking-wider rounded-full border border-primary/10">Final Pass</span>
                  <span class="px-3 py-1 bg-slate-100 dark:bg-slate-800 text-slate-500 text-[10px] font-extrabold uppercase tracking-wider rounded-full">Đang chờ Offer</span>
                </div>
              </div>
              <div class="mt-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div class="flex gap-8">
                  <div>
                    <p class="text-[10px] text-slate-400 uppercase font-bold tracking-widest">Expected Salary</p>
                    <p class="text-sm font-bold text-slate-900 dark:text-white mt-0.5">$2,500</p>
                  </div>
                  <div>
                    <p class="text-[10px] text-slate-400 uppercase font-bold tracking-widest">Interview Score</p>
                    <p class="text-sm font-bold text-emerald-600 mt-0.5">4.8/5.0</p>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <button class="flex items-center gap-2 px-5 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 hover:border-red-300 hover:bg-red-50 dark:hover:bg-red-900/10 hover:text-red-500 transition-all text-sm font-bold text-slate-500 bg-white dark:bg-slate-800 cursor-pointer">
                    <span class="material-symbols-outlined text-sm">close</span>
                    REJECTED
                  </button>
                  <button class="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-primary text-white shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-95 transition-all text-sm font-bold cursor-pointer">
                    <span class="material-symbols-outlined text-sm" style="font-variation-settings: 'FILL' 1;">check</span>
                    HIRED
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Card 2 — Selected/Hired -->
        <div class="bg-primary/5 dark:bg-primary/10 rounded-2xl shadow-sm p-6 border-2 border-primary/20 relative overflow-hidden">
          <div class="absolute top-0 right-0">
            <span class="bg-primary text-white text-[10px] font-bold px-4 py-1.5 rounded-bl-2xl uppercase tracking-widest">Selected for Offer</span>
          </div>
          <div class="flex items-start gap-4">
            <img src="https://i.pravatar.cc/150?u=long" class="w-16 h-16 rounded-2xl object-cover bg-slate-100" />
            <div class="flex-1">
              <h3 class="font-bold text-lg text-slate-900 dark:text-white">Trần Hoàng Long</h3>
              <p class="text-sm text-slate-500">Interaction Designer • 4 năm kinh nghiệm</p>
              <div class="mt-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div class="flex gap-8">
                  <div>
                    <p class="text-[10px] text-slate-400 uppercase font-bold tracking-widest">Negotiated Salary</p>
                    <p class="text-sm font-bold text-slate-900 dark:text-white mt-0.5">$2,200</p>
                  </div>
                  <div>
                    <p class="text-[10px] text-slate-400 uppercase font-bold tracking-widest">Joining Date</p>
                    <p class="text-sm font-bold text-slate-900 dark:text-white mt-0.5">15 Oct, 2024</p>
                  </div>
                </div>
                <div class="flex items-center gap-3">
                  <span class="text-emerald-600 flex items-center gap-1.5 font-bold text-sm">
                    <span class="material-symbols-outlined text-lg" style="font-variation-settings: 'FILL' 1;">verified</span>
                    Đã đồng ý Offer
                  </span>
                  <button @click="showConfirmModal = true" class="bg-slate-900 dark:bg-white dark:text-slate-900 text-white px-6 py-2.5 rounded-2xl font-extrabold text-sm shadow-lg hover:scale-[1.02] active:scale-95 transition-all flex items-center gap-2 cursor-pointer">
                    Hoàn thành tuyển dụng
                    <span class="material-symbols-outlined text-sm">arrow_forward</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Card 3 — Rejected -->
        <div class="bg-white dark:bg-slate-900 rounded-2xl shadow-sm p-6 opacity-60 border border-slate-100 dark:border-slate-800">
          <div class="flex items-start gap-4">
            <img src="https://i.pravatar.cc/150?u=chi" class="w-16 h-16 rounded-2xl object-cover grayscale bg-slate-100" />
            <div class="flex-1">
              <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                <div>
                  <h3 class="font-bold text-lg text-slate-900 dark:text-white">Lê Kim Chi</h3>
                  <p class="text-sm text-slate-500">Visual Designer • 3 năm kinh nghiệm</p>
                </div>
                <span class="px-3 py-1 bg-red-50 dark:bg-red-900/20 text-red-500 text-[10px] font-extrabold uppercase tracking-wider rounded-full self-start">Rejected Offer</span>
              </div>
              <p class="text-xs text-slate-400 italic mt-4">Lý do: Ứng viên từ chối mức lương đề xuất và đã nhận lời mời tại công ty khác.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Sidebar -->
      <div class="lg:col-span-4 space-y-6">
        <!-- Job Summary -->
        <div class="bg-white dark:bg-slate-900 rounded-2xl shadow-sm p-6 border border-slate-100 dark:border-slate-800">
          <h4 class="text-[10px] font-extrabold text-slate-400 uppercase tracking-widest mb-5">Chi tiết tin tuyển dụng</h4>
          <div class="space-y-4">
            <div class="flex justify-between items-center border-b border-slate-50 dark:border-slate-800/50 pb-3">
              <span class="text-sm text-slate-500">Vị trí</span>
              <span class="text-sm font-bold text-slate-900 dark:text-white">Senior UI/UX Designer</span>
            </div>
            <div class="flex justify-between items-center border-b border-slate-50 dark:border-slate-800/50 pb-3">
              <span class="text-sm text-slate-500">Phòng ban</span>
              <span class="text-sm font-bold text-slate-900 dark:text-white">Product Team</span>
            </div>
            <div class="flex justify-between items-center border-b border-slate-50 dark:border-slate-800/50 pb-3">
              <span class="text-sm text-slate-500">Số lượng cần tuyển</span>
              <span class="text-sm font-bold text-slate-900 dark:text-white">01 Người</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-slate-500">Trạng thái</span>
              <span class="px-2 py-0.5 bg-primary/10 text-primary text-[10px] font-extrabold rounded-lg">ACTIVE</span>
            </div>
          </div>

          <!-- Auto-action preview -->
          <div class="mt-6 p-4 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-100 dark:border-slate-700/50">
            <p class="text-[10px] text-slate-400 mb-3 uppercase font-bold tracking-wider">Hành động tự động khi hoàn thành:</p>
            <ul class="space-y-2.5">
              <li class="flex items-center gap-2 text-xs text-slate-700 dark:text-slate-300">
                <span class="material-symbols-outlined text-emerald-500 text-sm" style="font-variation-settings: 'FILL' 1;">check_circle</span>
                Trạng thái tin → <span class="font-bold text-primary ml-1">COMPLETED</span>
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-700 dark:text-slate-300">
                <span class="material-symbols-outlined text-emerald-500 text-sm" style="font-variation-settings: 'FILL' 1;">check_circle</span>
                <span class="font-bold">11 ứng viên</span> còn lại → <span class="font-bold text-red-500 ml-1">REJECTED</span>
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-700 dark:text-slate-300">
                <span class="material-symbols-outlined text-emerald-500 text-sm" style="font-variation-settings: 'FILL' 1;">check_circle</span>
                Gửi email thông báo tự động
              </li>
            </ul>
          </div>
        </div>

        <!-- AI Suggestion -->
        <div class="bg-gradient-to-br from-primary to-blue-700 rounded-2xl shadow-lg p-6 text-white relative overflow-hidden group cursor-pointer">
          <div class="absolute -right-4 -bottom-4 opacity-10 group-hover:scale-110 transition-transform duration-500">
            <span class="material-symbols-outlined text-[120px]">psychology</span>
          </div>
          <h4 class="text-sm font-bold mb-2">Gợi ý từ hệ thống</h4>
          <p class="text-xs text-blue-50 mb-5 leading-relaxed">Ứng viên <span class="font-bold">Nguyễn Thị Minh Anh</span> có kỹ năng phù hợp 98% với yêu cầu công việc. Hãy cân nhắc offer sớm nhất.</p>
          <button class="w-full py-2.5 bg-white/20 hover:bg-white/30 backdrop-blur-sm rounded-xl text-xs font-bold transition-all border border-white/30 cursor-pointer">
            Xem báo cáo chi tiết
          </button>
        </div>
      </div>
    </div>

    <!-- Confirm Modal -->
    <Teleport to="body">
      <Transition name="fade">
        <div v-if="showConfirmModal" class="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div class="bg-white dark:bg-slate-900 w-full max-w-md rounded-3xl shadow-2xl overflow-hidden">
            <div class="p-8 text-center">
              <div class="w-20 h-20 bg-red-50 dark:bg-red-900/20 text-red-500 rounded-full flex items-center justify-center mx-auto mb-6">
                <span class="material-symbols-outlined text-5xl" style="font-variation-settings: 'FILL' 1;">warning</span>
              </div>
              <h3 class="text-2xl font-extrabold text-slate-900 dark:text-white mb-3">Xác nhận hoàn thành?</h3>
              <p class="text-slate-500 text-sm leading-relaxed mb-3">
                Bạn đang chuẩn bị đóng tin tuyển dụng <span class="font-bold text-slate-900 dark:text-white">Senior UI/UX Designer</span>.
                Tất cả dữ liệu ứng viên chưa xử lý sẽ được chuyển sang trạng thái loại.
              </p>
              <span class="inline-block px-3 py-1 bg-red-50 dark:bg-red-900/20 text-red-500 font-bold text-xs rounded-xl uppercase tracking-wider mb-8">
                Hành động này không thể hoàn tác
              </span>

              <!-- Preview panel inside modal -->
              <div class="bg-slate-50 dark:bg-slate-800/50 rounded-2xl p-4 text-left space-y-3 mb-8">
                <div class="flex items-center justify-between text-xs">
                  <span class="text-slate-500">Tin tuyển dụng</span>
                  <span class="material-symbols-outlined text-slate-300 text-sm">trending_flat</span>
                  <span class="font-bold text-primary">COMPLETED</span>
                </div>
                <div class="flex items-center justify-between text-xs">
                  <span class="text-slate-500">Ứng viên Pass (1)</span>
                  <span class="material-symbols-outlined text-slate-300 text-sm">trending_flat</span>
                  <span class="font-bold text-emerald-600">HIRED</span>
                </div>
                <div class="flex items-center justify-between text-xs">
                  <span class="text-slate-500">Ứng viên còn lại (11)</span>
                  <span class="material-symbols-outlined text-slate-300 text-sm">trending_flat</span>
                  <span class="font-bold text-red-500">REJECTED</span>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-3">
                <button @click="showConfirmModal = false" class="py-3 px-6 rounded-xl border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 transition-all font-bold text-sm text-slate-700 dark:text-slate-300 cursor-pointer">
                  Quay lại
                </button>
                <button class="py-3 px-6 rounded-xl bg-red-500 text-white shadow-lg shadow-red-500/20 hover:brightness-110 transition-all font-bold text-sm cursor-pointer">
                  Xác nhận đóng tin
                </button>
              </div>
            </div>
            <div class="bg-slate-50 dark:bg-slate-800/50 p-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-center gap-2">
              <span class="material-symbols-outlined text-slate-400 text-[18px]">lock</span>
              <span class="text-[10px] text-slate-500 font-bold uppercase tracking-widest">Yêu cầu quyền Quản trị viên</span>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const showConfirmModal = ref(false)
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
