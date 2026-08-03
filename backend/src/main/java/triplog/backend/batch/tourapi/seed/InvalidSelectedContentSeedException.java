package triplog.backend.batch.tourapi.seed;

/**
 * 선정 관광 콘텐츠 CSV의 형식이나 분류가 올바르지 않을 때 발생합니다.
 */
public class InvalidSelectedContentSeedException extends RuntimeException {

    /**
     * CSV 검증 실패 메시지로 예외를 생성합니다.
     *
     * @param message 검증 실패 사유
     */
    public InvalidSelectedContentSeedException(String message) {
        super(message);
    }

    /**
     * CSV 읽기 또는 검증 실패 원인을 포함한 예외를 생성합니다.
     *
     * @param message 검증 실패 사유
     * @param cause 원인이 된 예외
     */
    public InvalidSelectedContentSeedException(String message, Throwable cause) {
        super(message, cause);
    }
}
