package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqJobPostSkillDTO {

    @NotNull(message = "Kỹ năng không được để trống")
    private Long skillId;

    @NotNull(message = "Phân loại kỹ năng không được để trống")
    private Boolean isRequired;

    @Min(value = 1, message = "Mức độ thành thạo tối thiểu là 1")
    @Max(value = 5, message = "Mức độ thành thạo tối đa là 5")
    private Integer proficiencyMin;
}