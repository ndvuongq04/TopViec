package com.topviec.topviec_be.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.topviec.topviec_be.dto.response.ReminderInfo;

public interface TokenService {
    // Verify email
    String generateVerifyEmailToken(String email);

    String verifyEmailToken(String token);

    // Reset password
    String generateResetPasswordToken(String email);

    String verifyResetPasswordToken(String token);

    String resendVerifyEmailToken(String email);

    // Interview Slot Selection
    String generateInterviewSlotToken(Long applicationId, Long roundId, java.time.Duration ttl);

    String verifyInterviewSlotToken(String token);

    void invalidateInterviewSlotToken(String token);

    String generateInterviewUpdateToken(Long scheduleId, java.time.Duration ttl);

    String verifyInterviewUpdateToken(String token);

    void invalidateInterviewUpdateToken(String token);

    // Talent Pool Invite
    String generateTalentPoolInviteToken(Long applicationId, Long jobPostId, Duration ttl);

    String verifyTalentPoolInviteToken(String token);

    void invalidateTalentPoolInviteToken(String token);

    void storeReminderInfo(Long applicationId, Long roundId, LocalDateTime deadline, Duration ttl);

    ReminderInfo getReminderInfo(Long applicationId, Long roundId);

    void updateReminderInfo(Long applicationId, Long roundId, int count, LocalDateTime lastRemindedAt);

    void deleteReminderInfo(Long applicationId, Long roundId);

}