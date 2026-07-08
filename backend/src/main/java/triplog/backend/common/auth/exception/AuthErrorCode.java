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

    ;

    /**
     * 클라이언트에게 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에게 전달할 에러 메시지입니다.
     */
    private final String message;
}
