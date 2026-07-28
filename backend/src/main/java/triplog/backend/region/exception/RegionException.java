package triplog.backend.region.exception;

import lombok.Getter;

/**
 * 지역 도메인의 비즈니스 규칙 위반을 전달하는 예외입니다.
 */
@Getter
public class RegionException extends RuntimeException {

    private final RegionErrorCode errorCode;

    /**
     * 지역 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 지역 오류 코드
     */
    public RegionException(RegionErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
