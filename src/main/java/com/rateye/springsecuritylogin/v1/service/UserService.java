package com.rateye.springsecuritylogin.v1.service;

import com.rateye.springsecuritylogin.common.exception.InvalidParamException;
import com.rateye.springsecuritylogin.common.response.ErrorCode;
import com.rateye.springsecuritylogin.common.util.jwt.JwtTokenProvider;
import com.rateye.springsecuritylogin.common.util.redis.RedisUtil;
import com.rateye.springsecuritylogin.entity.user.Users;
import com.rateye.springsecuritylogin.entity.user.role.Authority;
import com.rateye.springsecuritylogin.v1.dto.request.UserRequestDto;
import com.rateye.springsecuritylogin.v1.dto.response.UserResponseDto;
import com.rateye.springsecuritylogin.v1.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;
    private final RedisUtil redisUtil;

    /**
     * 회원 가입 서비스
     * @param request   회원 가입 DTO
     * @return          회원 정보 객체
     */
    @Transactional
    public UserResponseDto.UserInfo register(UserRequestDto.SignUp request) {
        if (usersRepository.existsById(request.getId())) throw new InvalidParamException(ErrorCode.USER_REDUPLICATION_ID);
        if (usersRepository.existsByEmail(request.getEmail())) throw new InvalidParamException(ErrorCode.USER_REDUPLICATION_ID);

        Users user = Users.builder()
                .id(request.getId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        usersRepository.save(user);
        return new UserResponseDto.UserInfo(user);
    }

    /**
     * 로그인 서비스
     * @param request   로그인 DTO
     * @return          토큰 정보 객체
     */
    @Transactional(readOnly = true)
    public UserResponseDto.TokenInfo login(UserRequestDto.Login request) {

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

            // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
            redisTemplate.opsForValue().set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

            return tokenInfo;
        } catch (BadCredentialsException e) {
            throw new InvalidParamException(ErrorCode.USER_FAIL_LOGIN);
        }

    }

    /**
     * 토큰 갱신 서비스
     * @param request   토큰 갱신 DTO
     * @return          토큰 정보
     */
    public UserResponseDto.TokenInfo reissueToken(UserRequestDto.Reissue request) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) throw new InvalidParamException(ErrorCode.USER_BAD_REFRESH_TOKEN);

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(request.getAccessToken());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());
        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) throw new InvalidParamException(ErrorCode.COMMON_BAD_REQUEST);

        if(!refreshToken.equals(request.getRefreshToken())) throw new InvalidParamException(ErrorCode.USER_FAIL_REFRESH_TOKEN);

        // 4. 새로운 토큰 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfo;
    }

    /**
     * 로그아웃 서비스
     * @param request    로그아웃 DTO
     */
    public void logout(UserRequestDto.Logout request) {
        // 1. Access Token 검증
        if (!jwtTokenProvider.validateToken(request.getAccessToken())) throw new InvalidParamException(ErrorCode.COMMON_BAD_REQUEST);

        // 2. Access Token 에서 User ID 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(request.getAccessToken());

        // 3. Redis 에서 해당 User ID 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제 합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(request.getAccessToken());
        redisUtil.setBlackList(request.getAccessToken(), "access_token", expiration);
    }

}
