<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const router = useRouter()
const route = useRoute()

// Interview State (Mocked)
const interviewStatus = ref<'PendingSelection' | 'Confirmed' | 'Completed'>(route.params.id === '4' ? 'PendingSelection' : 'Confirmed')
const interviewType = ref<'Online' | 'Offline'>(route.params.id === '2' ? 'Offline' : 'Online')

const officeInfo = {
  address: 'Tòa nhà TopViec, 123 Đường Cách Mạng Tháng 8, Quận 3, TP. Hồ Chí Minh',
  mapLink: 'https://maps.google.com/?q=123+CMT8+Quan+3+HCM',
  room: 'Phòng họp A2 - Tầng 15'
}

// Preparation Checklist (Computed based on type)
const preparationItems = ref([
  { id: 1, text: 'Xem lại Portfolio và các dự án liên quan', completed: true, types: ['Online', 'Offline'] },
  { id: 2, text: 'Chuẩn bị Case Study chi tiết về UI/UX', completed: false, types: ['Online', 'Offline'] },
  { id: 3, text: 'Kiểm tra mic, camera và đường truyền mạng', completed: false, types: ['Online'] },
  { id: 4, text: 'Chuẩn bị trang phục lịch sự & chuyên nghiệp', completed: false, types: ['Offline'] },
  { id: 5, text: 'Mang theo CCCD và bản in CV/Portfolio', completed: false, types: ['Offline'] },
  { id: 6, text: 'Tìm hiểu về văn hóa và sản phẩm của TopViec', completed: false, types: ['Online', 'Offline'] },
])

const filteredPrepItems = computed(() => {
  return preparationItems.value.filter(item => item.types.includes(interviewType.value))
})

// Modal states
const isSelectionModalOpen = ref(false)
const isSuccessModalOpen = ref(false)
const selectedAppointment = ref({ title: '', time: '', location: '' })

const handleSelectSlot = () => {
  isSelectionModalOpen.value = true
}

const toggleCheck = (id: number) => {
  const item = preparationItems.value.find(i => i.id === id)
  if (item) item.completed = !item.completed
}

const onConfirmSlot = () => {
  selectedAppointment.value = {
    title: 'Senior UI/UX Designer',
    time: '25 Tháng 10, 2026<br>10:00 AM — 11:00 AM',
    location: interviewType.value === 'Online' ? 'Google Meet (Kèm link sau)' : officeInfo.address
  }
  isSelectionModalOpen.value = false
  isSuccessModalOpen.value = true
}

// Slot Selection logic
const selectedSlotId = ref<number | null>(3)
const slots = ref([
  { id: 1, day: 'Thứ Hai', date: '24 Tháng 10', time: '09:00 AM — 10:00 AM', icon: 'calendar_today' },
  { id: 2, day: 'Thứ Hai', date: '24 Tháng 10', time: '02:30 PM — 03:30 PM', icon: 'calendar_today' },
  { id: 3, day: 'Thứ Ba', date: '25 Tháng 10', time: '10:00 AM — 11:00 AM', icon: 'event_available' },
  { id: 4, day: 'Thứ Tư', date: '26 Tháng 10', time: '11:00 AM — 12:00 PM', icon: 'calendar_today' },
])
</script>

<template>
  <main class="flex-1 min-w-0 pb-20 max-w-[1440px] mx-auto w-full px-4 md:px-10 py-6">

    <!-- Unified Header Card -->
    <div class="bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden mb-8">
      <div class="p-6 md:p-8">
        <div class="flex flex-col md:flex-row gap-6 items-start justify-between">
          <div class="flex gap-6 items-start">
            <div class="w-16 h-16 bg-slate-50 dark:bg-slate-800 rounded-2xl flex items-center justify-center border border-slate-100 dark:border-slate-700 shrink-0">
               <span class="material-symbols-outlined text-4xl text-primary font-variation-icon">apartment</span>
            </div>
            <div>
              <div class="flex items-center gap-2 mb-2">
                <span :class="[
                  'px-2 py-0.5 rounded text-xs font-black uppercase tracking-wider border',
                  interviewStatus === 'PendingSelection' ? 'bg-amber-50 text-amber-600 border-amber-200/50' : 'bg-primary/10 text-primary border-primary/20'
                ]">
                  {{ interviewStatus === 'PendingSelection' ? 'Cần xác nhận' : 'Đã lên lịch' }}
                </span>
                <span class="text-sm font-bold text-slate-400">• Vòng Technical Interview</span>
              </div>
              <h1 class="text-3xl md:text-4xl font-black text-slate-900 dark:text-white leading-tight mb-2">Senior UI/UX Designer</h1>
              <p class="text-slate-500 dark:text-gray-400 text-base font-bold">TopViec Technology</p>
            </div>
          </div>

          <div class="w-full md:w-auto shrink-0 pt-4 md:pt-0 border-t md:border-t-0 border-slate-100 dark:border-slate-800">
            <button 
              v-if="interviewStatus === 'PendingSelection'"
              @click="handleSelectSlot"
              class="w-full md:px-8 py-3.5 bg-amber-500 text-white rounded-xl font-black text-sm shadow-lg shadow-amber-500/20 hover:scale-105 active:scale-95 transition-all flex items-center justify-center gap-2"
            >
              <span class="material-symbols-outlined text-lg">event_available</span>
              Chọn lịch ngay
            </button>
            <div v-else class="flex flex-col gap-3">
              <template v-if="interviewType === 'Online'">
                <button class="w-full md:px-10 py-4 bg-primary text-white rounded-xl font-black text-base shadow-lg shadow-primary/20 hover:scale-105 active:scale-95 transition-all flex items-center justify-center gap-2">
                  <span class="material-symbols-outlined text-xl">videocam</span>
                  Vào phòng họp
                </button>
              </template>
              <template v-else>
                <a 
                  :href="officeInfo.mapLink" 
                  target="_blank"
                  class="w-full md:px-10 py-4 bg-primary text-white rounded-xl font-black text-base shadow-lg shadow-primary/20 hover:scale-105 active:scale-95 transition-all flex items-center justify-center gap-2"
                >
                  <span class="material-symbols-outlined text-xl">near_me</span>
                  Chỉ đường
                </a>
              </template>
              <p class="text-xs text-slate-400 text-center font-black uppercase tracking-widest">Bắt đầu sau 15 phút nữa</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Stats Sub-bar -->
      <div class="px-6 md:px-8 py-4 bg-slate-50/50 dark:bg-slate-800/30 border-t border-slate-100 dark:border-slate-800 grid grid-cols-2 md:grid-cols-4 gap-6">
        <div>
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1.5 font-manrope">THỜI GIAN</p>
          <div class="flex items-center gap-2 text-slate-700 dark:text-slate-200">
            <span class="material-symbols-outlined text-primary text-[18px]">schedule</span>
            <span class="text-sm font-black">45 Phút</span>
          </div>
        </div>
        <div>
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1.5 font-manrope">HÌNH THỨC</p>
          <div class="flex items-center gap-2 text-slate-700 dark:text-slate-200">
            <span class="material-symbols-outlined text-primary text-[18px]">{{ interviewType === 'Online' ? 'videocam' : 'location_on' }}</span>
            <span class="text-sm font-black">{{ interviewType === 'Online' ? 'Trực tuyến' : 'Tại văn phòng' }}</span>
          </div>
        </div>
        <div class="hidden md:block">
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1.5 font-manrope">NGƯỜI PHỎNG VẤN</p>
          <div class="flex items-center gap-2 text-slate-700 dark:text-slate-200">
            <span class="material-symbols-outlined text-primary text-[18px]">groups</span>
            <span class="text-sm font-black">02 Chuyên gia</span>
          </div>
        </div>
        <div class="hidden md:block">
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1.5 font-manrope">TRẠNG THÁI</p>
          <div class="flex items-center gap-2 text-emerald-600 dark:text-emerald-400">
            <span class="material-symbols-outlined text-[18px]">check_circle</span>
            <span class="text-xs font-black uppercase">Sẵn sàng</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Content Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
      
      <!-- Main Content -->
      <div class="lg:col-span-8 space-y-8">
        <!-- Preparation Checklist Card -->
        <div class="bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
          <div class="p-6 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between bg-slate-50/30">
            <h3 class="font-black text-slate-900 dark:text-white flex items-center gap-2 text-base">
              <span class="material-symbols-outlined text-primary">checklist</span>
              Checklist chuẩn bị
            </h3>
            <span class="px-3 py-1 bg-primary/10 text-primary text-[10px] font-black rounded-full uppercase tracking-tighter shadow-sm border border-primary/10">
              {{ filteredPrepItems.filter(i => i.completed).length }}/{{ filteredPrepItems.length }} Hoàn thành
            </span>
          </div>
          
          <div class="p-6">
            <div class="grid gap-3">
              <button 
                v-for="item in filteredPrepItems" 
                :key="item.id"
                @click="toggleCheck(item.id)"
                class="flex items-center gap-4 p-4 rounded-xl border border-slate-100 dark:border-slate-800 hover:border-primary/30 transition-all text-left group bg-white dark:bg-slate-900/50"
              >
                <div :class="[
                  'w-5 h-5 rounded flex items-center justify-center transition-all shrink-0 border-2',
                  item.completed ? 'bg-primary border-primary text-white' : 'border-slate-200 dark:border-slate-700 group-hover:border-primary'
                ]">
                  <span v-if="item.completed" class="material-symbols-outlined text-[14px] font-black">check</span>
                </div>
                <span :class="[
                  'text-sm font-bold transition-all',
                  item.completed ? 'text-slate-400 line-through' : 'text-slate-700 dark:text-slate-300'
                ]">
                  {{ item.text }}
                </span>
              </button>
            </div>
          </div>
        </div>

        <!-- Meeting Notes / Instructions -->
        <div class="bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
          <h3 class="font-black text-slate-900 dark:text-white flex items-center gap-2 mb-6 text-lg">
            <span class="material-symbols-outlined text-primary">description</span>
            Ghi chú từ nhà tuyển dụng
          </h3>
          <div class="space-y-4 text-base text-slate-600 dark:text-gray-400 leading-relaxed font-medium">
            <p>Chào bạn, buổi phỏng vấn này sẽ tập trung vào kỹ năng chuyên môn về UI/UX Design cũng như khả năng xử lý bài toán thực tế.</p>
            <div class="p-4 bg-amber-50 dark:bg-amber-900/20 rounded-xl border border-amber-100 dark:border-amber-800/30 text-amber-800 dark:text-amber-300">
               <p class="font-black flex items-center gap-2 mb-2">
                 <span class="material-symbols-outlined text-sm">warning</span>
                 Lưu ý quan trọng:
               </p>
               <ul v-if="interviewType === 'Online'" class="list-disc ml-5 space-y-1">
                 <li>Hãy chuẩn bị file Portfolio hoặc link Figma sẵn lòng chia sẻ màn hình.</li>
                 <li>Kết nối Internet ổn định và tai nghe để có chất lượng âm thanh tốt nhất.</li>
               </ul>
               <ul v-else class="list-disc ml-5 space-y-1">
                 <li>Vui lòng có mặt trước giờ phỏng vấn 10-15 phút để làm thủ tục check-in tại quầy lễ tân.</li>
                 <li>Chuẩn bị bản in CV và các chứng chỉ liên quan (nếu có).</li>
               </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Sidebar -->
      <div class="lg:col-span-4 space-y-6">
        <!-- Location Card (Only for Offline) -->
        <div v-if="interviewType === 'Offline'" class="bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-6">Địa điểm phỏng vấn</p>
          <div class="flex items-start gap-3 mb-6">
            <div class="w-10 h-10 bg-primary/10 rounded-xl flex items-center justify-center shrink-0 border border-primary/10">
              <span class="material-symbols-outlined text-primary">location_on</span>
            </div>
            <div>
              <p class="text-sm font-black text-slate-900 dark:text-white mb-1">{{ officeInfo.room }}</p>
              <p class="text-xs text-slate-500 dark:text-gray-400 font-bold leading-relaxed">{{ officeInfo.address }}</p>
            </div>
          </div>
          <a :href="officeInfo.mapLink" target="_blank" class="w-full py-3 border-2 border-slate-100 dark:border-slate-800 text-slate-600 dark:text-slate-300 rounded-xl font-black text-sm hover:bg-slate-50 dark:hover:bg-slate-800 transition-all flex items-center justify-center gap-2">
            <span class="material-symbols-outlined text-base">map</span>
            Mở Bản đồ
          </a>
        </div>
        <!-- Recruiter Card -->
        <div class="bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-6">Người phụ trách</p>
          <div class="flex items-center gap-4 mb-6">
            <div class="w-16 h-16 rounded-2xl overflow-hidden border-2 border-slate-50 dark:border-slate-800 shrink-0">
              <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuBCBt3MGVNjatqeeeyAhWAf1J2_oG2jUPoMTS7mrgehmZMhNM-Qqg3zAdchBk0msb2VNLl2mBoZsgmUJNqm3i_VxCDqYdcUzmlmQo7f9fyuNDlgmI3KvNZUMwjj-hWqHpWzs66l7RQqCThoO-wdwRMWIpSgHB0tCd_N9ZE7k121BmkXGbh-EusUO9ji0vtgIMwSgOmtNMg_1XG14DbjIGAT6_N0Rio3Ikipjyp65UV4De6yqkAPI4b-nCA6Vgh6YGmHysbDiffIrg" class="w-full h-full object-cover" />
            </div>
            <div>
              <h4 class="font-black text-slate-900 dark:text-white text-lg">Lê Hồng Quân</h4>
              <p class="text-slate-500 dark:text-gray-400 text-sm font-bold mt-0.5">Senior Talent Acquisition</p>
            </div>
          </div>
          
          <div class="grid grid-cols-2 gap-3 mb-6">
            <div class="bg-slate-50 dark:bg-slate-800 p-3 rounded-xl border border-slate-100 dark:border-slate-700/50">
              <p class="text-xs font-black text-slate-400 uppercase tracking-tighter mb-1">Fan-rate</p>
              <p class="text-sm font-black text-slate-700 dark:text-white">98%</p>
            </div>
            <div class="bg-slate-50 dark:bg-slate-800 p-3 rounded-xl border border-slate-100 dark:border-slate-700/50">
              <p class="text-xs font-black text-slate-400 uppercase tracking-tighter mb-1">Phản hồi</p>
              <p class="text-sm font-black text-slate-700 dark:text-white">&lt; 1h</p>
            </div>
          </div>

          <button class="w-full py-3 bg-slate-900 dark:bg-white text-white dark:text-slate-900 rounded-xl font-black text-sm hover:opacity-90 transition-all flex items-center justify-center gap-2">
            <span class="material-symbols-outlined text-base">chat_bubble</span>
            Nhắn tin trao đổi
          </button>
        </div>

        <!-- Helpful Tips -->
        <div class="bg-primary/5 dark:bg-primary/10 rounded-2xl border border-primary/10 dark:border-primary/20 p-6 relative overflow-hidden group">
          <div class="absolute -right-4 -bottom-4 w-20 h-20 bg-primary/10 rounded-full blur-2xl group-hover:scale-110 transition-transform"></div>
          <h4 class="text-lg font-black text-slate-900 dark:text-white mb-4 flex items-center gap-2">
            <span class="material-symbols-outlined text-primary text-xl">lightbulb</span>
            Mẹo thành công
          </h4>
          <ul class="space-y-4 text-slate-600 dark:text-gray-400 text-sm font-bold leading-relaxed relative z-10">
            <li class="pl-4 relative">
              <span class="absolute left-0 top-0 text-primary font-black">•</span>
              Tập trung nói về các vấn đề bạn đã giải quyết thay vì chỉ show sản phẩm.
            </li>
            <li class="pl-4 relative">
              <span class="absolute left-0 top-0 text-primary font-black">•</span>
              Chuẩn bị ít nhất 3 câu hỏi cho hội đồng phỏng vấn về quy trình làm việc.
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Modals (Kept as they are) -->
    <GlobalModal
      :visible="isSelectionModalOpen"
      title="Chọn lịch phỏng vấn"
      subtitle="TopViec Technology — Senior UI/UX Designer"
      icon="event_available"
      max-width="2xl"
      @close="isSelectionModalOpen = false"
      @confirm="onConfirmSlot"
      confirm-text="Xác nhận lịch hẹn"
      confirm-icon="verified"
    >
      <div class="grid gap-4">
        <label 
          v-for="slot in slots" 
          :key="slot.id"
          :class="[
            'group relative flex items-center p-4 rounded-2xl border-2 transition-all duration-200 cursor-pointer',
            selectedSlotId === slot.id 
              ? 'border-primary bg-primary/5 dark:bg-primary/10' 
              : 'border-slate-100 dark:border-slate-800 bg-white dark:bg-slate-900 hover:border-primary/50'
          ]"
        >
          <input 
            type="radio" 
            name="interview_slot" 
            :value="slot.id" 
            v-model="selectedSlotId"
            class="w-5 h-5 text-primary border-slate-300 focus:ring-primary transition-all cursor-pointer"
          />
          <div class="ml-4 flex flex-grow items-center justify-between">
            <div>
              <p class="font-black text-slate-900 dark:text-white">{{ slot.day }}, {{ slot.date }}</p>
              <p class="text-sm font-bold text-slate-500 dark:text-gray-400">{{ slot.time }}</p>
            </div>
            <span 
              class="material-symbols-outlined transition-colors"
              :class="selectedSlotId === slot.id ? 'text-primary' : 'text-slate-300 group-hover:text-primary'"
              style="font-variation-settings: 'FILL' 1;"
            >
              {{ selectedSlotId === slot.id ? 'check_circle' : 'calendar_today' }}
            </span>
          </div>
        </label>
      </div>
      
      <div class="flex items-center justify-center gap-6 mt-6">
        <button class="text-slate-500 font-bold text-sm hover:text-primary transition-colors flex items-center gap-1.5 group">
          <span class="material-symbols-outlined text-lg group-hover:rotate-12 transition-transform">help</span>
          Yêu cầu khung giờ khác
        </button>
        <div class="h-4 w-[1px] bg-slate-200 dark:bg-slate-800"></div>
        <button class="text-slate-500 font-bold text-sm hover:text-primary transition-colors flex items-center gap-1.5 group">
          <span class="material-symbols-outlined text-lg group-hover:-translate-y-0.5 transition-transform">mail</span>
          Liên hệ nhân sự
        </button>
      </div>
    </GlobalModal>

    <GlobalModal
      :visible="isSuccessModalOpen"
      title="Xác nhận Thành công"
      icon="check_circle"
      max-width="2xl"
      variant="primary"
      :show-default-footer="false"
      @close="isSuccessModalOpen = false"
    >
      <div class="text-center py-4">
        <div class="mb-6 flex justify-center">
          <div class="w-20 h-20 bg-emerald-50 dark:bg-emerald-900/20 rounded-3xl flex items-center justify-center shadow-inner relative group">
            <div class="absolute inset-0 bg-emerald-500/10 rounded-3xl blur-xl animate-pulse"></div>
            <span class="material-symbols-outlined text-emerald-500 text-5xl relative z-10" style="font-variation-settings: 'FILL' 1;">check_circle</span>
          </div>
        </div>
        <h2 class="text-2xl font-black text-slate-900 dark:text-white mb-2">Lịch phỏng vấn đã được xác nhận</h2>
        <p class="text-slate-500 dark:text-gray-400 font-bold mb-8 max-w-sm mx-auto">
          Cảm ơn bạn. Chúng tôi đã nhận được sự lựa chọn của bạn.
        </p>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8 text-left">
          <div class="bg-slate-50 dark:bg-slate-800/50 p-4 rounded-2xl border border-slate-100 dark:border-slate-800/50">
            <p class="text-[9px] uppercase tracking-widest font-black text-slate-400 mb-2 font-manrope">VỊ TRÍ</p>
            <div class="flex items-start gap-2">
              <span class="material-symbols-outlined text-primary text-lg">work</span>
              <span class="font-black text-[11px] text-slate-700 dark:text-slate-200 leading-tight">{{ selectedAppointment.title }}</span>
            </div>
          </div>
          <div class="bg-slate-50 dark:bg-slate-800/50 p-4 rounded-2xl border border-slate-100 dark:border-slate-800/50">
            <p class="text-[9px] uppercase tracking-widest font-black text-slate-400 mb-2 font-manrope">THỜI GIAN</p>
            <div class="flex items-start gap-2">
              <span class="material-symbols-outlined text-primary text-lg">calendar_today</span>
              <span class="font-black text-[11px] text-slate-700 dark:text-slate-200 leading-tight" v-html="selectedAppointment.time"></span>
            </div>
          </div>
          <div class="bg-slate-50 dark:bg-slate-800/50 p-4 rounded-2xl border border-slate-100 dark:border-slate-800/50">
            <p class="text-[9px] uppercase tracking-widest font-black text-slate-400 mb-2 font-manrope">PHƯƠNG THỨC</p>
            <div class="flex items-start gap-2">
              <span class="material-symbols-outlined text-primary text-lg">{{ interviewType === 'Online' ? 'videocam' : 'location_on' }}</span>
              <span class="font-black text-[11px] text-slate-700 dark:text-slate-200 leading-tight">{{ selectedAppointment.location }}</span>
            </div>
          </div>
        </div>

        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <button class="bg-primary text-white font-black py-3.5 px-8 rounded-2xl shadow-xl shadow-primary/30 hover:scale-105 active:scale-95 transition-all flex items-center justify-center gap-2 text-sm">
            <span class="material-symbols-outlined text-lg" style="font-variation-settings: 'FILL' 1;">event</span>
            Add to Google Calendar
          </button>
          <button class="bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 font-black py-3.5 px-8 rounded-2xl hover:bg-slate-100 dark:hover:bg-slate-700 active:scale-95 transition-all flex items-center justify-center gap-2 text-sm">
            <span class="material-symbols-outlined text-lg">download</span>
            Download Invite
          </button>
        </div>
        
        <button @click="isSuccessModalOpen = false" class="mt-8 text-slate-400 hover:text-primary font-black text-xs flex items-center gap-1.5 justify-center group transition-colors">
          <span class="material-symbols-outlined text-sm group-hover:-translate-x-1 transition-transform">arrow_back</span>
          Quay lại trang chủ
        </button>
      </div>
    </GlobalModal>
  </main>
</template>
