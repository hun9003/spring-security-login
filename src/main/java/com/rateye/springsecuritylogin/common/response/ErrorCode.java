package com.rateye.springsecuritylogin.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 장애 상황
    COMMON_INVALID_PARAMETER("요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND("존재하지 않는 엔티티입니다."),
    COMMON_ILLEGAL_STATUS("잘못된 상태값입니다."),
    COMMON_BAD_REQUEST("잘못된 요청 입니다."),

    // USER
    USER_REDUPLICATION_ID("이미 회원가입 된 아이디 입니다"),
    USER_REDUPLICATION_EMAIL("이미 회원가입 된 이메일 입니다"),
    USER_NOT_FIND("회원을 찾을 수 없습니다."),
    USER_FAIL_LOGIN("아이디 혹은 비밀번호가 일치 하지 않습니다."),
    USER_BAD_PERMISSION_TOKEN("권한 정보가 없는 토큰입니다."),
    USER_BAD_REFRESH_TOKEN("Refresh Token 정보가 유효 하지 않습니다."),
    USER_FAIL_REFRESH_TOKEN("Refresh Token 정보가 일치 하지 않습니다."),
    USER_FAIL_EXPIRED_TOKEN("토큰이 만료 되었습니다."),
    USER_FAIL_INVALID_TOKEN("유효하지 않는 토큰 입니다."),
    USER_FAIL_UNSUPPORTED_TOKEN("지원하지 않는 토큰 입니다."),
    USER_FAIL_NON_LOGIN("로그인 후 이용 가능 합니다.");


    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
