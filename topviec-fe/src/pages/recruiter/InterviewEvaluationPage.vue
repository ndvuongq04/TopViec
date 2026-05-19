<script setup lang="ts">
// Logic sẽ được implement sau khi tích hợp API
</script>

<template>
  <div class="space-y-8 pt-6">
    <!-- Page Header -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <nav class="flex items-center gap-2 text-xs text-slate-400 mb-2 uppercase tracking-widest font-bold">
          <router-link to="/recruiter/interviews" class="hover:text-primary transition-colors">Phỏng vấn</router-link>
          <span class="material-symbols-outlined text-[12px]">chevron_right</span>
          <span class="text-primary">Ghi nhận Kết quả</span>
        </nav>
        <div class="flex items-center gap-2 mb-2">
          <span class="bg-primary/10 text-primary text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-lg tracking-wider">Phiên phỏng vấn</span>
          <span class="text-slate-500 dark:text-slate-400 text-sm">#IV-2024-0852</span>
        </div>
        <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Ghi nhận Kết quả Phỏng vấn</h2>

        <p class="text-slate-500 mt-1">Đánh giá năng lực ứng viên cho vị trí <span class="font-bold text-slate-900 dark:text-white">Senior Product Designer</span></p>
      </div>
      <div class="flex gap-3">
        <button class="flex items-center gap-2 px-4 py-2.5 text-sm font-bold text-slate-600 dark:text-slate-400 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl hover:shadow-md transition-all cursor-pointer">
          <span class="material-symbols-outlined text-lg">history</span>
          Lịch sử
        </button>
        <button class="flex items-center gap-2 px-6 py-2.5 text-sm font-bold text-white bg-primary rounded-xl shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-95 transition-all cursor-pointer">
          <span class="material-symbols-outlined text-lg">check_circle</span>
          Lưu tất cả kết quả
        </button>
      </div>
    </div>

    <!-- Session Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="bg-white dark:bg-slate-900 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-800">
        <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Vòng Phỏng vấn</p>
        <p class="text-lg font-extrabold text-slate-900 dark:text-white leading-tight">Vòng 2: Kỹ năng Chuyên sâu</p>
      </div>
      <div class="bg-white dark:bg-slate-900 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-800">
        <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Số lượng ứng viên</p>
        <p class="text-xl font-extrabold text-slate-900 dark:text-white">03 <span class="text-sm font-medium text-slate-500">Người</span></p>
      </div>
      <div class="bg-white dark:bg-slate-900 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-800">
        <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Thời gian</p>
        <p class="text-xl font-extrabold text-slate-900 dark:text-white">14:00 - 16:30</p>
      </div>
      <div class="bg-white dark:bg-slate-900 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-800">
        <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Hội đồng PV</p>
        <div class="flex -space-x-2 mt-2">
          <img src="https://i.pravatar.cc/150?u=i1" alt="Interviewer 1" class="h-9 w-9 rounded-full border-2 border-white dark:border-slate-900 object-cover" />
          <img src="https://i.pravatar.cc/150?u=i2" alt="Interviewer 2" class="h-9 w-9 rounded-full border-2 border-white dark:border-slate-900 object-cover" />
          <div class="h-9 w-9 rounded-full bg-slate-100 dark:bg-slate-800 border-2 border-white dark:border-slate-900 flex items-center justify-center text-[10px] font-bold text-slate-500">+1</div>
        </div>
      </div>
    </div>

    <!-- Multi-Candidate Evaluation List -->
    <div class="space-y-4">
      <!-- Candidate 1 — Expanded & Done -->
      <div class="bg-white dark:bg-slate-900 rounded-2xl shadow-sm overflow-hidden border-l-4 border-primary">
        <div class="p-6 border-b border-slate-50 dark:border-slate-800/50 flex items-start justify-between">
          <div class="flex items-center gap-4">
            <div class="relative">
              <img src="https://i.pravatar.cc/150?u=manh" class="w-14 h-14 rounded-full object-cover" />
              <div class="absolute bottom-0 right-0 w-4 h-4 bg-emerald-500 border-2 border-white dark:border-slate-900 rounded-full"></div>
            </div>
            <div>
              <h3 class="text-lg font-extrabold text-slate-900 dark:text-white">Nguyễn Minh Anh</h3>
              <p class="text-sm text-slate-500">UX Researcher | 4 năm kinh nghiệm</p>
              <div class="flex items-center gap-4 mt-2">
                <span class="flex items-center gap-1 text-[10px] font-bold text-primary bg-primary/5 px-2 py-0.5 rounded-lg border border-primary/10">
                  <span class="material-symbols-outlined text-[12px]">file_present</span> Portfolio.pdf
                </span>
                <span class="flex items-center gap-1 text-[10px] font-bold text-slate-500">
                  <span class="material-symbols-outlined text-[12px]">schedule</span> Phỏng vấn xong
                </span>
              </div>
            </div>
          </div>
          <div class="flex flex-col items-end gap-1">
            <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Xếp hạng năng lực</span>
            <div class="flex gap-0.5">
              <span v-for="i in 5" :key="i" class="material-symbols-outlined text-xl" :style="i <= 4 ? 'font-variation-settings: \'FILL\' 1; color: #f59e0b' : 'color: #e2e8f0'">star</span>
            </div>
          </div>
        </div>
        <div class="p-6 grid grid-cols-12 gap-6 bg-slate-50/50 dark:bg-slate-800/20">
          <div class="col-span-12 md:col-span-7 space-y-4">
            <div>
              <label class="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-2">Nhận xét (Công khai cho team)</label>
              <textarea class="w-full bg-white dark:bg-slate-900 border border-slate-100 dark:border-slate-700 rounded-xl text-sm p-3 focus:ring-2 focus:ring-primary/20 resize-none" placeholder="Ghi lại các điểm mạnh và điểm yếu về chuyên môn..." rows="3">Ứng viên có tư duy phân tích tốt, biết cách trình bày portfolio logic.</textarea>
            </div>
            <div>
              <label class="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-2">Ghi chú nội bộ (Chỉ dành cho NTT)</label>
              <textarea class="w-full bg-white/70 dark:bg-slate-900/50 border border-slate-100 dark:border-slate-700 rounded-xl text-sm p-3 focus:ring-2 focus:ring-primary/20 resize-none italic" placeholder="Các lưu ý đặc biệt về lương, tính cách..." rows="2">Mức lương kỳ vọng 25-30M.</textarea>
            </div>
          </div>
          <div class="col-span-12 md:col-span-5 flex flex-col justify-between">
            <div class="space-y-3">
              <label class="block text-[10px] font-bold text-slate-400 uppercase tracking-widest">Quyết định tuyển dụng</label>
              <div class="grid grid-cols-2 gap-3">
                <button class="flex items-center justify-center gap-2 py-3 px-4 rounded-xl border-2 border-emerald-500 bg-emerald-600 text-white font-extrabold text-sm shadow-lg shadow-emerald-500/20 transition-all cursor-pointer">
                  <span class="material-symbols-outlined">verified</span> PASS
                </button>
                <button class="flex items-center justify-center gap-2 py-3 px-4 rounded-xl border-2 border-slate-200 dark:border-slate-700 text-slate-400 font-extrabold text-sm hover:border-red-500 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 transition-all bg-white dark:bg-slate-900 cursor-pointer">
                  <span class="material-symbols-outlined">cancel</span> FAIL
                </button>
              </div>
            </div>
            <div class="mt-6 p-4 bg-white dark:bg-slate-900 rounded-xl border border-slate-100 dark:border-slate-800">
              <div class="flex items-center justify-between text-xs mb-2">
                <span class="text-slate-500 font-medium">Độ phù hợp văn hóa:</span>
                <span class="font-bold text-slate-900 dark:text-white">85%</span>
              </div>
              <div class="w-full bg-slate-100 dark:bg-slate-800 h-1.5 rounded-full overflow-hidden">
                <div class="bg-primary h-full w-[85%] transition-all duration-700"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Candidate 2 — Minimized/Draft -->
      <div class="bg-white dark:bg-slate-900 rounded-2xl shadow-sm overflow-hidden opacity-75 border-l-4 border-slate-200 dark:border-slate-700 hover:opacity-100 transition-all">
        <div class="p-6 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <img src="https://i.pravatar.cc/150?u=duong" class="w-12 h-12 rounded-full grayscale object-cover" />
            <div>
              <h3 class="text-base font-bold text-slate-900 dark:text-white">Trần Thái Dương</h3>
              <p class="text-xs text-slate-500">Product Designer | 2 năm kinh nghiệm</p>
            </div>
          </div>
          <div class="flex items-center gap-6">
            <div class="text-right">
              <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Đánh giá nhanh</p>
              <div class="flex gap-0.5">
                <span v-for="i in 5" :key="i" class="material-symbols-outlined text-lg" style="color: #e2e8f0">star</span>
              </div>
            </div>
            <button class="bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 font-bold text-xs py-2.5 px-5 rounded-xl transition-colors cursor-pointer">
              Ghi nhận đánh giá
            </button>
          </div>
        </div>
      </div>

      <!-- Candidate 3 — In Progress -->
      <div class="bg-white dark:bg-slate-900 rounded-2xl shadow-sm overflow-hidden border-l-4 border-amber-400">
        <div class="p-6 border-b border-slate-50 dark:border-slate-800/50 flex items-start justify-between">
          <div class="flex items-center gap-4">
            <img src="https://i.pravatar.cc/150?u=nam" class="w-14 h-14 rounded-full object-cover" />
            <div>
              <h3 class="text-lg font-extrabold text-slate-900 dark:text-white">Lê Hoàng Nam</h3>
              <p class="text-sm text-slate-500">Senior Designer | 6 năm kinh nghiệm</p>
              <div class="flex items-center gap-4 mt-2">
                <span class="flex items-center gap-1 text-[10px] font-bold text-amber-600 bg-amber-50 dark:bg-amber-900/20 px-2 py-0.5 rounded-lg border border-amber-100 dark:border-amber-800/50">
                  <span class="material-symbols-outlined text-[12px]">pending</span> Đang thực hiện
                </span>
              </div>
            </div>
          </div>
          <div class="flex flex-col items-end gap-1">
            <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Xếp hạng năng lực</span>
            <div class="flex gap-0.5">
              <span v-for="i in 5" :key="i" class="material-symbols-outlined text-xl" :style="i <= 3 ? 'font-variation-settings: \'FILL\' 1; color: #f59e0b' : 'color: #e2e8f0'">star</span>
            </div>
          </div>
        </div>
        <div class="p-6 bg-slate-50/50 dark:bg-slate-800/20">
          <div class="flex items-center justify-between mb-5">
            <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest italic">Hệ thống đang lưu tạm bản nháp...</p>
            <button class="text-primary text-xs font-bold hover:underline cursor-pointer">Chi tiết bản nháp</button>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <button class="py-2.5 px-4 rounded-xl border border-primary text-primary font-bold text-xs bg-white dark:bg-slate-900 hover:bg-primary/5 transition-all cursor-pointer">
              Gửi thông báo kết quả sớm
            </button>
            <button class="py-2.5 px-4 rounded-xl bg-primary text-white font-bold text-xs shadow-sm shadow-primary/20 hover:scale-[1.02] transition-all cursor-pointer">
              Hoàn thành đánh giá
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Bulk Actions Footer -->
    <div class="bg-indigo-50/80 dark:bg-indigo-950/30 backdrop-blur-md rounded-3xl p-8 flex flex-col md:flex-row items-center justify-between shadow-xl border border-indigo-100 dark:border-indigo-800/50">
      <div class="flex items-center gap-6 mb-6 md:mb-0">
        <div class="bg-indigo-100 dark:bg-indigo-900/50 p-4 rounded-2xl flex items-center justify-center">
          <span class="material-symbols-outlined text-3xl text-primary font-variation-settings-'FILL'-1">fact_check</span>
        </div>
        <div>
          <h4 class="text-xl font-bold tracking-tight text-indigo-900 dark:text-white">Ghi nhận hàng loạt</h4>
          <p class="text-indigo-600/70 dark:text-slate-400 text-sm mt-0.5">Áp dụng một trạng thái cho tất cả ứng viên còn lại trong danh sách.</p>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row items-center gap-4 w-full md:w-auto">
        <div class="flex items-center gap-3 bg-white dark:bg-slate-900/50 border border-indigo-200/50 dark:border-indigo-800/50 rounded-xl px-5 py-2.5 w-full sm:w-auto">
          <span class="text-[10px] font-bold text-indigo-600/50 dark:text-slate-500 uppercase tracking-widest">Trạng thái:</span>
          <select class="bg-transparent border-none text-sm font-bold focus:ring-0 p-0 cursor-pointer min-w-[160px] text-slate-900 dark:text-white">
            <option class="text-slate-900">Chuyển vòng tiếp</option>
            <option class="text-slate-900">Từ chối (Email mẫu)</option>
            <option class="text-slate-900">Lưu hồ sơ tiềm năng</option>
          </select>
        </div>
        <button class="w-full sm:w-auto bg-primary hover:bg-primary/90 text-white font-extrabold px-10 py-3.5 rounded-xl transition-all shadow-lg shadow-primary/30 hover:scale-[1.02] active:scale-95 cursor-pointer">
          Thực thi hàng loạt
        </button>
      </div>
    </div>
  </div>
</template>
