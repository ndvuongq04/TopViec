<template>
  <div class="messages-page">
    <ConversationList
      :conversations="conversations"
      :active-id="activeId"
      :full-width="activeId === null"
      @select="activeId = $event"
    />
    <div v-if="activeId !== null && activeContact" class="messages-page__chat">
      <ChatHeader :contact="activeContact" @close="activeId = null" />
      <ChatMessages :messages="messages" />
      <ChatInput />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import ConversationList from '@/components/recruiter/messages/ConversationList.vue'
import ChatHeader from '@/components/recruiter/messages/ChatHeader.vue'
import ChatMessages from '@/components/recruiter/messages/ChatMessages.vue'
import ChatInput from '@/components/recruiter/messages/ChatInput.vue'

const activeId = ref<number | null>(null)

const conversations = ref([
  {
    id: 1, name: 'Nguyễn Thị Mai', role: 'UI/UX Designer',
    lastMsg: 'Dạ vâng, em cảm ơn anh/chị ạ.', time: '10:42',
    unread: 2, online: true,
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCPitbmB9fh9jkrOX9u4qHmkbiWmyNXKQ8Lu4dUnvC9U2ebwKYOG5-Xvr6_gV3t_LvmxPuWZTv-2PwtNRGMZwCglgztVdh1tVBo0Hl4t7pRnH1gx90DFR8lpzJf3OCzncatymX2qFooDLoeYlDBwZWCLn0bXjZseYu5SXPCLxInbj3Vox2P6jMvGhxLcqSAxJbiKMLGD1BNqfSEKx1VA-lg45nRPJPSN55Qzb6FXr2Laf9EVpLSzlfZd4C8MFq_owMMhMe0rZzA4A',
  },
  {
    id: 2, name: 'Trần Đức Kiên', role: 'Frontend Developer',
    lastMsg: 'Em đã gửi CV qua email rồi ạ.', time: 'Hôm qua',
    unread: 0, online: false,
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBi3FIwXCRDFSEBU-1HJ-cBSy5D09HrUaYIvcUv4dKBXjYw5yafH3KN3ItHZm4HsoQYpLLkVk2CYzoaiMEdYdpbudE24XouJkEyurTIEQAdywV239E7GK0ZC4PPz0v6pZFWTKB-VKZNDuV20H8ccUKXsRY23JnfE9CBkEWzwxg72eQqU5viAsJnqvj0BwOrRCgg89Xoze_x57gLdyj_9UVKsHf9g_BXS_pH4diAYWVMSqgLpnZgr2G1oBP84YotMzg69csJvjObjw',
  },
  {
    id: 3, name: 'Lê Hoàng Yến', role: 'Marketing Executive',
    lastMsg: 'Cảm ơn công ty đã cho em cơ hội.', time: 'T2',
    unread: 0, online: false,
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBSyS4dc3ExVpoY4HqBaaqHGrts8fR8ygshXm2sOx3K2t2ifNMT5XvYbPQVOoo35tMjKXrlLW5XhBdrQJ-fND8zFXMRwREdv7w7MqVtM2-H7CcO6fplPOAIyh_ljEgR0YuACVPRRGyvdnrqsVo9OofOFD530ENeTFNF9xLCbB-z_hic9Fj_0mIEn6CJz_TbRsetN-7-WquCtj6eUjhXxDnJwVBP0rf8BSewu3C_eKchJk7t1C0otRdGg0Z7ikDHTvEee7CmeEBf2w',
  },
])

const activeContact = computed(() => conversations.value.find(c => c.id === activeId.value))

const messages = ref([
  { id: 1, mine: false, text: 'Chào anh/chị, em thấy công ty đang tuyển vị trí UI/UX Designer. Em có kinh nghiệm 2 năm làm việc với Figma và đã từng tham gia thiết kế các sản phẩm SaaS.', time: '10:30', avatar: conversations.value[0].avatar },
  { id: 2, mine: true,  text: 'Chào Mai, cảm ơn bạn đã quan tâm. Bạn có thể gửi thêm link portfolio các dự án SaaS bạn đã làm để team tham khảo được không?', time: '10:35', avatar: '' },
  { id: 3, mine: false, text: 'Dạ vâng, em cảm ơn anh/chị ạ. Đây là link behance của em: behance.net/nguyenmai', time: '10:42', avatar: conversations.value[0].avatar },
])
</script>

<style scoped>
.messages-page {
  display: flex;
  flex: 1;
  height: 100%;
  overflow: hidden;
  background: #f6f7f8;
}
.messages-page__chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f6f7f8;
}
</style>