package triplog.backend.notification.exception;

import lombok.Getter;

/**
 * 알림 도메인 비즈니스 로직 처리 중 발생하는 예외입니다.
 */
@Getter
public class NotificationException extends RuntimeException {

    /**
     * 발생한 오류의 HTTP 상태와 메시지를 담은 알림 오류 코드입니다.
     */
    private final NotificationErrorCode errorCode;

    /**
     * 지정한 알림 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 알림 오류 코드
     */
    public NotificationException(NotificationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
