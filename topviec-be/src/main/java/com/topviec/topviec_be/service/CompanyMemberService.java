package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqAddMemberDTO;
import com.topviec.topviec_be.dto.request.ReqUpdatePermissionDTO;
import com.topviec.topviec_be.dto.response.ResCompanyMemberDTO;
import com.topviec.topviec_be.dto.response.ResEmployerMemberStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResEmployerProfileDTO;
import com.topviec.topviec_be.dto.response.ResMemberPermissionDetailDTO;
import com.topviec.topviec_be.dto.response.ResPermissionChangeLogDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CompanyMemberService {

        /**
         * Thêm thành viên vào công ty.
         * Xử lý 3 trường hợp (TH1, TH2, TH3) dựa trên trạng thái user.
         *
         * @param inviterUserId ID người mời (Owner/Manager)
         * @param companyId     ID công ty
         * @param req           Thông tin thành viên cần thêm
         */
        ResCompanyMemberDTO addMember(Long inviterUserId, Long companyId, ReqAddMemberDTO req);

        /**
         * Kích hoạt các CompanyMember đang pending của một user sau khi user xác thực
         * email.
         *
         * @param userId ID user vừa xác thực email
         */
        void activatePendingMembers(Long userId);

        /**
         * Kiểm tra xem user có quyền (action) cấu hình hiện tại hay không.
         * Dựa trên Role mặc định và các quyền ghi đè (grant/revoke).
         */
        boolean hasPermission(Long companyId, Long userId, String action);

        /**
         * Đổi vai trò và thay đổi quyền của một thành viên.
         * Ghi lại log và gửi email.
         */
        ResCompanyMemberDTO updateMemberPermission(Long inviterUserId, Long companyId, Long targetUserId,
                        ReqUpdatePermissionDTO req);

        void checkPermission(Long companyId, Long userId, String action);

        /**
         * Lấy danh sách thành viên trong công ty (phân trang + lọc + tìm kiếm).
         */
        ResultPaginationDTO getMembers(Long companyId, String status, String role, String keyword, Pageable pageable);

        /**
         * Xóa thành viên khỏi công ty.
         */
        void removeMember(Long inviterUserId, Long companyId, Long targetUserId);

        /**
         * Lấy thông tin cá nhân của nhà tuyển dụng đang đăng nhập.
         */
        ResEmployerProfileDTO getMyProfile(Long userId);

        /**
         * Lấy quyền hạn chi tiết (effective permissions) của 1-5 thành viên trong công
         * ty.
         * Kết quả bao gồm toàn bộ action đã được tính toán từ role mặc định + custom
         * grant/revoke.
         */
        List<ResMemberPermissionDetailDTO> getBatchMemberPermissions(Long companyId, List<Long> userIds);

        /**
         * Bật/tắt một quyền cụ thể của member.
         * Chỉ OWNER hoặc MANAGER mới được phép thực hiện.
         */
        ResMemberPermissionDetailDTO toggleMemberActionPermission(Long inviterUserId, Long companyId, Long targetUserId,
                        String actionCode, boolean enabled);

        /**
         * Lấy lịch sử thay đổi quyền của một member cụ thể trong công ty.
         */
        List<ResPermissionChangeLogDTO> getMemberPermissionHistory(Long companyId, Long targetUserId);

        /**
         * Lấy toàn bộ lịch sử thay đổi quyền trong công ty (phân trang).
         */
        ResultPaginationDTO getCompanyPermissionHistory(Long companyId, LocalDate fromDate, LocalDate toDate,
                        Pageable pageable);

        /**
         * Lấy thống kê thành viên của công ty (tổng số, active, pending, locked).
         */
        ResEmployerMemberStatisticsDTO getMemberStatistics(Long companyId);
}
