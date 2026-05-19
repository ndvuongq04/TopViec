package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqAdjustViolationScoreDTO;
import com.topviec.topviec_be.dto.request.ReqResetViolationScoreDTO;
import com.topviec.topviec_be.dto.response.ResMyViolationScoreDTO;
import com.topviec.topviec_be.dto.response.ResViolationScoreDTO;
import com.topviec.topviec_be.entity.AdminUser;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.EmployerViolationScore;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.entity.ViolationLog;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.complaints.ViolationSource;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AdminUserRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.EmployerViolationScoreRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.repository.ViolationLogRepository;
import com.topviec.topviec_be.service.ViolationScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViolationScoreServiceImpl implements ViolationScoreService {

    private static final int LIMITED_SCORE_THRESHOLD = 20;
    private static final int SUSPENDED_SCORE_THRESHOLD = 50;

    private final EmployerViolationScoreRepository violationScoreRepository;
    private final ViolationLogRepository violationLogRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;

    @Override
    @Transactional(readOnly = true)
    public ResViolationScoreDTO getScore(Long employerId) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));

        EmployerViolationScore score = violationScoreRepository.findByEmployerId(employerId)
                .orElseGet(() -> EmployerViolationScore.builder()
                        .employerId(employerId)
                        .totalScore(0)
                        .build());

        Company company = companyRepository.findByUserId(employerId).orElse(null);

        List<ViolationLog> logs = violationLogRepository.findByEmployerIdOrderByCreatedAtDesc(employerId);

        // Batch load admin names for logs
        List<Long> adminIds = logs.stream()
                .map(ViolationLog::getCreatedBy)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, AdminUser> adminMap = adminIds.isEmpty()
                ? Collections.emptyMap()
                : adminUserRepository.findAllById(adminIds).stream()
                        .collect(Collectors.toMap(AdminUser::getAdminUsersId, a -> a));

        // Find admin name for resetBy
        String resetByAdminName = null;
        if (score.getResetBy() != null) {
            AdminUser resetAdmin = adminMap.get(score.getResetBy());
            if (resetAdmin == null) {
                resetAdmin = adminUserRepository.findById(score.getResetBy()).orElse(null);
            }
            resetByAdminName = resetAdmin != null ? resetAdmin.getFullName() : null;
        }

        int totalScore = score.getTotalScore() != null ? score.getTotalScore() : 0;

        return ResViolationScoreDTO.builder()
                .employerId(employerId)
                .employerEmail(employer.getEmail())
                .company(company == null ? null : ResViolationScoreDTO.CompanyInfo.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .logoUrl(company.getLogoUrl())
                        .status(company.getStatus())
                        .build())
                .score(ResViolationScoreDTO.ScoreInfo.builder()
                        .totalScore(totalScore)
                        .scoreLevel(calculateScoreLevel(totalScore))
                        .lastGroupBViolationAt(score.getLastGroupBViolationAt())
                        .lastResetAt(score.getLastResetAt())
                        .resetByAdminName(resetByAdminName)
                        .canResetScore(canResetScore(score.getLastGroupBViolationAt()))
                        .build())
                .history(logs.stream()
                        .map(log -> {
                            AdminUser creator = adminMap.get(log.getCreatedBy());
                            return ResViolationScoreDTO.ViolationLogInfo.builder()
                                    .id(log.getId())
                                    .violationType(log.getViolationType())
                                    .points(log.getPoints())
                                    .source(log.getSource())
                                    .complaintId(log.getComplaintId())
                                    .note(log.getNote())
                                    .createdByAdminName(creator != null ? creator.getFullName() : null)
                                    .createdAt(log.getCreatedAt())
                                    .build();
                        })
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public ResViolationScoreDTO resetScore(Long adminUserId, Long employerId, ReqResetViolationScoreDTO request) {
        AdminUser adminUser = adminUserRepository.findActiveByUserId(adminUserId)
                .orElseThrow(() -> AppException.forbidden("Không tìm thấy tài khoản admin hợp lệ"));

        userRepository.findById(employerId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));

        EmployerViolationScore score = violationScoreRepository.findByEmployerId(employerId)
                .orElseGet(() -> EmployerViolationScore.builder()
                        .employerId(employerId)
                        .totalScore(0)
                        .build());

        if (!canResetScore(score.getLastGroupBViolationAt())) {
            throw AppException.badRequest(
                    "Không đủ điều kiện reset: NTD vẫn trong thời gian theo dõi 6 tháng sau vi phạm nhóm B gần nhất");
        }

        score.setTotalScore(0);
        score.setLastGroupBViolationAt(null);
        score.setLastResetAt(LocalDateTime.now());
        score.setResetBy(adminUser.getAdminUsersId());
        violationScoreRepository.save(score);

        // Sync company violation score
        companyRepository.findByUserId(employerId).ifPresent(company -> {
            company.setViolationScore(0);
            company.setUpdatedBy(adminUserId);
            companyRepository.save(company);
        });

        return getScore(employerId);
    }

    @Override
    @Transactional
    public ResViolationScoreDTO adjustScore(Long adminUserId, Long employerId, ReqAdjustViolationScoreDTO request) {
        AdminUser adminUser = adminUserRepository.findActiveByUserId(adminUserId)
                .orElseThrow(() -> AppException.forbidden("Không tìm thấy tài khoản admin hợp lệ"));

        userRepository.findById(employerId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));

        EmployerViolationScore score = violationScoreRepository.findByEmployerId(employerId)
                .orElseGet(() -> EmployerViolationScore.builder()
                        .employerId(employerId)
                        .totalScore(0)
                        .build());

        int currentScore = score.getTotalScore() != null ? score.getTotalScore() : 0;
        int newScore = currentScore - request.getPointsToDecrease();
        if (newScore < 0) {
            throw AppException.badRequest(
                    "Không thể giảm quá tổng điểm hiện tại. Điểm hiện tại: " + currentScore);
        }

        score.setTotalScore(newScore);
        violationScoreRepository.save(score);

        // Record adjustment in violation log (negative points = giảm điểm)
        violationLogRepository.save(ViolationLog.builder()
                .employerId(employerId)
                .jobPostId(0L) // không liên quan job post cụ thể
                .violationType("score_adjustment")
                .points(-request.getPointsToDecrease())
                .source(ViolationSource.ADMIN.getValue())
                .note(request.getNote())
                .createdBy(adminUser.getAdminUsersId())
                .build());

        // Sync company violation score
        companyRepository.findByUserId(employerId).ifPresent(company -> {
            company.setViolationScore(newScore);
            company.setUpdatedBy(adminUserId);
            companyRepository.save(company);
        });

        return getScore(employerId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResMyViolationScoreDTO getMyScore(Long employerUserId) {
        userRepository.findById(employerUserId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));

        EmployerViolationScore score = violationScoreRepository.findByEmployerId(employerUserId)
                .orElseGet(() -> EmployerViolationScore.builder()
                        .employerId(employerUserId)
                        .totalScore(0)
                        .build());

        Company company = companyRepository.findByUserId(employerUserId).orElse(null);
        int totalScore = score.getTotalScore() != null ? score.getTotalScore() : 0;
        String level = calculateScoreLevel(totalScore);

        return ResMyViolationScoreDTO.builder()
                .totalScore(totalScore)
                .scoreLevel(level)
                .restrictionDescription(buildRestrictionDescription(level))
                .lastGroupBViolationAt(score.getLastGroupBViolationAt())
                .canRequestReset(canResetScore(score.getLastGroupBViolationAt()))
                .companyStatus(company != null ? company.getStatus() : null)
                .build();
    }

    @Override
    @Transactional
    public void autoResetEligibleScores() {
        List<EmployerViolationScore> scores = violationScoreRepository.findByTotalScoreGreaterThan(0);
        LocalDateTime now = LocalDateTime.now();

        for (EmployerViolationScore score : scores) {
            if (!canResetScore(score.getLastGroupBViolationAt())) {
                continue;
            }

            int currentScore = score.getTotalScore() != null ? score.getTotalScore() : 0;

            score.setTotalScore(0);
            score.setLastGroupBViolationAt(null);
            score.setLastResetAt(now);
            score.setResetBy(null);
            violationScoreRepository.save(score);

            violationLogRepository.save(ViolationLog.builder()
                    .employerId(score.getEmployerId())
                    .jobPostId(0L)
                    .violationType("score_reset")
                    .points(-currentScore)
                    .source(ViolationSource.SYSTEM.getValue())
                    .note("Tự động reset điểm định kỳ do không tái phạm nhóm B")
                    .build());

            companyRepository.findByUserId(score.getEmployerId()).ifPresent(company -> {
                company.setViolationScore(0);
                if (CompanyStatus.SUSPENDED.getValue().equals(company.getStatus())) {
                    company.setStatus(CompanyStatus.ACTIVE.getValue());
                    company.setSuspendedAt(null);
                    company.setSuspendedReason(null);
                }
                company.setUpdatedBy(null);
                companyRepository.save(company);
            });
        }
    }

    private String buildRestrictionDescription(String level) {
        return switch (level) {
            case "limited" -> "Chỉ được đăng tối đa 3 tin/tuần. Tin mới cần Admin duyệt trước khi hiển thị.";
            case "suspended" -> "Tài khoản bị tạm khóa 30 ngày. Toàn bộ tin hiện tại bị ẩn.";
            default -> "Hoạt động bình thường.";
        };
    }

    private String calculateScoreLevel(int totalScore) {
        if (totalScore >= SUSPENDED_SCORE_THRESHOLD) return "suspended";
        if (totalScore >= LIMITED_SCORE_THRESHOLD) return "limited";
        return "normal";
    }

    /**
     * Đủ điều kiện reset khi chưa từng vi phạm nhóm B,
     * hoặc vi phạm nhóm B gần nhất đã cách đây hơn 6 tháng.
     */
    private boolean canResetScore(LocalDateTime lastGroupBViolationAt) {
        if (lastGroupBViolationAt == null) return true;
        return lastGroupBViolationAt.isBefore(LocalDateTime.now().minusMonths(6));
    }
}
