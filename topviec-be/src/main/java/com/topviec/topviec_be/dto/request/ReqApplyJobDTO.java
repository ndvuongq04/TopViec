package com.topviec.topviec_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqApplyJobDTO {

    private Long cvId;
    private String applyMethod; // normal | quick | bulk
}