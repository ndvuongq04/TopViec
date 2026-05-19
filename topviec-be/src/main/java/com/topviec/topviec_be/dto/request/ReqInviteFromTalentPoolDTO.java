package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqInviteFromTalentPoolDTO {

    @NotNull(message = "jobPostId không được để trống")
    private Long jobPostId;
}
