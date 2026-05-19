package com.topviec.topviec_be.util;

import com.topviec.topviec_be.annotation.Trackable;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.Hibernate;

/**
 * Utility theo dõi thay đổi dữ liệu entity (Change Data Capture).
 *
 * <p>Cách dùng trong service:
 * <pre>
 * // 1. Snapshot TRƯỚC khi sửa
 * ChangeTracker tracker = ChangeTracker.of(entity);
 *
 * // 2. Thực hiện sửa đổi
 * entity.setTitle("new title");
 * repository.save(entity);
 *
 * // 3. So sánh + ghi vào log context
 * tracker.compare(entity).apply();
 * </pre>
 *
 * <p>Chỉ so sánh các field có annotation {@code @Trackable}.
 * Kết quả ghi vào {@code LogContextHolder.metadata} với format:
 * <pre>
 * {
 *   "changedFields": [
 *     { "field": "title", "label": "Tiêu đề", "oldValue": "A", "newValue": "B" }
 *   ],
 *   "totalChanges": 1
 * }
 * </pre>
 */
@Slf4j
public class ChangeTracker {

    private final Map<String, FieldSnapshot> oldSnapshot;
    private List<Map<String, Object>> changedFields;

    private ChangeTracker(Map<String, FieldSnapshot> oldSnapshot) {
        this.oldSnapshot = oldSnapshot;
        this.changedFields = new ArrayList<>();
    }

    /**
     * Tạo snapshot từ entity hiện tại (TRƯỚC khi sửa).
     *
     * @param entity entity cần track — phải có field đánh {@code @Trackable}
     * @return ChangeTracker instance
     */
    public static ChangeTracker of(Object entity) {
        Map<String, FieldSnapshot> snapshot = takeSnapshot(entity);
        return new ChangeTracker(snapshot);
    }

    /**
     * So sánh entity SAU khi sửa với snapshot trước đó.
     * Tìm các field có giá trị thay đổi.
     *
     * @param entity entity sau khi đã sửa
     * @return this (fluent API)
     */
    public ChangeTracker compare(Object entity) {
        Map<String, FieldSnapshot> newSnapshot = takeSnapshot(entity);
        this.changedFields = new ArrayList<>();

        for (Map.Entry<String, FieldSnapshot> entry : oldSnapshot.entrySet()) {
            String fieldName = entry.getKey();
            FieldSnapshot oldField = entry.getValue();
            FieldSnapshot newField = newSnapshot.get(fieldName);

            if (newField == null) continue;

            Object oldValue = oldField.value;
            Object newValue = newField.value;

            boolean isChanged = false;
            if (oldValue instanceof BigDecimal o && newValue instanceof BigDecimal n) {
                isChanged = o.compareTo(n) != 0;
            } else {
                isChanged = !Objects.equals(oldValue, newValue);
            }

            if (isChanged) {
                Map<String, Object> change = new LinkedHashMap<>();
                change.put("field", fieldName);
                change.put("label", oldField.label);
                change.put("oldValue", formatValue(oldValue));
                change.put("newValue", formatValue(newValue));
                changedFields.add(change);
            }
        }

        return this;
    }

    /**
     * Ghi kết quả so sánh vào LogContextHolder metadata.
     * Chỉ ghi nếu có ít nhất 1 field thay đổi.
     */
    public void apply() {
        if (changedFields.isEmpty()) return;

        LogContextHolder.addMetadata("changedFields", changedFields);
        LogContextHolder.addMetadata("totalChanges", changedFields.size());
    }

    /**
     * Trả về danh sách field đã thay đổi (dùng khi cần kiểm tra thủ công).
     */
    public List<Map<String, Object>> getChangedFields() {
        return Collections.unmodifiableList(changedFields);
    }

    /**
     * Kiểm tra có field nào thay đổi hay không.
     */
    public boolean hasChanges() {
        return !changedFields.isEmpty();
    }

    // ══════════════════════════════════════════════════
    // Internal
    // ══════════════════════════════════════════════════

    /**
     * Quét tất cả field có @Trackable trong entity, lấy giá trị hiện tại.
     */
    private static Map<String, FieldSnapshot> takeSnapshot(Object entity) {
        Map<String, FieldSnapshot> snapshot = new LinkedHashMap<>();
        if (entity == null) return snapshot;

        // Xử lý Hibernate Proxy để có thể truy xuất trực tiếp vào fields
        Object actualEntity = Hibernate.unproxy(entity);
        Class<?> clazz = actualEntity.getClass();

        // Quét cả class cha (nếu entity kế thừa)
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                Trackable trackable = field.getAnnotation(Trackable.class);
                if (trackable == null) continue;

                field.setAccessible(true);
                try {
                    Object value = field.get(actualEntity);
                    snapshot.put(field.getName(), new FieldSnapshot(trackable.label(), value));
                } catch (IllegalAccessException e) {
                    log.warn("[ChangeTracker] Cannot read field {}: {}", field.getName(), e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }

        return snapshot;
    }

    /**
     * Format giá trị để lưu vào JSON metadata.
     * Enum → name(), Collection → toString(), null → null.
     */
    private static Object formatValue(Object value) {
        if (value == null) return null;
        if (value instanceof Enum<?> e) return e.name();
        if (value instanceof LocalDate d) return d.toString();
        if (value instanceof LocalDateTime dt) return dt.toString();
        return value;
    }

    /**
     * Internal: giữ label + value cho 1 field snapshot.
     */
    private record FieldSnapshot(String label, Object value) {}
}
