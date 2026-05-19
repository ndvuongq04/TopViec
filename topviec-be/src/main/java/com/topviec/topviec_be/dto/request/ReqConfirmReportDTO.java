package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqConfirmReportDTO {

    @NotNull(message = "approved không được để trống")
    private Boolean approved;

    private String resolutionNote;
}
