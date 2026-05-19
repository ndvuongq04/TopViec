package com.topviec.topviec_be.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateShareTokenDTO {
    private Integer days;
    private Integer hours;
    private Integer minutes;
}
