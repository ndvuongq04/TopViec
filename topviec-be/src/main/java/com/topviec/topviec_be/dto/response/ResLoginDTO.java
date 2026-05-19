package com.topviec.topviec_be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResLoginDTO {
    private String accessToken; // refreshToken bỏ ra khỏi body
    private UserInfo user;

    @Getter
    @Setter
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String role;
    }
}