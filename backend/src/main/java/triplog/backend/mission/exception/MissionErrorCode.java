package triplog.backend.mission.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 미션(Mission) 도메인에서 사용하는 에러 코드를 정의하는 Enum입니다.
 */
@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements BaseErrorCode {

    /**
     * 미션 진행 정보를 찾을 수 없을 때 사용하는 에러 코드입니다.
     */
    MISSION_PROGRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "미션 진행 정보를 찾을 수 없습니다."),

    /**
     * 미션 정보를 찾을 수 없을 때 사용하는 에러 코드입니다.
     */
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "미션 정보를 찾을 수 없습니다.");

    /**
     * 클라이언트에 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 전달할 에러 메시지입니다.
     */
    private final String message;
}
