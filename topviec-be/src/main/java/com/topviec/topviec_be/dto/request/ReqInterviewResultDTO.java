package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqInterviewResultDTO {

    @NotBlank(message = "Kết quả phỏng vấn không được trống")
    private String result; // PASS / FAIL

    @Min(value = 1, message = "Rating tối thiểu là 1")
    @Max(value = 5, message = "Rating tối đa là 5")
    private Integer rating;

    private String note;

    private Boolean notifyCandidate;
}
