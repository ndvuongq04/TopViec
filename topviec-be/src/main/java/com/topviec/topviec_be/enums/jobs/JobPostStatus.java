package com.topviec.topviec_be.enums.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Set;

public enum JobPostStatus {
    DRAFT("draft"),
    PENDING_APPROVAL("pending_approval"),
    REJECTED("rejected"),
    HIDDEN("hidden"),
    SCHEDULED("scheduled"),
    PUBLISHED("published"),
    PAUSED("paused"),
    CLOSED("closed"),
    EXPIRED("expired"),
    RENEWED("renewed"),
    INTERVIEWING("interviewing"),
    COMPLETED("completed"),
    DELETED("deleted");

    private final String value;

    /**
     * Các trạng thái tin tuyển dụng được phép phân công cho NTD.
     * scheduled: sắp đăng, phân công trước để NTD chuẩn bị
     * published: đang nhận ứng viên
     * paused: tạm dừng nhưng vẫn cần theo dõi hồ sơ
     * renewed: gia hạn (tương tự published)
     * interviewing: đang phỏng vấn, cần NTD quản lý
     * closed: đã đóng nhưng vẫn cần xử lý hồ sơ tồn đọng
     */
    private static final Set<JobPostStatus> ASSIGNABLE_STATUSES = Set.of(
            SCHEDULED, PUBLISHED, PAUSED, RENEWED, INTERVIEWING, CLOSED);

    JobPostStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Kiểm tra trạng thái tin có được phép phân công cho NTD hay không.
     */
    public boolean isAssignable() {
        return ASSIGNABLE_STATUSES.contains(this);
    }

    /**
     * Lấy danh sách giá trị String của các trạng thái được phép phân công.
     */
    public static Set<String> getAssignableValues() {
        return ASSIGNABLE_STATUSES.stream()
                .map(JobPostStatus::getValue)
                .collect(java.util.stream.Collectors.toSet());
    }

    @JsonCreator
    public static JobPostStatus fromValue(String value) {
        for (JobPostStatus status : JobPostStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown JobPostStatus: " + value);
    }
}