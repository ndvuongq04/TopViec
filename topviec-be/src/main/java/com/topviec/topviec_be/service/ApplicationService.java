package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqApplyJobDTO;
import com.topviec.topviec_be.dto.request.ReqBulkApplyDTO;
import com.topviec.topviec_be.dto.request.ReqWithdrawApplicationDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateApplicationDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateApplicationCvDTO;
import com.topviec.topviec_be.dto.response.ResApplicationDTO;
import com.topviec.topviec_be.dto.response.ResEmployerApplicationDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {

    // CN-UV-010: Nộp đơn đầy đủ
    ResApplicationDTO apply(Long candidateUserId, Long jobPostId, ReqApplyJobDTO request);

    // CN-UV-011: Ứng tuyển nhanh (CV mặc định)
    ResApplicationDTO quickApply(Long candidateUserId, Long jobPostId);

    // CN-UV-012: Ứng tuyển hàng loạt (tối đa 10)
    List<ResApplicationDTO> bulkApply(Long candidateUserId, ReqBulkApplyDTO request);

    // CN-UV-013: Theo dõi trạng thái đơn (thêm filter status)
    ResultPaginationDTO getMyApplications(Long candidateUserId, String status, Pageable pageable);

    /** Lấy các đơn của UV có ít nhất 1 lịch PV (dùng cho trang "Lịch PV của tôi") */
    List<ResApplicationDTO> getMyApplicationsWithInterviews(Long candidateUserId);

    // CN-UV-015: Rút đơn
    ResApplicationDTO withdraw(Long candidateUserId, Long applicationId, ReqWithdrawApplicationDTO request);

    ResApplicationDTO updateApplicationCv(Long candidateUserId, Long applicationId, ReqUpdateApplicationCvDTO request);

    // -------------------------------------------------------------------------
    // API cho Employer
    // -------------------------------------------------------------------------

    ResultPaginationDTO getApplicationsByJobPost(Long userId, Long companyId, Long jobPostId, String status, String search, Pageable pageable);

    ResEmployerApplicationDTO getApplicationDetailByEmployer(Long userId, Long companyId, Long applicationId);

    ResEmployerApplicationDTO updateApplication(Long userId, Long companyId, Long applicationId, ReqUpdateApplicationDTO request);

    ResApplicationDTO acceptInvite(Long candidateUserId, Long applicationId);

    ResApplicationDTO declineInvite(Long candidateUserId, Long applicationId);
}