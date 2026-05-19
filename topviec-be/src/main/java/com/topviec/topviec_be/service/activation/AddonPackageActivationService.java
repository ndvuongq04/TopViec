package com.topviec.topviec_be.service.activation;

import com.topviec.topviec_be.enums.services.ServiceCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Category-level service cho nhóm ADDON_PACKAGE (Gói dịch vụ thêm).
 *
 * Nhóm này sẽ bao gồm các gói dịch vụ bổ sung khác
 * không thuộc 3 nhóm chính (JOB_POSTING, CANDIDATE, BRANDING).
 *
 * Sẽ triển khai chi tiết ở giai đoạn sau.
 */
@Service
@Slf4j
public class AddonPackageActivationService {

    public static final ServiceCategory CATEGORY = ServiceCategory.ADDON_PACKAGE;

    // TODO: Định nghĩa các service code cho nhóm ADDON_PACKAGE
}
