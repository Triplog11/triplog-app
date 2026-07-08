package triplog.backend.common.auth.client;

import triplog.backend.users.entity.LoginType;

/**
 * 외부 소셜 로그인 제공자 API와 통신하는 Client의 공통 계약입니다.
 * <p>
 * Google, Naver처럼 OAuth 인가 코드를 백엔드에서 검증해야 하는 외부 제공자 연동에 사용합니다.
 * 자체 이메일/비밀번호 로그인은 외부 API Client가 아니므로 이 인터페이스의 대상이 아닙니다.
 */
public interface SocialApiClient {

    /**
     * 전달된 로그인 제공자를 처리할 수 있는지 확인합니다.
     *
     * @param provider 로그인 제공자
     * @return 처리 가능 여부
     */
    boolean supports(LoginType provider);

    /**
     * 소셜 인가 코드로 로그인에 사용할 이메일을 조회합니다.
     *
     * @param code 소셜 인가 코드
     * @return 소셜 계정 이메일
     */
    String getEmail(String code);
}
