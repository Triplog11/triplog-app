package triplog.backend.event.exception;

/**
 * Event에 허용되지 않은 TourAPI 콘텐츠 타입이 입력된 경우 발생하는 예외입니다.
 */
public class InvalidEventContentTypeException extends RuntimeException {

    /**
     * 허용되지 않은 콘텐츠 타입을 포함한 예외를 생성합니다.
     *
     * @param contentTypeId TourAPI 콘텐츠 타입
     */
    public InvalidEventContentTypeException(String contentTypeId) {
        super("Event는 contentTypeId=15만 허용합니다: " + contentTypeId);
    }
}
