package triplog.backend.batch.tourapi.seed;

/**
 * 랜드마크 CSV의 구조 또는 값이 동기화 규칙에 맞지 않을 때 발생합니다.
 */
public class InvalidLandmarkSeedException extends RuntimeException {

    /**
     * 검증 실패 사유로 예외를 생성합니다.
     *
     * @param message 비밀정보를 포함하지 않은 실패 사유
     */
    public InvalidLandmarkSeedException(String message) {
        super(message);
    }

    /**
     * CSV 읽기 실패 원인을 포함해 예외를 생성합니다.
     *
     * @param message 실패 사유
     * @param cause 원본 예외
     */
    public InvalidLandmarkSeedException(String message, Throwable cause) {
        super(message, cause);
    }
}
