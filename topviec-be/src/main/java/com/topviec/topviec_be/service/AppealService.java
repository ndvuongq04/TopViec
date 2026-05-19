package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqCreateAppealDTO;
import com.topviec.topviec_be.dto.request.ReqUnsuspendDTO;
import com.topviec.topviec_be.dto.response.ResAppealDTO;

import java.util.List;
import java.util.Optional;

public interface AppealService {

    /** NTD nộp kháng cáo cho một báo cáo nhóm B */
    ResAppealDTO create(Long employerUserId, ReqCreateAppealDTO request);

    /** Admin xem toàn bộ danh sách kháng cáo của một NTD */
    List<ResAppealDTO> getByEmployer(Long employerId);

    /** Admin lấy kháng cáo của một báo cáo cụ thể (nếu có) */
    Optional<ResAppealDTO> getByComplaint(Long complaintId);

    /** NTD lấy kháng cáo của một báo cáo thuộc công ty mình (nếu có) */
    Optional<ResAppealDTO> getByComplaintAsEmployer(Long employerUserId, Long complaintId);

    /**
     * Admin duyệt kháng cáo, gỡ điểm vi phạm từ complaint liên quan và mở khóa tài khoản sớm.
     * Chỉ áp dụng khi appeal ở trạng thái pending.
     */
    ResAppealDTO unsuspend(Long adminUserId, Long companyId, ReqUnsuspendDTO request);
}
