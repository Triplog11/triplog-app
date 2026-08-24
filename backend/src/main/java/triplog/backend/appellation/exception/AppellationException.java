package triplog.backend.appellation.exception;

import lombok.Getter;

/**
 * 칭호 도메인의 비즈니스 규칙 위반을 전달하는 예외입니다.
 */
@Getter
public class AppellationException extends RuntimeException {

    private final AppellationErrorCode errorCode;

    /**
     * 칭호 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 칭호 오류 코드
     */
    public AppellationException(AppellationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
