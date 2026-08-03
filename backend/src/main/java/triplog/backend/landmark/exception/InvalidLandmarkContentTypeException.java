package triplog.backend.landmark.exception;

/**
 * Landmark에 허용되지 않은 TourAPI 콘텐츠 타입이 입력된 경우 발생하는 예외입니다.
 */
public class InvalidLandmarkContentTypeException extends RuntimeException {

    /**
     * 허용되지 않은 콘텐츠 타입을 포함한 예외를 생성합니다.
     *
     * @param contentTypeId TourAPI 콘텐츠 타입
     */
    public InvalidLandmarkContentTypeException(String contentTypeId) {
        super("Landmark는 contentTypeId 12, 14, 28만 허용합니다: " + contentTypeId);
    }
}
