package triplog.backend.common.auth.client;

/**
 * 외부 소셜 로그인 제공자 API와 통신하는 Client의 공통 계약입니다.
 * <p>
 * Google, Naver처럼 OAuth 인가 코드를 백엔드에서 검증해야 하는 외부 제공자 연동에 사용합니다.
 * 자체 이메일/비밀번호 로그인은 외부 API Client가 아니므로 이 인터페이스의 대상이 아닙니다.
 */
public interface SocialApiClient {
}
