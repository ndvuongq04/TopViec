package com.topviec.topviec_be.service.activation;

import com.topviec.topviec_be.enums.services.ServiceCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Category-level service cho nhóm CANDIDATE (Hồ sơ ứng viên).
 *
 * Nhóm này sẽ bao gồm các dịch vụ nhỏ liên quan đến ứng viên
 * (ví dụ: xem hồ sơ, mở khóa thông tin liên lạc, ...).
 *
 * Sẽ triển khai chi tiết ở giai đoạn sau.
 */
@Service
@Slf4j
public class CandidateActivationService {

    public static final ServiceCategory CATEGORY = ServiceCategory.CANDIDATE;

    // TODO: Định nghĩa các service code cho nhóm CANDIDATE
    // public static final String CODE_VIEW_PROFILE = "CANDIDATE_VIEW_PROFILE";
    // public static final String CODE_UNLOCK_CONTACT = "CANDIDATE_UNLOCK_CONTACT";
}
