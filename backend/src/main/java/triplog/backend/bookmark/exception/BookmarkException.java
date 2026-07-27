package triplog.backend.bookmark.exception;

import lombok.Getter;

/**
 * 북마크(Bookmark) 도메인 비즈니스 로직 수행 중 발생하는 전용 예외 클래스입니다.
 */
@Getter
public class BookmarkException extends RuntimeException {

    /**
     * 발생한 예외의 구체적인 종류인 HTTP 상태 코드와 메시지를 담고 있는 Enum입니다.
     */
    private final BookmarkErrorCode errorCode;

    /**
     * 북마크 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 북마크 오류 코드
     */
    public BookmarkException(BookmarkErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
