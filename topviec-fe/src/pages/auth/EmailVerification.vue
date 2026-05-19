<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import EmailVerificationCard from '@/components/ui/EmailVerificationCard.vue'
import authService from '@/services/auth.service'
import { useToast } from '@/composables/useToast'
import router from '@/router'

const route = useRoute()
const toast = useToast()

const MAX_ATTEMPTS = 5
const COOLDOWN = 60

const email = computed(() => route.query.email as string)
const attempt = ref(1)
const canResend = ref(true)
const justResent = ref(false)

let timer: ReturnType<typeof setInterval> | null = null

function startCooldown() {
  let remaining = COOLDOWN
  canResend.value = false
  timer = setInterval(() => {
    remaining--
    if (remaining <= 0) {
      clearInterval(timer!)
      canResend.value = true
    }
  }, 1000)
}

async function handleResend() {
  if (!canResend.value || attempt.value >= MAX_ATTEMPTS) return
  try {
    await authService.resendVerifyEmail({ email: email.value })
    attempt.value++
    justResent.value = true
    toast.success('Đã gửi lại!', 'Vui lòng kiểm tra hộp thư của bạn.')
    setTimeout(() => (justResent.value = false), 3000)
    startCooldown()
  } catch {
    toast.error('Gửi lại thất bại', 'Vui lòng thử lại sau.')
  }
}

function goToLogin() {
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex items-center justify-center p-4">
    <EmailVerificationCard
      :email="email"
      :attempt="attempt"
      :max-attempts="MAX_ATTEMPTS"
      :can-resend="canResend"
      :just-resent="justResent"
      @resend="handleResend"
      @go-to-login="goToLogin"
    />
  </div>
</template>