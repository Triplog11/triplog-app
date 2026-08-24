package triplog.backend.appellation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 칭호 도메인에서 발생할 수 있는 오류의 HTTP 상태와 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum AppellationErrorCode implements BaseErrorCode {

    /** 사용자가 대표로 지정하려는 칭호를 아직 획득하지 않은 경우입니다. */
    APPELLATION_NOT_ACQUIRED(HttpStatus.NOT_FOUND, "획득한 칭호를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
