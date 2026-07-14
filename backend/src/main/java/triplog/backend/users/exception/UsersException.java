package triplog.backend.users.exception;

import lombok.Getter;

/**
 * 사용자(Users) 도메인 비즈니스 로직 수행 중 발생하는 전용 예외 클래스입니다.
 * <p>
 * 회원 가입, 정보 수정, 닉네임 중복 확인 등 사용자 관련 서비스 로직에서 예외 상황 발생 시 이 클래스를 throw 합니다.
 * {@code GlobalExceptionHandler}에서 이 예외를 가로채어 {@link UsersErrorCode}에 정의된 표준 응답으로 변환합니다.
 */
@Getter
public class UsersException extends RuntimeException {

    /**
     * 발생한 예외의 구체적인 종류(상태 코드, 메시지)를 담고 있는 Enum입니다.
     */
    private final UsersErrorCode errorCode;

    /**
     * 사용자 오류 코드로 예외를 생성합니다.
     *
     * @param errorCode 발생한 사용자 오류 코드
     */
    public UsersException(UsersErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
