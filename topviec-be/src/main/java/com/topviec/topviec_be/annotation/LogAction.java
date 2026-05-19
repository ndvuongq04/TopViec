package com.topviec.topviec_be.annotation;

import com.topviec.topviec_be.enums.logging.LogActionType;

import java.lang.annotation.*;

/**
 * Đánh dấu method cần ghi log.
 * Developer chỉ khai báo action type, AOP tự thu thập phần còn lại.
 *
 * <pre>
 * {@code @LogAction(LogActionType.SUSPEND_COMPANY)}
 * public ResponseEntity<ResCompanyDTO> suspendCompany(...) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAction {

    /** Action type — bắt buộc */
    LogActionType value();

    /** Ghi chú thêm (tùy chọn) */
    String description() default "";
}
