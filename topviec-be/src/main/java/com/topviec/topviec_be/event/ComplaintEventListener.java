package com.topviec.topviec_be.event;

import com.topviec.topviec_be.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintEventListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onComplaintAutoClosed(ComplaintAutoClosedEvent event) {
        try {
            emailService.sendComplaintAutoClosedEmail(
                    event.employerEmail(),
                    event.jobTitle(),
                    event.complaintType(),
                    event.reportCode());
        } catch (Exception e) {
            log.error("Failed to send auto-close email to {}: {}", event.employerEmail(), e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTalentPoolInvite(TalentPoolInviteEvent event) {
        try {
            emailService.sendTalentPoolInviteEmail(
                    event.candidateEmail(),
                    event.candidateName(),
                    event.companyName(),
                    event.jobTitle(),
                    event.jobLink());
        } catch (Exception e) {
            log.error("Failed to send talent pool invite email to {}: {}", event.candidateEmail(), e.getMessage(), e);
        }
    }
}
