<template>
  <footer class="bg-white dark:bg-surface-dark border-t border-slate-200 dark:border-slate-800 pt-8 pb-4">
    <div class="max-w-[1440px] mx-auto w-full px-4 md:px-10">

      <!-- ═══ TOP SECTION ═══ -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-8 pb-8 border-b border-slate-200 dark:border-slate-800">
        <!-- Logo & Contact (approx 30%) -->
        <div class="lg:col-span-4 lg:pr-8">
          <router-link to="/" class="flex items-center gap-1.5 mb-1.5">
            <span class="text-2xl font-bold tracking-tight text-primary">top<span class="text-text-main dark:text-white">viec</span></span>
            <span class="text-xs text-primary font-bold -mt-3">®</span>
          </router-link>
          <p class="text-sm font-medium text-slate-500 dark:text-slate-400 mb-3">Tiếp lợi thế – Nối thành công</p>

          <div class="flex items-center gap-2 mb-4">
            <div class="border border-slate-200 dark:border-slate-700 rounded-md px-2 py-1 text-xs font-bold uppercase text-text-main dark:text-slate-300 flex items-center gap-1 bg-slate-50 dark:bg-slate-800/50">
              <svg width="12" height="12" viewBox="0 0 24 24"><path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/><path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/><path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z"/><path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/></svg>
              Google for Startups
            </div>
            <div class="border border-slate-200 dark:border-slate-700 rounded-md px-2 py-1 text-xs font-bold uppercase text-amber-800 dark:text-amber-500 flex items-center gap-1 bg-amber-50 dark:bg-amber-900/10">
              <svg width="11" height="11" viewBox="0 0 24 24" fill="currentColor"><path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4z"/></svg>
              DMCA Protected
            </div>
          </div>

          <div class="mb-4">
            <h4 class="text-base font-bold text-text-main dark:text-white mb-1.5">Liên hệ</h4>
            <p class="text-sm text-text-secondary dark:text-slate-400 mb-0.5">Hotline: <strong class="text-text-main dark:text-gray-300">(024) 6680 5588</strong> (Giờ hành chính)</p>
            <p class="text-sm text-text-secondary dark:text-slate-400">Email: <strong class="text-text-main dark:text-gray-300">hotro@topviec.vn</strong></p>
          </div>

          <div>
            <h4 class="text-base font-bold text-text-main dark:text-white mb-2">Cộng đồng TopViec</h4>
            <div class="flex gap-2.5">
              <div class="w-[30px] h-[30px] rounded-full bg-slate-800 dark:bg-slate-700 flex items-center justify-center hover:bg-slate-700 dark:hover:bg-slate-600 transition-colors cursor-pointer group">
                <svg viewBox="0 0 24 24" fill="#fff" class="w-3.5 h-3.5"><path d="M18 2h-3a5 5 0 00-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 011-1h3z"/></svg>
              </div>
              <div class="w-[30px] h-[30px] rounded-full bg-slate-800 dark:bg-slate-700 flex items-center justify-center hover:bg-slate-700 dark:hover:bg-slate-600 transition-colors cursor-pointer">
                <svg viewBox="0 0 24 24" fill="#fff" class="w-3.5 h-3.5"><path d="M22.54 6.42a2.78 2.78 0 00-1.95-1.96C18.88 4 12 4 12 4s-6.88 0-8.59.46a2.78 2.78 0 00-1.95 1.96A29 29 0 001 12a29 29 0 00.46 5.58A2.78 2.78 0 003.41 19.54C5.12 20 12 20 12 20s6.88 0 8.59-.46a2.78 2.78 0 001.95-1.96A29 29 0 0023 12a29 29 0 00-.46-5.58z"/><polygon points="9.75 15.02 15.5 12 9.75 8.98 9.75 15.02" fill="#1f2937"/></svg>
              </div>
              <div class="w-[30px] h-[30px] rounded-full bg-slate-800 dark:bg-slate-700 flex items-center justify-center hover:bg-slate-700 dark:hover:bg-slate-600 transition-colors cursor-pointer">
                <svg viewBox="0 0 24 24" fill="#fff" class="w-3.5 h-3.5"><path d="M16 8a6 6 0 016 6v7h-4v-7a2 2 0 00-2-2 2 2 0 00-2 2v7h-4v-7a6 6 0 016-6zM2 9h4v12H2z"/><circle cx="4" cy="4" r="2" fill="#fff"/></svg>
              </div>
              <div class="w-[30px] h-[30px] rounded-full bg-slate-800 dark:bg-slate-700 flex items-center justify-center hover:bg-slate-700 dark:hover:bg-slate-600 transition-colors cursor-pointer">
                <svg viewBox="0 0 24 24" fill="#fff" class="w-3.5 h-3.5"><path d="M19.59 6.69a4.83 4.83 0 01-3.77-4.25V2h-3.45v13.67a2.89 2.89 0 01-2.88 2.5 2.89 2.89 0 01-2.89-2.89 2.89 2.89 0 012.89-2.89c.28 0 .54.04.79.1V9.01a6.33 6.33 0 00-.79-.05 6.34 6.34 0 00-6.34 6.34 6.34 6.34 0 006.34 6.34 6.34 6.34 0 006.33-6.34l-.01-8.72a8.2 8.2 0 004.79 1.53V4.67a4.85 4.85 0 01-1.02-.02v2.04z"/></svg>
              </div>
            </div>
          </div>
        </div>

        <!-- Links Columns -->
        <div class="lg:col-span-2 space-y-5 lg:space-y-6">
          <!-- Về TopViec -->
          <div>
            <h4 class="text-base font-bold text-text-main dark:text-white mb-2">Về TopViec</h4>
            <ul class="flex flex-col gap-1.5 list-none">
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Giới thiệu</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Góc báo chí</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Tuyển dụng</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Liên hệ</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Hỏi đáp</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Chính sách bảo mật</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Điều khoản dịch vụ</router-link></li>
            </ul>
          </div>
          <!-- Đối tác -->
          <div>
            <h4 class="text-base font-bold text-text-main dark:text-white mb-2">Đối tác</h4>
            <ul class="flex flex-col gap-1.5 list-none">
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">TestCenter</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">TopHR</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">ViecNgay</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Happy Time</router-link></li>
            </ul>
          </div>
        </div>

        <div class="lg:col-span-3 space-y-5 lg:space-y-6">
          <!-- Hồ sơ và CV -->
          <div>
            <h4 class="text-base font-bold text-text-main dark:text-white mb-2">Hồ sơ và CV</h4>
            <ul class="flex flex-col gap-1.5 list-none">
              <li><router-link to="/cv-templates" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Quản lý CV của bạn</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Hướng dẫn viết CV</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Thư viện CV theo ngành nghề</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Review CV</router-link></li>
            </ul>
          </div>
          <!-- Khám phá -->
          <div>
            <h4 class="text-base font-bold text-text-main dark:text-white mb-2">Khám phá</h4>
            <ul class="flex flex-col gap-1.5 list-none">
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Ứng dụng di động TopViec</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Tính lương Gross - Net</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Tính lãi suất kép</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Lập kế hoạch tiết kiệm</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Tính bảo hiểm thất nghiệp</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Tính bảo hiểm xã hội một lần</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Trắc nghiệm MBTI</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Trắc nghiệm MI</router-link></li>
            </ul>
          </div>
        </div>

        <div class="lg:col-span-3 space-y-5 lg:space-y-6">
          <!-- Xây dựng sự nghiệp -->
          <div>
            <h4 class="text-base font-bold text-text-main dark:text-white mb-2">Xây dựng sự nghiệp</h4>
            <ul class="flex flex-col gap-1.5 list-none">
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Việc làm tốt nhất</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Việc làm lương cao</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Việc làm quản lý</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Việc làm IT</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Việc làm Senior</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Việc làm bán thời gian</router-link></li>
            </ul>
          </div>
          <!-- Quy tắc chung -->
          <div>
            <h4 class="text-base font-bold text-text-main dark:text-white mb-2">Quy tắc chung</h4>
            <ul class="flex flex-col gap-1.5 list-none">
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Điều kiện giao dịch chung</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Giá dịch vụ &amp; Cách thanh toán</router-link></li>
              <li><router-link to="#" class="text-sm text-text-secondary dark:text-slate-400 hover:text-primary transition-colors">Thông tin vận chuyển</router-link></li>
            </ul>
          </div>
        </div>
      </div>

      <!-- ═══ BOTTOM SECTION ═══ -->
      <div class="pt-6">

        <p class="text-center text-xs text-slate-400 py-4 border-t border-slate-100 dark:border-slate-800 mt-1">
          © 2014–{{ new Date().getFullYear() }} TopViec Vietnam JSC. All rights reserved.
        </p>

      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
// CandidateFooter based precisely on Topcv structure, styled per skill-font.md
</script>
