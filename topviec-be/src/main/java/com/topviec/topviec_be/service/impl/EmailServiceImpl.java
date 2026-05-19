package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.verify-email-url}")
    private String verifyEmailPath;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerifyEmail(String toEmail, String token) {
        // 1. Tạo context chứa các biến truyền vào template
        Context context = new Context();
        context.setVariable("verifyLink", verifyEmailPath + "?token=" + token);

        // 2. Render template thành HTML string
        String htmlBody = templateEngine.process("email/verify-email", context);

        // 3. Gửi email
        sendHtmlEmail(toEmail, "[Topviec] Xác thực tài khoản", htmlBody);
    }

    @Override
    public void sendResetPasswordEmail(String toEmail, String token, String fullName) {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("resetLink", baseUrl + "/reset-password?token=" + token);

        String htmlBody = templateEngine.process("email/reset-password", context);
        sendHtmlEmail(toEmail, "[Topviec] Đặt lại mật khẩu", htmlBody);
    }

    @Override
    public void sendMemberInviteNewUser(String toEmail, String tempPassword, String verifyToken) {
        Context context = new Context();
        context.setVariable("email", toEmail);
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("verifyLink", verifyEmailPath + "?token=" + verifyToken);

        String htmlBody = templateEngine.process("email/member-invite-new-user", context);
        sendHtmlEmail(toEmail, "[Topviec] Bạn được mời tham gia công ty", htmlBody);
    }

    @Override
    public void sendPermissionChangedEmail(String toEmail, String companyName, String newRoleName) {
        Context context = new Context();
        context.setVariable("companyName", companyName);
        context.setVariable("newRoleName", newRoleName);

        String htmlBody = templateEngine.process("email/member-permission-changed", context);
        sendHtmlEmail(toEmail, "[Topviec] Thay đổi phân quyền thành viên công ty", htmlBody);
    }

    @Override
    public void sendUpdateScheduleEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                        String oldSchedule, String newScheduleTime, String newScheduleDate,
                                        String interviewLocation, String interviewerName, String confirmLink) {
        Context context = new Context();
        context.setVariable("candidateName", candidateName);
        context.setVariable("companyName", companyName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("oldSchedule", oldSchedule);
        context.setVariable("newScheduleTime", newScheduleTime);
        context.setVariable("newScheduleDate", newScheduleDate);
        context.setVariable("interviewLocation", interviewLocation);
        context.setVariable("interviewerName", interviewerName);
        context.setVariable("confirmLink", confirmLink);

        String htmlBody = templateEngine.process("email/update-schedule", context);
        sendHtmlEmail(toEmail, "[Topviec] Thông báo thay đổi lịch phỏng vấn", htmlBody);
    }

    @Override
    public void sendCancelScheduleEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                        String scheduledTime, String scheduledDate, String roundName) {
        Context context = new Context();
        context.setVariable("candidateName", candidateName);
        context.setVariable("companyName", companyName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("scheduledTime", scheduledTime);
        context.setVariable("scheduledDate", scheduledDate);
        context.setVariable("roundName", roundName);

        String htmlBody = templateEngine.process("email/cancel-schedule", context);
        sendHtmlEmail(toEmail, "[Topviec] Thông báo hủy lịch phỏng vấn", htmlBody);
    }

    @Override
    public void sendInterviewResultEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                         String roundName, boolean passed, Integer rating, String note) {
        Context context = new Context();
        context.setVariable("candidateName", candidateName);
        context.setVariable("companyName", companyName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("roundName", roundName);
        context.setVariable("passed", passed);
        context.setVariable("rating", rating);
        context.setVariable("note", note);

        String htmlBody = templateEngine.process("email/interview-result", context);
        sendHtmlEmail(toEmail, "[Topviec] Thông báo kết quả phỏng vấn", htmlBody);
    }

    @Override
    public void sendSlotSelectionEmail(String toEmail, String candidateName, String companyName, String jobTitle,
                                       String roundName, String deadline, String selectSlotLink) {
        Context context = new Context();
        context.setVariable("candidateName", candidateName);
        context.setVariable("companyName", companyName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("roundName", roundName);
        context.setVariable("deadline", deadline);
        context.setVariable("selectSlotLink", selectSlotLink);

        String htmlBody = templateEngine.process("email/select-slot", context);
        sendHtmlEmail(toEmail, "[Topviec] Chọn lịch phỏng vấn - " + companyName, htmlBody);
    }

    @Override
    public void sendComplaintGroupAEmail(String toEmail, String jobPostTitle, String complaintType,
                                         String reportCode, String deadline) {
        Context context = new Context();
        context.setVariable("jobPostTitle", jobPostTitle);
        context.setVariable("complaintType", complaintType);
        context.setVariable("reportCode", reportCode);
        context.setVariable("deadline", deadline);
        context.setVariable("dashboardLink", baseUrl + "/employer/complaints");

        String htmlBody = templateEngine.process("email/complaint-group-a", context);
        sendHtmlEmail(toEmail, "[Topviec] Tin tuyển dụng của bạn bị khiếu nại - Vui lòng xử lý trong 48h", htmlBody);
    }

    @Override
    public void sendComplaintGroupBEmail(String toEmail, String jobPostTitle, String complaintType,
                                         String reportCode) {
        Context context = new Context();
        context.setVariable("jobPostTitle", jobPostTitle);
        context.setVariable("complaintType", complaintType);
        context.setVariable("reportCode", reportCode);
        context.setVariable("dashboardLink", baseUrl + "/employer/complaints");

        String htmlBody = templateEngine.process("email/complaint-group-b", context);
        sendHtmlEmail(toEmail, "[Topviec] Tin tuyển dụng của bạn đã bị xử lý vi phạm", htmlBody);
    }

    @Override
    public void sendComplaintAutoClosedEmail(String toEmail, String jobPostTitle, String complaintType,
                                             String reportCode) {
        Context context = new Context();
        context.setVariable("jobPostTitle", jobPostTitle);
        context.setVariable("complaintType", complaintType);
        context.setVariable("reportCode", reportCode);
        context.setVariable("dashboardLink", baseUrl + "/employer/complaints");

        String htmlBody = templateEngine.process("email/complaint-auto-closed", context);
        sendHtmlEmail(toEmail, "[Topviec] Tin tuyển dụng của bạn đã bị ẩn do không xử lý khiếu nại", htmlBody);
    }

    @Override
    public void sendTalentPoolInviteEmail(String toEmail, String candidateName, String companyName,
                                          String jobTitle, String jobLink) {
        Context context = new Context();
        context.setVariable("candidateName", candidateName);
        context.setVariable("companyName", companyName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("jobLink", jobLink);

        String htmlBody = templateEngine.process("email/talent-pool-invite", context);
        sendHtmlEmail(toEmail, "[Topviec] " + companyName + " mời bạn ứng tuyển vào vị trí " + jobTitle, htmlBody);
    }

    @Override
    public void sendSubscriptionExpiryReminder(String toEmail, String companyName, String packageName,
                                                String expiredAt, int daysRemaining) {
        Context context = new Context();
        context.setVariable("companyName", companyName);
        context.setVariable("packageName", packageName);
        context.setVariable("expiredAt", expiredAt);
        context.setVariable("daysRemaining", daysRemaining);
        context.setVariable("renewLink", baseUrl + "/employer/services");

        String htmlBody = templateEngine.process("email/subscription-expiry-reminder", context);
        sendHtmlEmail(toEmail, "[Topviec] Gói dịch vụ của bạn sắp hết hạn — Gia hạn ngay", htmlBody);
    }

    @Override
    public void sendJobAssignedEmail(String toEmail, String companyName, String jobTitle,
                                     String assignedByEmail, String assignedAt,
                                     String note, boolean isReassign) {
        Context context = new Context();
        context.setVariable("companyName", companyName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("assignedByEmail", assignedByEmail);
        context.setVariable("assignedAt", assignedAt);
        context.setVariable("note", note);
        context.setVariable("isReassign", isReassign);
        context.setVariable("dashboardLink", baseUrl + "/employer/assignments");

        String subject = isReassign
                ? "[Topviec] Bạn được đổi phân công quản lý tin tuyển dụng"
                : "[Topviec] Bạn được phân công quản lý tin tuyển dụng mới";

        String htmlBody = templateEngine.process("email/job-assigned", context);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    @Override
    public void sendJobRevokedEmail(String toEmail, String companyName, String jobTitle,
                                    String revokedByEmail, String revokedAt, String note) {
        Context context = new Context();
        context.setVariable("companyName", companyName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("revokedByEmail", revokedByEmail);
        context.setVariable("revokedAt", revokedAt);
        context.setVariable("note", note);

        String htmlBody = templateEngine.process("email/job-revoked", context);
        sendHtmlEmail(toEmail, "[Topviec] Thông báo thu hồi phân công tin tuyển dụng", htmlBody);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }
}