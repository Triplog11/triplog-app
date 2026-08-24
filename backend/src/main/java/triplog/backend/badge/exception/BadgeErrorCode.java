package triplog.backend.badge.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 배지 도메인에서 발생할 수 있는 오류의 HTTP 상태와 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum BadgeErrorCode implements BaseErrorCode {

    /** 페이지 번호 또는 크기가 유효하지 않은 경우입니다. */
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "페이지 번호와 크기가 올바르지 않습니다."),

    /** 요청한 배지 목록 페이지가 조회 범위를 벗어난 경우입니다. */
    BADGES_NOT_FOUND(HttpStatus.NOT_FOUND, "뱃지 정보들을 찾을 수 없습니다."),

    /** 요청한 ID에 해당하는 배지가 존재하지 않는 경우입니다. */
    BADGE_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "뱃지 상세 정보를 찾을 수 없습니다."),

    /** 사용자가 대표로 지정하려는 배지를 아직 획득하지 않은 경우입니다. */
    BADGE_NOT_ACQUIRED(HttpStatus.NOT_FOUND, "획득한 뱃지를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
