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

    CONTENT_ID_REQUIRED(HttpStatus.BAD_REQUEST, "TourAPI contentId는 필수입니다."),
    CONFIGURATION_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "TourAPI 설정이 올바르지 않습니다."),
    HTTP_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "TourAPI 요청에 실패했습니다."),
    HTTP_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, "TourAPI가 HTTP 요청을 거부했습니다."),
    REQUEST_LIMIT_EXCEEDED(HttpStatus.SERVICE_UNAVAILABLE, "TourAPI 요청 제한 횟수를 초과했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "TourAPI 서비스를 일시적으로 사용할 수 없습니다."),
    API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "TourAPI가 요청을 정상 처리하지 못했습니다."),
    RESPONSE_INVALID(HttpStatus.BAD_GATEWAY, "TourAPI 응답 형식이 올바르지 않습니다."),
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "TourAPI에서 관광 콘텐츠를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
