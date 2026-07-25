package triplog.backend.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 알림 도메인에서 발생하는 오류의 HTTP 상태와 응답 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {

    /**
     * 요청한 알림 목록 페이지를 찾을 수 없는 경우 사용합니다.
     */
    NOTIFICATIONS_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 정보들을 찾을 수 없습니다."),

    /**
     * 알림 목록의 페이지 번호 또는 크기가 올바르지 않은 경우 사용합니다.
     */
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    /**
     * 요청한 알림 정보를 찾을 수 없는 경우 사용합니다.
     */
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 정보를 찾을 수 없습니다."),

    /**
     * 이미 읽음 처리된 알림을 다시 읽음 처리하는 경우 사용합니다.
     */
    NOTIFICATION_ALREADY_READ(HttpStatus.CONFLICT, "이미 읽은 알림입니다."),

    /**
     * 알림 설정에 필요한 정책 정보를 찾을 수 없는 경우 사용합니다.
     */
    NOTIFICATION_SETTINGS_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 설정 정보를 찾을 수 없습니다."),

    /**
     * 요청한 알림 정책 정보를 찾을 수 없는 경우 사용합니다.
     */
    NOTIFICATION_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 정책 정보를 찾을 수 없습니다.");

    /**
     * 클라이언트에 반환할 HTTP 상태입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 반환할 오류 메시지입니다.
     */
    private final String message;
}
