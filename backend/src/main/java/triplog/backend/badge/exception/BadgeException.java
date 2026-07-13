package triplog.backend.badge.exception;

import lombok.Getter;

/**
 * 배지 도메인의 비즈니스 규칙 위반을 전달하는 예외입니다.
 */
@Getter
public class BadgeException extends RuntimeException {

    private final BadgeErrorCode errorCode;

    /**
     * 배지 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 배지 오류 코드
     */
    public BadgeException(BadgeErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
