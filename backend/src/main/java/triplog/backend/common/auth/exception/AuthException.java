package triplog.backend.common.auth.exception;

import lombok.Getter;

/**
 * 인증(Auth) 도메인의 비즈니스 로직 수행 중 발생하는 전용 예외 클래스입니다.
 * <p>
 * 자체 로그인, 소셜 로그인, 토큰 재발급, 로그아웃 등 인증 관련 서비스 로직에서
 * 예외 상황이 발생했을 때 이 클래스를 throw 합니다.
 * {@code GlobalExceptionHandler}에서 이 예외를 가로채고 {@link AuthErrorCode}에 정의된
 * HTTP 상태 코드와 메시지로 응답을 변환합니다.
 */
@Getter
public class AuthException extends RuntimeException {

    /**
     * 발생한 예외의 구체적인 종류(상태 코드, 메시지)를 담고 있는 Enum입니다.
     */
    private final AuthErrorCode errorCode;

    /**
     * 인증 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 인증 오류 코드
     */
    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
