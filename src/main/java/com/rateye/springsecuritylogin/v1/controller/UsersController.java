package com.rateye.springsecuritylogin.v1.controller;

import com.rateye.springsecuritylogin.common.response.CommonResponse;
import com.rateye.springsecuritylogin.v1.dto.request.UserRequestDto;
import com.rateye.springsecuritylogin.v1.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Api(tags = {"유저 API"})
@RequestMapping("/api/v1/users")
@RestController
public class UsersController {
    private final UserService usersService;

    @PostMapping("/sign-up")
    @ApiOperation(value = "회원가입", notes = "전달 받은 정보로 회원가입을 진행 합니다.")
    public CommonResponse<?> registerMember(@RequestBody @Valid UserRequestDto.SignUp request) {
        var userInfo = usersService.register(request);
        return CommonResponse.success(userInfo, "회원 가입을 완료 했습니다.");
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "아이디와 비밀번호로 로그인을 진행 합니다.")
    public CommonResponse<?> loginMember(@RequestBody @Valid UserRequestDto.Login request) {
        var tokenInfo = usersService.login(request);
        return CommonResponse.success(tokenInfo, "로그인을 완료 했습니다.");
    }

    @PostMapping("/reissue")
    @ApiOperation(value = "토큰 정보 갱신", notes = "refreshToken 으로 accessToken의 유효시간을 갱신 합니다.")
    public CommonResponse<?> reissue(@RequestBody @Valid UserRequestDto.Reissue request) {
        var tokenInfo = usersService.reissueToken(request);
        return CommonResponse.success(tokenInfo, "토큰 정보가 갱신되었습니다.");
    }

    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃", notes = "회원의 토큰 정보를 삭제 합니다")
    public CommonResponse<?> logoutMember(@RequestBody @Valid UserRequestDto.Logout request) {
        usersService.logout(request);
        return CommonResponse.success("로그아웃이 완료되었습니다.");
    }

}
