package triplog.backend.stats.exception;

import lombok.Getter;

/**
 * 사용자 통계(Stats) 도메인 비즈니스 로직 수행 중 발생하는 전용 예외 클래스입니다.
 * <p>
 * 통계 정보 생성, 조회, 수정 및 점수 처리 등 Stats 관련 서비스 로직에서
 * 예외 상황 발생 시 이 클래스를 throw 합니다.
 * {@code GlobalExceptionHandler}에서 이 예외를 가로채어
 * {@link StatsErrorCode}에 정의된 표준 응답으로 변환합니다.
 */
@Getter
public class StatsException extends RuntimeException {

    /**
     * 발생한 예외의 구체적인 종류인 HTTP 상태 코드와 메시지를 담고 있는 Enum입니다.
     */
    private final StatsErrorCode errorCode;

    /**
     * 통계 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 통계 오류 코드
     */
    public StatsException(StatsErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
