package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.response.dashboard.JobApplicationCount;
import com.topviec.topviec_be.dto.response.dashboard.PendingCandidateDTO;
import com.topviec.topviec_be.dto.response.dashboard.RecentJobSummary;
import com.topviec.topviec_be.dto.response.dashboard.ResManagerDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResOwnerDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResRecruiterDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResViewerDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.WeeklyApplicationStat;
import com.topviec.topviec_be.entity.Application;
import com.topviec.topviec_be.entity.CandidateProfile;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.enums.application.ApplicationStatus;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.ApplicationRepository;
import com.topviec.topviec_be.repository.CandidateProfileRepository;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.repository.CompanySubscriptionRepository;
import com.topviec.topviec_be.repository.InterviewRepository;
import com.topviec.topviec_be.repository.JobPostAssignmentRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.service.EmployerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployerDashboardServiceImpl implements EmployerDashboardService {

    private static final int WEEK_COUNT = 4;
    private static final int OWNER_RECENT_JOB_LIMIT = 5;
    private static final int TABLE_LIMIT = 10;

    private static final List<String> PENDING_AND_SEEN_STATUSES = List.of(
            ApplicationStatus.PENDING.getValue(),
            ApplicationStatus.SEEN.getValue());

    private static final List<String> MANAGER_STATUS_KEYS = List.of(
            ApplicationStatus.PENDING.getValue(),
            ApplicationStatus.SEEN.getValue(),
            ApplicationStatus.CV_PASSED.getValue(),
            ApplicationStatus.INTERVIEWING.getValue(),
            ApplicationStatus.HIRED.getValue(),
            ApplicationStatus.REJECTED.getValue());

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final CompanySubscriptionRepository companySubscriptionRepository;
    private final InterviewRepository interviewRepository;
    private final JobPostAssignmentRepository jobPostAssignmentRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @Override
    public ResOwnerDashboardDTO getOwnerDashboard(Long companyId) {
        DateRange currentMonth = currentMonthRange();

        return ResOwnerDashboardDTO.builder()
                .activeJobs(jobPostingRepository.countActiveByCompanyId(companyId))
                .newApplicationsThisMonth(applicationRepository.countByCompanyAndStatusesAndDateRange(
                        companyId, PENDING_AND_SEEN_STATUSES, currentMonth.start(), currentMonth.end()))
                .activeMembers(companyMemberRepository.countByCompanyIdAndStatusAndDeletedAtIsNull(companyId, "active"))
                .activeSubscriptions(companySubscriptionRepository.countByCompanyIdAndStatus(
                        companyId, SubscriptionStatus.ACTIVE))
                .weeklyApplications(buildWeeklyApplications(companyId))
                .recentJobs(buildRecentJobs(companyId))
                .build();
    }

    @Override
    public ResManagerDashboardDTO getManagerDashboard(Long companyId) {
        DateRange upcomingRange = nextSevenDaysRange();
        Map<String, Long> applicationsByStatus = buildApplicationsByStatus(companyId);

        return ResManagerDashboardDTO.builder()
                .activeJobs(jobPostingRepository.countActiveByCompanyId(companyId))
                .pendingApplications(applicationsByStatus.getOrDefault(ApplicationStatus.PENDING.getValue(), 0L))
                .upcomingInterviews(interviewRepository.countUpcomingByCompany(
                        companyId, upcomingRange.start(), upcomingRange.end()))
                .applicationsByStatus(applicationsByStatus)
                .pendingCandidates(buildPendingCandidatesForCompany(companyId))
                .build();
    }

    @Override
    public ResRecruiterDashboardDTO getRecruiterDashboard(Long companyId, Long userId) {
        MemberRole memberRole = getMemberRole(companyId, userId);
        boolean companyWide = memberRole == MemberRole.OWNER || memberRole == MemberRole.MANAGER;

        List<Long> jobPostIds = companyWide
                ? jobPostingRepository.findActiveIdsByCompanyId(companyId)
                : jobPostAssignmentRepository.findActiveAssignedJobPostIds(userId, companyId);

        DateRange upcomingRange = nextSevenDaysRange();
        long activeJobCount = companyWide ? jobPostingRepository.countActiveByCompanyId(companyId) : jobPostIds.size();

        return ResRecruiterDashboardDTO.builder()
                .assignedActiveJobs(activeJobCount)
                .pendingApplications(countApplicationsByJobPostIds(jobPostIds, PENDING_AND_SEEN_STATUSES))
                .upcomingInterviews(countUpcomingByJobPostIds(jobPostIds, upcomingRange))
                .applicationsByJob(buildApplicationsByJob(jobPostIds))
                .pendingCandidates(buildPendingCandidatesForJobPostIds(jobPostIds))
                .build();
    }

    @Override
    public ResViewerDashboardDTO getViewerDashboard(Long companyId) {
        DateRange currentMonth = currentMonthRange();

        return ResViewerDashboardDTO.builder()
                .activeJobs(jobPostingRepository.countActiveByCompanyId(companyId))
                .totalApplicationsThisMonth(applicationRepository.countByCompanyAndDateRange(
                        companyId, currentMonth.start(), currentMonth.end()))
                .build();
    }

    private List<WeeklyApplicationStat> buildWeeklyApplications(Long companyId) {
        LocalDate firstWeekStart = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .minusWeeks(WEEK_COUNT - 1L);
        LocalDateTime start = firstWeekStart.atStartOfDay();
        LocalDateTime end = firstWeekStart.plusWeeks(WEEK_COUNT).atStartOfDay();

        Map<Integer, Long> weeklyCounts = applicationRepository.countWeeklyByCompany(companyId, start, end)
                .stream()
                .collect(Collectors.toMap(
                        row -> toInt(row[0]),
                        row -> toLong(row[1]),
                        Long::sum,
                        LinkedHashMap::new));

        return IntStream.range(0, WEEK_COUNT)
                .mapToObj(index -> WeeklyApplicationStat.builder()
                        .weekLabel("Tuan " + (index + 1))
                        .count(weeklyCounts.getOrDefault(index, 0L))
                        .build())
                .toList();
    }

    private List<RecentJobSummary> buildRecentJobs(Long companyId) {
        List<JobPosting> jobs = jobPostingRepository.findRecentByCompany(
                companyId, PageRequest.of(0, OWNER_RECENT_JOB_LIMIT));
        Map<Long, Long> applicationCounts = countApplicationsByJobIds(jobs.stream()
                .map(JobPosting::getId)
                .collect(Collectors.toSet()));

        return jobs.stream()
                .map(job -> RecentJobSummary.builder()
                        .jobId(job.getId())
                        .title(job.getTitle())
                        .status(job.getStatus())
                        .applicationCount(applicationCounts.getOrDefault(job.getId(), 0L))
                        .createdAt(job.getCreatedAt())
                        .build())
                .toList();
    }

    private Map<String, Long> buildApplicationsByStatus(Long companyId) {
        Map<String, Long> result = new LinkedHashMap<>();
        MANAGER_STATUS_KEYS.forEach(status -> result.put(status, 0L));

        applicationRepository.countByCompanyGroupByStatus(companyId)
                .forEach(row -> {
                    String status = String.valueOf(row[0]);
                    if (result.containsKey(status)) {
                        result.put(status, toLong(row[1]));
                    }
                });

        return result;
    }

    private List<PendingCandidateDTO> buildPendingCandidatesForCompany(Long companyId) {
        List<Application> applications = applicationRepository.findPendingByCompany(
                companyId,
                PENDING_AND_SEEN_STATUSES,
                PageRequest.of(0, TABLE_LIMIT));
        return toPendingCandidateDTOs(applications);
    }

    private List<PendingCandidateDTO> buildPendingCandidatesForJobPostIds(List<Long> jobPostIds) {
        if (jobPostIds.isEmpty()) {
            return List.of();
        }
        List<Application> applications = applicationRepository.findPendingByJobPostIds(
                jobPostIds,
                PENDING_AND_SEEN_STATUSES,
                PageRequest.of(0, TABLE_LIMIT));
        return toPendingCandidateDTOs(applications);
    }

    private List<JobApplicationCount> buildApplicationsByJob(List<Long> jobPostIds) {
        if (jobPostIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> applicationCounts = countApplicationsByJobIds(jobPostIds);
        Map<Long, String> jobTitles = getJobTitles(jobPostIds);

        return jobPostIds.stream()
                .map(jobId -> JobApplicationCount.builder()
                        .jobId(jobId)
                        .jobTitle(jobTitles.get(jobId))
                        .applicationCount(applicationCounts.getOrDefault(jobId, 0L))
                        .build())
                .toList();
    }

    private List<PendingCandidateDTO> toPendingCandidateDTOs(List<Application> applications) {
        Map<Long, String> candidateNames = getCandidateNames(applications.stream()
                .map(Application::getCandidateUserId)
                .collect(Collectors.toSet()));

        return applications.stream()
                .map(application -> PendingCandidateDTO.builder()
                        .applicationId(application.getId())
                        .candidateName(resolveCandidateName(application, candidateNames))
                        .jobTitle(application.getJobPosting() != null ? application.getJobPosting().getTitle() : null)
                        .jobPostId(application.getJobPostId())
                        .status(application.getStatus())
                        .createdAt(application.getCreatedAt())
                        .build())
                .toList();
    }

    private long countApplicationsByJobPostIds(List<Long> jobPostIds, List<String> statuses) {
        if (jobPostIds.isEmpty()) {
            return 0L;
        }
        return applicationRepository.countByJobPostIdsAndStatuses(jobPostIds, statuses);
    }

    private long countUpcomingByJobPostIds(List<Long> jobPostIds, DateRange upcomingRange) {
        if (jobPostIds.isEmpty()) {
            return 0L;
        }
        return interviewRepository.countUpcomingByJobPostIds(
                jobPostIds, upcomingRange.start(), upcomingRange.end());
    }

    private Map<Long, Long> countApplicationsByJobIds(Collection<Long> jobPostIds) {
        List<Long> ids = jobPostIds.stream()
                .filter(Objects::nonNull)
                .toList();
        if (ids.isEmpty()) {
            return Map.of();
        }

        return applicationRepository.countByJobPostIdsGroupByJob(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> toLong(row[0]),
                        row -> toLong(row[1]),
                        Long::sum));
    }

    private Map<Long, String> getJobTitles(Collection<Long> jobPostIds) {
        Set<Long> ids = jobPostIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }

        return jobPostingRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(
                        JobPosting::getId,
                        JobPosting::getTitle,
                        (first, second) -> first));
    }

    private Map<Long, String> getCandidateNames(Collection<Long> candidateUserIds) {
        List<Long> ids = candidateUserIds.stream()
                .filter(Objects::nonNull)
                .toList();
        if (ids.isEmpty()) {
            return Map.of();
        }

        return candidateProfileRepository.findByUserIdIn(ids)
                .stream()
                .filter(profile -> profile.getDeletedAt() == null)
                .collect(Collectors.toMap(
                        CandidateProfile::getUserId,
                        CandidateProfile::getFullName,
                        (first, second) -> first));
    }

    private String resolveCandidateName(Application application, Map<Long, String> candidateNames) {
        String profileName = candidateNames.get(application.getCandidateUserId());
        if (profileName != null && !profileName.isBlank()) {
            return profileName;
        }
        return application.getCandidate() != null ? application.getCandidate().getEmail() : null;
    }

    private MemberRole getMemberRole(Long companyId, Long userId) {
        CompanyMember member = companyMemberRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> AppException.forbidden("User is not a company member."));
        return member.getMemberRole();
    }

    private DateRange currentMonthRange() {
        YearMonth currentMonth = YearMonth.now();
        return new DateRange(
                currentMonth.atDay(1).atStartOfDay(),
                currentMonth.plusMonths(1).atDay(1).atStartOfDay());
    }

    private DateRange nextSevenDaysRange() {
        LocalDateTime now = LocalDateTime.now();
        return new DateRange(now, now.plusDays(7));
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private record DateRange(LocalDateTime start, LocalDateTime end) {
    }
}
