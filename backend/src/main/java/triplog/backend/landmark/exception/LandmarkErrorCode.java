package triplog.backend.landmark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 랜드마크 도메인에서 발생할 수 있는 오류의 HTTP 상태와 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum LandmarkErrorCode implements BaseErrorCode {

    /** 랜드마크 상세 정보를 찾을 수 없는 경우입니다. */
    LANDMARK_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "랜드마크 상세 정보를 찾을 수 없습니다."),

    /** 랜드마크에 연결된 카드를 찾을 수 없는 경우입니다. */
    LANDMARK_CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "랜드마크 카드 정보를 찾을 수 없습니다."),

    /** 페이지 번호 또는 크기가 올바르지 않은 경우입니다. */
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "페이지 번호와 크기를 확인해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
