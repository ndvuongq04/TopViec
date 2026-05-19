package com.topviec.topviec_be.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResJobPostLocationDTO {

    private Long id;
    private Long provinceId;
    private String addressDetail;
    private Boolean isRemote;
}
