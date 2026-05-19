package com.topviec.topviec_be.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.topviec.topviec_be.dto.response.ReminderInfo;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${app.token.verify-email-ttl:1440}")
    private long VERIFY_EMAIL_TTL;
    @Value("${app.token.reset-password-ttl:15}")
    private long RESET_PASSWORD_TTL;
    @Value("${app.token.verify-email-prefix:verify-email}")
    private String VERIFY_EMAIL_PREFIX;
    @Value("${app.token.reset-password-prefix:reset-password}")
    private String RESET_PASSWORD_PREFIX;

    @Value("${app.token.interview-reminder-prefix:interview:reminder:}")
    private String INTERVIEW_REMINDER_PREFIX;

    private final StringRedisTemplate redisTemplate;

    @Override
    public String generateVerifyEmailToken(String email) {
        String token = UUID.randomUUID().toString();
        String key = VERIFY_EMAIL_PREFIX + token;

        // Lưu vào Redis: key=token, value=email, TTL=24h
        redisTemplate.opsForValue()
                .set(key, email, Duration.ofMinutes(VERIFY_EMAIL_TTL));

        return token;
    }

    @Override
    public String verifyEmailToken(String token) {
        String key = VERIFY_EMAIL_PREFIX + token;
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw AppException.badRequest("Link xác thực đã hết hạn hoặc không hợp lệ");
        }

        redisTemplate.delete(key); // Xóa ngay sau khi dùng → không dùng lại được
        return email;
    }

    @Override
    public String generateResetPasswordToken(String email) {
        String token = UUID.randomUUID().toString();
        String key = RESET_PASSWORD_PREFIX + token;

        redisTemplate.opsForValue()
                .set(key, email, Duration.ofMinutes(RESET_PASSWORD_TTL));

        return token;
    }

    @Override
    public String verifyResetPasswordToken(String token) {
        String key = RESET_PASSWORD_PREFIX + token;
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw AppException.badRequest("Link đặt lại mật khẩu đã hết hạn hoặc không hợp lệ");
        }

        redisTemplate.delete(key); // Xóa sau khi dùng → không dùng lại được
        return email;
    }

    @Override
    public String resendVerifyEmailToken(String email) {
        // Tạo token mới → lưu Redis (token cũ tự hết hạn, không cần xóa thủ công)
        String token = UUID.randomUUID().toString();
        String key = VERIFY_EMAIL_PREFIX + token;

        redisTemplate.opsForValue()
                .set(key, email, Duration.ofMinutes(VERIFY_EMAIL_TTL));

        return token;
    }

    @Override
    public String generateInterviewSlotToken(Long applicationId, Long roundId, Duration ttl) {
        String token = UUID.randomUUID().toString();
        String key = "interview-slot:" + token;
        String payload = applicationId + ":" + roundId;

        // Lưu vào Redis với TTL động
        redisTemplate.opsForValue()
                .set(key, payload, ttl);

        return token;
    }

    @Override
    public String verifyInterviewSlotToken(String token) {
        String key = "interview-slot:" + token;
        String payload = redisTemplate.opsForValue().get(key);

        if (payload == null) {
            throw AppException.badRequest("Link chọn lịch phỏng vấn đã hết hạn hoặc không hợp lệ");
        }

        return payload;
    }

    @Override
    public void invalidateInterviewSlotToken(String token) {
        String key = "interview-slot:" + token;
        redisTemplate.delete(key);
    }

    @Override
    public String generateInterviewUpdateToken(Long scheduleId, Duration ttl) {
        String token = UUID.randomUUID().toString();
        String key = "interview-update:" + token;
        
        redisTemplate.opsForValue().set(key, scheduleId.toString(), ttl);
        return token;
    }

    @Override
    public String verifyInterviewUpdateToken(String token) {
        String key = "interview-update:" + token;
        String payload = redisTemplate.opsForValue().get(key);

        if (payload == null) {
            throw AppException.badRequest("Link xác nhận đã hết hạn hoặc không hợp lệ");
        }

        return payload; // which is scheduleId
    }

    @Override
    public void invalidateInterviewUpdateToken(String token) {
        String key = "interview-update:" + token;
        redisTemplate.delete(key);
    }

    @Override
    public String generateTalentPoolInviteToken(Long applicationId, Long jobPostId, Duration ttl) {
        String token = UUID.randomUUID().toString();
        String key = "talent-pool-invite:" + token;
        String payload = applicationId + ":" + jobPostId;
        redisTemplate.opsForValue().set(key, payload, ttl);
        return token;
    }

    @Override
    public String verifyTalentPoolInviteToken(String token) {
        String key = "talent-pool-invite:" + token;
        String payload = redisTemplate.opsForValue().get(key);
        if (payload == null) {
            throw AppException.badRequest("Link mời ứng tuyển đã hết hạn hoặc không hợp lệ");
        }
        return payload;
    }

    @Override
    public void invalidateTalentPoolInviteToken(String token) {
        redisTemplate.delete("talent-pool-invite:" + token);
    }

    @Override
    public void storeReminderInfo(Long applicationId, Long roundId, LocalDateTime deadline, Duration ttl) {
        String key = INTERVIEW_REMINDER_PREFIX + applicationId + ":" + roundId;
        // format: reminderCount:lastRemindedAt:deadline
        String value = "0::" + deadline.toString();
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public ReminderInfo getReminderInfo(Long applicationId, Long roundId) {
        String key = INTERVIEW_REMINDER_PREFIX + applicationId + ":" + roundId;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null)
            return null;

        String[] parts = value.split(":", 3);
        return ReminderInfo.builder()
                .reminderCount(Integer.parseInt(parts[0]))
                .lastRemindedAt(parts[1].isBlank() ? null : LocalDateTime.parse(parts[1]))
                .deadline(parts[2].isBlank() ? null : LocalDateTime.parse(parts[2]))
                .build();
    }

    @Override
    public void updateReminderInfo(Long applicationId, Long roundId, int count, LocalDateTime lastRemindedAt) {
        String key = INTERVIEW_REMINDER_PREFIX + applicationId + ":" + roundId;
        String existing = redisTemplate.opsForValue().get(key);
        if (existing == null)
            return;

        String[] parts = existing.split(":", 3);
        String deadline = parts[2];
        String updated = count + ":" + lastRemindedAt.toString() + ":" + deadline;

        // Giữ nguyên TTL còn lại
        Long ttlSeconds = redisTemplate.getExpire(key);
        if (ttlSeconds != null && ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, updated, Duration.ofSeconds(ttlSeconds));
        }
    }

    @Override
    public void deleteReminderInfo(Long applicationId, Long roundId) {
        String key = INTERVIEW_REMINDER_PREFIX + applicationId + ":" + roundId;
        redisTemplate.delete(key);
    }
}
