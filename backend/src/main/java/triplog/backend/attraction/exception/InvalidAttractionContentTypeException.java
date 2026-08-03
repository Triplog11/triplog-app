package triplog.backend.attraction.exception;

/**
 * 관광지가 지원하지 않는 TourAPI 콘텐츠 유형과 연결될 때 발생합니다.
 */
public class InvalidAttractionContentTypeException extends RuntimeException {

    /**
     * 허용되지 않은 TourAPI 콘텐츠 유형을 포함한 예외를 생성합니다.
     *
     * @param contentTypeId 일반 관광지로 등록하려 한 TourAPI 콘텐츠 유형
     */
    public InvalidAttractionContentTypeException(String contentTypeId) {
        super("관광지는 TourAPI contentTypeId 12, 14, 28만 등록할 수 있습니다. contentTypeId=" + contentTypeId);
    }
}
