package triplog.backend.levelpolicy.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 레벨 정책 설정 오류의 HTTP 상태와 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum LevelPolicyErrorCode implements BaseErrorCode {

    /** 레벨 정책이 한 건도 등록되지 않은 경우입니다. */
    LEVEL_POLICY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "레벨 정책이 등록되지 않았습니다."),

    /** 레벨 번호가 연속적이지 않거나 필요 XP가 유효하지 않은 경우입니다. */
    INVALID_LEVEL_POLICY(HttpStatus.INTERNAL_SERVER_ERROR, "레벨 정책 설정이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
