package com.topviec.topviec_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqAdminUpdateCompanyDTO {

    private String action; // "verify" | "suspend" | "unsuspend" | null
    private Boolean approved; // dùng khi action = "verify"
    private String rejectionReason; // dùng khi action = "verify" + approved = false
    private String suspendedReason; // dùng khi action = "suspend"

    private String slug;
    private String name;
    private String logoUrl;
    private String coverUrl;
    private String description;
    private Long industryId;
    private com.topviec.topviec_be.enums.company.CompanySize companySize;
    private Integer foundedYear;
    private String website;
    private String email;
    private String phone;
    private String address;
    private Integer provinceId;
    private String taxCode;
    private String businessLicenseUrl;
    private String culture;
    private String benefits;
    private String socialLinks;
}