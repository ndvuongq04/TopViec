import { ref } from 'vue'

export type ToastType = 'success' | 'error' | 'warning' | 'info'

export interface Toast {
    id: number
    type: ToastType
    title: string
    message?: string
    duration?: number
}

// Singleton state — dùng chung toàn app
const toasts = ref<Toast[]>([])
let _id = 0

export function useToast() {
    function add(type: ToastType, title: string, message?: string, duration = 4000) {
        const id = ++_id
        toasts.value.push({ id, type, title, message, duration })
        setTimeout(() => remove(id), duration)
    }

    function remove(id: number) {
        const idx = toasts.value.findIndex(t => t.id === id)
        if (idx !== -1) toasts.value.splice(idx, 1)
    }

    return {
        toasts,
        remove,
        success: (title: string, message?: string, duration?: number) =>
            add('success', title, message, duration),
        error: (title: string, message?: string, duration?: number) =>
            add('error', title, message, duration),
        warning: (title: string, message?: string, duration?: number) =>
            add('warning', title, message, duration),
        info: (title: string, message?: string, duration?: number) =>
            add('info', title, message, duration),
    }
}