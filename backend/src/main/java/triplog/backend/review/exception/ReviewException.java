package triplog.backend.review.exception;

import lombok.Getter;

/**
 * 리뷰 도메인의 비즈니스 규칙 위반을 전달하는 예외입니다.
 */
@Getter
public class ReviewException extends RuntimeException {

    private final ReviewErrorCode errorCode;

    /**
     * 리뷰 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 리뷰 오류 코드
     */
    public ReviewException(ReviewErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
