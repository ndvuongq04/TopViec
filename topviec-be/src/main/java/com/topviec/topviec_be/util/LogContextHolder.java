package com.topviec.topviec_be.util;

import com.topviec.topviec_be.dto.internal.LogContext;

/**
 * ThreadLocal holder cho phép service layer bổ sung metadata vào log context
 * trong khi method đang thực thi.
 *
 * <pre>
 * // Trong service:
 * LogContextHolder.addMetadata("oldStatus", app.getStatus().name());
 * app.setStatus(newStatus);
 * LogContextHolder.addMetadata("newStatus", newStatus.name());
 * </pre>
 *
 * AOP tự khởi tạo và dọn dẹp ThreadLocal — service chỉ cần gọi addMetadata().
 */
public final class LogContextHolder {

    private static final ThreadLocal<LogContext> CONTEXT = new ThreadLocal<>();

    private LogContextHolder() {
    }

    /** AOP gọi — khởi tạo context trước khi method thực thi */
    public static void init(LogContext context) {
        CONTEXT.set(context);
    }

    /** Lấy context hiện tại (nullable) */
    public static LogContext get() {
        return CONTEXT.get();
    }

    /** AOP gọi — dọn dẹp sau khi xử lý xong */
    public static void clear() {
        CONTEXT.remove();
    }

    // ── Convenience methods cho service layer ──

    /** Đặt targetId nếu không lấy được từ @PathVariable (VD: sau khi create) */
    public static void setTargetId(Long targetId) {
        LogContext ctx = CONTEXT.get();
        if (ctx != null) {
            ctx.setTargetId(targetId);
        }
    }

    /** Thêm metadata key-value (oldStatus, newStatus, jobTitle, ...) */
    public static void addMetadata(String key, Object value) {
        LogContext ctx = CONTEXT.get();
        if (ctx != null) {
            ctx.addMetadata(key, value);
        }
    }
}
