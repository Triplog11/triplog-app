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

    /** 방문 인증 리뷰를 찾을 수 없거나 조회 권한이 없는 경우입니다. */
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "방문 인증 정보를 찾을 수 없습니다."),

    /** 랜드마크 정보를 찾을 수 없는 경우입니다. */
    LANDMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "랜드마크 정보를 찾을 수 없습니다."),

    /** 관광 콘텐츠 정보를 찾을 수 없는 경우입니다. */
    TOURISM_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "관광 콘텐츠 정보를 찾을 수 없습니다."),

    /** 방문 인증을 지원하지 않는 관광 콘텐츠인 경우입니다. */
    UNSUPPORTED_VISIT_CONTENT(HttpStatus.BAD_REQUEST, "방문 인증을 지원하지 않는 관광 콘텐츠입니다."),

    /** 인증 위치 코드가 관광 콘텐츠 지역과 일치하지 않는 경우입니다. */
    REGION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증 위치가 관광 콘텐츠 지역과 일치하지 않습니다."),

    /** 페이지 번호 또는 크기가 올바르지 않은 경우입니다. */
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "페이지 번호와 크기를 확인해주세요."),

    /** 멱등성 키가 비어 있거나 허용 길이를 초과한 경우입니다. */
    INVALID_IDEMPOTENCY_KEY(HttpStatus.BAD_REQUEST, "Idempotency-Key를 1자 이상 100자 이하로 입력해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
