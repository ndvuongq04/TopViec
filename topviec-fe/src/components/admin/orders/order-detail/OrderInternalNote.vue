<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6">
    <h3 class="text-sm font-bold text-slate-900 dark:text-slate-100 mb-4">Ghi chú nội bộ</h3>
    <textarea
      class="w-full text-sm text-slate-700 dark:text-slate-300 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg p-3 resize-none outline-none focus:ring-2 focus:ring-[#963131]/20 transition-colors"
      rows="4"
      placeholder="Nhập ghi chú nội bộ..."
    ></textarea>
    <div class="flex justify-end mt-3">
      <button class="px-4 py-2 bg-[#963131] hover:bg-[#963131]/90 text-white text-sm font-bold rounded-lg transition-colors">
        Lưu ghi chú
      </button>
    </div>
  </div>
</template>
