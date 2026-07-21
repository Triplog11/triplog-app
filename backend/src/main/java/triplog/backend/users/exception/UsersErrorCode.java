package triplog.backend.users.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 사용자(Users) 도메인에서 사용하는 에러 코드를 정의하는 Enum입니다.
 * <p>
 * 각 에러 코드는 HTTP 상태 코드와 클라이언트에 전달할 메시지를 포함하며,
 * {@link triplog.backend.common.exception.ErrorResponse}에서 공통 응답 형식으로 변환됩니다.
 */
@Getter
@RequiredArgsConstructor
public enum UsersErrorCode implements BaseErrorCode {

    /**
     * 요청한 사용자 정보를 찾을 수 없거나 프로필 수정 대상이 올바르지 않은 경우 사용합니다.
     */
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    /**
     * 이미 사용 중인 닉네임으로 프로필 수정을 요청한 경우 사용합니다.
     */
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    /**
     * 이미 회원가입이 완료된 이메일로 회원가입을 요청한 경우 사용합니다.
     */
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 회원가입이 완료된 이메일입니다.");

    /**
     * 클라이언트에 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 전달할 에러 메시지입니다.
     */
    private final String message;
}
