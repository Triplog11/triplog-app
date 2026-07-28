package triplog.backend.region.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 지역 도메인에서 발생할 수 있는 오류의 HTTP 상태와 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum RegionErrorCode implements BaseErrorCode {

    /** 전국 지도 현황 정보를 찾을 수 없는 경우입니다. */
    NATIONWIDE_MAP_NOT_FOUND(HttpStatus.NOT_FOUND, "전국 지도 현황 정보를 찾을 수 없습니다."),

    /** 광역 지도 현황 정보를 찾을 수 없는 경우입니다. */
    PROVINCE_MAP_NOT_FOUND(HttpStatus.NOT_FOUND, "광역 지도 현황 정보를 찾을 수 없습니다."),

    /** 지역 상세 정보를 찾을 수 없는 경우입니다. */
    REGION_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "지역 상세 정보를 찾을 수 없습니다."),

    /** 지역 목록 정보를 찾을 수 없는 경우입니다. */
    REGION_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "지역 정보들을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
