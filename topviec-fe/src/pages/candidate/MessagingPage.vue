<template>
  <main class="page-wrap mt-2">
    <ConversationList
      :conversations="conversations"
      :active-id="activeConversationId"
      @select="activeConversationId = $event"
    />
    <ChatWindow
      v-if="activeConversation"
      :conversation="activeConversation"
      :messages="activeMessages"
      @send="handleSend"
    />
  </main>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import ConversationList from '@/components/candidate/messaging/ConversationList.vue'
import ChatWindow from '@/components/candidate/messaging/ChatWindow.vue'

const activeConversationId = ref(1)

const conversations = ref([
  {
    id: 1, companyName: 'TechNova Solutions', position: 'Senior Frontend Developer',
    tag: 'Mời phỏng vấn', tagColor: 'green', lastMessage: 'Chào bạn, chúng tôi rất ấn tượng...',
    time: '10:42', unread: true, avatar: '',
  },
  {
    id: 2, companyName: 'InnoCorp Vietnam', position: 'React Native Engineer',
    tag: 'Đã xem hồ sơ', tagColor: 'blue', lastMessage: 'Cảm ơn bạn đã ứng tuyển...',
    time: 'Hôm qua', unread: false, avatar: '',
  },
  {
    id: 3, companyName: 'GlobalTech Industries', position: 'UI/UX Designer',
    tag: '', tagColor: '', lastMessage: 'Bạn: Xin chào, tôi muốn hỏi thêm...',
    time: '18/04', unread: false, avatar: '',
  },
  {
    id: 4, companyName: 'FPT Software', position: 'Backend Developer (Java)',
    tag: 'Mời phỏng vấn', tagColor: 'green', lastMessage: 'Anh/chị vui lòng xác nhận lịch phỏng vấn...',
    time: '17/04', unread: true, avatar: '',
  },
  {
    id: 5, companyName: 'Viettel Digital', position: 'DevOps Engineer',
    tag: 'Đã xem hồ sơ', tagColor: 'blue', lastMessage: 'Hồ sơ của bạn đã được chuyển sang bước tiếp theo.',
    time: '16/04', unread: false, avatar: '',
  },
  {
    id: 6, companyName: 'Shopee Vietnam', position: 'Data Engineer',
    tag: '', tagColor: '', lastMessage: 'Bạn: Cảm ơn anh/chị đã phản hồi!',
    time: '15/04', unread: false, avatar: '',
  },
  {
    id: 7, companyName: 'MoMo (M_Service)', position: 'Mobile Developer (Flutter)',
    tag: 'Mời phỏng vấn', tagColor: 'green', lastMessage: 'Chúng tôi muốn mời bạn tham gia buổi phỏng vấn...',
    time: '14/04', unread: false, avatar: '',
  },
  {
    id: 8, companyName: 'Tiki Corporation', position: 'Product Manager',
    tag: '', tagColor: '', lastMessage: 'Rất tiếc, hồ sơ của bạn chưa phù hợp...',
    time: '12/04', unread: false, avatar: '',
  },
  {
    id: 9, companyName: 'VNG Corporation', position: 'Game Backend Engineer',
    tag: 'Đã xem hồ sơ', tagColor: 'blue', lastMessage: 'Chúng tôi đang xem xét hồ sơ của bạn.',
    time: '10/04', unread: false, avatar: '',
  },
  {
    id: 10, companyName: 'Zalo (VNG)', position: 'iOS Developer',
    tag: '', tagColor: '', lastMessage: 'Bạn: Tôi rất mong được làm việc cùng team.',
    time: '08/04', unread: false, avatar: '',
  },
])

const messages = ref<Record<number, any[]>>({
  1: [
    { id: 1, type: 'system', text: 'Bạn đã ứng tuyển vị trí Senior Frontend Developer · 20/04/2026', icon: 'work_history' },
    { id: 2, type: 'received', senderName: 'Trần Mai (HR)', time: '09:10', text: 'Chào Nguyễn Văn A, chúng tôi đã nhận được hồ sơ của bạn.' },
    { id: 3, type: 'sent', time: '09:15', text: 'Chào chị, cảm ơn chị đã xem xét hồ sơ của em ạ!', seen: true },
    { id: 4, type: 'received', senderName: 'Trần Mai (HR)', time: '09:30', text: 'Bạn có thể cho chúng tôi biết thêm về kinh nghiệm với Vue.js không?' },
    { id: 5, type: 'sent', time: '09:35', text: 'Em có 3 năm kinh nghiệm với Vue.js, đã làm việc với Vuex, Pinia và Vue Router trong nhiều dự án thực tế ạ.', seen: true },
    { id: 6, type: 'received', senderName: 'Trần Mai (HR)', time: '10:42',
      text: 'Chào Nguyễn Văn A,\n\nChúng tôi rất ấn tượng với portfolio của bạn. Chúng tôi muốn mời bạn tham gia phỏng vấn.',
      interviewCard: { title: 'Thư mời phỏng vấn trực tuyến', datetime: 'Thứ Tư, 22/04/2026 - 14:00' },
    },
    { id: 7, type: 'sent', time: '10:45', text: 'Chào chị Mai,\n\nCảm ơn chị đã liên hệ. Tôi rất vui và vinh dự được tham gia buổi phỏng vấn. Tôi xác nhận sẽ tham dự đúng giờ ạ!', seen: true },
    { id: 8, type: 'received', senderName: 'Trần Mai (HR)', time: '10:50', text: 'Tuyệt vời! Chúng tôi sẽ gửi link Meet trước buổi phỏng vấn 30 phút. Chúc bạn chuẩn bị tốt nhé.' },
    { id: 9, type: 'sent', time: '10:52', text: 'Em cảm ơn chị nhiều ạ. Em sẽ chuẩn bị kỹ cho buổi phỏng vấn.', seen: false },
  ],
  2: [
    { id: 1, type: 'system', text: 'Bạn đã ứng tuyển vị trí React Native Engineer · 19/04/2026', icon: 'work_history' },
    { id: 2, type: 'received', senderName: 'Lê Hoàng (Recruiter)', time: 'Hôm qua', text: 'Chào bạn, chúng tôi đã xem qua hồ sơ của bạn và thấy khá phù hợp với vị trí này.' },
    { id: 3, type: 'sent', time: 'Hôm qua', text: 'Chào anh, em rất vui khi được phản hồi. Em có thể hỏi thêm về tech stack của team không ạ?', seen: true },
    { id: 4, type: 'received', senderName: 'Lê Hoàng (Recruiter)', time: 'Hôm qua', text: 'Team dùng React Native với TypeScript, state management bằng Redux Toolkit, và backend là Node.js.' },
    { id: 5, type: 'sent', time: 'Hôm qua', text: 'Em đã có kinh nghiệm với cả stack đó ạ, đặc biệt là Redux Toolkit. Cảm ơn anh đã chia sẻ!', seen: true },
  ],
  3: [
    { id: 1, type: 'system', text: 'Bạn đã ứng tuyển vị trí UI/UX Designer · 18/04/2026', icon: 'work_history' },
    { id: 2, type: 'sent', time: '18/04', text: 'Xin chào, tôi muốn hỏi thêm về quy trình thiết kế tại công ty ạ.', seen: true },
    { id: 3, type: 'received', senderName: 'Nguyễn Linh (HR)', time: '18/04', text: 'Chào bạn! Chúng tôi dùng Figma là công cụ chính, kết hợp design system nội bộ.' },
    { id: 4, type: 'sent', time: '18/04', text: 'Tuyệt vời, em đã làm việc với Figma được 2 năm và có kinh nghiệm xây dựng design system ạ.', seen: true },
  ],
  4: [
    { id: 1, type: 'system', text: 'Bạn đã ứng tuyển vị trí Backend Developer (Java) · 17/04/2026', icon: 'work_history' },
    { id: 2, type: 'received', senderName: 'Phạm Hùng (HR)', time: '17/04', text: 'Chào bạn, hồ sơ của bạn rất ấn tượng! Chúng tôi muốn sắp xếp buổi phỏng vấn sớm.',
      interviewCard: { title: 'Phỏng vấn kỹ thuật - Vòng 1', datetime: 'Thứ Sáu, 19/04/2026 - 09:00' },
    },
    { id: 3, type: 'sent', time: '17/04', text: 'Anh ơi, em xin xác nhận tham gia. Em sẽ chuẩn bị kỹ về Spring Boot và microservices ạ.', seen: true },
  ],
  5: [
    { id: 1, type: 'system', text: 'Bạn đã ứng tuyển vị trí DevOps Engineer · 16/04/2026', icon: 'work_history' },
    { id: 2, type: 'received', senderName: 'Trần Khánh (Talent Acquisition)', time: '16/04', text: 'Hồ sơ của bạn đã được chuyển sang bước tiếp theo trong quy trình tuyển dụng.' },
    { id: 3, type: 'sent', time: '16/04', text: 'Cảm ơn anh/chị đã thông báo. Em mong chờ các bước tiếp theo ạ.', seen: true },
  ],
})

const activeConversation = computed(() =>
  conversations.value.find(c => c.id === activeConversationId.value)
)

const activeMessages = computed(() => messages.value[activeConversationId.value] ?? [])

function handleSend(text: string) {
  if (!text.trim()) return
  messages.value[activeConversationId.value]?.push({
    id: Date.now(), type: 'sent', time: 'Vừa xong', text, seen: false,
  })
}
</script>

<style scoped>
.page-wrap {
  display: flex;
  width: 100%;
  height: calc(100vh - 65px);
  overflow: hidden;
}
</style>