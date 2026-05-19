package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.company.CompanySize;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateCompanyDTO {

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 255, message = "Slug tối đa 255 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug chỉ chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 300, message = "Tên công ty tối đa 300 ký tự")
    private String name;

    private String logoUrl;

    private String coverUrl;

    @NotBlank(message = "Mô tả công ty không được để trống")
    @Size(min = 100, message = "Mô tả công ty tối thiểu 100 ký tự")
    private String description;

    @NotNull(message = "Lĩnh vực hoạt động không được để trống")
    private Long industryId;

    @NotNull(message = "Quy mô công ty không được để trống")
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

    // JSON string: ["Bảo hiểm sức khỏe", "13 tháng lương"]
    private String benefits;

    // JSON string: {"facebook": "...", "linkedin": "..."}
    private String socialLinks;
}