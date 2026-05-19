package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResShareTokenDTO {
    private String shareToken;
    private LocalDateTime expiresAt;
}
