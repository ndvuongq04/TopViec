package com.topviec.topviec_be.specification;

import com.topviec.topviec_be.entity.JobPosting;
import org.springframework.data.jpa.domain.Specification;

public class JobPostingSpecification {

    // ── Điều kiện cơ bản ───────────────────────────────────────────────────

    public static Specification<JobPosting> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<JobPosting> hasStatus(String status) {
        return (root, query, cb) -> (status == null || status.isBlank())
                ? null
                : cb.equal(root.get("status"), status.trim());
    }

    /**
     * Tin hiển thị công khai cho UV: published HOẶC interviewing.
     * Nghiệp vụ: NTT vừa phỏng vấn vừa tuyển dụng → tin vẫn phải hiển thị.
     */
    public static Specification<JobPosting> isVisibleToCandidate() {
        return (root, query, cb) -> root.get("status").in("published", "interviewing");
    }

    public static Specification<JobPosting> hasKeyword(String keyword) {
        return (root, query, cb) -> (keyword == null || keyword.isBlank())
                ? null
                : cb.like(cb.lower(root.get("title")), "%" + keyword.trim().toLowerCase() + "%");
    }

    public static Specification<JobPosting> hasCompany(Long companyId) {
        return (root, query, cb) -> companyId == null
                ? null
                : cb.equal(root.get("companyId"), companyId);
    }

    public static Specification<JobPosting> hasIndustry(Long industryId) {
        return (root, query, cb) -> industryId == null
                ? null
                : cb.equal(root.get("industryId"), industryId);
    }

    public static Specification<JobPosting> hasLevel(Long levelId) {
        return (root, query, cb) -> levelId == null
                ? null
                : cb.equal(root.get("levelId"), levelId);
    }

    public static Specification<JobPosting> hasWorkType(String workType) {
        return (root, query, cb) -> (workType == null || workType.isBlank())
                ? null
                : cb.equal(root.get("workType"), workType.trim());
    }

    public static Specification<JobPosting> isFeatured(Boolean isFeatured) {
        return (root, query, cb) -> isFeatured == null
                ? null
                : cb.equal(root.get("isFeatured"), isFeatured);
    }

    public static Specification<JobPosting> isUrgent(Boolean isUrgent) {
        return (root, query, cb) -> isUrgent == null
                ? null
                : cb.equal(root.get("isUrgent"), isUrgent);
    }

    public static Specification<JobPosting> isHot(Boolean isHot) {
        return (root, query, cb) -> isHot == null
                ? null
                : cb.equal(root.get("isHot"), isHot);
    }

    // ── Lọc lương (giao nhau với khoảng lương của job) ─────────────────────

    /** Lọc job có salaryMax >= salaryMin của filter (khoảng lương có giao nhau). */
    public static Specification<JobPosting> salaryAtLeast(Long salaryMin) {
        return (root, query, cb) -> salaryMin == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("salaryMax"), salaryMin);
    }

    /** Lọc job có salaryMin <= salaryMax của filter (khoảng lương có giao nhau). */
    public static Specification<JobPosting> salaryAtMost(Long salaryMax) {
        return (root, query, cb) -> salaryMax == null
                ? null
                : cb.lessThanOrEqualTo(root.get("salaryMin"), salaryMax);
    }

    // ── Lọc kinh nghiệm ────────────────────────────────────────────────────

    public static Specification<JobPosting> experienceAtLeast(Integer years) {
        return (root, query, cb) -> years == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("experienceYearsMin"), years);
    }

    public static Specification<JobPosting> experienceAtMost(Integer years) {
        return (root, query, cb) -> years == null
                ? null
                : cb.lessThanOrEqualTo(root.get("experienceYearsMin"), years);
    }

    // ── Bộ lọc tổng hợp (dùng cho Employer/Admin) ─────────────────────────

    public static Specification<JobPosting> withFilter(
            String keyword, Long companyId, Long industryId, Long levelId,
            String workType, String status, Boolean isFeatured, Boolean isUrgent, Boolean isHot,
            Long salaryMin, Long salaryMax,
            Integer experienceYearsMin, Integer experienceYearsMax) {

        return Specification.where(notDeleted())
                .and(hasKeyword(keyword))
                .and(hasCompany(companyId))
                .and(hasIndustry(industryId))
                .and(hasLevel(levelId))
                .and(hasWorkType(workType))
                .and(hasStatus(status))
                .and(isFeatured(isFeatured))
                .and(isUrgent(isUrgent))
                .and(isHot(isHot))
                .and(salaryAtLeast(salaryMin))
                .and(salaryAtMost(salaryMax))
                .and(experienceAtLeast(experienceYearsMin))
                .and(experienceAtMost(experienceYearsMax));
    }

    // ── Bộ lọc Employer (bao gồm tin đã xóa mềm) ──────────────────────────

    public static Specification<JobPosting> withEmployerFilter(
            String keyword, Long companyId, Long industryId, Long levelId,
            String workType, String status, Boolean isFeatured, Boolean isUrgent, Boolean isHot,
            Long salaryMin, Long salaryMax,
            Integer experienceYearsMin, Integer experienceYearsMax) {

        // Không dùng notDeleted() → lấy tất cả kể cả đã xóa mềm
        return Specification.where(hasKeyword(keyword))
                .and(hasCompany(companyId))
                .and(hasIndustry(industryId))
                .and(hasLevel(levelId))
                .and(hasWorkType(workType))
                .and(hasStatus(status))
                .and(isFeatured(isFeatured))
                .and(isUrgent(isUrgent))
                .and(isHot(isHot))
                .and(salaryAtLeast(salaryMin))
                .and(salaryAtMost(salaryMax))
                .and(experienceAtLeast(experienceYearsMin))
                .and(experienceAtMost(experienceYearsMax));
    }

    public static Specification<JobPosting> withPublicCompanyFilter(
            String keyword, Long companyId) {

        // Không dùng notDeleted() → lấy tất cả kể cả đã xóa mềm
        return Specification.where(hasKeyword(keyword))
                .and(hasCompany(companyId));
    }

    // ── Bộ lọc public (chỉ published, không có filter status) ─────────────

    public static Specification<JobPosting> withPublicFilter(
            String keyword, Long companyId, Long industryId, Long levelId,
            String workType, Boolean isFeatured, Boolean isUrgent, Boolean isHot,
            Long salaryMin, Long salaryMax,
            Integer experienceYearsMin, Integer experienceYearsMax) {

        return Specification.where(notDeleted())
                .and(isVisibleToCandidate())
                .and(hasKeyword(keyword))
                .and(hasCompany(companyId))
                .and(hasIndustry(industryId))
                .and(hasLevel(levelId))
                .and(hasWorkType(workType))
                .and(isFeatured(isFeatured))
                .and(isUrgent(isUrgent))
                .and(isHot(isHot))
                .and(salaryAtLeast(salaryMin))
                .and(salaryAtMost(salaryMax))
                .and(experienceAtLeast(experienceYearsMin))
                .and(experienceAtMost(experienceYearsMax));
    }
}