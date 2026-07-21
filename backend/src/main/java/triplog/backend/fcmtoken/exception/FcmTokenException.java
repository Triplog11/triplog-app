package triplog.backend.fcmtoken.exception;

import lombok.Getter;

/**
 * FCM 푸시 토큰 도메인 비즈니스 로직 수행 중 발생하는 전용 예외 클래스입니다.
 * <p>
 * FCM 토큰 등록, 조회, 수정, 삭제 처리 중 예외 상황이 발생하면 이 예외를 사용합니다.
 */
@Getter
public class FcmTokenException extends RuntimeException {

    /**
     * 발생한 예외의 구체적인 종류와 HTTP 상태 코드, 메시지를 담고 있는 에러 코드입니다.
     */
    private final FcmTokenErrorCode errorCode;

    /**
     * FCM 푸시 토큰 에러 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 FCM 푸시 토큰 에러 코드
     */
    public FcmTokenException(FcmTokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
