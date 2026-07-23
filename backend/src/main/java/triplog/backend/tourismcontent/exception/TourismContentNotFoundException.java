package triplog.backend.tourismcontent.exception;

/**
 * 요청한 TourAPI contentId의 관광 콘텐츠가 DB에 없을 때 발생하는 예외입니다.
 */
public class TourismContentNotFoundException extends RuntimeException {

    /**
     * TourAPI contentId를 포함한 예외를 생성합니다.
     *
     * @param externalContentId TourAPI contentId
     */
    public TourismContentNotFoundException(String externalContentId) {
        super("관광 콘텐츠를 찾을 수 없습니다: " + externalContentId);
    }
}
