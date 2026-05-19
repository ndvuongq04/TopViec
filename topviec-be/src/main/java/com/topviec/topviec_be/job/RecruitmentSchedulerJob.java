package com.topviec.topviec_be.job;

import com.topviec.topviec_be.entity.*;
import com.topviec.topviec_be.enums.services.BrandingAddonStatus;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import com.topviec.topviec_be.service.activation.BrandingActivationService;
import com.topviec.topviec_be.enums.services.JobPostAddonStatus;
import com.topviec.topviec_be.repository.*;
import com.topviec.topviec_be.service.EmailService;
import com.topviec.topviec_be.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Các cron job định kỳ liên quan đến quy trình tuyển dụng.
 * Thêm method mới vào đây khi cần thêm job.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecruitmentSchedulerJob {

    private final ApplicationRepository applicationRepository;
    private final InterviewSlotInvitationRepository invitationRepository;
    private final InterviewRoundRepository roundRepository;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobPostAddonRepository jobPostAddonRepository;
    private final CompanyRepository companyRepository;
    private final CompanyBrandingRepository companyBrandingRepository;
    private final CompanySubscriptionRepository companySubscriptionRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Value("${app.slot-selection-url}")
    private String slotSelectionUrl;

    /**
     * Tự động chuyển application từ SCHEDULE_PENDING → OVERDUE
     * khi tất cả invitation của UV đã qua deadline.
     *
     * Dùng LocalDateTime.now() từ Java thay vì CURRENT_TIMESTAMP của DB
     * để tránh timezone mismatch.
     *
     * Chạy mỗi 5 phút, trễ tối đa 5 phút so với deadline thực tế.
     */
    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void markOverdueApplications() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[Scheduler] markOverdueApplications chạy lúc: {}", now);
        int updated = applicationRepository.bulkMarkOverdue(now);
        if (updated > 0) {
            log.info("[Scheduler] markOverdueApplications: chuyển {} application sang OVERDUE", updated);
        }
    }

    /**
     * Tự động gửi email nhắc nhở UV chọn slot PV khi deadline còn <= 1 ngày.
     *
     * Logic:
     * 1. Tìm các invitation có deadline trong khoảng [now, now + 1 ngày]
     * mà application vẫn ở trạng thái SCHEDULE_PENDING (UV chưa chọn).
     * 2. Kiểm tra reminderInfo trong Redis: nếu chưa nhắc lần nào thì gửi email.
     * 3. Tạo token mới với TTL = thời gian còn lại đến deadline.
     * 4. Gửi lại email chọn slot + cập nhật reminderCount trong Redis.
     *
     * Chạy mỗi giờ để đảm bảo bắt kịp các invitation sắp hết hạn.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void remindApproachingDeadline() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);

        log.info("[Scheduler] remindApproachingDeadline chạy lúc: {}", now);

        List<InterviewSlotInvitation> approachingInvitations = invitationRepository
                .findApproachingDeadlineInvitations(now, oneDayLater);

        if (approachingInvitations.isEmpty()) {
            return;
        }

        log.info("[Scheduler] Tìm thấy {} invitation sắp hết hạn cần nhắc nhở", approachingInvitations.size());

        int sentCount = 0;
        for (InterviewSlotInvitation invitation : approachingInvitations) {
            try {
                Long applicationId = invitation.getApplicationId();
                Long roundId = invitation.getRoundId();

                // Kiểm tra reminder trong Redis — chỉ gửi nếu chưa nhắc lần nào
                var reminderInfo = tokenService.getReminderInfo(applicationId, roundId);
                if (reminderInfo != null && reminderInfo.getReminderCount() != null
                        && reminderInfo.getReminderCount() > 0) {
                    log.debug("[Scheduler] Đã nhắc rồi, bỏ qua application={}, round={}", applicationId, roundId);
                    continue;
                }

                Application application = invitation.getApplication();
                if (application == null)
                    continue;

                User candidateUser = userRepository.findById(application.getCandidateUserId()).orElse(null);
                if (candidateUser == null)
                    continue;

                String candidateName = getCandidateName(application.getCandidateUserId());
                String candidateEmail = candidateUser.getEmail();

                InterviewRound round = roundRepository.findByIdAndDeletedAtIsNull(roundId).orElse(null);
                if (round == null)
                    continue;

                String jobTitle = "Vị trí ứng tuyển";
                String companyName = "Nhà tuyển dụng";
                JobPosting jobPosting = jobPostingRepository.findById(round.getJobPostId()).orElse(null);
                if (jobPosting != null) {
                    jobTitle = jobPosting.getTitle();
                    Company company = companyRepository.findById(jobPosting.getCompanyId()).orElse(null);
                    if (company != null)
                        companyName = company.getName();
                }

                String roundName = "Vòng " + round.getRoundNumber() + " - " + round.getRoundName();
                String deadlineStr = invitation.getDeadline()
                        .format(DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy"));

                Duration ttl = Duration.between(now, invitation.getDeadline());
                String token = tokenService.generateInterviewSlotToken(applicationId, roundId, ttl);
                String selectSlotLink = slotSelectionUrl + "?token=" + token;

                emailService.sendSlotSelectionEmail(candidateEmail, candidateName, companyName,
                        jobTitle, roundName, deadlineStr, selectSlotLink);

                // Cập nhật số lần nhắc + thời gian nhắc trong Redis
                tokenService.updateReminderInfo(applicationId, roundId, 1, now);

                sentCount++;
                log.info("📧 [Scheduler] Đã gửi email nhắc nhở chọn slot cho application={}, round={}",
                        applicationId, roundId);

            } catch (Exception e) {
                log.error("[Scheduler] Lỗi gửi email nhắc nhở cho invitation={}", invitation.getId(), e);
            }
        }

        if (sentCount > 0) {
            log.info("[Scheduler] remindApproachingDeadline: đã gửi {} email nhắc nhở", sentCount);
        }
    }

    /**
     * Tự động gỡ cờ HOT và đánh dấu EXPIRED cho các gói dịch vụ HOT đã hết thời hạn.
     * Chạy mỗi 5 phút.
     */
    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void expireHotJobPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<JobPostAddon> expiredAddons = jobPostAddonRepository.findExpiredAddons("JOB_POSTING_HOT", now);

        if (expiredAddons.isEmpty()) {
            return;
        }

        log.info("[Scheduler] expireHotJobPosts chạy lúc: {}, tìm thấy {} tin HOT hết hạn", now, expiredAddons.size());

        for (JobPostAddon addon : expiredAddons) {
            addon.setStatus(JobPostAddonStatus.EXPIRED);
            jobPostAddonRepository.save(addon);

            JobPosting jobPosting = addon.getJobPosting();
            if (jobPosting != null) {
                jobPosting.setIsHot(false);
                jobPostingRepository.save(jobPosting);
            }
        }

        log.info("[Scheduler] Đã gỡ cờ HOT cho {} tin tuyển dụng", expiredAddons.size());
    }

    /**
     * Tự động gỡ cờ URGENT và đánh dấu EXPIRED cho các gói dịch vụ TUYỂN GẤP đã hết thời hạn.
     * Chạy mỗi 5 phút.
     */
    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void expireUrgentJobPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<JobPostAddon> expiredAddons = jobPostAddonRepository.findExpiredAddons("JOB_POSTING_URGENT", now);

        if (expiredAddons.isEmpty()) {
            return;
        }

        log.info("[Scheduler] expireUrgentJobPosts chạy lúc: {}, tìm thấy {} tin TUYỂN GẤP hết hạn", now, expiredAddons.size());

        for (JobPostAddon addon : expiredAddons) {
            addon.setStatus(JobPostAddonStatus.EXPIRED);
            jobPostAddonRepository.save(addon);

            JobPosting jobPosting = addon.getJobPosting();
            if (jobPosting != null) {
                jobPosting.setIsUrgent(false);
                jobPostingRepository.save(jobPosting);
            }
        }

        log.info("[Scheduler] Đã gỡ cờ URGENT cho {} tin tuyển dụng", expiredAddons.size());
    }

    /**
     * Tự động gỡ các dịch vụ BRANDING đã hết hạn và reset cờ tương ứng trên Company.
     * Chạy mỗi 5 phút.
     */
    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void expireBrandingServices() {
        LocalDateTime now = LocalDateTime.now();
        expireBrandingByCode(BrandingActivationService.CODE_BANNER_HOME, now,
                company -> company.setIsBanner(false));
        expireBrandingByCode(BrandingActivationService.CODE_TOP_EMPLOYER, now,
                company -> company.setIsTopEmployer(false));
        expireBrandingByCode(BrandingActivationService.CODE_VERIFIED, now,
                company -> company.setIsBrandVerified(false));
    }

    private void expireBrandingByCode(String serviceCode, LocalDateTime now,
            java.util.function.Consumer<Company> flagResetter) {
        List<CompanyBranding> expired = companyBrandingRepository
                .findByServiceCodeAndStatusAndExpiredAtBefore(serviceCode, BrandingAddonStatus.ACTIVE, now);

        if (expired.isEmpty()) return;

        log.info("[Scheduler] Expire {} — {} bản ghi hết hạn", serviceCode, expired.size());

        for (CompanyBranding record : expired) {
            record.setStatus(BrandingAddonStatus.EXPIRED);
            companyBrandingRepository.save(record);
            companyRepository.findById(record.getCompanyId()).ifPresent(company -> {
                flagResetter.accept(company);
                companyRepository.save(company);
            });
        }
    }

    /**
     * Tự động chuyển subscription từ ACTIVE → EXPIRED khi đã quá ngày hết hạn.
     * Chạy mỗi giờ để đảm bảo trạng thái luôn chính xác.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<CompanySubscription> expired = companySubscriptionRepository
                .findExpiredActiveSubscriptions(SubscriptionStatus.ACTIVE, now);

        if (expired.isEmpty()) return;

        log.info("[Scheduler] expireSubscriptions: {} gói subscription đã hết hạn, đang chuyển sang EXPIRED", expired.size());

        for (CompanySubscription sub : expired) {
            try {
                sub.setStatus(SubscriptionStatus.EXPIRED);
                companySubscriptionRepository.save(sub);
                log.info("[Scheduler] Đã expire subscription id={}, companyId={}, expiredAt={}",
                        sub.getId(), sub.getCompanyId(), sub.getExpiredAt());
            } catch (Exception e) {
                log.error("[Scheduler] Lỗi khi expire subscription id={}", sub.getId(), e);
            }
        }
    }

    /**
     * Tự động gửi email nhắc nhở NTD khi gói subscription sắp hết hạn (trong 7 ngày).
     * Chạy mỗi ngày lúc 9h sáng. Mỗi subscription chỉ gửi 1 lần duy nhất.
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void remindSubscriptionExpiry() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        log.info("[Scheduler] remindSubscriptionExpiry chạy lúc: {}", now);

        // Query đã lọc reminderSentAt IS NULL — chỉ lấy subscription chưa được nhắc
        List<CompanySubscription> expiringSubs = companySubscriptionRepository
                .findByStatusAndExpiredAtBetween(SubscriptionStatus.ACTIVE, now, sevenDaysLater);

        if (expiringSubs.isEmpty()) {
            return;
        }

        log.info("[Scheduler] Tìm thấy {} gói subscription sắp hết hạn (chưa nhắc)", expiringSubs.size());

        int sentCount = 0;
        for (CompanySubscription sub : expiringSubs) {
            try {
                Company company = companyRepository.findById(sub.getCompanyId()).orElse(null);
                if (company == null) continue;

                ServicePackage pkg = servicePackageRepository.findById(sub.getServicePackageId()).orElse(null);
                String packageName = pkg != null ? pkg.getName() : "Gói dịch vụ";

                // Tìm email owner của công ty
                String ownerEmail = companyMemberRepository
                        .findOwnerByCompanyId(sub.getCompanyId())
                        .flatMap(member -> userRepository.findById(member.getUserId()))
                        .map(User::getEmail)
                        .orElse(company.getEmail());

                if (ownerEmail == null) continue;

                int daysRemaining = (int) ChronoUnit.DAYS.between(now.toLocalDate(), sub.getExpiredAt().toLocalDate());
                String expiredAtStr = sub.getExpiredAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                emailService.sendSubscriptionExpiryReminder(
                        ownerEmail, company.getName(), packageName, expiredAtStr, daysRemaining);

                // Đánh dấu đã gửi — không gửi lại lần nữa
                sub.setReminderSentAt(now);
                companySubscriptionRepository.save(sub);

                sentCount++;
                log.info("📧 [Scheduler] Đã gửi email nhắc gia hạn cho company={}, hết hạn={}",
                        sub.getCompanyId(), expiredAtStr);
            } catch (Exception e) {
                log.error("[Scheduler] Lỗi gửi email nhắc gia hạn cho subscription={}", sub.getId(), e);
            }
        }

        if (sentCount > 0) {
            log.info("[Scheduler] remindSubscriptionExpiry: đã gửi {} email nhắc nhở", sentCount);
        }
    }

    /**
     * Lấy tên hiển thị UV: ưu tiên fullName từ CandidateProfile, fallback email.
     */
    private String getCandidateName(Long candidateUserId) {
        return candidateProfileRepository.findByUserId(candidateUserId)
                .map(CandidateProfile::getFullName)
                .orElseGet(() -> userRepository.findById(candidateUserId)
                        .map(User::getEmail)
                        .orElse("Ứng viên"));
    }
}
