package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import com.topviec.topviec_be.enums.candidateProfile.JobSeekingStatus;
import com.topviec.topviec_be.enums.candidateProfile.PreferredWorkType;

@Getter
@Setter
public class ReqCreateCandidateProfileDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
    private String fullName;

    private String avatarUrl;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(male|female|other)$", message = "Giới tính không hợp lệ, chọn: male, female, other")
    private String gender;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phoneDisplay;

    @Size(max = 1000, message = "Giới thiệu bản thân tối đa 1000 ký tự")
    private String bio;

    @Size(max = 512, message = "LinkedIn URL tối đa 512 ký tự")
    private String linkedinUrl;

    @Size(max = 512, message = "GitHub URL tối đa 512 ký tự")
    private String githubUrl;

    @Size(max = 512, message = "Website cá nhân tối đa 512 ký tự")
    private String personalWebsite;

    @PositiveOrZero(message = "Mức lương tối thiểu không được âm")
    private Double expectedSalaryMin;

    @PositiveOrZero(message = "Mức lương tối đa không được âm")
    private Double expectedSalaryMax;

    private Boolean salaryNegotiable = false;

    private JobSeekingStatus jobSeekingStatus = JobSeekingStatus.ACTIVE;

    private PreferredWorkType preferredWorkType;

    @Size(max = 255, message = "Vị trí mong muốn tối đa 255 ký tự")
    private String preferredJobTitle;

    private Integer preferredLocationId;

    private Boolean isCvPublic = true;

    private Boolean hidePhone = false;

    private Boolean hideEmail = false;

    private Boolean hideDateOfBirth = false;

    private Boolean hideExpectedSalary = false;
}
