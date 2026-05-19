package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqExtendJobPostDTO {

    @NotNull(message = "Hạn nộp hồ sơ mới không được để trống")
    @Future(message = "Hạn nộp hồ sơ mới phải là ngày trong tương tương lai")
    private LocalDateTime newDeadline;
}
