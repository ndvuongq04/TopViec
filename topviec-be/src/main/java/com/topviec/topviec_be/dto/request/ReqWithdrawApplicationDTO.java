package com.topviec.topviec_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqWithdrawApplicationDTO {

    private String withdrawalReason; // tùy chọn
}