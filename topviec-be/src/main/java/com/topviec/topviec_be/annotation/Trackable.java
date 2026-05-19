package com.topviec.topviec_be.annotation;

import java.lang.annotation.*;

/**
 * Đánh dấu field trong entity cần theo dõi thay đổi (Change Data Capture).
 *
 * <p>Khi entity được cập nhật, {@code ChangeTracker} sẽ so sánh giá trị trước/sau
 * của các field có annotation này và ghi vào log metadata.
 *
 * <pre>
 * {@code @Trackable(label = "Tiêu đề")}
 * private String title;
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trackable {

    /**
     * Tên hiển thị thân thiện (tiếng Việt) cho field.
     * Dùng để hiển thị trên giao diện khi xem chi tiết log.
     */
    String label();
}
