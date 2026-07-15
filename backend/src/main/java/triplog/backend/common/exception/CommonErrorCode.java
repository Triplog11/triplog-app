package triplog.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 특정 도메인에 속하지 않는 공통 에러 코드를 정의하는 Enum입니다.
 * <p>
 * 요청 형식 오류, 유효성 검증 실패, 예상하지 못한 서버 내부 오류처럼
 * 여러 도메인에서 공통으로 사용할 수 있는 예외 응답에 사용됩니다.
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {

    /**
     * 요청 파라미터, 요청 본문, 유효성 검증 값이 올바르지 않은 경우 사용하는 에러 코드입니다.
     */
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    /**
     * 짧은 시간 안에 허용된 횟수를 초과해 요청한 경우 사용하는 에러 코드입니다.
     */
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청을 너무 많이 보냈습니다."),

    /**
     * 별도로 처리되지 않은 서버 내부 오류가 발생한 경우 사용하는 에러 코드입니다.
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    /**
     * 클라이언트에게 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에게 전달할 에러 메시지입니다.
     */
    private final String message;
}
