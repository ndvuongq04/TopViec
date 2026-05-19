package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCandidateProfileVisibilityDTO {

    @NotNull(message = "Trạng thái hiển thị số điện thoại không được để trống")
    private Boolean hidePhone;

    @NotNull(message = "Trạng thái hiển thị email không được để trống")
    private Boolean hideEmail;

    @NotNull(message = "Trạng thái hiển thị ngày sinh không được để trống")
    private Boolean hideDateOfBirth;

    @NotNull(message = "Trạng thái hiển thị mức lương kỳ vọng không được để trống")
    private Boolean hideExpectedSalary;
}
