package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.company.CompanySize;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCompanyDTO {

    @Size(max = 255, message = "Slug tối đa 255 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug chỉ chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    @Size(max = 300, message = "Tên công ty tối đa 300 ký tự")
    private String name;

    private String logoUrl;

    private String coverUrl;

    @Size(min = 100, message = "Mô tả công ty tối thiểu 100 ký tự")
    private String description;

    private Long industryId;

    private CompanySize companySize;

    private Integer foundedYear;

    private String website;

    @Email(message = "Email không hợp lệ")
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