package triplog.backend.common.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 인증(Auth) 도메인에서 사용하는 에러 코드를 정의하는 Enum입니다.
 * <p>
 * 각 에러 코드는 클라이언트에 전달할 HTTP 상태 코드와 메시지를 포함하며,
 * {@link triplog.backend.common.exception.ErrorResponse}에서 공통 응답 형식으로 변환됩니다.
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    /**
     * 소셜 로그인 인가 코드가 비어 있는 경우 사용합니다.
     */
    AUTHORIZATION_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "소셜 로그인 인가 코드는 필수입니다."),

    /**
     * 현재 지원하지 않는 로그인 타입으로 요청한 경우 사용합니다.
     */
    UNSUPPORTED_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 방식입니다."),

    /**
     * Google 토큰 발급 요청이 실패한 경우 사용합니다.
     */
    GOOGLE_TOKEN_REQUEST_FAILED(HttpStatus.UNAUTHORIZED, "Google 토큰 발급에 실패했습니다."),

    /**
     * Google 토큰 발급 응답이 비어 있거나 필수 값이 없는 경우 사용합니다.
     */
    GOOGLE_TOKEN_RESPONSE_INVALID(HttpStatus.UNAUTHORIZED, "Google 토큰 응답이 올바르지 않습니다."),

    /**
     * Google ID Token 형식이 올바르지 않은 경우 사용합니다.
     */
    GOOGLE_ID_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Google ID Token이 올바르지 않습니다."),

    /**
     * Google ID Token에서 이메일을 찾을 수 없는 경우 사용합니다.
     */
    GOOGLE_EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Google 계정 이메일을 찾을 수 없습니다.");

    /**
     * 클라이언트에게 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에게 전달할 에러 메시지입니다.
     */
    private final String message;
}
