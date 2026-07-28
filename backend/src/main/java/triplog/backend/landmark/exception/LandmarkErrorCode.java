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
    LANDMARK_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "랜드마크 상세 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
