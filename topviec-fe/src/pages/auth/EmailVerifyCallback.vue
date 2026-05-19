<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EmailVerifyCallbackCard from '@/components/ui/EmailVerifyCallbackCard.vue'
import authService from '@/services/auth.service'
import { useToast } from '@/composables/useToast'

type Status = 'loading' | 'success' | 'error'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const status = ref<Status>('loading')
const errorMessage = ref<string>('')

onMounted(async () => {
  const token = route.query.token as string

  if (!token) {
    status.value = 'error'
    errorMessage.value = 'Liên kết xác thực không hợp lệ.'
    toast.error('Xác thực thất bại', 'Liên kết xác thực không hợp lệ.')
    return
  }

  try {
    await authService.verifyEmail(token)
    status.value = 'success'
    toast.success('Xác thực thành công!', 'Tài khoản của bạn đã được kích hoạt.')
    setTimeout(() => router.push({ name: 'login' }), 5000)
  } catch {
    status.value = 'error'
    errorMessage.value = 'Liên kết đã hết hạn hoặc không hợp lệ.'
    toast.error('Xác thực thất bại', 'Liên kết đã hết hạn hoặc không hợp lệ.')
  }
})

function goToLogin() {
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex items-center justify-center p-4">
    <EmailVerifyCallbackCard
      :status="status"
      :error-message="errorMessage"
      @go-to-login="goToLogin"
    />
  </div>
</template>