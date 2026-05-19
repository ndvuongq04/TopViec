package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqJobPostLocationDTO {

    @NotNull(message = "Tỉnh/thành phố không được để trống")
    private Long provinceId;

    private String addressDetail;

    @NotNull(message = "Hỗ trợ remote không được để trống")
    private Boolean isRemote;
}