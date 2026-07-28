package triplog.backend.landmark.exception;

import lombok.Getter;

/**
 * 랜드마크 도메인의 비즈니스 규칙 위반을 전달하는 예외입니다.
 */
@Getter
public class LandmarkException extends RuntimeException {

    private final LandmarkErrorCode errorCode;

    /**
     * 랜드마크 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 랜드마크 오류 코드
     */
    public LandmarkException(LandmarkErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
