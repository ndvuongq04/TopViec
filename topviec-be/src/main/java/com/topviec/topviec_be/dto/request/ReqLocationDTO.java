package com.topviec.topviec_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqLocationDTO {

    private String name;
    private String code;
    private Integer sortOrder;
}