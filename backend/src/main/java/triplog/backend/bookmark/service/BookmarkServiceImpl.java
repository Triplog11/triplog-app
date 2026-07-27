package triplog.backend.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.bookmark.dto.request.BookmarkRequest.CreateRequest;
import triplog.backend.bookmark.dto.response.BookmarkResponse.BookmarkListResult;
import triplog.backend.bookmark.dto.response.BookmarkResponse.CreateResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.DeleteResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.EventListResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.LandmarkListResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.RegionListResponse;
import triplog.backend.bookmark.entity.Bookmark;
import triplog.backend.bookmark.entity.BookmarkType;
import triplog.backend.bookmark.exception.BookmarkException;
import triplog.backend.bookmark.repository.BookmarkRepository;
import triplog.backend.event.repository.EventRepository;
import triplog.backend.event.service.EventService;
import triplog.backend.landmark.repository.LandmarkRepository;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.repository.RegionRepository;
import triplog.backend.region.service.RegionService;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;

import java.util.Arrays;

import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_FORBIDDEN;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_LIST_NOT_FOUND;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_NOT_FOUND;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_TARGET_NOT_FOUND;

/**
 * {@link BookmarkService}의 구현 클래스입니다.
 * <p>
 * 북마크 등록, 해제, 목록 조회 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UsersRepository usersRepository;
    private final RegionService regionService;
    private final LandmarkService landmarkService;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final LandmarkRepository landmarkRepository;
    private final RegionRepository regionRepository;

    /**
     * 로그인 사용자의 북마크를 해제합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param bookmarkId 해제할 북마크 ID
     * @return 북마크 해제 결과
     * @throws BookmarkException 북마크를 찾을 수 없거나 권한이 없는 경우
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
     * 로그인 사용자의 북마크를 등록합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param request 북마크 등록 요청
     * @return 북마크 등록 결과
     * @throws BookmarkException 대상을 찾을 수 없거나 이미 등록된 경우
     */
    @Override
    @Transactional
    public CreateResponse createBookmark(String usersId, CreateRequest request) {
        boolean validType = Arrays.stream(BookmarkType.values())
                .anyMatch(type -> type.name().equals(request.getBookmarkType()));

        if (!validType) {
            throw new BookmarkException(BOOKMARK_TARGET_NOT_FOUND);
        }

        BookmarkType bookmarkType = BookmarkType.valueOf(request.getBookmarkType());

        boolean targetExists = switch (bookmarkType) {
            case REGION -> regionService.existsById(request.getBookmarkIdentifier());
            case LANDMARK -> landmarkService.existsById(request.getBookmarkIdentifier());
            case EVENT -> eventService.existsById(request.getBookmarkIdentifier());
        };
        if (!targetExists) {
            throw new BookmarkException(BOOKMARK_TARGET_NOT_FOUND);
        }

        boolean alreadyExists = bookmarkRepository.existsByUsersUsersIdAndBookmarkTypeAndBookmarkIdentifier(
                usersId, bookmarkType, request.getBookmarkIdentifier()
        );
        if (alreadyExists) {
            throw new BookmarkException(BOOKMARK_ALREADY_EXISTS);
        }

        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new BookmarkException(BOOKMARK_TARGET_NOT_FOUND));

        Bookmark bookmark = new Bookmark(users, bookmarkType, request.getBookmarkIdentifier());
        Bookmark saved = bookmarkRepository.save(bookmark);

        return CreateResponse.toDto(saved);
    }

    /**
     * 로그인 사용자의 북마크 목록을 타입별로 조회합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param bookmarkType 북마크 타입 (EVENT, LANDMARK, REGION)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 타입에 맞는 북마크 목록 응답
     * @throws BookmarkException 유효하지 않은 북마크 타입이거나 페이지 범위 초과인 경우
     */
    @Override
    public BookmarkListResult getBookmarks(String usersId, String bookmarkType, int page, int size) {
        boolean validType = Arrays.stream(BookmarkType.values())
                .anyMatch(type -> type.name().equals(bookmarkType));
        if (!validType) {
            throw new BookmarkException(BOOKMARK_LIST_NOT_FOUND);
        }

        BookmarkType type = BookmarkType.valueOf(bookmarkType);
        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUsersUsersIdAndBookmarkType(
                usersId, type, PageRequest.of(page, size)
        );

        if (page > 0 && page >= bookmarkPage.getTotalPages()) {
            throw new BookmarkException(BOOKMARK_LIST_NOT_FOUND);
        }

        return switch (type) {
            case EVENT -> EventListResponse.toDto(bookmarkPage,
                    id -> eventRepository.findById(id).orElse(null));
            case LANDMARK -> LandmarkListResponse.toDto(bookmarkPage,
                    id -> landmarkRepository.findById(id).orElse(null));
            case REGION -> RegionListResponse.toDto(bookmarkPage,
                    id -> regionRepository.findById(id).orElse(null));
        };
    }
}
