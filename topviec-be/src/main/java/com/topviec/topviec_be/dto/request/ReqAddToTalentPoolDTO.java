package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.application.TalentPoolSource;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAddToTalentPoolDTO {

    @NotNull(message = "candidateUserId không được để trống")
    private Long candidateUserId;

    @NotNull(message = "source không được để trống")
    private TalentPoolSource source;

    private String note;
}
