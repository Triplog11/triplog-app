package triplog.backend.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import triplog.backend.event.dto.response.EventResponse.EventDetailResponse;
import triplog.backend.event.dto.response.EventResponse.EventListResponse;
import triplog.backend.event.entity.Event;
import triplog.backend.event.exception.EventException;
import triplog.backend.event.repository.EventRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static triplog.backend.event.exception.EventErrorCode.EVENT_DETAIL_NOT_FOUND;

/**
 * {@link EventServiceImpl}의 이벤트 상세 조회 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository);
    }

    @Test
    @DisplayName("이벤트 상세 정보를 조회한다")
    void getEventDetail() {
        // given
        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getTitle()).thenReturn("수원 문화 행사");
        when(tourismContent.getOverview()).thenReturn("지역 이벤트 안내");
        when(tourismContent.getPrimaryImageUrl()).thenReturn("https://example.com/event-main.png");
        when(tourismContent.getThumbnailImageUrl()).thenReturn("https://example.com/event-sub.png");

        Event event = mock(Event.class);
        when(event.getEventId()).thenReturn(201L);
        when(event.getTourismContent()).thenReturn(tourismContent);
        when(event.getEventStartDate()).thenReturn(LocalDate.of(2026, 7, 1));
        when(event.getEventEndDate()).thenReturn(LocalDate.of(2026, 7, 7));
        given(eventRepository.findByEventId(201L)).willReturn(Optional.of(event));

        // when
        EventDetailResponse response = eventService.getEventDetail(201L);

        // then
        assertThat(response.getEventId()).isEqualTo(201L);
        assertThat(response.getEventTitle()).isEqualTo("수원 문화 행사");
        assertThat(response.getEventContent()).isEqualTo("지역 이벤트 안내");
        assertThat(response.getEventImageUrl1()).isEqualTo("https://example.com/event-main.png");
        assertThat(response.getEventImageUrl2()).isEqualTo("https://example.com/event-sub.png");
        assertThat(response.getEventStart()).isEqualTo("2026-07-01T00:00:00");
        assertThat(response.getEventEnd()).isEqualTo("2026-07-07T00:00:00");
    }

    @Test
    @DisplayName("이벤트 기간이 없으면 null로 반환한다")
    void getEventDetail_NullDate() {
        // given
        TourismContent tourismContent = mock(TourismContent.class);
        Event event = mock(Event.class);
        when(event.getEventId()).thenReturn(201L);
        when(event.getTourismContent()).thenReturn(tourismContent);
        given(eventRepository.findByEventId(201L)).willReturn(Optional.of(event));

        // when
        EventDetailResponse response = eventService.getEventDetail(201L);

        // then
        assertThat(response.getEventStart()).isNull();
        assertThat(response.getEventEnd()).isNull();
    }

    @Test
    @DisplayName("이벤트 상세 조회 시 이벤트가 없으면 예외가 발생한다")
    void getEventDetail_NotFound() {
        // given
        given(eventRepository.findByEventId(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventService.getEventDetail(999L))
                .isInstanceOf(EventException.class)
                .extracting("errorCode")
                .isEqualTo(EVENT_DETAIL_NOT_FOUND);
    }

    @Test
    @DisplayName("이벤트 목록을 페이지 단위로 조회한다")
    void getEvents() {
        // given
        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getTitle()).thenReturn("수원 문화 행사");
        when(tourismContent.getOverview()).thenReturn("지역 이벤트 안내");
        when(tourismContent.getPrimaryImageUrl()).thenReturn("https://example.com/event-main.png");

        Event event = mock(Event.class);
        when(event.getEventId()).thenReturn(201L);
        when(event.getTourismContent()).thenReturn(tourismContent);
        when(event.getEventStartDate()).thenReturn(LocalDate.of(2026, 7, 1));
        when(event.getEventEndDate()).thenReturn(LocalDate.of(2026, 7, 7));

        Pageable pageable = PageRequest.of(0, 10);
        given(eventRepository.findAll(pageable))
                .willReturn(new PageImpl<>(List.of(event), pageable, 42));

        // when
        EventListResponse response = eventService.getEvents(pageable);

        // then
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(42);
        assertThat(response.getTotalPages()).isEqualTo(5);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().getFirst().getEventId()).isEqualTo(201L);
        assertThat(response.getItems().getFirst().getEventTitle()).isEqualTo("수원 문화 행사");
        assertThat(response.getItems().getFirst().getEventContent()).isEqualTo("지역 이벤트 안내");
        assertThat(response.getItems().getFirst().getEventImageUrl())
                .isEqualTo("https://example.com/event-main.png");
        assertThat(response.getItems().getFirst().getEventStart()).isEqualTo("2026-07-01T00:00:00");
        assertThat(response.getItems().getFirst().getEventEnd()).isEqualTo("2026-07-07T00:00:00");
    }

    @Test
    @DisplayName("이벤트가 없으면 빈 목록을 반환한다")
    void getEvents_Empty() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        given(eventRepository.findAll(pageable))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));

        // when
        EventListResponse response = eventService.getEvents(pageable);

        // then
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
        assertThat(response.getItems()).isEmpty();
    }
}
