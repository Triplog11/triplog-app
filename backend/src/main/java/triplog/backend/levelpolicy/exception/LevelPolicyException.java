package triplog.backend.levelpolicy.exception;

import lombok.Getter;

/**
 * 레벨 정책 조회 또는 계산 중 발견된 설정 오류를 전달합니다.
 */
@Getter
public class LevelPolicyException extends RuntimeException {

    private final LevelPolicyErrorCode errorCode;

    /**
     * 레벨 정책 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 레벨 정책 오류 코드
     */
    public LevelPolicyException(LevelPolicyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
