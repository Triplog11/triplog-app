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
     * 자체 로그인 이메일이 비어 있는 경우 사용합니다.
     */
    LOCAL_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "자체 로그인 이메일은 필수입니다."),

    /**
     * 자체 로그인 비밀번호가 비어 있는 경우 사용합니다.
     */
    LOCAL_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "자체 로그인 비밀번호는 필수입니다."),

    /**
     * 자체 로그인 이메일 또는 비밀번호가 일치하지 않는 경우 사용합니다.
     */
    LOCAL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    /**
     * Access Token의 유효 기간이 만료된 경우 사용합니다.
     */
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),

    /**
     * Refresh Token이 만료된 경우 사용합니다.
     */
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

    /**
     * Refresh Token이 유효하지 않거나 저장된 토큰과 일치하지 않는 경우 사용합니다.
     */
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),

    /**
     * Naver 소셜 로그인 state 값이 비어 있는 경우 사용합니다.
     */
    NAVER_STATE_REQUIRED(HttpStatus.BAD_REQUEST, "Naver 로그인 state 값은 필수입니다."),

    /**
     * Google 토큰 발급 요청에 실패한 경우 사용합니다.
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
    GOOGLE_EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Google 계정 이메일을 찾을 수 없습니다."),

    /**
     * Naver 토큰 발급 요청에 실패한 경우 사용합니다.
     */
    NAVER_TOKEN_REQUEST_FAILED(HttpStatus.UNAUTHORIZED, "Naver 토큰 발급에 실패했습니다."),

    /**
     * Naver 토큰 발급 응답이 비어 있거나 필수 값이 없는 경우 사용합니다.
     */
    NAVER_TOKEN_RESPONSE_INVALID(HttpStatus.UNAUTHORIZED, "Naver 토큰 응답이 올바르지 않습니다."),

    /**
     * Naver 사용자 정보 요청에 실패한 경우 사용합니다.
     */
    NAVER_USER_INFO_REQUEST_FAILED(HttpStatus.UNAUTHORIZED, "Naver 사용자 정보 조회에 실패했습니다."),

    /**
     * Naver 사용자 정보 응답에서 이메일을 찾을 수 없는 경우 사용합니다.
     */
    NAVER_EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Naver 계정 이메일을 찾을 수 없습니다."),

    /**
     * 회원가입용 임시 토큰이 유효하지 않은 경우 사용합니다.
     */
    TEMPORARY_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 회원가입용 임시 토큰입니다."),

    /**
     * 로그아웃 대상 Refresh Token 정보를 찾을 수 없는 경우 사용합니다.
     */
    LOGOUT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰 정보를 찾을 수 없습니다.");

    /**
     * 클라이언트에 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 전달할 에러 메시지입니다.
     */
    private final String message;
}
