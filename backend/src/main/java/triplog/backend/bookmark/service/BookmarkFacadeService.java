package triplog.backend.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.bookmark.dto.request.BookmarkRequest.CreateRequest;
import triplog.backend.bookmark.dto.response.BookmarkResponse.BookmarkListResult;
import triplog.backend.bookmark.dto.response.BookmarkResponse.CreateResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.EventListResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.LandmarkListResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.RegionListResponse;
import triplog.backend.bookmark.entity.Bookmark;
import triplog.backend.bookmark.entity.BookmarkType;
import triplog.backend.bookmark.exception.BookmarkErrorCode;
import triplog.backend.bookmark.exception.BookmarkException;
import triplog.backend.event.service.EventService;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.service.RegionService;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;

import java.util.Arrays;

import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_LIST_NOT_FOUND;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_TARGET_NOT_FOUND;

/**
 * 북마크와 사용자·이벤트·랜드마크·지역 도메인의 호출 흐름을 조합합니다.
 */
@Service
@RequiredArgsConstructor
public class BookmarkFacadeService {

    private final BookmarkService bookmarkService;
    private final UsersService usersService;
    private final EventService eventService;
    private final LandmarkService landmarkService;
    private final RegionService regionService;

    /**
     * 대상의 존재를 검증하고 로그인 사용자의 북마크를 등록합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 북마크 등록 요청
     * @return 북마크 등록 응답
     */
    @Transactional
    public CreateResponse createBookmark(String usersId, CreateRequest request) {
        BookmarkType bookmarkType = parseType(
                request.getBookmarkType(), BOOKMARK_TARGET_NOT_FOUND
        );
        validateTargetExists(bookmarkType, request.getBookmarkIdentifier());
        Users users = usersService.findById(usersId);
        Bookmark bookmark = bookmarkService.createBookmark(
                users, bookmarkType, request.getBookmarkIdentifier()
        );
        return CreateResponse.toDto(bookmark);
    }

    /**
     * 타입별 북마크와 실제 대상 정보를 조합해 목록 응답을 생성합니다.
     *
     * @param usersId 사용자 식별자
     * @param bookmarkType 북마크 대상 유형 문자열
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 타입에 맞는 북마크 목록 응답
     */
    @Transactional(readOnly = true)
    public BookmarkListResult getBookmarks(
            String usersId, String bookmarkType, int page, int size
    ) {
        validatePage(page, size);
        BookmarkType type = parseType(bookmarkType, BOOKMARK_LIST_NOT_FOUND);
        Page<Bookmark> bookmarkPage = bookmarkService.getBookmarkPage(
                usersId, type, PageRequest.of(page, size)
        );

        return switch (type) {
            case EVENT -> EventListResponse.toDto(
                    bookmarkPage,
                    id -> eventService.findOptionalById(id).orElse(null)
            );
            case LANDMARK -> LandmarkListResponse.toDto(
                    bookmarkPage,
                    id -> landmarkService.findOptionalByIdWithContent(id).orElse(null)
            );
            case REGION -> RegionListResponse.toDto(
                    bookmarkPage,
                    id -> regionService.findOptionalById(id).orElse(null)
            );
        };
    }

    /**
     * 문자열 북마크 유형을 열거형으로 변환합니다.
     *
     * @param value 변환할 유형 문자열
     * @param errorCode 변환 실패 시 사용할 오류 코드
     * @return 변환된 북마크 유형
     * @throws BookmarkException 지원하지 않는 유형인 경우
     */
    private BookmarkType parseType(String value, BookmarkErrorCode errorCode) {
        return Arrays.stream(BookmarkType.values())
                .filter(type -> type.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new BookmarkException(errorCode));
    }

    /**
     * 북마크 대상이 해당 도메인에 존재하는지 검증합니다.
     *
     * @param bookmarkType 북마크 대상 유형
     * @param targetId 북마크 대상 식별자
     * @throws BookmarkException 대상이 존재하지 않는 경우
     */
    private void validateTargetExists(BookmarkType bookmarkType, Long targetId) {
        boolean exists = switch (bookmarkType) {
            case EVENT -> eventService.existsById(targetId);
            case LANDMARK -> landmarkService.existsById(targetId);
            case REGION -> regionService.existsById(targetId);
        };
        if (!exists) {
            throw new BookmarkException(BOOKMARK_TARGET_NOT_FOUND);
        }
    }

    /**
     * 페이지 번호와 크기가 Spring Data 페이지 요청 범위에 맞는지 검증합니다.
     *
     * @param page 0부터 시작하는 페이지 번호
     * @param size 페이지 크기
     * @throws BookmarkException 페이지 번호가 음수이거나 크기가 1 미만인 경우
     */
    private void validatePage(int page, int size) {
        if (page < 0 || size < 1) {
            throw new BookmarkException(BOOKMARK_LIST_NOT_FOUND);
        }
    }
}
