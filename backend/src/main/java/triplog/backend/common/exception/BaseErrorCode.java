package triplog.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 모든 도메인 에러 코드 Enum이 구현해야 할 공통 인터페이스입니다.
 * <p>
 * 이 인터페이스를 통해 각기 다른 도메인의 에러 코드들을
 * {@link ErrorResponse}에서 통일된 방식으로 처리할 수 있습니다.
 */
public interface BaseErrorCode {

    HttpStatus getHttpStatus();

    String getMessage();

    String name();
}