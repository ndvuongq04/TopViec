package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.company.CompanySize;
import com.topviec.topviec_be.enums.company.CompanyStatus;
import com.topviec.topviec_be.enums.company.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResCompanyDTO {

    private Long id;
    private String slug;
    private String name;
    private String logoUrl;
    private String coverUrl;
    private String description;
    private Long industryId;
    private String industryName;
    private Integer jobCount;
    private CompanySize companySize;
    private Integer foundedYear;

    // Liên hệ
    private String website;
    private String email;
    private String phone;
    private String address;
    private Integer provinceId;

    // Pháp lý
    private String taxCode;
    private String businessLicenseUrl;

    // Văn hóa & phúc lợi
    private String culture;
    private String benefits;
    private String socialLinks;

    // Xét duyệt
    private VerificationStatus verificationStatus;
    private LocalDateTime verifiedAt;
    private Long verifiedBy;
    private String rejectionReason;

    // Dịch vụ Branding đang active
    private Boolean isBanner;
    private Boolean isTopEmployer;
    private Boolean isBrandVerified;

    // Trạng thái
    private CompanyStatus status;
    private Integer violationScore;
    private LocalDateTime suspendedAt;
    private String suspendedReason;

    // Audit
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}