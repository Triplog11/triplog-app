package triplog.backend.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.bookmark.dto.response.BookmarkResponse.DeleteResponse;
import triplog.backend.bookmark.entity.Bookmark;
import triplog.backend.bookmark.entity.BookmarkType;
import triplog.backend.bookmark.exception.BookmarkException;
import triplog.backend.bookmark.repository.BookmarkRepository;
import triplog.backend.users.entity.Users;

import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_FORBIDDEN;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_LIST_NOT_FOUND;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_NOT_FOUND;

/**
 * {@link BookmarkService}의 기본 구현체입니다.
 * 북마크 도메인의 저장소와 중복·소유권 규칙만 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * 로그인 사용자의 북마크를 해제합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param bookmarkId 해제할 북마크 식별자
     * @return 북마크 해제 결과
     * @throws BookmarkException 북마크가 없거나 다른 사용자의 북마크인 경우
     */
    @Override
    @Transactional
    public DeleteResponse deleteBookmark(String usersId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BookmarkException(BOOKMARK_NOT_FOUND));
        if (!bookmark.getUsers().getUsersId().equals(usersId)) {
            throw new BookmarkException(BOOKMARK_FORBIDDEN);
        }

        bookmarkRepository.delete(bookmark);
        return DeleteResponse.toDto(true);
    }

    /**
     * 사용자와 대상 식별자로 북마크를 생성합니다.
     *
     * @param users 북마크 소유 사용자
     * @param bookmarkType 북마크 대상 유형
     * @param bookmarkIdentifier 북마크 대상 식별자
     * @return 저장된 북마크
     * @throws BookmarkException 동일 대상이 이미 북마크된 경우
     */
    @Override
    @Transactional
    public Bookmark createBookmark(
            Users users, BookmarkType bookmarkType, Long bookmarkIdentifier
    ) {
        boolean alreadyExists = bookmarkRepository
                .existsByUsersUsersIdAndBookmarkTypeAndBookmarkIdentifier(
                        users.getUsersId(), bookmarkType, bookmarkIdentifier
                );
        if (alreadyExists) {
            throw new BookmarkException(BOOKMARK_ALREADY_EXISTS);
        }

        return bookmarkRepository.save(
                new Bookmark(users, bookmarkType, bookmarkIdentifier)
        );
    }

    /**
     * 사용자의 타입별 북마크 페이지를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param bookmarkType 북마크 대상 유형
     * @param pageable 페이지 정보
     * @return 북마크 페이지
     * @throws BookmarkException 전체 페이지 범위를 벗어난 경우
     */
    @Override
    public Page<Bookmark> getBookmarkPage(
            String usersId, BookmarkType bookmarkType, Pageable pageable
    ) {
        Page<Bookmark> bookmarkPage = bookmarkRepository
                .findByUsersUsersIdAndBookmarkType(usersId, bookmarkType, pageable);
        if (pageable.getPageNumber() > 0
                && pageable.getPageNumber() >= bookmarkPage.getTotalPages()) {
            throw new BookmarkException(BOOKMARK_LIST_NOT_FOUND);
        }
        return bookmarkPage;
    }
}
