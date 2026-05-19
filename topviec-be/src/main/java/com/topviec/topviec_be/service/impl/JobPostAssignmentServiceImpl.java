package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqAssignJobPostDTO;
import com.topviec.topviec_be.dto.request.ReqRevokeAssignmentDTO;
import com.topviec.topviec_be.dto.response.ResJobPostAssignmentDTO;
import com.topviec.topviec_be.dto.response.ResRecruiterWithAssignmentCountDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Company;
import com.topviec.topviec_be.entity.CompanyMember;
import com.topviec.topviec_be.entity.JobPostAssignment;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import com.topviec.topviec_be.enums.jobs.JobPostStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CompanyMemberRepository;
import com.topviec.topviec_be.repository.CompanyRepository;
import com.topviec.topviec_be.repository.JobPostAssignmentRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.CompanyMemberService;
import com.topviec.topviec_be.service.EmailService;
import com.topviec.topviec_be.service.JobPostAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostAssignmentServiceImpl implements JobPostAssignmentService {

    private final JobPostAssignmentRepository assignmentRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final UserRepository userRepository;
    private final CompanyMemberService companyMemberService;
    private final CompanyRepository companyRepository;
    private final EmailService emailService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

    // =========================================================================
    // Giao việc
    // =========================================================================

    @Override
    @Transactional
    public ResJobPostAssignmentDTO assignJobPost(ReqAssignJobPostDTO request, Long assignedBy, Long companyId) {
        Long jobPostId = request.getJobPostId();
        Long userId = request.getUserId();

        // 1. Kiểm tra tin tuyển dụng tồn tại và thuộc về công ty
        JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Tin tuyển dụng không thuộc công ty của bạn");
        }

        // 2. Kiểm tra trạng thái tin có được phép phân công không
        if (!JobPostStatus.fromValue(jobPosting.getStatus()).isAssignable()) {
            throw AppException.badRequest(
                    "Không thể phân công tin ở trạng thái '" + jobPosting.getStatus()
                            + "'. Chỉ cho phép phân công tin: scheduled, published, paused, renewed, interviewing, closed.");
        }

        // 3. Kiểm tra tin đã có người được phân công chưa (1 tin chỉ 1 NTD)
        if (assignmentRepository.existsByJobPostIdAndRevokedAtIsNull(jobPostId)) {
            throw AppException.conflict("Tin tuyển dụng này đã có nhà tuyển dụng được phân công. "
                    + "Vui lòng thu hồi phân công cũ trước khi giao cho người khác.");
        }

        // 3. Kiểm tra NTD là thành viên active trong công ty
        CompanyMember member = companyMemberRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> AppException.notFound("Nhà tuyển dụng không phải thành viên của công ty"));

        if (!"active".equals(member.getStatus())) {
            throw AppException.badRequest("Nhà tuyển dụng chưa kích hoạt tài khoản trong công ty");
        }

        // 5. Kiểm tra NTD có quyền được nhận phân công không
        if (!companyMemberService.hasPermission(companyId, userId, "job_assignment:receive")) {
            throw AppException.forbidden("Nhà tuyển dụng này không có quyền nhận phân công tin tuyển dụng");
        }

        // 4. Tạo phân công mới
        JobPostAssignment assignment = JobPostAssignment.builder()
                .jobPostId(jobPostId)
                .userId(userId)
                .assignedBy(assignedBy)
                .note(request.getNote())
                .build();

        JobPostAssignment saved = assignmentRepository.save(assignment);

        log.info("Phân công tin [{}] cho NTD userId [{}] bởi userId [{}] trong công ty [{}]",
                jobPostId, userId, assignedBy, companyId);

        // Gửi email thông báo cho member
        sendAssignmentEmail(userId, assignedBy, companyId, jobPosting.getTitle(), request.getNote(), false);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getRecruitersWithAssignmentCount(Long companyId, String keyword, Long jobPostId,
            Pageable pageable) {
        // 1. Lấy danh sách thành viên active trong công ty (phân trang + tìm kiếm)
        Page<CompanyMember> pageMembers = companyMemberRepository.findByFilters(
                companyId, "active", null, keyword, pageable);

        List<CompanyMember> members = pageMembers.getContent();

        // 2. Lọc chỉ giữ lại các member có quyền job_assignment:receive
        members = members.stream()
                .filter(m -> companyMemberService.hasPermission(companyId, m.getUserId(), "job_assignment:receive"))
                .toList();

        // 2. Lấy email users
        List<Long> userIds = members.stream().map(CompanyMember::getUserId).toList();
        Map<Long, String> emailMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));

        // 3. Lấy số tin đang quản lý
        Map<Long, Long> assignmentCountMap = Map.of();
        if (!userIds.isEmpty()) {
            assignmentCountMap = assignmentRepository.countActiveByUserIds(userIds, companyId).stream()
                    .collect(Collectors.toMap(
                            row -> (Long) row[0],
                            row -> (Long) row[1]));
        }

        // 4. Tìm NTD đang quản lý tin cụ thể (nếu jobPostId được truyền vào)
        Long currentAssigneeUserId = null;
        if (jobPostId != null) {
            currentAssigneeUserId = assignmentRepository.findByJobPostIdAndRevokedAtIsNull(jobPostId)
                    .map(JobPostAssignment::getUserId)
                    .orElse(null);
        }

        // 5. Map to DTO
        Map<Long, Long> finalAssignmentCountMap = assignmentCountMap;
        Long finalCurrentAssigneeUserId = currentAssigneeUserId;
        List<ResRecruiterWithAssignmentCountDTO> result = members.stream()
                .map(m -> ResRecruiterWithAssignmentCountDTO.builder()
                        .userId(m.getUserId())
                        .email(emailMap.get(m.getUserId()))
                        .roleName(m.getMemberRole() != null ? m.getMemberRole().getValue() : null)
                        .jobTitle(m.getJobTitle())
                        .status(m.getStatus())
                        .assignedJobCount(finalAssignmentCountMap.getOrDefault(m.getUserId(), 0L))
                        .isCurrentAssignee(finalCurrentAssigneeUserId != null
                                ? m.getUserId().equals(finalCurrentAssigneeUserId)
                                : null)
                        .build())
                .toList();

        // 6. Build pagination response
        return buildPaginationResult(pageMembers, result);
    }

    @Override
    @Transactional
    public ResJobPostAssignmentDTO reassignJobPost(ReqAssignJobPostDTO request, Long reassignedBy, Long companyId) {
        Long jobPostId = request.getJobPostId();
        Long newUserId = request.getUserId();

        // 1. Kiểm tra tin tuyển dụng tồn tại và thuộc về công ty
        JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Tin tuyển dụng không thuộc công ty của bạn");
        }

        // 2. Kiểm tra trạng thái tin có được phép phân công không
        if (!JobPostStatus.fromValue(jobPosting.getStatus()).isAssignable()) {
            throw AppException.badRequest(
                    "Không thể phân công tin ở trạng thái '" + jobPosting.getStatus()
                            + "'. Chỉ cho phép phân công tin: scheduled, published, paused, renewed, interviewing, closed.");
        }

        // 3. Kiểm tra NTD mới là thành viên active trong công ty
        CompanyMember member = companyMemberRepository.findByCompanyIdAndUserId(companyId, newUserId)
                .orElseThrow(() -> AppException.notFound("Nhà tuyển dụng không phải thành viên của công ty"));

        if (!"active".equals(member.getStatus())) {
            throw AppException.badRequest("Nhà tuyển dụng chưa kích hoạt tài khoản trong công ty");
        }

        // 4. Kiểm tra NTD mới có quyền được nhận phân công không
        if (!companyMemberService.hasPermission(companyId, newUserId, "job_assignment:receive")) {
            throw AppException.forbidden("Nhà tuyển dụng này không có quyền nhận phân công tin tuyển dụng");
        }

        // 3. Thu hồi phân công cũ (nếu có)
        assignmentRepository.findByJobPostIdAndRevokedAtIsNull(jobPostId)
                .ifPresent(oldAssignment -> {
                    // Không cho phép đổi cho chính người đang quản lý
                    if (oldAssignment.getUserId().equals(newUserId)) {
                        throw AppException.conflict("Nhà tuyển dụng này đang quản lý tin tuyển dụng này rồi");
                    }
                    oldAssignment.setRevokedAt(LocalDateTime.now());
                    oldAssignment.setRevokedBy(reassignedBy);
                    String revokeNote = "Đổi phân công sang NTD mới (userId: " + newUserId + ")";
                    oldAssignment.setNote(oldAssignment.getNote() != null
                            ? oldAssignment.getNote() + " | " + revokeNote
                            : revokeNote);
                    assignmentRepository.save(oldAssignment);
                });

        // 4. Tạo phân công mới
        JobPostAssignment newAssignment = JobPostAssignment.builder()
                .jobPostId(jobPostId)
                .userId(newUserId)
                .assignedBy(reassignedBy)
                .note(request.getNote())
                .build();

        JobPostAssignment saved = assignmentRepository.save(newAssignment);

        log.info("Đổi phân công tin [{}] sang NTD userId [{}] bởi userId [{}] trong công ty [{}]",
                jobPostId, newUserId, reassignedBy, companyId);

        // Gửi email thông báo cho member mới
        sendAssignmentEmail(newUserId, reassignedBy, companyId, jobPosting.getTitle(), request.getNote(), true);

        return toResponse(saved);
    }

    // =========================================================================
    // Quản lý theo tin tuyển dụng
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getJobPostsWithAssignment(Long companyId, String keyword, String status,
            Boolean assigned, Pageable pageable) {
        // 1. Lấy danh sách tin tuyển dụng của công ty
        Page<JobPosting> pageJobPosts = jobPostingRepository.findAll(
                (root, query, cb) -> {
                    var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

                    predicates.add(cb.equal(root.get("companyId"), companyId));
                    predicates.add(cb.isNull(root.get("deletedAt")));

                    if (keyword != null && !keyword.trim().isEmpty()) {
                        predicates.add(cb.like(cb.lower(root.get("title")),
                                "%" + keyword.trim().toLowerCase() + "%"));
                    }
                    if (status != null && !status.trim().isEmpty()) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }

                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                },
                pageable);

        // 2. Lấy assignments cho những tin này
        List<Long> jobPostIds = pageJobPosts.getContent().stream().map(JobPosting::getId).toList();
        Map<Long, JobPostAssignment> assignmentMap = Map.of();
        if (!jobPostIds.isEmpty()) {
            assignmentMap = assignmentRepository.findActiveByJobPostIds(jobPostIds).stream()
                    .collect(Collectors.toMap(JobPostAssignment::getJobPostId, a -> a));
        }

        // 3. Filter theo assigned/unassigned nếu cần
        Map<Long, JobPostAssignment> finalAssignmentMap = assignmentMap;
        List<JobPosting> filteredPosts = pageJobPosts.getContent();
        if (assigned != null) {
            filteredPosts = filteredPosts.stream()
                    .filter(jp -> assigned.equals(finalAssignmentMap.containsKey(jp.getId())))
                    .toList();
        }

        // 4. Lấy email NTD đang phân công
        List<Long> assignedUserIds = finalAssignmentMap.values().stream()
                .map(JobPostAssignment::getUserId).distinct().toList();
        Map<Long, String> emailMap = Map.of();
        if (!assignedUserIds.isEmpty()) {
            emailMap = userRepository.findAllById(assignedUserIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getEmail));
        }

        // 5. Build response
        Map<Long, String> finalEmailMap = emailMap;
        List<Map<String, Object>> result = filteredPosts.stream()
                .map(jp -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", jp.getId());
                    item.put("title", jp.getTitle());
                    item.put("slug", jp.getSlug());
                    item.put("status", jp.getStatus());
                    item.put("publishedAt", jp.getPublishedAt());
                    item.put("deadline", jp.getDeadline());
                    item.put("createdAt", jp.getCreatedAt());

                    JobPostAssignment assignment = finalAssignmentMap.get(jp.getId());
                    if (assignment != null) {
                        Map<String, Object> assignmentInfo = new java.util.LinkedHashMap<>();
                        assignmentInfo.put("assignmentId", assignment.getId());
                        assignmentInfo.put("userId", assignment.getUserId());
                        assignmentInfo.put("userEmail", finalEmailMap.get(assignment.getUserId()));
                        assignmentInfo.put("assignedAt", assignment.getAssignedAt());
                        item.put("assignment", assignmentInfo);
                    } else {
                        item.put("assignment", null);
                    }

                    return item;
                })
                .toList();

        return buildPaginationResult(pageJobPosts, result);
    }

    @Override
    @Transactional(readOnly = true)
    public ResJobPostAssignmentDTO getCurrentAssignment(Long jobPostId, Long companyId) {
        // 1. Kiểm tra tin thuộc công ty
        JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Tin tuyển dụng không thuộc công ty của bạn");
        }

        // 2. Tìm phân công active
        JobPostAssignment assignment = assignmentRepository.findByJobPostIdAndRevokedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Tin tuyển dụng chưa được phân công cho ai"));

        return toResponse(assignment);
    }

    // =========================================================================
    // Quản lý theo NTD
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getJobPostsByRecruiter(Long userId, Long companyId, Pageable pageable) {
        // 1. Kiểm tra NTD là thành viên công ty
        companyMemberRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> AppException.notFound("Nhà tuyển dụng không phải thành viên của công ty"));

        // 2. Lấy danh sách phân công active
        Page<JobPostAssignment> pageAssignments = assignmentRepository.findActiveByUserId(userId, pageable);

        // 3. Lấy thông tin tin tuyển dụng
        List<Long> jobPostIds = pageAssignments.getContent().stream()
                .map(JobPostAssignment::getJobPostId).toList();

        Map<Long, JobPosting> jobPostMap = Map.of();
        if (!jobPostIds.isEmpty()) {
            jobPostMap = jobPostingRepository.findAllById(jobPostIds).stream()
                    .collect(Collectors.toMap(JobPosting::getId, jp -> jp));
        }

        // 4. Map to response
        Map<Long, JobPosting> finalJobPostMap = jobPostMap;
        List<Map<String, Object>> result = pageAssignments.getContent().stream()
                .map(a -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("assignmentId", a.getId());
                    item.put("assignedAt", a.getAssignedAt());
                    item.put("note", a.getNote());

                    JobPosting jp = finalJobPostMap.get(a.getJobPostId());
                    if (jp != null) {
                        Map<String, Object> jobPost = new java.util.LinkedHashMap<>();
                        jobPost.put("id", jp.getId());
                        jobPost.put("title", jp.getTitle());
                        jobPost.put("slug", jp.getSlug());
                        jobPost.put("status", jp.getStatus());
                        jobPost.put("publishedAt", jp.getPublishedAt());
                        jobPost.put("deadline", jp.getDeadline());
                        jobPost.put("createdAt", jp.getCreatedAt());
                        item.put("jobPost", jobPost);
                    }

                    return item;
                })
                .toList();

        return buildPaginationResult(pageAssignments, result);
    }

    // =========================================================================
    // Thu hồi phân công
    // =========================================================================

    @Override
    @Transactional
    public ResJobPostAssignmentDTO revokeAssignment(ReqRevokeAssignmentDTO request, Long revokedBy, Long companyId) {
        Long jobPostId = request.getJobPostId();

        // 1. Kiểm tra tin thuộc công ty
        JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng"));

        if (!jobPosting.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Tin tuyển dụng không thuộc công ty của bạn");
        }

        // 2. Tìm phân công active
        JobPostAssignment assignment = assignmentRepository.findByJobPostIdAndRevokedAtIsNull(jobPostId)
                .orElseThrow(() -> AppException.notFound("Tin tuyển dụng chưa có phân công nào đang active"));

        // 3. Thu hồi
        assignment.setRevokedAt(LocalDateTime.now());
        assignment.setRevokedBy(revokedBy);
        if (request.getNote() != null && !request.getNote().trim().isEmpty()) {
            String combinedNote = assignment.getNote() != null
                    ? assignment.getNote() + " | Thu hồi: " + request.getNote()
                    : "Thu hồi: " + request.getNote();
            assignment.setNote(combinedNote);
        }

        JobPostAssignment saved = assignmentRepository.save(assignment);

        log.info("Thu hồi phân công tin [{}] từ NTD userId [{}] bởi userId [{}]",
                jobPostId, assignment.getUserId(), revokedBy);

        // Gửi email thông báo thu hồi cho member
        sendRevokeEmail(assignment.getUserId(), revokedBy, companyId, jobPosting.getTitle(), request.getNote());

        return toResponse(saved);
    }

    // =========================================================================
    // Tin chưa phân công
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getUnassignedJobPosts(Long companyId, String keyword, String status, Pageable pageable) {
        // 1. Lấy tất cả jobPostId đang được phân công (active) trong công ty
        List<Long> assignedJobPostIds = assignmentRepository.findAssignedJobPostIdsByCompanyId(companyId);

        // 2. Query tin tuyển dụng KHÔNG nằm trong danh sách đã phân công
        Page<JobPosting> pageJobPosts = jobPostingRepository.findAll(
                (root, query, cb) -> {
                    var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

                    predicates.add(cb.equal(root.get("companyId"), companyId));
                    predicates.add(cb.isNull(root.get("deletedAt")));

                    // Chỉ lấy tin có trạng thái được phép phân công
                    predicates.add(root.get("status").in(JobPostStatus.getAssignableValues()));

                    // Loại trừ các tin đã được phân công
                    if (!assignedJobPostIds.isEmpty()) {
                        predicates.add(cb.not(root.get("id").in(assignedJobPostIds)));
                    }

                    if (keyword != null && !keyword.trim().isEmpty()) {
                        predicates.add(cb.like(cb.lower(root.get("title")),
                                "%" + keyword.trim().toLowerCase() + "%"));
                    }
                    if (status != null && !status.trim().isEmpty()) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }

                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                },
                pageable);

        // 3. Build response
        List<Map<String, Object>> result = pageJobPosts.getContent().stream()
                .map(jp -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", jp.getId());
                    item.put("title", jp.getTitle());
                    item.put("slug", jp.getSlug());
                    item.put("status", jp.getStatus());
                    item.put("workType", jp.getWorkType());
                    item.put("headcount", jp.getHeadcount());
                    item.put("publishedAt", jp.getPublishedAt());
                    item.put("deadline", jp.getDeadline());
                    item.put("createdAt", jp.getCreatedAt());
                    return item;
                })
                .toList();

        return buildPaginationResult(pageJobPosts, result);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private ResJobPostAssignmentDTO toResponse(JobPostAssignment a) {
        // Lấy email các user liên quan
        List<Long> userIds = new java.util.ArrayList<>();
        userIds.add(a.getUserId());
        userIds.add(a.getAssignedBy());
        if (a.getRevokedBy() != null) {
            userIds.add(a.getRevokedBy());
        }

        Map<Long, String> emailMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));

        // Lấy thông tin tin tuyển dụng
        String jobPostTitle = null;
        String jobPostStatus = null;
        JobPosting jp = jobPostingRepository.findById(a.getJobPostId()).orElse(null);
        if (jp != null) {
            jobPostTitle = jp.getTitle();
            jobPostStatus = jp.getStatus();
        }

        return ResJobPostAssignmentDTO.builder()
                .id(a.getId())
                .jobPostId(a.getJobPostId())
                .jobPostTitle(jobPostTitle)
                .jobPostStatus(jobPostStatus)
                .userId(a.getUserId())
                .userEmail(emailMap.get(a.getUserId()))
                .assignedBy(a.getAssignedBy())
                .assignedByEmail(emailMap.get(a.getAssignedBy()))
                .assignedAt(a.getAssignedAt())
                .revokedAt(a.getRevokedAt())
                .revokedBy(a.getRevokedBy())
                .revokedByEmail(a.getRevokedBy() != null ? emailMap.get(a.getRevokedBy()) : null)
                .note(a.getNote())
                .build();
    }

    private <T> ResultPaginationDTO buildPaginationResult(Page<?> page, List<T> content) {
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(content);
        return result;
    }

    /**
     * Gửi email thông báo phân công (hoặc đổi phân công) cho member.
     * Lỗi email không ảnh hưởng luồng chính.
     */
    private void sendAssignmentEmail(Long memberId, Long assignedById,
                                     Long companyId, String jobTitle, String note, boolean isReassign) {
        try {
            String memberEmail = userRepository.findById(memberId).map(User::getEmail).orElse(null);
            if (memberEmail == null) return;

            String assignedByEmail = userRepository.findById(assignedById).map(User::getEmail).orElse("-");
            String companyName = companyRepository.findById(companyId)
                    .map(Company::getName).orElse("Công ty");
            String assignedAt = LocalDateTime.now().format(DATE_TIME_FORMATTER);

            emailService.sendJobAssignedEmail(
                    memberEmail, companyName, jobTitle,
                    assignedByEmail, assignedAt, note, isReassign);
        } catch (Exception e) {
            log.warn("Không gửi được email thông báo phân công cho userId [{}]: {}", memberId, e.getMessage());
        }
    }

    /**
     * Gửi email thông báo thu hồi phân công cho member.
     * Lỗi email không ảnh hưởng luồng chính.
     */
    private void sendRevokeEmail(Long memberId, Long revokedById,
                                  Long companyId, String jobTitle, String note) {
        try {
            String memberEmail = userRepository.findById(memberId).map(User::getEmail).orElse(null);
            if (memberEmail == null) return;

            String revokedByEmail = userRepository.findById(revokedById).map(User::getEmail).orElse("-");
            String companyName = companyRepository.findById(companyId)
                    .map(Company::getName).orElse("Công ty");
            String revokedAt = LocalDateTime.now().format(DATE_TIME_FORMATTER);

            emailService.sendJobRevokedEmail(
                    memberEmail, companyName, jobTitle,
                    revokedByEmail, revokedAt, note);
        } catch (Exception e) {
            log.warn("Không gửi được email thông báo thu hồi cho userId [{}]: {}", memberId, e.getMessage());
        }
    }
}
