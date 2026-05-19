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
public class ReqRejectJobPostDTO {

    @NotBlank(message = "Lý do từ chối/gỡ tin không được để trống")
    private String rejectionReason;

    private String moderationNote;

}
