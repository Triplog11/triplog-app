package triplog.backend.batch.tourapi.exception;

import lombok.Getter;

/**
 * TourAPI 요청 또는 응답 처리 실패를 나타내는 예외입니다.
 */
@Getter
public class TourApiException extends RuntimeException {

    private final TourApiErrorCode errorCode;
    private final String providerCode;

    /**
     * 애플리케이션 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode TourAPI 연동 오류 코드
     */
    public TourApiException(TourApiErrorCode errorCode) {
        this(errorCode, null, null);
    }

    /**
     * 애플리케이션 오류 코드와 원인 예외로 예외를 생성합니다.
     *
     * @param errorCode TourAPI 연동 오류 코드
     * @param cause 오류의 원인이 된 예외
     */
    public TourApiException(TourApiErrorCode errorCode, Throwable cause) {
        this(errorCode, null, cause);
    }

    /**
     * 애플리케이션 오류 코드와 TourAPI 제공자 코드로 예외를 생성합니다.
     *
     * @param errorCode TourAPI 연동 오류 코드
     * @param providerCode TourAPI 또는 HTTP 제공자 오류 코드
     */
    public TourApiException(TourApiErrorCode errorCode, String providerCode) {
        this(errorCode, providerCode, null);
    }

    /**
     * 오류 코드, 제공자 코드, 원인 예외를 모두 포함해 예외를 생성합니다.
     *
     * @param errorCode TourAPI 연동 오류 코드
     * @param providerCode TourAPI 또는 HTTP 제공자 오류 코드
     * @param cause 오류의 원인이 된 예외
     */
    private TourApiException(
            TourApiErrorCode errorCode,
            String providerCode,
            Throwable cause
    ) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.providerCode = providerCode;
    }
}
