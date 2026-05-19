import { ref } from 'vue'

export interface ConfirmOptions {
    title: string
    message: string
    confirmText?: string
    cancelText?: string
    confirmColor?: 'primary' | 'red' | 'blue'
    icon?: string
}

interface ConfirmState extends ConfirmOptions {
    isOpen: boolean
}

const state = ref<ConfirmState>({
    isOpen: false,
    title: '',
    message: '',
    confirmText: 'Xác nhận',
    cancelText: 'Hủy bỏ',
    confirmColor: 'primary',
    icon: 'warning',
})

let resolvePromise: ((value: boolean) => void) | null = null

export function useConfirm() {
    const confirm = (options: ConfirmOptions): Promise<boolean> => {
        state.value = {
            isOpen: true,
            title: options.title,
            message: options.message,
            confirmText: options.confirmText || 'Xác nhận',
            cancelText: options.cancelText || 'Hủy bỏ',
            confirmColor: options.confirmColor || 'primary',
            icon: options.icon || 'warning',
        }

        return new Promise((resolve) => {
            resolvePromise = resolve
        })
    }

    const onConfirm = () => {
        state.value.isOpen = false
        if (resolvePromise) {
            resolvePromise(true)
            resolvePromise = null
        }
    }

    const onCancel = () => {
        state.value.isOpen = false
        if (resolvePromise) {
            resolvePromise(false)
            resolvePromise = null
        }
    }

    return {
        state,
        confirm,
        onConfirm,
        onCancel,
    }
}
