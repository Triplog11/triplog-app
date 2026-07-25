package triplog.backend.rankpolicy.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 랭크 정책 도메인에서 발생하는 오류의 HTTP 상태와 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum RankPolicyErrorCode implements BaseErrorCode {

    /**
     * 요청한 랭크 정책 정보를 찾을 수 없는 경우 사용합니다.
     */
    RANK_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "랭크 정책 정보를 찾을 수 없습니다.");

    /**
     * 클라이언트에 반환할 HTTP 상태입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 반환할 오류 메시지입니다.
     */
    private final String message;
}
