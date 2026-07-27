package triplog.backend.bookmark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import triplog.backend.common.exception.BaseErrorCode;

/**
 * 북마크(Bookmark) 도메인에서 사용하는 에러 코드를 정의하는 Enum입니다.
 */
@Getter
@RequiredArgsConstructor
public enum BookmarkErrorCode implements BaseErrorCode {

    /**
     * 북마크 정보를 찾을 수 없을 때 사용하는 에러 코드입니다.
     */
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크 정보를 찾을 수 없습니다."),

    /**
     * 북마크를 해제할 권한이 없을 때 사용하는 에러 코드입니다.
     */
    BOOKMARK_FORBIDDEN(HttpStatus.FORBIDDEN, "북마크를 해제할 권한이 없습니다."),

    /**
     * 이미 해제된 북마크를 다시 해제하려 할 때 사용하는 에러 코드입니다.
     */
    BOOKMARK_ALREADY_DELETED(HttpStatus.CONFLICT, "북마크가 이미 해제된 항목입니다."),

    /**
     * 이미 북마크가 등록된 대상을 다시 등록하려 할 때 사용하는 에러 코드입니다.
     */
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 북마크 지정된 항목입니다."),

    /**
     * 북마크 등록 권한이 없을 때 사용하는 에러 코드입니다.
     */
    BOOKMARK_CREATE_FORBIDDEN(HttpStatus.FORBIDDEN, "북마크 등록을 할 권한이 없습니다."),

    /**
     * 북마크할 대상을 찾을 수 없을 때 사용하는 에러 코드입니다.
     */
    BOOKMARK_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크 할 대상을 찾을 수 없습니다."),

    /**
     * 북마크 목록 정보를 찾을 수 없을 때 사용하는 에러 코드입니다.
     */
    BOOKMARK_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크 정보들을 찾을 수 없습니다.");

    /**
     * 클라이언트에 응답할 HTTP 상태 코드입니다.
     */
    private final HttpStatus httpStatus;

    /**
     * 클라이언트에 전달할 에러 메시지입니다.
     */
    private final String message;
}
