package com.topviec.topviec_be.enums.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum trung tâm — mỗi action mang đầy đủ metadata để AOP tự routing.
 * <p>
 * Quy ước Severity:
 * - CRITICAL: Ảnh hưởng toàn hệ thống (suspend, delete company, takedown)
 * - HIGH: Thay đổi dữ liệu nhạy cảm (password, quyền, xóa)
 * - MEDIUM: CRUD nghiệp vụ thông thường (tạo/sửa job, application)
 * - LOW: Hành động nhẹ (view, follow, save)
 */
@Getter
@RequiredArgsConstructor
public enum LogActionType {

    // ════════════════════════════════════════════
    // AUTH
    // ════════════════════════════════════════════

    // Audit
    CHANGE_PASSWORD(LogCategory.AUTH, LogType.AUDIT, Severity.HIGH, "USER"),

    // Business
    REGISTER_CANDIDATE(LogCategory.AUTH, LogType.BUSINESS, Severity.MEDIUM, "USER"),
    REGISTER_EMPLOYER(LogCategory.AUTH, LogType.BUSINESS, Severity.MEDIUM, "USER"),
    VERIFY_EMAIL(LogCategory.AUTH, LogType.BUSINESS, Severity.LOW, "USER"),

    // ════════════════════════════════════════════
    // CANDIDATE_PROFILE
    // ════════════════════════════════════════════

    // Audit
    UPDATE_CANDIDATE_PROFILE(LogCategory.CANDIDATE_PROFILE, LogType.AUDIT, Severity.MEDIUM, "CANDIDATE_PROFILE"),
    UPDATE_CANDIDATE_VISIBILITY(LogCategory.CANDIDATE_PROFILE, LogType.AUDIT, Severity.MEDIUM, "CANDIDATE_PROFILE"),
    DELETE_CANDIDATE_PROFILE(LogCategory.CANDIDATE_PROFILE, LogType.AUDIT, Severity.HIGH, "CANDIDATE_PROFILE"),

    // Business
    CREATE_CANDIDATE_PROFILE(LogCategory.CANDIDATE_PROFILE, LogType.BUSINESS, Severity.MEDIUM, "CANDIDATE_PROFILE"),

    // ════════════════════════════════════════════
    // CV_MANAGEMENT
    // ════════════════════════════════════════════

    // Audit
    DELETE_CV(LogCategory.CV_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "CV"),

    // Business
    UPLOAD_CV(LogCategory.CV_MANAGEMENT, LogType.BUSINESS, Severity.MEDIUM, "CV"),
    SHARE_CV(LogCategory.CV_MANAGEMENT, LogType.BUSINESS, Severity.LOW, "CV"),
    VIEW_PUBLIC_CV(LogCategory.CV_MANAGEMENT, LogType.BUSINESS, Severity.LOW, "CV"),

    // ════════════════════════════════════════════
    // APPLICATION
    // ════════════════════════════════════════════

    // Audit
    UPDATE_APPLICATION_CV(LogCategory.APPLICATION, LogType.AUDIT, Severity.MEDIUM, "APPLICATION"),

    // Business
    APPLY_JOB(LogCategory.APPLICATION, LogType.BUSINESS, Severity.MEDIUM, "APPLICATION"),
    QUICK_APPLY_JOB(LogCategory.APPLICATION, LogType.BUSINESS, Severity.MEDIUM, "APPLICATION"),
    BULK_APPLY_JOB(LogCategory.APPLICATION, LogType.BUSINESS, Severity.MEDIUM, "APPLICATION"),
    WITHDRAW_APPLICATION(LogCategory.APPLICATION, LogType.BUSINESS, Severity.MEDIUM, "APPLICATION"),
    ACCEPT_TALENT_POOL_INVITE(LogCategory.APPLICATION, LogType.BUSINESS, Severity.MEDIUM, "APPLICATION"),
    DECLINE_TALENT_POOL_INVITE(LogCategory.APPLICATION, LogType.BUSINESS, Severity.LOW, "APPLICATION"),

    // ════════════════════════════════════════════
    // SAVED_JOB & COMPANY_FOLLOW
    // ════════════════════════════════════════════

    SAVE_JOB(LogCategory.SAVED_JOB, LogType.BUSINESS, Severity.LOW, "JOB_POSTING"),
    FOLLOW_COMPANY(LogCategory.COMPANY_FOLLOW, LogType.BUSINESS, Severity.LOW, "COMPANY"),

    // ════════════════════════════════════════════
    // REPORTING — BOTH (*)
    // ════════════════════════════════════════════

    CREATE_REPORT(LogCategory.REPORTING, LogType.BOTH, Severity.HIGH, "REPORT"),

    // ════════════════════════════════════════════
    // COMPANY_MANAGEMENT (Audit)
    // ════════════════════════════════════════════

    UPDATE_COMPANY_PROFILE(LogCategory.COMPANY_MANAGEMENT, LogType.AUDIT, Severity.MEDIUM, "COMPANY"),

    // ════════════════════════════════════════════
    // MEMBER_MANAGEMENT (Audit)
    // ════════════════════════════════════════════

    ADD_MEMBER(LogCategory.MEMBER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "COMPANY_MEMBER"),
    UPDATE_MEMBER_PERMISSION(LogCategory.MEMBER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "COMPANY_MEMBER"),
    TOGGLE_MEMBER_ACTION_PERMISSION(LogCategory.MEMBER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "COMPANY_MEMBER"),
    REMOVE_MEMBER(LogCategory.MEMBER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "COMPANY_MEMBER"),

    // ════════════════════════════════════════════
    // JOB_MANAGEMENT
    // ════════════════════════════════════════════

    // Audit
    UPDATE_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.AUDIT, Severity.MEDIUM, "JOB_POSTING"),
    PAUSE_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.AUDIT, Severity.MEDIUM, "JOB_POSTING"),
    RESUME_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.AUDIT, Severity.MEDIUM, "JOB_POSTING"),
    DELETE_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "JOB_POSTING"),
    RESTORE_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.AUDIT, Severity.MEDIUM, "JOB_POSTING"),

    // Business
    CREATE_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.BUSINESS, Severity.MEDIUM, "JOB_POSTING"),
    CLOSE_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.BUSINESS, Severity.MEDIUM, "JOB_POSTING"),
    EXTEND_JOB_POSTING(LogCategory.JOB_MANAGEMENT, LogType.BUSINESS, Severity.MEDIUM, "JOB_POSTING"),
    SUBMIT_JOB_POSTING_APPROVAL(LogCategory.JOB_MANAGEMENT, LogType.BUSINESS, Severity.MEDIUM, "JOB_POSTING"),

    // ════════════════════════════════════════════
    // APPLICATION_REVIEW — BOTH (*)
    // ════════════════════════════════════════════

    UPDATE_APPLICATION(LogCategory.APPLICATION_REVIEW, LogType.BOTH, Severity.MEDIUM, "APPLICATION"),

    // ════════════════════════════════════════════
    // TALENT_POOL
    // ════════════════════════════════════════════

    // Audit
    VIEW_CANDIDATE_DETAIL(LogCategory.TALENT_POOL, LogType.AUDIT, Severity.LOW, "CANDIDATE_PROFILE"),
    UPDATE_TALENT_POOL_NOTE(LogCategory.TALENT_POOL, LogType.AUDIT, Severity.LOW, "TALENT_POOL"),
    REMOVE_FROM_TALENT_POOL(LogCategory.TALENT_POOL, LogType.AUDIT, Severity.MEDIUM, "TALENT_POOL"),

    // Business
    SEARCH_CANDIDATES(LogCategory.TALENT_POOL, LogType.BUSINESS, Severity.LOW, "TALENT_POOL"),
    ADD_TO_TALENT_POOL(LogCategory.TALENT_POOL, LogType.BUSINESS, Severity.MEDIUM, "TALENT_POOL"),
    INVITE_FROM_TALENT_POOL(LogCategory.TALENT_POOL, LogType.BUSINESS, Severity.MEDIUM, "TALENT_POOL"),

    // ════════════════════════════════════════════
    // INTERVIEW
    // ════════════════════════════════════════════

    // Audit
    UPDATE_INTERVIEW_ROUND(LogCategory.INTERVIEW, LogType.AUDIT, Severity.MEDIUM, "INTERVIEW_ROUND"),
    DELETE_INTERVIEW_ROUND(LogCategory.INTERVIEW, LogType.AUDIT, Severity.HIGH, "INTERVIEW_ROUND"),
    CREATE_INTERVIEW_SLOTS(LogCategory.INTERVIEW, LogType.AUDIT, Severity.MEDIUM, "INTERVIEW_SLOT"),
    UPDATE_INTERVIEW_SCHEDULE(LogCategory.INTERVIEW, LogType.AUDIT, Severity.MEDIUM, "INTERVIEW"),
    DELETE_INTERVIEW_SCHEDULE(LogCategory.INTERVIEW, LogType.AUDIT, Severity.HIGH, "INTERVIEW"),
    EXTEND_INTERVIEW_DEADLINE(LogCategory.INTERVIEW, LogType.AUDIT, Severity.MEDIUM, "INTERVIEW"),
    FORCE_SCHEDULE_INTERVIEW(LogCategory.INTERVIEW, LogType.AUDIT, Severity.HIGH, "INTERVIEW"),

    // Business
    CREATE_INTERVIEW_ROUND(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW_ROUND"),
    CREATE_INTERVIEW_SCHEDULE(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    SEND_OFFER(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    UPDATE_OFFER_RESULT(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    START_INTERVIEW_PHASE(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    START_INTERVIEWING(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    COMPLETE_RECRUITMENT(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    CONFIRM_INTERVIEW_SCHEDULE(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    CANDIDATE_CONFIRM_INTERVIEW(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    CONFIRM_UPDATED_INTERVIEW_SCHEDULE(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),
    CANDIDATE_CONFIRM_UPDATED_SCHEDULE(LogCategory.INTERVIEW, LogType.BUSINESS, Severity.MEDIUM, "INTERVIEW"),

    // Both (*)
    CREATE_INTERVIEW_RESULT(LogCategory.INTERVIEW, LogType.BOTH, Severity.MEDIUM, "INTERVIEW"),
    RECORD_INTERVIEW_RESULT(LogCategory.INTERVIEW, LogType.BOTH, Severity.MEDIUM, "INTERVIEW"),

    // ════════════════════════════════════════════
    // BILLING (Business)
    // ════════════════════════════════════════════

    CREATE_ORDER(LogCategory.BILLING, LogType.BUSINESS, Severity.HIGH, "ORDER"),
    CANCEL_ORDER(LogCategory.BILLING, LogType.BUSINESS, Severity.HIGH, "ORDER"),
    RENEW_SUBSCRIPTION(LogCategory.BILLING, LogType.BUSINESS, Severity.MEDIUM, "SUBSCRIPTION"),
    APPLY_JOB_POST_ADDON(LogCategory.BILLING, LogType.BUSINESS, Severity.MEDIUM, "JOB_POSTING"),
    APPLY_COMPANY_BRANDING(LogCategory.BILLING, LogType.BUSINESS, Severity.MEDIUM, "COMPANY"),

    // ════════════════════════════════════════════
    // APPEAL — BOTH (*)
    // ════════════════════════════════════════════

    CREATE_APPEAL(LogCategory.APPEAL, LogType.BOTH, Severity.HIGH, "APPEAL"),
    EMPLOYER_SUBMIT_APPEAL(LogCategory.APPEAL, LogType.BOTH, Severity.HIGH, "APPEAL"),
    RESPOND_TO_REPORT(LogCategory.APPEAL, LogType.BOTH, Severity.MEDIUM, "REPORT"),

    // ════════════════════════════════════════════
    // ADMIN_USER_MANAGEMENT (Audit)
    // ════════════════════════════════════════════

    CREATE_ADMIN(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.CRITICAL, "ADMIN_USER"),
    CREATE_ADMIN_USER(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.CRITICAL, "ADMIN_USER"),
    UPDATE_ADMIN(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "ADMIN_USER"),
    UPDATE_ADMIN_USER(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "ADMIN_USER"),
    TOGGLE_ADMIN_ACTIVE(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "ADMIN_USER"),
    TOGGLE_ADMIN_USER_ACTIVE(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "ADMIN_USER"),
    DELETE_ADMIN(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.CRITICAL, "ADMIN_USER"),
    DELETE_ADMIN_USER(LogCategory.ADMIN_USER_MANAGEMENT, LogType.AUDIT, Severity.CRITICAL, "ADMIN_USER"),

    // ════════════════════════════════════════════
    // COMPANY_ADMIN
    // ════════════════════════════════════════════

    // Audit
    ADMIN_UPDATE_COMPANY(LogCategory.COMPANY_ADMIN, LogType.AUDIT, Severity.HIGH, "COMPANY"),
    UPDATE_COMPANY_BY_ADMIN(LogCategory.COMPANY_ADMIN, LogType.AUDIT, Severity.HIGH, "COMPANY"),
    DELETE_COMPANY(LogCategory.COMPANY_ADMIN, LogType.AUDIT, Severity.CRITICAL, "COMPANY"),

    // Business
    CREATE_EMPLOYER_COMPANY(LogCategory.COMPANY_ADMIN, LogType.BUSINESS, Severity.HIGH, "COMPANY"),

    // ════════════════════════════════════════════
    // COMPANY_MODERATION
    // ════════════════════════════════════════════

    // Audit
    REJECT_COMPANY_VERIFICATION(LogCategory.COMPANY_MODERATION, LogType.AUDIT, Severity.HIGH, "COMPANY"),
    UNSUSPEND_COMPANY(LogCategory.COMPANY_MODERATION, LogType.AUDIT, Severity.HIGH, "COMPANY"),

    // Business
    VERIFY_COMPANY(LogCategory.COMPANY_MODERATION, LogType.BUSINESS, Severity.MEDIUM, "COMPANY"),

    // Both (*)
    SUSPEND_COMPANY(LogCategory.COMPANY_MODERATION, LogType.BOTH, Severity.CRITICAL, "COMPANY"),

    // ════════════════════════════════════════════
    // CANDIDATE_ADMIN (Audit)
    // ════════════════════════════════════════════

    TOGGLE_CANDIDATE_STATUS(LogCategory.CANDIDATE_ADMIN, LogType.AUDIT, Severity.HIGH, "CANDIDATE_PROFILE"),

    // ════════════════════════════════════════════
    // MODERATION
    // ════════════════════════════════════════════

    // Audit
    REJECT_JOB_POSTING(LogCategory.MODERATION, LogType.AUDIT, Severity.HIGH, "JOB_POSTING"),
    ADMIN_RESTORE_JOB_POSTING(LogCategory.MODERATION, LogType.AUDIT, Severity.HIGH, "JOB_POSTING"),
    RESTORE_JOB_POSTING_BY_ADMIN(LogCategory.MODERATION, LogType.AUDIT, Severity.HIGH, "JOB_POSTING"),

    // Business
    APPROVE_JOB_POSTING(LogCategory.MODERATION, LogType.BUSINESS, Severity.MEDIUM, "JOB_POSTING"),

    // Both (*)
    TAKEDOWN_JOB_POSTING(LogCategory.MODERATION, LogType.BOTH, Severity.CRITICAL, "JOB_POSTING"),

    // ════════════════════════════════════════════
    // REPORT_MODERATION (Audit)
    // ════════════════════════════════════════════

    CONFIRM_REPORT(LogCategory.REPORT_MODERATION, LogType.AUDIT, Severity.MEDIUM, "REPORT"),
    PROCESS_REPORT(LogCategory.REPORT_MODERATION, LogType.AUDIT, Severity.MEDIUM, "REPORT"),

    // ════════════════════════════════════════════
    // VIOLATION_MANAGEMENT
    // ════════════════════════════════════════════

    // Audit
    RESET_VIOLATION_SCORE(LogCategory.VIOLATION_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "VIOLATION_SCORE"),
    ADJUST_VIOLATION_SCORE(LogCategory.VIOLATION_MANAGEMENT, LogType.AUDIT, Severity.HIGH, "VIOLATION_SCORE"),

    // Both (*)
    UNSUSPEND_EMPLOYER_AFTER_APPEAL(LogCategory.VIOLATION_MANAGEMENT, LogType.BOTH, Severity.HIGH, "COMPANY"),

    // ════════════════════════════════════════════
    // SERVICE_CATALOG (Audit)
    // ════════════════════════════════════════════

    CREATE_SERVICE(LogCategory.SERVICE_CATALOG, LogType.AUDIT, Severity.MEDIUM, "SERVICE"),
    UPDATE_SERVICE(LogCategory.SERVICE_CATALOG, LogType.AUDIT, Severity.MEDIUM, "SERVICE"),
    CREATE_ADDON_SERVICE(LogCategory.SERVICE_CATALOG, LogType.AUDIT, Severity.MEDIUM, "ADDON_SERVICE"),
    UPDATE_ADDON_SERVICE(LogCategory.SERVICE_CATALOG, LogType.AUDIT, Severity.MEDIUM, "ADDON_SERVICE"),
    CREATE_SERVICE_PACKAGE(LogCategory.SERVICE_CATALOG, LogType.AUDIT, Severity.MEDIUM, "SERVICE_PACKAGE"),
    UPDATE_SERVICE_PACKAGE(LogCategory.SERVICE_CATALOG, LogType.AUDIT, Severity.MEDIUM, "SERVICE_PACKAGE"),

    // ════════════════════════════════════════════
    // ORDER_MANAGEMENT (Business)
    // ════════════════════════════════════════════

    ADMIN_UPDATE_ORDER_STATUS(LogCategory.ORDER_MANAGEMENT, LogType.BUSINESS, Severity.MEDIUM, "ORDER"),
    UPDATE_ORDER_STATUS(LogCategory.ORDER_MANAGEMENT, LogType.BUSINESS, Severity.MEDIUM, "ORDER"),

    // ════════════════════════════════════════════
    // SYSTEM_MAINTENANCE
    // ════════════════════════════════════════════

    // Audit
    AUTO_RESET_VIOLATION_SCORE(LogCategory.SYSTEM_MAINTENANCE, LogType.AUDIT, Severity.MEDIUM, "VIOLATION_SCORE"),

    // Business
    EXPIRE_SUBSCRIPTION(LogCategory.SYSTEM_MAINTENANCE, LogType.BUSINESS, Severity.MEDIUM, "SUBSCRIPTION"),
    EXPIRE_JOB_POST_HOT(LogCategory.SYSTEM_MAINTENANCE, LogType.BUSINESS, Severity.LOW, "JOB_POSTING"),
    EXPIRE_JOB_POST_URGENT(LogCategory.SYSTEM_MAINTENANCE, LogType.BUSINESS, Severity.LOW, "JOB_POSTING"),
    EXPIRE_COMPANY_BRANDING(LogCategory.SYSTEM_MAINTENANCE, LogType.BUSINESS, Severity.LOW, "COMPANY");

    private final LogCategory category;
    private final LogType logType;
    private final Severity severity;
    private final String targetEntity;
}
