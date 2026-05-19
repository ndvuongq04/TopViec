package com.topviec.topviec_be.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqBulkApplyDTO {

    private Long cvId;
    private List<Long> jobPostIds; // tối đa 10
}