package triplog.backend.users.service;

import triplog.backend.users.entity.LoginType;
import triplog.backend.users.entity.Users;

import java.util.Optional;

/**
 * 사용자(Users)와 관련된 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * 회원 가입, 로그인, 사용자 정보 조회 및 수정 등
 * 사용자 도메인의 비즈니스 기능을 선언합니다.
 */
public interface UsersService {

    /**
     * 이메일과 로그인 타입으로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 로그인 타입
     * @return 조회된 사용자
     */
    Optional<Users> findByEmailAndLoginType(String email, LoginType loginType);
}
