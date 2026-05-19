// src/stores/auth.store.ts
// Store quản lý trạng thái đăng nhập: token, user. Gọi Service để gọi API; điều hướng qua router import trực tiếp (không dùng useRouter trong store).

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '@/router'
import authService from '@/services/auth.service'
import { resetAdminRoleCache } from '@/router/guards'
import type { ReqLoginDTO, ReqRegisterCandidateDTO, ReqRegisterEmployerDTO, UserInfo } from '@/types/auth.types'

/** Đọc accessToken từ localStorage an toàn (tránh lỗi khi SSR hoặc storage bị tắt) */
const getStoredToken = (): string | null => {
  try {
    return localStorage.getItem('accessToken')
  } catch {
    return null
  }
}

/** Đọc user từ localStorage, parse JSON an toàn */
const getStoredUser = (): UserInfo | null => {
  try {
    const raw = localStorage.getItem('user')
    if (!raw || raw === 'undefined') return null
    return JSON.parse(raw) as UserInfo
  } catch {
    localStorage.removeItem('user')
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  // --- STATE ---
  const accessToken = ref<string | null>(getStoredToken())
  const user = ref<UserInfo | null>(getStoredUser())

  // --- GETTERS ---
  const isAuthenticated = computed(() => !!accessToken.value)
  const userRole = computed(() => user.value?.role ?? null)

  // --- ACTIONS ---

  /**
   * Đăng nhập: gọi service → lưu token + user vào state và localStorage → redirect
   * Redirect path lấy từ query.redirect (router guard đã set khi chưa login)
   */
  async function login(credentials: ReqLoginDTO) {
    const res = await authService.login(credentials)
    accessToken.value = res.accessToken
    user.value = res.user
    localStorage.setItem('accessToken', res.accessToken)
    localStorage.setItem('user', JSON.stringify(res.user))

    const redirectPath = (router.currentRoute.value.query.redirect as string)
    if (redirectPath) {
      await router.push(redirectPath)
    } else {
      const roleHome: Record<string, string> = {
        ADMIN: 'admin-home',
        EMPLOYER: 'recruiter-dashboard',
        CANDIDATE: 'home',
      }
      await router.push({ name: roleHome[res.user.role] ?? 'home' })
    }
  }

  /**
   * Đăng ký: gọi service → backend có thể trả luôn accessToken + user → lưu state và redirect
   * Nếu backend chỉ trả message thì cần điều chỉnh: không lưu token, redirect về login
   */
  // async function register(payload: ReqRegisterDTO) {
  //   await authService.register(payload)
  //   await router.push({ name: 'login' })
  // }
  async function register(payload: ReqRegisterCandidateDTO) {
    await authService.register(payload)
    await router.push({
      name: 'email-verification',
      query: { email: payload.email }
    })
  }

  async function registerEmployer(payload: ReqRegisterEmployerDTO) {
    await authService.registerEmployer(payload)
    await router.push({
      name: 'email-verification',
      query: { email: payload.email }
    })
  }

  /**
   * Refresh token: gọi API /auth/refresh (cookie tự gửi) → cập nhật accessToken trong state và localStorage
   * Được gọi từ axios interceptor khi nhận 401
   */
  async function refreshToken() {
    const res = await authService.refresh()
    accessToken.value = res.accessToken
    localStorage.setItem('accessToken', res.accessToken)

  }

  /**
   * Đăng xuất: gọi API logout (xóa cookie phía server), xóa state và localStorage, redirect về /login
   * Không dùng useRouter; import router từ @/router
   */
  async function logout() {
    try {
      await authService.logout()
    } catch {
      // Bỏ qua lỗi network: vẫn logout phía client
    } finally {
      accessToken.value = null
      user.value = null
      try {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('user')
      } catch {
        // Một số môi trường có thể chặn localStorage
      }
      // Reset adminRole cache để tránh dữ liệu cũ khi đăng nhập lại
      resetAdminRoleCache()
      router.push({ name: 'home' })
    }
  }


  async function forgotPassword(email: string) {
    await authService.forgotPassword({ email })
  }

  async function resetPassword(token: string, newPassword: string) {
    await authService.resetPassword({ token, newPassword })
    await router.push({ name: 'login' })
  }

  async function verifyEmail(token: string) {
    await authService.verifyEmail(token)
    await router.push({ name: 'login' })
  }

  async function resendVerifyEmail(email: string) {
    await authService.resendVerifyEmail({ email })
  }

  return {
    accessToken,
    user,
    isAuthenticated,
    userRole,
    login,
    register,
    registerEmployer,
    refreshToken,
    logout,
    forgotPassword,
    resetPassword,
    verifyEmail,
    resendVerifyEmail,
  }
})
