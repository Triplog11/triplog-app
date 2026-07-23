package triplog.backend.batch.tourapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * TourAPI 연동 과정에서 사용하는 오류 코드입니다.
 */
@Getter
@RequiredArgsConstructor
public enum TourApiErrorCode implements BaseErrorCode {

    /** TourAPI 요청에 필요한 contentId가 전달되지 않은 경우입니다. */
    CONTENT_ID_REQUIRED(HttpStatus.BAD_REQUEST, "TourAPI contentId는 필수입니다."),

    /** 서비스 키나 기본 URL 등 TourAPI 연동 설정이 유효하지 않은 경우입니다. */
    CONFIGURATION_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "TourAPI 설정이 올바르지 않습니다."),

    /** 네트워크 오류 등으로 TourAPI HTTP 요청 자체를 완료하지 못한 경우입니다. */
    HTTP_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "TourAPI 요청에 실패했습니다."),

    /** TourAPI가 4xx 응답으로 요청을 거부한 경우입니다. */
    HTTP_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, "TourAPI가 HTTP 요청을 거부했습니다."),

    /** TourAPI 호출 한도를 초과한 경우입니다. */
    REQUEST_LIMIT_EXCEEDED(HttpStatus.SERVICE_UNAVAILABLE, "TourAPI 요청 제한 횟수를 초과했습니다."),

    /** TourAPI가 일시적인 장애나 서버 오류로 응답할 수 없는 경우입니다. */
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "TourAPI 서비스를 일시적으로 사용할 수 없습니다."),

    /** HTTP 요청은 성공했지만 TourAPI 결과 코드가 실패를 나타낸 경우입니다. */
    API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "TourAPI가 요청을 정상 처리하지 못했습니다."),

    /** TourAPI 응답 본문이 없거나 예상한 구조와 다른 경우입니다. */
    RESPONSE_INVALID(HttpStatus.BAD_GATEWAY, "TourAPI 응답 형식이 올바르지 않습니다."),

    /** 요청한 contentId에 해당하는 관광 콘텐츠가 TourAPI에 없는 경우입니다. */
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "TourAPI에서 관광 콘텐츠를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
