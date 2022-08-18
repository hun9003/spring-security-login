# 스프링 시큐리티 로그인 프로젝트

## 개발 환경
- SpringBoot 2.7.2
- Java 11

## Entity
- [유저](https://github.com/hun9003/spring-security-login/blob/master/src/main/java/com/rateye/springsecuritylogin/entity/user)

## 필수 설치

### docker 설치
- MacOS : [https://docs.docker.com/docker-for-mac/install/](https://docs.docker.com/docker-for-mac/install/)
- Window : [https://docs.docker.com/docker-for-windows/install/](https://docs.docker.com/docker-for-windows/install/)

### MySQL 설치
docker를 통해 MySQL을 설치하고 진행했습니다.<br/>
terminal 을 통해 [spring-security-login/docker-compose/](https://github.com/hun9003/spring-security-login/tree/master/docker-compose) 디렉토리에 위치한 다음 <br/>
```docker-compose -p demo-db up -d``` 를 입력합니다.

### Redis 설치
토큰 재발급 등의 서비스를 위해 Redis를 설치 합니다.
```
# 이미지 다운 (docker images 로 확인 가능)
$ docker pull redis

# 컨테이너로 레디스 실행 (--name: 컨테이너 이름 설정, -p: 포트 포워딩, -d: 백그라운드에서 실행)
$ docker run --name demo-redis -p 6379:6379 -d redis

# redis-cli 접속
$ docker exec -it demo-redis redis-cli
```

## API 명세서
API 명세서는 Swagger 3.0.0 과 연동하여 작성 했습니다. <br/>
서버를 실행 한 뒤 ```http://localhost:8080/swagger-ui/index.html``` 로 이동하시면 확인 하실 수 있습니다.

