package com.topviec.topviec_be.service;

public interface EmailService {
    void sendVerifyEmail(String toEmail, String verifyUrl);

    void sendResetPasswordEmail(String toEmail, String token, String fullName);

    /**
     * TH1: Email mời thành viên mới chưa có tài khoản.
     * Gửi kèm mật khẩu tạm + link xác thực.
     */
    void sendMemberInviteNewUser(String toEmail, String tempPassword, String verifyToken);

    /**
     * Thông báo thay đổi quyền (CN-NTT-019).
     */
    void sendPermissionChangedEmail(String toEmail, String companyName, String newRoleName);

    /**
     * Thông báo thay đổi lịch phỏng vấn (updateSchedule).
     */
    void sendUpdateScheduleEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                 String oldSchedule, String newScheduleTime, String newScheduleDate,
                                 String interviewLocation, String interviewerName, String confirmLink);

    /**
     * Thông báo hủy lịch phỏng vấn (deleteSchedule).
     */
    void sendCancelScheduleEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                 String scheduledTime, String scheduledDate, String roundName);

    /**
     * Thông báo kết quả phỏng vấn cho UV (PASS hoặc FAIL).
     */
    void sendInterviewResultEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                  String roundName, boolean passed, Integer rating, String note);

    /**
     * Thông báo UV chọn slot phỏng vấn (Cách 2: UV tự chọn lịch).
     */
    void sendSlotSelectionEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                String roundName, String deadline, String selectSlotLink);

    // ── Khiếu nại tin tuyển dụng ──────────────────────────────────────────────

    /** Nhóm A: NTD có 48h để sửa tin trước khi bị ẩn */
    void sendComplaintGroupAEmail(String toEmail, String jobPostTitle, String complaintType,
                                  String reportCode, String deadline);

    /** Nhóm B: vi phạm nghiêm trọng — ẩn tin + cộng điểm ngay */
    void sendComplaintGroupBEmail(String toEmail, String jobPostTitle, String complaintType,
                                  String reportCode);

    /** Quá 48h NTD không sửa — tin đã bị ẩn + cộng điểm */
    void sendComplaintAutoClosedEmail(String toEmail, String jobPostTitle, String complaintType,
                                      String reportCode);

    /** Mời UV từ talent pool ứng tuyển vào tin tuyển dụng */
    void sendTalentPoolInviteEmail(String toEmail, String candidateName, String companyName,
                                   String jobTitle, String jobLink);

    /** Nhắc nhở NTD gia hạn gói subscription sắp hết hạn */
    void sendSubscriptionExpiryReminder(String toEmail, String companyName, String packageName,
                                        String expiredAt, int daysRemaining);

    // ── Phân công tin tuyển dụng ──────────────────────────────────────────────

    /**
     * Thông báo member được phân công (hoặc đổi phân công) một tin tuyển dụng.
     *
     * @param isReassign true nếu là đổi phân công, false nếu là phân công lần đầu
     */
    void sendJobAssignedEmail(String toEmail, String companyName, String jobTitle,
                              String assignedByEmail, String assignedAt,
                              String note, boolean isReassign);

    /**
     * Thông báo member bị thu hồi phân công tin tuyển dụng.
     */
    void sendJobRevokedEmail(String toEmail, String companyName, String jobTitle,
                             String revokedByEmail, String revokedAt, String note);
}