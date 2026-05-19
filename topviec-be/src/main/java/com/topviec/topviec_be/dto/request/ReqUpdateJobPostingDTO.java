package com.topviec.topviec_be.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateJobPostingDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Mô tả công việc không được để trống")
    private String description;

    @NotBlank(message = "Yêu cầu ứng viên không được để trống")
    private String requirements;

    private String benefits;

    @NotNull(message = "Ngành nghề không được để trống")
    private Long industryId;

    @NotNull(message = "Cấp bậc không được để trống")
    private Long levelId;

    @NotNull(message = "Số năm kinh nghiệm tối thiểu không được để trống")
    @Min(value = 0, message = "Số năm kinh nghiệm tối thiểu không được âm")
    private Integer experienceYearsMin;

    private Integer experienceYearsMax;

    private Long salaryMin;

    private Long salaryMax;

    @NotNull(message = "Trạng thái lương không được để trống")
    private Boolean salaryNegotiable;

    @NotBlank(message = "Hình thức làm việc không được để trống")
    private String workType;

    @NotNull(message = "Số lượng tuyển không được để trống")
    @Min(value = 1, message = "Số lượng tuyển phải ít nhất là 1")
    private Integer headcount;

    @NotNull(message = "Hạn nộp hồ sơ không được để trống")
    @Future(message = "Hạn nộp hồ sơ phải là ngày trong tương lai")
    private LocalDateTime deadline;

    @NotNull(message = "Phải có ít nhất 1 địa điểm làm việc")
    @Size(min = 1, message = "Phải có ít nhất 1 địa điểm làm việc")
    @Valid
    private List<ReqJobPostLocationDTO> locations;

    private List<@Valid ReqJobPostSkillDTO> skills;

    private Boolean isFeatured;

    private Boolean isUrgent;
}