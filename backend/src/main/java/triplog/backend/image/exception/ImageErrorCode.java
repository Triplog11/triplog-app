package triplog.backend.image.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 이미지 도메인에서 발생하는 오류의 HTTP 상태와 응답 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {

    /**
     * 요청 파일이 비어 있거나 이미지 형식이 아닌 경우 사용합니다.
     */
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "유효한 이미지 파일이 필요합니다."),

    /**
     * Cloudinary 이미지 업로드 처리에 실패한 경우 사용합니다.
     */
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");

    /**
     * 클라이언트에 반환할 HTTP 상태입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 반환할 오류 메시지입니다.
     */
    private final String message;
}
