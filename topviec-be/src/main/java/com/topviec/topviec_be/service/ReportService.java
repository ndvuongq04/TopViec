package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqConfirmReportDTO;
import com.topviec.topviec_be.dto.request.ReqCreateReportDTO;
import com.topviec.topviec_be.dto.request.ReqProcessReportDTO;
import com.topviec.topviec_be.dto.response.ResAdminReportStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResEmployerComplaintDetailDTO;
import com.topviec.topviec_be.dto.response.ResReportDetailDTO;
import com.topviec.topviec_be.dto.response.ResViolationReasonDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    ResReportDetailDTO create(Long reporterUserId, ReqCreateReportDTO request);

    ResultPaginationDTO getReports(
            String search,
            String status,
            String group,
            String complaintType,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable);

    ResReportDetailDTO getDetail(Long reportId);

    /** Admin xác nhận báo cáo hợp lệ hay không */
    ResReportDetailDTO confirm(Long adminUserId, Long reportId, ReqConfirmReportDTO request);

    /** Admin chủ động xử lý thủ công (override trước khi scheduler chạy) */
    ResReportDetailDTO process(Long adminUserId, Long reportId, ReqProcessReportDTO request);

    void autoCloseExpiredGroupAComplaints();

    List<ResViolationReasonDTO> getViolationReasons();

    ResultPaginationDTO getMyReports(Long reporterUserId, String status, Pageable pageable);

    // ── NTD xem khiếu nại bị báo cáo ─────────────────────────────────────────

    /** Danh sách khiếu nại nhắm vào tin của NTD (ẩn danh người báo cáo) */
    ResultPaginationDTO getEmployerReports(Long employerUserId, String search, String status, String group, String complaintType, Pageable pageable);

    /** Chi tiết một khiếu nại — chỉ cho phép nếu tin thuộc công ty của NTD */
    ResEmployerComplaintDetailDTO getEmployerReportDetail(Long employerUserId, Long reportId);

    /** NTD xác nhận đã sửa tin → tự đóng báo cáo nhóm A */
    ResEmployerComplaintDetailDTO respondToReport(Long employerUserId, Long reportId);

    /** Admin xem danh sách khiếu nại của 1 tin tuyển dụng */
    ResultPaginationDTO getReportsByJobPost(Long jobPostId, String status, String group, String complaintType, Pageable pageable);

    /** NTD xem danh sách khiếu nại của 1 tin thuộc công ty mình */
    ResultPaginationDTO getEmployerReportsByJobPost(Long employerUserId, Long jobPostId, String status, String group, String complaintType, Pageable pageable);

    /** Admin xem tất cả khiếu nại của cùng tin với 1 complaintId cho trước */
    ResultPaginationDTO getReportsByComplaint(Long complaintId, String status, String group, String complaintType, Pageable pageable);

    /** NTD xem tất cả khiếu nại của cùng tin với 1 complaintId (phải thuộc công ty mình) */
    ResultPaginationDTO getEmployerReportsByComplaint(Long employerUserId, Long complaintId, String status, String group, String complaintType, Pageable pageable);

    /** Admin: thống kê tổng quan báo cáo toàn hệ thống */
    ResAdminReportStatisticsDTO getReportStatistics();
}
