package triplog.backend.image.exception;

import lombok.Getter;

/**
 * 이미지 업로드 처리 중 발생하는 도메인 예외입니다.
 */
@Getter
public class ImageException extends RuntimeException {

    /**
     * 발생한 오류의 HTTP 상태와 메시지를 담은 이미지 오류 코드입니다.
     */
    private final ImageErrorCode errorCode;

    /**
     * 지정한 이미지 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 이미지 오류 코드
     */
    public ImageException(ImageErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 지정한 이미지 오류 코드와 원인 예외로 예외를 생성합니다.
     *
     * @param errorCode 발생한 이미지 오류 코드
     * @param cause 이미지 업로드 실패의 원인 예외
     */
    public ImageException(ImageErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
