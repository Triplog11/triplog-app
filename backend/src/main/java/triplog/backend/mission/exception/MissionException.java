package triplog.backend.mission.exception;

import lombok.Getter;

/**
 * 미션(Mission) 도메인 비즈니스 로직 수행 중 발생하는 전용 예외 클래스입니다.
 */
@Getter
public class MissionException extends RuntimeException {

    /**
     * 발생한 예외의 구체적인 종류인 HTTP 상태 코드와 메시지를 담고 있는 Enum입니다.
     */
    private final MissionErrorCode errorCode;

    /**
     * 미션 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 미션 오류 코드
     */
    public MissionException(MissionErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
