package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqProcessReportDTO {

    /** approve | reject */
    @NotBlank(message = "decision không được để trống")
    private String decision;

    private String resolutionNote;
}
