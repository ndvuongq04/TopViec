// src/services/axios.ts
import axios from 'axios'
import { useAuthStore } from '@/stores/auth.store'
import router from '@/router'

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  withCredentials: true,
})

const PUBLIC_URLS = [
  '/auth/verify-email',
  '/auth/login',
  '/auth/register',
  '/auth/resend-verify-email',
  '/auth/refresh',
  '/auth/forgot-password',
  '/auth/reset-password',
  '/auth/logout',
]

const isPublicRequest = (url?: string) => PUBLIC_URLS.some((publicUrl) => url?.includes(publicUrl))

const isValidStoredToken = (token: string | null): token is string =>
  !!token && token !== 'undefined' && token !== 'null'

const parseJwtExp = (token: string): number | null => {
  try {
    const payload = token.split('.')[1]
    if (!payload) return null

    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/')
    const decoded = atob(normalized.padEnd(normalized.length + ((4 - normalized.length % 4) % 4), '='))
    const parsed = JSON.parse(decoded) as { exp?: number }

    return typeof parsed.exp === 'number' ? parsed.exp : null
  } catch {
    return null
  }
}

const isTokenExpired = (token: string, skewSeconds = 30) => {
  const exp = parseJwtExp(token)
  if (!exp) return false
  return exp * 1000 <= Date.now() + skewSeconds * 1000
}

// RESPONSE INTERCEPTOR - Tự động refresh khi 401
let isRefreshing = false
let failedQueue: Array<{
  resolve: (token: string) => void
  reject: (err: unknown) => void
}> = []

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((p) => (error ? p.reject(error) : p.resolve(token!)))
  failedQueue = []
}

const refreshAccessToken = async () => {
  if (isRefreshing) {
    return new Promise<string>((resolve, reject) => {
      failedQueue.push({ resolve, reject })
    })
  }

  isRefreshing = true

  try {
    const authStore = useAuthStore()
    await authStore.refreshToken()
    const newToken = authStore.accessToken

    if (!newToken) {
      throw new Error('Refresh succeeded but no access token was returned.')
    }

    processQueue(null, newToken)
    return newToken
  } catch (refreshError) {
    processQueue(refreshError, null)
    throw refreshError
  } finally {
    isRefreshing = false
  }
}

// REQUEST INTERCEPTOR - Gắn token vào header, refresh trước nếu token đã hết hạn
axiosInstance.interceptors.request.use(async (config) => {
  if (isPublicRequest(config.url)) {
    return config
  }

  let token = localStorage.getItem('accessToken')

  if (isValidStoredToken(token) && isTokenExpired(token)) {
    try {
      token = await refreshAccessToken()
    } catch {
      const authStore = useAuthStore()
      await authStore.logout()
      throw new axios.Cancel('Unable to refresh expired access token before request.')
    }
  }

  if (isValidStoredToken(token)) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

axiosInstance.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config

    // Bỏ qua interceptor cho các public routes
    if (isPublicRequest(originalRequest.url)) {
      return Promise.reject(error)
    }

    // Không phải 401 hoặc đã retry rồi → bỏ qua
    if (error.response?.status !== 401 || originalRequest._retry) {
      return Promise.reject(error)
    }

    // Nếu không có accessToken thì user chưa đăng nhập → không cần refresh, bỏ qua
    const token = localStorage.getItem('accessToken')
    if (!isValidStoredToken(token)) {
      return Promise.reject(error)
    }

    originalRequest._retry = true

    try {
      const newToken = await refreshAccessToken()
      originalRequest.headers.Authorization = `Bearer ${newToken}`
      return axiosInstance(originalRequest)
    } catch (refreshError) {
      const authStore = useAuthStore()
      await authStore.logout()
      // Sau khi refresh thất bại, về trang chủ (không force về login)
      router.push({ name: 'home' })
      return Promise.reject(refreshError)
    }
  }
)

export default axiosInstance
