package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqAssignJobPostDTO;
import com.topviec.topviec_be.dto.request.ReqRevokeAssignmentDTO;
import com.topviec.topviec_be.dto.response.ResJobPostAssignmentDTO;
import com.topviec.topviec_be.dto.response.ResRecruiterWithAssignmentCountDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobPostAssignmentService {

    // ── Giao việc ────────────────────────────────────────────────────────

    /**
     * Phân công tin tuyển dụng cho một NTD.
     * Quy định: 1 tin chỉ có 1 NTD được phân công tại một thời điểm.
     *
     * @param request       Thông tin phân công (jobPostId, userId, note)
     * @param assignedBy    ID người thực hiện phân công (Owner/Manager)
     * @param companyId     ID công ty
     */
    ResJobPostAssignmentDTO assignJobPost(ReqAssignJobPostDTO request, Long assignedBy, Long companyId);

    /**
     * Lấy danh sách NTD (recruiter) trong công ty kèm số tin đang quản lý.
     * Dùng cho màn hình "chọn NTD" khi phân công.
     *
     * @param companyId  ID công ty
     * @param keyword    Tìm kiếm theo email (optional)
     * @param jobPostId  ID tin tuyển dụng (optional) — nếu truyền sẽ đánh dấu NTD đang quản lý tin này
     * @param pageable   Phân trang
     */
    ResultPaginationDTO getRecruitersWithAssignmentCount(Long companyId, String keyword, Long jobPostId, Pageable pageable);

    /**
     * Đổi người phân công: thu hồi NTD cũ + giao cho NTD mới trong 1 bước.
     *
     * @param request       Thông tin phân công mới (jobPostId, userId, note)
     * @param reassignedBy  ID người thực hiện đổi phân công
     * @param companyId     ID công ty
     */
    ResJobPostAssignmentDTO reassignJobPost(ReqAssignJobPostDTO request, Long reassignedBy, Long companyId);

    // ── Quản lý theo tin tuyển dụng ──────────────────────────────────────

    /**
     * Lấy danh sách tin tuyển dụng của công ty (có thông tin phân công).
     * Filter: keyword, status, assigned/unassigned.
     */
    ResultPaginationDTO getJobPostsWithAssignment(Long companyId, String keyword, String status,
            Boolean assigned, Pageable pageable);

    /**
     * Lấy NTD đang được phân công quản lý tin tuyển dụng.
     *
     * @param jobPostId  ID tin tuyển dụng
     * @param companyId  ID công ty (để xác minh quyền)
     */
    ResJobPostAssignmentDTO getCurrentAssignment(Long jobPostId, Long companyId);

    // ── Quản lý theo NTD ─────────────────────────────────────────────────

    /**
     * Lấy danh sách tin tuyển dụng đang được phân công cho một NTD.
     *
     * @param userId     ID recruiter
     * @param companyId  ID công ty (để xác minh quyền)
     * @param pageable   Phân trang
     */
    ResultPaginationDTO getJobPostsByRecruiter(Long userId, Long companyId, Pageable pageable);

    // ── Thu hồi phân công ────────────────────────────────────────────────

    /**
     * Thu hồi phân công tin tuyển dụng.
     *
     * @param request     Thông tin thu hồi (jobPostId, note)
     * @param revokedBy   ID người thu hồi (Owner/Manager)
     * @param companyId   ID công ty
     */
    ResJobPostAssignmentDTO revokeAssignment(ReqRevokeAssignmentDTO request, Long revokedBy, Long companyId);

    // ── Tin chưa phân công ───────────────────────────────────────────────

    /**
     * Lấy danh sách tin tuyển dụng chưa được phân công cho member nào.
     *
     * @param companyId  ID công ty
     * @param keyword    Tìm kiếm theo tiêu đề (optional)
     * @param status     Lọc theo trạng thái tin (optional)
     * @param pageable   Phân trang
     */
    ResultPaginationDTO getUnassignedJobPosts(Long companyId, String keyword, String status, Pageable pageable);
}
