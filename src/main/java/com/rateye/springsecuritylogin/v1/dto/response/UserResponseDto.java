package com.rateye.springsecuritylogin.v1.dto.response;

import com.rateye.springsecuritylogin.entity.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserResponseDto {

    @Getter
    public static class UserInfo {
        private final String id;
        private final String email;

        public UserInfo(Users user) {
            this.id = user.getId();
            this.email = user.getEmail();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
    }
}
