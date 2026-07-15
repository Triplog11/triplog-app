package triplog.backend.users.service;

import triplog.backend.users.entity.LoginType;
import triplog.backend.users.dto.response.UsersResponse.NicknameCheckResponse;

import java.util.Optional;

/**
 * 사용자(Users)와 관련된 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * 회원 가입, 로그인, 사용자 정보 조회 및 수정 등 사용자 도메인의 비즈니스 기능을 선언합니다.
 */
public interface UsersService {

    /**
     * 이메일과 로그인 타입으로 인증 처리에 필요한 사용자 정보를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 로그인 타입
     * @return 인증 처리에 필요한 사용자 정보
     */
    Optional<UsersAuthInfo> findAuthInfoByEmailAndLoginType(String email, LoginType loginType);

    /**
     * 소셜 로그인 추가정보를 기반으로 신규 사용자를 생성합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 소셜 로그인 타입
     * @param nickname 닉네임
     * @param profileUrl 프로필 이미지 URL
     * @return 저장된 사용자 요약 정보
     */
    UsersSignupInfo createSocialUser(String email, LoginType loginType, String nickname, String profileUrl);

    /**
     * 닉네임 사용 가능 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임 사용 가능 여부와 결과 메시지
     */
    NicknameCheckResponse checkNickname(String nickname);
}
