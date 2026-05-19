package com.topviec.topviec_be.enums.logging;

/**
 * Xác định log được ghi vào đâu.
 */
public enum LogType {
    /** Chỉ ghi vào bảng audit_logs */
    AUDIT,

    /** Chỉ ghi vào bảng business_event_logs */
    BUSINESS,

    /** Ghi vào cả 2 bảng */
    BOTH
}
