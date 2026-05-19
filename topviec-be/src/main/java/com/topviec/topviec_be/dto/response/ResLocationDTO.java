package com.topviec.topviec_be.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResLocationDTO {

    private Long id;
    private String name;
    private String code;
    private Integer sortOrder;
}