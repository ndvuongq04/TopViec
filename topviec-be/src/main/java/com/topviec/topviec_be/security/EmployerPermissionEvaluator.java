package com.topviec.topviec_be.security;

import com.topviec.topviec_be.entity.Application;
import com.topviec.topviec_be.entity.Interview;
import com.topviec.topviec_be.entity.InterviewRound;
import com.topviec.topviec_be.entity.JobPostAssignment;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.repository.ApplicationRepository;
import com.topviec.topviec_be.repository.InterviewRepository;
import com.topviec.topviec_be.repository.InterviewRoundRepository;
import com.topviec.topviec_be.repository.JobPostAssignmentRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.service.CompanyMemberService;
import com.topviec.topviec_be.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("employerPerm")
@Slf4j
@RequiredArgsConstructor
public class EmployerPermissionEvaluator {

    private final CompanyService companyService;
    private final CompanyMemberService companyMemberService;
    private final JobPostingRepository jobPostingRepository;
    private final JobPostAssignmentRepository jobPostAssignmentRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRoundRepository interviewRoundRepository;
    private final InterviewRepository interviewRepository;

    // Check quyền thao tác trên job theo ngữ cảnh "tin của mình" hoặc "tin của người khác".
    public boolean canManageJob(Authentication auth, Long jobPostId, String ownAction, String otherAction) {
        JobContext context = resolveJobContext(auth, jobPostId);
        if (context == null) {
            return false;
        }

        String action = context.jobPosting().getCreatedByUserId().equals(context.userContext().userId())
                ? ownAction
                : otherAction;
        return hasPermission(context.userContext(), action);
    }

    // Check quyền xem danh sách CV/apply của 1 job theo assignment hiện tại của job đó.
    public boolean canViewApplications(Authentication auth, Long jobPostId) {
        return hasCvAccess(auth, jobPostId);
    }

    // Check quyền xem 1 application bằng cách quy về jobPostId của application đó.
    public boolean canViewApplication(Authentication auth, Long applicationId) {
        Long jobPostId = resolveJobPostIdFromApplication(applicationId);
        return jobPostId != null && hasCvAccess(auth, jobPostId);
    }

    // Check quyền cập nhật application: phải xem được CV của job và có thêm action cv:classify.
    public boolean canUpdateApplication(Authentication auth, Long applicationId) {
        Long jobPostId = resolveJobPostIdFromApplication(applicationId);
        return jobPostId != null && hasJobScopedCvPermission(auth, jobPostId, "cv:classify");
    }

    // Check quyền tạo/sửa flow interview ở cấp job: bám theo assignment của job và action cv:invite_interview.
    public boolean canInviteInterviewForJob(Authentication auth, Long jobPostId) {
        return hasJobScopedCvPermission(auth, jobPostId, "cv:invite_interview");
    }

    // Check quyền interview ở cấp round bằng cách quy round về jobPostId gốc.
    public boolean canInviteInterviewForRound(Authentication auth, Long roundId) {
        Long jobPostId = resolveJobPostIdFromRound(roundId);
        return jobPostId != null && canInviteInterviewForJob(auth, jobPostId);
    }

    // Check quyền interview ở cấp schedule bằng cách quy schedule -> round -> jobPostId.
    public boolean canInviteInterviewForSchedule(Authentication auth, Long scheduleId) {
        Long jobPostId = resolveJobPostIdFromSchedule(scheduleId);
        return jobPostId != null && canInviteInterviewForJob(auth, jobPostId);
    }

    // Check quyền ghi nhận kết quả interview ở cấp job: bám theo assignment và action cv:record_interview.
    public boolean canRecordInterviewForJob(Authentication auth, Long jobPostId) {
        return hasJobScopedCvPermission(auth, jobPostId, "cv:record_interview");
    }

    // Check quyền ghi nhận kết quả ở cấp schedule bằng cách quy schedule -> round -> jobPostId.
    public boolean canRecordInterviewForSchedule(Authentication auth, Long scheduleId) {
        Long jobPostId = resolveJobPostIdFromSchedule(scheduleId);
        return jobPostId != null && canRecordInterviewForJob(auth, jobPostId);
    }

    // Check quyền ghi nhận kết quả ở cấp application bằng cách quy application về jobPostId.
    public boolean canRecordInterviewForApplication(Authentication auth, Long applicationId) {
        Long jobPostId = resolveJobPostIdFromApplication(applicationId);
        return jobPostId != null && canRecordInterviewForJob(auth, jobPostId);
    }

    // Check action tác nghiệp trên CV/interview sau khi đã qua rule assigned/unassigned của job.
    private boolean hasJobScopedCvPermission(Authentication auth, Long jobPostId, String action) {
        JobContext context = resolveJobContext(auth, jobPostId);
        return context != null
                && hasCvAccess(context)
                && hasPermission(context.userContext(), action);
    }

    // Check quyền xem CV/apply/interview theo rule assignee hiện tại của job.
    private boolean hasCvAccess(Authentication auth, Long jobPostId) {
        JobContext context = resolveJobContext(auth, jobPostId);
        return context != null && hasCvAccess(context);
    }

    // Rule gốc cho apply/interview: assignee dùng cv:view_assigned, còn lại dùng cv:view_unassigned.
    private boolean hasCvAccess(JobContext context) {
        JobPostAssignment assignment = jobPostAssignmentRepository
                .findByJobPostIdAndRevokedAtIsNull(context.jobPosting().getId())
                .orElse(null);

        String action = (assignment != null && assignment.getUserId().equals(context.userContext().userId()))
                ? "cv:view_assigned"
                : "cv:view_unassigned";

        return hasPermission(context.userContext(), action);
    }

    // Resolve user context + job posting và đảm bảo job thuộc đúng company hiện tại.
    private JobContext resolveJobContext(Authentication auth, Long jobPostId) {
        UserContext userContext = resolveContext(auth);
        if (userContext == null) {
            return null;
        }

        JobPosting jobPosting = jobPostingRepository.findById(jobPostId).orElse(null);
        if (jobPosting == null || !jobPosting.getCompanyId().equals(userContext.companyId())) {
            return null;
        }

        return new JobContext(userContext, jobPosting);
    }

    // Quy application về jobPostId để dùng chung toàn bộ rule theo job scope.
    private Long resolveJobPostIdFromApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId).orElse(null);
        return application != null ? application.getJobPostId() : null;
    }

    // Quy round về jobPostId để dùng chung toàn bộ rule theo job scope.
    private Long resolveJobPostIdFromRound(Long roundId) {
        InterviewRound round = interviewRoundRepository.findByIdAndDeletedAtIsNull(roundId).orElse(null);
        return round != null ? round.getJobPostId() : null;
    }

    // Quy schedule về jobPostId qua interview -> round để dùng chung rule theo job scope.
    private Long resolveJobPostIdFromSchedule(Long scheduleId) {
        Interview interview = interviewRepository.findByIdAndDeletedAtIsNull(scheduleId).orElse(null);
        return interview != null && interview.getRound() != null ? interview.getRound().getJobPostId() : null;
    }

    // Wrapper gọi về CompanyMemberService để lấy effective permission của member trong công ty.
    private boolean hasPermission(UserContext context, String action) {
        return hasPermission(context.companyId(), context.userId(), action);
    }

    // Wrapper gọi về CompanyMemberService để lấy effective permission của member trong công ty.
    private boolean hasPermission(Long companyId, Long userId, String action) {
        try {
            return companyMemberService.hasPermission(companyId, userId, action);
        } catch (Exception e) {
            log.error("Error checking employer permission [{}]: {}", action, e.getMessage());
            return false;
        }
    }

    // Lấy userId từ JWT và resolve companyId hiện tại để các hàm check dùng chung.
    private UserContext resolveContext(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        try {
            Long userId = Long.parseLong(jwt.getSubject());
            Long companyId = companyService.getCompanyIdByUserId(userId);
            return new UserContext(userId, companyId);
        } catch (Exception e) {
            log.error("Error resolving employer context: {}", e.getMessage());
            return null;
        }
    }

    private record UserContext(Long userId, Long companyId) {
    }

    private record JobContext(UserContext userContext, JobPosting jobPosting) {
    }
}
