package triplog.backend.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 리뷰 도메인에서 발생할 수 있는 오류의 HTTP 상태와 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {

    /** 랜드마크 정보를 찾을 수 없는 경우입니다. */
    LANDMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "랜드마크 정보를 찾을 수 없습니다."),

    /** 관광 콘텐츠 정보를 찾을 수 없는 경우입니다. */
    TOURISM_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "관광 콘텐츠 정보를 찾을 수 없습니다."),

    /** 방문 인증을 지원하지 않는 관광 콘텐츠인 경우입니다. */
    UNSUPPORTED_VISIT_CONTENT(HttpStatus.BAD_REQUEST, "방문 인증을 지원하지 않는 관광 콘텐츠입니다."),

    /** 인증 위치 코드가 관광 콘텐츠 지역과 일치하지 않는 경우입니다. */
    REGION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증 위치가 관광 콘텐츠 지역과 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
