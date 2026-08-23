package triplog.backend.event.exception;

import lombok.Getter;

/**
 * 이벤트 도메인의 비즈니스 규칙 위반을 전달하는 예외입니다.
 */
@Getter
public class EventException extends RuntimeException {

    private final EventErrorCode errorCode;

    /**
     * 이벤트 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 이벤트 오류 코드
     */
    public EventException(EventErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
