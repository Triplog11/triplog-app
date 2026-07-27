package triplog.backend.bookmark.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import triplog.backend.bookmark.dto.request.BookmarkRequest.CreateRequest;
import triplog.backend.bookmark.dto.response.BookmarkResponse.CreateResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.DeleteResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.EventListResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.LandmarkListResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.RegionListResponse;
import triplog.backend.bookmark.entity.Bookmark;
import triplog.backend.bookmark.entity.BookmarkType;
import triplog.backend.bookmark.exception.BookmarkException;
import triplog.backend.bookmark.repository.BookmarkRepository;
import triplog.backend.event.entity.Event;
import triplog.backend.event.repository.EventRepository;
import triplog.backend.event.service.EventService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.repository.LandmarkRepository;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.repository.RegionRepository;
import triplog.backend.region.service.RegionService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_FORBIDDEN;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_LIST_NOT_FOUND;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_NOT_FOUND;
import static triplog.backend.bookmark.exception.BookmarkErrorCode.BOOKMARK_TARGET_NOT_FOUND;

/**
 * {@link BookmarkServiceImpl}의 북마크 등록, 해제, 목록 조회 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

    private static final String USERS_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String OTHER_USERS_ID = "550e8400-e29b-41d4-a716-446655440099";
    private static final Long BOOKMARK_ID = 1L;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RegionService regionService;

    @Mock
    private LandmarkService landmarkService;

    @Mock
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private LandmarkRepository landmarkRepository;

    @Mock
    private RegionRepository regionRepository;

    private BookmarkServiceImpl bookmarkService;

    @BeforeEach
    void setUp() {
        bookmarkService = new BookmarkServiceImpl(
                bookmarkRepository, usersRepository,
                regionService, landmarkService, eventService,
                eventRepository, landmarkRepository, regionRepository
        );
    }

    /**
     * 본인의 북마크를 정상적으로 해제하는지 검증합니다.
     */
    @Test
    @DisplayName("북마크를 정상 해제한다")
    void deleteBookmark() {
        // given
        Bookmark bookmark = mock(Bookmark.class);
        Users users = mock(Users.class);
        when(bookmark.getUsers()).thenReturn(users);
        when(users.getUsersId()).thenReturn(USERS_ID);
        given(bookmarkRepository.findById(BOOKMARK_ID)).willReturn(Optional.of(bookmark));

        // when
        DeleteResponse response = bookmarkService.deleteBookmark(USERS_ID, BOOKMARK_ID);

        // then
        assertThat(response.getIsDeleted()).isTrue();
        verify(bookmarkRepository).delete(bookmark);
    }

    /**
     * 존재하지 않는 북마크 ID로 해제하면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("존재하지 않는 북마크이면 예외가 발생한다")
    void deleteBookmark_NotFound() {
        // given
        given(bookmarkRepository.findById(BOOKMARK_ID)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> bookmarkService.deleteBookmark(USERS_ID, BOOKMARK_ID))
                .isInstanceOf(BookmarkException.class)
                .extracting("errorCode")
                .isEqualTo(BOOKMARK_NOT_FOUND);
    }

    /**
     * 다른 사용자의 북마크를 해제하면 권한 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("본인의 북마크가 아니면 권한 예외가 발생한다")
    void deleteBookmark_Forbidden() {
        // given
        Bookmark bookmark = mock(Bookmark.class);
        Users users = mock(Users.class);
        when(bookmark.getUsers()).thenReturn(users);
        when(users.getUsersId()).thenReturn(OTHER_USERS_ID);
        given(bookmarkRepository.findById(BOOKMARK_ID)).willReturn(Optional.of(bookmark));

        // when / then
        assertThatThrownBy(() -> bookmarkService.deleteBookmark(USERS_ID, BOOKMARK_ID))
                .isInstanceOf(BookmarkException.class)
                .extracting("errorCode")
                .isEqualTo(BOOKMARK_FORBIDDEN);
        verify(bookmarkRepository, never()).delete(any());
    }

    /**
     * REGION 타입 북마크를 정상적으로 등록하는지 검증합니다.
     */
    @Test
    @DisplayName("REGION 북마크를 정상 등록한다")
    void createBookmark_Region() {
        // given
        CreateRequest request = new CreateRequest("REGION", 101L);
        Users users = mock(Users.class);
        Bookmark saved = mock(Bookmark.class);

        given(regionService.existsById(101L)).willReturn(true);
        given(bookmarkRepository.existsByUsersUsersIdAndBookmarkTypeAndBookmarkIdentifier(
                USERS_ID, BookmarkType.REGION, 101L)).willReturn(false);
        given(usersRepository.findById(USERS_ID)).willReturn(Optional.of(users));
        given(bookmarkRepository.save(any(Bookmark.class))).willReturn(saved);
        when(saved.getBookmarkId()).thenReturn(9001L);
        when(saved.getBookmarkType()).thenReturn(BookmarkType.REGION);
        when(saved.getBookmarkIdentifier()).thenReturn(101L);

        // when
        CreateResponse response = bookmarkService.createBookmark(USERS_ID, request);

        // then
        assertThat(response.getBookmarkId()).isEqualTo(9001L);
        assertThat(response.getBookmarkType()).isEqualTo("REGION");
        assertThat(response.getBookmarkIdentifier()).isEqualTo(101L);
        assertThat(response.getBookmarked()).isTrue();
    }

    /**
     * 북마크 대상이 존재하지 않으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("대상이 존재하지 않으면 예외가 발생한다")
    void createBookmark_TargetNotFound() {
        // given
        CreateRequest request = new CreateRequest("REGION", 999L);
        given(regionService.existsById(999L)).willReturn(false);

        // when / then
        assertThatThrownBy(() -> bookmarkService.createBookmark(USERS_ID, request))
                .isInstanceOf(BookmarkException.class)
                .extracting("errorCode")
                .isEqualTo(BOOKMARK_TARGET_NOT_FOUND);
        verify(bookmarkRepository, never()).save(any());
    }

    /**
     * 이미 등록된 북마크를 다시 등록하면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("이미 등록된 북마크이면 예외가 발생한다")
    void createBookmark_AlreadyExists() {
        // given
        CreateRequest request = new CreateRequest("LANDMARK", 5L);
        given(landmarkService.existsById(5L)).willReturn(true);
        given(bookmarkRepository.existsByUsersUsersIdAndBookmarkTypeAndBookmarkIdentifier(
                USERS_ID, BookmarkType.LANDMARK, 5L)).willReturn(true);

        // when / then
        assertThatThrownBy(() -> bookmarkService.createBookmark(USERS_ID, request))
                .isInstanceOf(BookmarkException.class)
                .extracting("errorCode")
                .isEqualTo(BOOKMARK_ALREADY_EXISTS);
        verify(bookmarkRepository, never()).save(any());
    }

    /**
     * 유효하지 않은 북마크 타입이면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("유효하지 않은 북마크 타입이면 예외가 발생한다")
    void createBookmark_InvalidType() {
        // given
        CreateRequest request = new CreateRequest("INVALID", 1L);

        // when / then
        assertThatThrownBy(() -> bookmarkService.createBookmark(USERS_ID, request))
                .isInstanceOf(BookmarkException.class)
                .extracting("errorCode")
                .isEqualTo(BOOKMARK_TARGET_NOT_FOUND);
    }

    /**
     * EVENT 타입 북마크 목록을 정상 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("EVENT 타입 북마크 목록을 조회한다")
    void getBookmarks_Event() {
        // given
        Bookmark bookmark = mock(Bookmark.class);
        when(bookmark.getBookmarkId()).thenReturn(9101L);
        when(bookmark.getBookmarkIdentifier()).thenReturn(201L);

        Event event = mock(Event.class);
        TourismContent tc = mock(TourismContent.class);
        when(event.getEventId()).thenReturn(201L);
        when(event.getTourismContent()).thenReturn(tc);
        when(tc.getTitle()).thenReturn("수원 문화 행사");
        when(tc.getPrimaryImageUrl()).thenReturn("event-main.png");
        when(event.getEventStartDate()).thenReturn(LocalDate.of(2026, 7, 1));
        when(event.getEventEndDate()).thenReturn(LocalDate.of(2026, 7, 7));

        Page<Bookmark> page = new PageImpl<>(List.of(bookmark), PageRequest.of(0, 10), 1);
        given(bookmarkRepository.findByUsersUsersIdAndBookmarkType(
                eq(USERS_ID), eq(BookmarkType.EVENT), any(Pageable.class))).willReturn(page);
        given(eventRepository.findById(201L)).willReturn(Optional.of(event));

        // when
        Object result = bookmarkService.getBookmarks(USERS_ID, "EVENT", 0, 10);

        // then
        assertThat(result).isInstanceOf(EventListResponse.class);
        EventListResponse response = (EventListResponse) result;
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getBookmarks()).hasSize(1);
        assertThat(response.getBookmarks().get(0).getEventId()).isEqualTo(201L);
        assertThat(response.getBookmarks().get(0).getEventTitle()).isEqualTo("수원 문화 행사");
    }

    /**
     * LANDMARK 타입 북마크 목록을 정상 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("LANDMARK 타입 북마크 목록을 조회한다")
    void getBookmarks_Landmark() {
        // given
        Bookmark bookmark = mock(Bookmark.class);
        when(bookmark.getBookmarkId()).thenReturn(9011L);
        when(bookmark.getBookmarkIdentifier()).thenReturn(301L);

        Landmark landmark = mock(Landmark.class);
        TourismContent tc = mock(TourismContent.class);
        Region region = mock(Region.class);
        when(landmark.getLandmarkId()).thenReturn(301L);
        when(landmark.getLandmarkName()).thenReturn("수원화성");
        when(landmark.getTourismContent()).thenReturn(tc);
        when(tc.getRegion()).thenReturn(region);
        when(tc.getExternalContentId()).thenReturn("TOUR-10001");
        when(region.getRegionId()).thenReturn(101L);
        when(region.getRegionName()).thenReturn("수원시");

        Page<Bookmark> page = new PageImpl<>(List.of(bookmark), PageRequest.of(0, 10), 1);
        given(bookmarkRepository.findByUsersUsersIdAndBookmarkType(
                eq(USERS_ID), eq(BookmarkType.LANDMARK), any(Pageable.class))).willReturn(page);
        given(landmarkRepository.findById(301L)).willReturn(Optional.of(landmark));

        // when
        Object result = bookmarkService.getBookmarks(USERS_ID, "LANDMARK", 0, 10);

        // then
        assertThat(result).isInstanceOf(LandmarkListResponse.class);
        LandmarkListResponse response = (LandmarkListResponse) result;
        assertThat(response.getBookmarks()).hasSize(1);
        assertThat(response.getBookmarks().get(0).getLandmarkName()).isEqualTo("수원화성");
        assertThat(response.getBookmarks().get(0).getRegionName()).isEqualTo("수원시");
        assertThat(response.getBookmarks().get(0).getContentId()).isEqualTo("TOUR-10001");
    }

    /**
     * REGION 타입 북마크 목록을 정상 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("REGION 타입 북마크 목록을 조회한다")
    void getBookmarks_Region() {
        // given
        Bookmark bookmark = mock(Bookmark.class);
        when(bookmark.getBookmarkId()).thenReturn(9201L);
        when(bookmark.getBookmarkIdentifier()).thenReturn(101L);

        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(101L);
        when(region.getRegionName()).thenReturn("서울특별시 강남구");

        Page<Bookmark> page = new PageImpl<>(List.of(bookmark), PageRequest.of(0, 10), 1);
        given(bookmarkRepository.findByUsersUsersIdAndBookmarkType(
                eq(USERS_ID), eq(BookmarkType.REGION), any(Pageable.class))).willReturn(page);
        given(regionRepository.findById(101L)).willReturn(Optional.of(region));

        // when
        Object result = bookmarkService.getBookmarks(USERS_ID, "REGION", 0, 10);

        // then
        assertThat(result).isInstanceOf(RegionListResponse.class);
        RegionListResponse response = (RegionListResponse) result;
        assertThat(response.getBookmarks()).hasSize(1);
        assertThat(response.getBookmarks().get(0).getRegionName()).isEqualTo("서울특별시 강남구");
    }

    /**
     * 존재하지 않는 페이지를 요청하면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("존재하지 않는 페이지 요청 시 예외가 발생한다")
    void getBookmarks_PageOutOfRange() {
        // given
        Page<Bookmark> emptyPage = new PageImpl<>(List.of(), PageRequest.of(2, 10), 10);
        given(bookmarkRepository.findByUsersUsersIdAndBookmarkType(
                eq(USERS_ID), eq(BookmarkType.EVENT), any(Pageable.class))).willReturn(emptyPage);

        // when / then
        assertThatThrownBy(() -> bookmarkService.getBookmarks(USERS_ID, "EVENT", 2, 10))
                .isInstanceOf(BookmarkException.class)
                .extracting("errorCode")
                .isEqualTo(BOOKMARK_LIST_NOT_FOUND);
    }

    /**
     * 유효하지 않은 bookmarkType이면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("유효하지 않은 bookmarkType이면 예외가 발생한다")
    void getBookmarks_InvalidType() {
        // when / then
        assertThatThrownBy(() -> bookmarkService.getBookmarks(USERS_ID, "INVALID", 0, 10))
                .isInstanceOf(BookmarkException.class)
                .extracting("errorCode")
                .isEqualTo(BOOKMARK_LIST_NOT_FOUND);
    }
}
