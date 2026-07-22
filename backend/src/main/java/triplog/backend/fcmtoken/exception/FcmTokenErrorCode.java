package triplog.backend.fcmtoken.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * FCM 푸시 토큰 도메인에서 사용하는 에러 코드를 정의하는 Enum입니다.
 * <p>
 * 각 에러 코드는 HTTP 상태 코드와 클라이언트에 전달할 메시지를 포함합니다.
 */
@Getter
@RequiredArgsConstructor
public enum FcmTokenErrorCode implements BaseErrorCode {

    /**
     * 요청한 FCM 푸시 토큰 정보를 찾을 수 없는 경우 사용합니다.
     */
    FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM 토큰 정보를 찾을 수 없습니다."),

    /**
     * 동일한 FCM 토큰이 이미 등록되어 있는 경우 사용합니다.
     */
    FCM_TOKEN_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 등록된 푸시 토큰입니다.");

    /**
     * 클라이언트에 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 전달할 에러 메시지입니다.
     */
    private final String message;
}
