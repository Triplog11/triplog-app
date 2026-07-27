package triplog.backend.bookmark.service;

import triplog.backend.bookmark.dto.request.BookmarkRequest.CreateRequest;
import triplog.backend.bookmark.dto.response.BookmarkResponse.BookmarkListResult;
import triplog.backend.bookmark.dto.response.BookmarkResponse.CreateResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.DeleteResponse;

/**
 * 북마크(Bookmark)와 관련된 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 */
public interface BookmarkService {

    /**
     * 로그인 사용자의 북마크를 해제합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param bookmarkId 해제할 북마크 ID
     * @return 북마크 해제 결과
     */
    DeleteResponse deleteBookmark(String usersId, Long bookmarkId);

    /**
     * 로그인 사용자의 북마크를 등록합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param request 북마크 등록 요청
     * @return 북마크 등록 결과
     */
    CreateResponse createBookmark(String usersId, CreateRequest request);

    /**
     * 로그인 사용자의 북마크 목록을 타입별로 조회합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param bookmarkType 북마크 타입 (EVENT, LANDMARK, REGION)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 타입에 맞는 북마크 목록 응답
     */
    BookmarkListResult getBookmarks(String usersId, String bookmarkType, int page, int size);
}
