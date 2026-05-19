package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.response.dashboard.ActionableOrder;
import com.topviec.topviec_be.dto.response.dashboard.DailyModerationStat;
import com.topviec.topviec_be.dto.response.dashboard.DailyUserGrowth;
import com.topviec.topviec_be.dto.response.dashboard.MonthlyRevenue;
import com.topviec.topviec_be.dto.response.dashboard.OldestPendingJob;
import com.topviec.topviec_be.dto.response.dashboard.RecentAdminActivity;
import com.topviec.topviec_be.dto.response.dashboard.ResContentModeratorDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResFinanceAdminDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResSuperAdminDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.ResSupportAdminDashboardDTO;
import com.topviec.topviec_be.dto.response.dashboard.UrgentComplaint;
import com.topviec.topviec_be.entity.AdminUser;
import com.topviec.topviec_be.entity.AuditLog;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.Complaint;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.Order;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.company.VerificationStatus;
import com.topviec.topviec_be.enums.complaints.AppealStatus;
import com.topviec.topviec_be.enums.complaints.ComplaintStatus;
import com.topviec.topviec_be.enums.jobs.JobPostStatus;
import com.topviec.topviec_be.enums.services.OrderStatus;
import com.topviec.topviec_be.enums.users.UserStatus;
import com.topviec.topviec_be.enums.users.UserType;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.repository.AuditLogRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.ComplaintAppealRepository;
import com.topviec.topviec_be.repository.ComplaintRepository;
import com.topviec.topviec_be.repository.EmployerViolationScoreRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.OrderRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final int USER_GROWTH_DAYS = 30;
    private static final int MODERATION_DAYS = 7;
    private static final int REVENUE_MONTHS = 6;
    private static final int TABLE_LIMIT = 10;
    private static final int RESTRICTED_EMPLOYER_SCORE = 20;

    private static final List<String> OPEN_COMPLAINT_STATUSES = List.of(
            ComplaintStatus.PENDING.getValue(),
            ComplaintStatus.PROCESSING.getValue());

    private static final List<String> ALL_COMPLAINT_STATUSES = List.of(
            ComplaintStatus.PENDING.getValue(),
            ComplaintStatus.PROCESSING.getValue(),
            ComplaintStatus.WAITING_EMPLOYER.getValue(),
            ComplaintStatus.RESOLVED.getValue(),
            ComplaintStatus.REJECTED.getValue(),
            ComplaintStatus.AUTO_CLOSED.getValue());

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;
    private final OrderRepository orderRepository;
    private final AuditLogRepository auditLogRepository;
    private final AdminUserRepository adminUserRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintAppealRepository complaintAppealRepository;
    private final EmployerViolationScoreRepository employerViolationScoreRepository;

    @Override
    public ResSuperAdminDashboardDTO getSuperAdminDashboard() {
        DateRange currentMonth = currentMonthRange();

        long totalActiveUsers = userRepository.countByUserTypeAndStatusAndDeletedAtIsNull(
                UserType.CANDIDATE, UserStatus.ACTIVE)
                + userRepository.countByUserTypeAndStatusAndDeletedAtIsNull(UserType.EMPLOYER, UserStatus.ACTIVE);

        return ResSuperAdminDashboardDTO.builder()
                .totalActiveUsers(totalActiveUsers)
                .totalActiveCompanies(companyRepository.countByStatusAndDeletedAtIsNull(CompanyStatus.ACTIVE.getValue()))
                .totalPublishedJobs(jobPostingRepository.countActiveJobs())
                .monthlyRevenue(sumPaidRevenue(currentMonth))
                .userGrowth(buildUserGrowth())
                .revenueByMonth(buildRevenueByMonth())
                .recentActivities(buildRecentAdminActivities())
                .build();
    }

    @Override
    public ResContentModeratorDashboardDTO getContentModeratorDashboard() {
        DateRange currentMonth = currentMonthRange();

        return ResContentModeratorDashboardDTO.builder()
                .pendingApprovalJobs(jobPostingRepository.countByStatusAndDeletedAtIsNull(
                        JobPostStatus.PENDING_APPROVAL.getValue()))
                .pendingVerifyCompanies(companyRepository.countByVerificationStatusAndDeletedAtIsNull(
                        VerificationStatus.PENDING.getValue()))
                .rejectedJobsThisMonth(jobPostingRepository.countRejectedInDateRange(
                        currentMonth.start(), currentMonth.end()))
                .moderationStats(buildModerationStats())
                .oldestPendingJobs(buildOldestPendingJobs())
                .build();
    }

    @Override
    public ResSupportAdminDashboardDTO getSupportAdminDashboard() {
        return ResSupportAdminDashboardDTO.builder()
                .pendingComplaints(complaintRepository.countByStatusIn(OPEN_COMPLAINT_STATUSES))
                .pendingAppeals(complaintAppealRepository.countByStatus(AppealStatus.PENDING.getValue()))
                .restrictedEmployers(employerViolationScoreRepository
                        .countByTotalScoreGreaterThanEqual(RESTRICTED_EMPLOYER_SCORE))
                .complaintsByStatus(buildComplaintsByStatus())
                .urgentComplaints(buildUrgentComplaints())
                .build();
    }

    @Override
    public ResFinanceAdminDashboardDTO getFinanceAdminDashboard() {
        DateRange currentMonth = currentMonthRange();

        return ResFinanceAdminDashboardDTO.builder()
                .monthlyRevenue(sumPaidRevenue(currentMonth))
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .refundRequests(orderRepository.countByStatus(OrderStatus.REFUND_REQUESTED))
                .revenueByMonth(buildRevenueByMonth())
                .actionableOrders(buildActionableOrders())
                .build();
    }

    private List<DailyUserGrowth> buildUserGrowth() {
        LocalDate startDate = LocalDate.now().minusDays(USER_GROWTH_DAYS - 1L);
        LocalDate endDateExclusive = LocalDate.now().plusDays(1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDateExclusive.atStartOfDay();

        Map<LocalDate, Long> candidateCounts = toDailyCountMap(
                userRepository.countNewUsersByTypeAndDateRange(UserType.CANDIDATE, start, end));
        Map<LocalDate, Long> employerCounts = toDailyCountMap(
                userRepository.countNewUsersByTypeAndDateRange(UserType.EMPLOYER, start, end));

        return startDate.datesUntil(endDateExclusive)
                .map(date -> DailyUserGrowth.builder()
                        .date(date)
                        .candidateCount(candidateCounts.getOrDefault(date, 0L))
                        .employerCount(employerCounts.getOrDefault(date, 0L))
                        .build())
                .toList();
    }

    private List<MonthlyRevenue> buildRevenueByMonth() {
        YearMonth firstMonth = YearMonth.now().minusMonths(REVENUE_MONTHS - 1L);
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        LocalDateTime start = firstMonth.atDay(1).atStartOfDay();
        LocalDateTime end = nextMonth.atDay(1).atStartOfDay();

        Map<String, BigDecimal> revenueByMonth = orderRepository
                .sumRevenueGroupByMonth(OrderStatus.PAID, start, end)
                .stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> toBigDecimal(row[1]),
                        BigDecimal::add,
                        LinkedHashMap::new));

        return IntStream.range(0, REVENUE_MONTHS)
                .mapToObj(firstMonth::plusMonths)
                .map(YearMonth::toString)
                .map(month -> MonthlyRevenue.builder()
                        .month(month)
                        .totalAmount(revenueByMonth.getOrDefault(month, BigDecimal.ZERO))
                        .build())
                .toList();
    }

    private List<RecentAdminActivity> buildRecentAdminActivities() {
        List<Long> adminUserIds = adminUserRepository.findAllActiveAdminUserIds();
        if (adminUserIds.isEmpty()) {
            return List.of();
        }

        Map<Long, String> adminNames = adminUserRepository.findByUserIdIn(adminUserIds)
                .stream()
                .filter(admin -> admin.getUser() != null && admin.getUser().getId() != null)
                .collect(Collectors.toMap(
                        admin -> admin.getUser().getId(),
                        AdminUser::getFullName,
                        (first, second) -> first));

        return auditLogRepository.findRecentByAdminUserIds(adminUserIds, PageRequest.of(0, TABLE_LIMIT))
                .stream()
                .map(log -> toRecentAdminActivity(log, adminNames))
                .toList();
    }

    private List<DailyModerationStat> buildModerationStats() {
        LocalDate startDate = LocalDate.now().minusDays(MODERATION_DAYS - 1L);
        LocalDate endDateExclusive = LocalDate.now().plusDays(1);

        Map<LocalDate, Long> approvedCounts = new LinkedHashMap<>();
        Map<LocalDate, Long> rejectedCounts = new LinkedHashMap<>();

        jobPostingRepository.countModerationByDateAndStatus(
                startDate.atStartOfDay(),
                endDateExclusive.atStartOfDay())
                .forEach(row -> {
                    LocalDate date = toLocalDate(row[0]);
                    String status = String.valueOf(row[1]);
                    long count = toLong(row[2]);
                    if (JobPostStatus.PUBLISHED.getValue().equals(status)) {
                        approvedCounts.put(date, count);
                    } else if (JobPostStatus.REJECTED.getValue().equals(status)) {
                        rejectedCounts.put(date, count);
                    }
                });

        return startDate.datesUntil(endDateExclusive)
                .map(date -> DailyModerationStat.builder()
                        .date(date)
                        .approvedCount(approvedCounts.getOrDefault(date, 0L))
                        .rejectedCount(rejectedCounts.getOrDefault(date, 0L))
                        .build())
                .toList();
    }

    private List<OldestPendingJob> buildOldestPendingJobs() {
        List<JobPosting> jobs = jobPostingRepository.findOldestPendingJobs(PageRequest.of(0, TABLE_LIMIT));
        Map<Long, String> companyNames = getCompanyNames(jobs.stream()
                .map(JobPosting::getCompanyId)
                .collect(Collectors.toSet()));
        LocalDateTime now = LocalDateTime.now();

        return jobs.stream()
                .map(job -> OldestPendingJob.builder()
                        .jobId(job.getId())
                        .title(job.getTitle())
                        .companyName(companyNames.get(job.getCompanyId()))
                        .createdAt(job.getCreatedAt())
                        .waitingDays(elapsed(job.getCreatedAt(), now, ChronoUnit.DAYS))
                        .build())
                .toList();
    }

    private Map<String, Long> buildComplaintsByStatus() {
        Map<String, Long> result = new LinkedHashMap<>();
        ALL_COMPLAINT_STATUSES.forEach(status -> result.put(status, 0L));

        complaintRepository.countGroupByStatus()
                .forEach(row -> result.put(String.valueOf(row[0]), toLong(row[1])));

        return result;
    }

    private List<UrgentComplaint> buildUrgentComplaints() {
        LocalDateTime now = LocalDateTime.now();
        return complaintRepository.findUrgentComplaints(PageRequest.of(0, TABLE_LIMIT))
                .stream()
                .map(row -> {
                    Complaint complaint = (Complaint) row[0];
                    String companyName = row[2] != null ? String.valueOf(row[2]) : null;
                    return UrgentComplaint.builder()
                            .complaintId(complaint.getId())
                            .reportCode("RPT-" + complaint.getId())
                            .priority(complaint.getPriority())
                            .complaintType(complaint.getComplaintType())
                            .companyName(companyName)
                            .waitingHours(elapsed(complaint.getCreatedAt(), now, ChronoUnit.HOURS))
                            .createdAt(complaint.getCreatedAt())
                            .build();
                })
                .toList();
    }

    private List<ActionableOrder> buildActionableOrders() {
        List<Order> orders = orderRepository.findActionableOrders(
                List.of(OrderStatus.PENDING, OrderStatus.REFUND_REQUESTED),
                PageRequest.of(0, TABLE_LIMIT));
        Map<Long, String> companyNames = getCompanyNames(orders.stream()
                .map(Order::getCompanyId)
                .collect(Collectors.toSet()));

        return orders.stream()
                .map(order -> ActionableOrder.builder()
                        .orderId(order.getId())
                        .orderCode(order.getOrderCode())
                        .companyName(companyNames.get(order.getCompanyId()))
                        .totalAmount(order.getTotalAmount())
                        .status(order.getStatus() != null ? order.getStatus().getValue() : null)
                        .createdAt(order.getCreatedAt())
                        .build())
                .toList();
    }

    private RecentAdminActivity toRecentAdminActivity(AuditLog log, Map<Long, String> adminNames) {
        return RecentAdminActivity.builder()
                .adminName(adminNames.getOrDefault(log.getUserId(), "Unknown Admin"))
                .action(log.getAction())
                .targetEntity(log.getTargetEntity())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private BigDecimal sumPaidRevenue(DateRange range) {
        BigDecimal total = orderRepository.sumPaidRevenueInDateRange(
                OrderStatus.PAID, range.start(), range.end());
        return total != null ? total : BigDecimal.ZERO;
    }

    private Map<Long, String> getCompanyNames(Collection<Long> companyIds) {
        Set<Long> ids = companyIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }

        return companyRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(
                        Company::getId,
                        Company::getName,
                        (first, second) -> first));
    }

    private Map<LocalDate, Long> toDailyCountMap(List<Object[]> rows) {
        return rows.stream()
                .collect(Collectors.toMap(
                        row -> toLocalDate(row[0]),
                        row -> toLong(row[1]),
                        Long::sum,
                        LinkedHashMap::new));
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toLocalDate();
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        if (value instanceof java.util.Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.parse(String.valueOf(value));
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

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal amount) {
            return amount;
        }
        return new BigDecimal(String.valueOf(value));
    }

    private long elapsed(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        if (start == null || end == null) {
            return 0L;
        }
        return Math.max(0L, unit.between(start, end));
    }

    private DateRange currentMonthRange() {
        YearMonth currentMonth = YearMonth.now();
        return new DateRange(
                currentMonth.atDay(1).atStartOfDay(),
                currentMonth.plusMonths(1).atDay(1).atStartOfDay());
    }

    private record DateRange(LocalDateTime start, LocalDateTime end) {
    }
}
