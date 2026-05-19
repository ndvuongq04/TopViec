package com.topviec.topviec_be.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResLevelDTO {

    private Long id;
    private String name;
    private Integer rank;
}