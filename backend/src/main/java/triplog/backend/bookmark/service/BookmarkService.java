package triplog.backend.bookmark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import triplog.backend.bookmark.dto.response.BookmarkResponse.DeleteResponse;
import triplog.backend.bookmark.entity.Bookmark;
import triplog.backend.bookmark.entity.BookmarkType;
import triplog.backend.users.entity.Users;

/**
 * 북마크 관계의 생성·삭제·조회 기능을 정의합니다.
 */
public interface BookmarkService {

    /** 로그인 사용자의 북마크를 해제합니다. */
    DeleteResponse deleteBookmark(String usersId, Long bookmarkId);

    /** 사용자와 대상 식별자로 북마크를 생성합니다. */
    Bookmark createBookmark(Users users, BookmarkType bookmarkType, Long bookmarkIdentifier);

    /** 사용자의 타입별 북마크 페이지를 조회합니다. */
    Page<Bookmark> getBookmarkPage(
            String usersId, BookmarkType bookmarkType, Pageable pageable
    );
}
