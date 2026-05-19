package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateApplicationCvDTO {
    @NotNull(message = "CV ID không được để trống")
    private Long cvId;
}
