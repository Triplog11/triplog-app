package triplog.backend.event.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import triplog.backend.event.entity.Event;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 이벤트(Event) 관련 응답 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "이벤트 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventResponse {

    private static final DateTimeFormatter EVENT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * 이벤트 상세 조회 응답 DTO입니다.
     */
    @Getter
    @Schema(description = "이벤트 상세 조회 응답")
    public static class EventDetailResponse {

        @Schema(description = "이벤트 ID", example = "201")
        private final Long eventId;

        @Schema(description = "이벤트 제목", example = "수원 문화 행사")
        private final String eventTitle;

        @Schema(description = "이벤트 내용", nullable = true, example = "지역 이벤트 안내")
        private final String eventContent;

        @Schema(description = "이벤트 대표 이미지 URL", example = "https://example.com/event-main.png")
        private final String eventImageUrl1;

        @Schema(description = "이벤트 썸네일 이미지 URL", nullable = true,
                example = "https://example.com/event-sub.png")
        private final String eventImageUrl2;

        @Schema(description = "이벤트 시작 일시(yyyy-MM-dd'T'HH:mm:ss)", nullable = true,
                example = "2026-07-01T10:00:00")
        private final String eventStart;

        @Schema(description = "이벤트 종료 일시(yyyy-MM-dd'T'HH:mm:ss)", nullable = true,
                example = "2026-07-07T18:00:00")
        private final String eventEnd;

        public EventDetailResponse(
                Long eventId,
                String eventTitle,
                String eventContent,
                String eventImageUrl1,
                String eventImageUrl2,
                String eventStart,
                String eventEnd
        ) {
            this.eventId = eventId;
            this.eventTitle = eventTitle;
            this.eventContent = eventContent;
            this.eventImageUrl1 = eventImageUrl1;
            this.eventImageUrl2 = eventImageUrl2;
            this.eventStart = eventStart;
            this.eventEnd = eventEnd;
        }

        /**
         * 이벤트 엔티티를 상세 조회 응답으로 변환합니다.
         *
         * @param event 변환할 이벤트
         * @return 이벤트 상세 조회 응답
         */
        public static EventDetailResponse toDto(Event event) {
            TourismContent tourismContent = event.getTourismContent();
            return new EventDetailResponse(
                    event.getEventId(),
                    tourismContent.getTitle(),
                    tourismContent.getOverview(),
                    tourismContent.getPrimaryImageUrl(),
                    tourismContent.getThumbnailImageUrl(),
                    toDateTimeText(event.getEventStartDate()),
                    toDateTimeText(event.getEventEndDate())
            );
        }
    }

    /**
     * 이벤트 목록 조회 응답 DTO입니다.
     */
    @Getter
    @Schema(description = "이벤트 목록 조회 응답")
    public static class EventListResponse {

        @Schema(description = "현재 페이지 번호(0부터 시작)", example = "0")
        private final int page;

        @Schema(description = "페이지 크기", example = "10")
        private final int size;

        @Schema(description = "전체 이벤트 수", example = "42")
        private final long totalElements;

        @Schema(description = "전체 페이지 수", example = "5")
        private final int totalPages;

        @Schema(description = "이벤트 목록")
        private final List<EventListItem> items;

        public EventListResponse(
                int page,
                int size,
                long totalElements,
                int totalPages,
                List<EventListItem> items
        ) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.items = items;
        }

        /**
         * 이벤트 페이지를 목록 조회 응답으로 변환합니다.
         *
         * @param result 이벤트 조회 결과
         * @return 이벤트 목록 조회 응답
         */
        public static EventListResponse toDto(Page<Event> result) {
            List<EventListItem> items = result.getContent().stream()
                    .map(EventListItem::toDto)
                    .toList();
            return new EventListResponse(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    items
            );
        }
    }

    /**
     * 이벤트 목록의 개별 항목 DTO입니다.
     */
    @Getter
    @Schema(description = "이벤트 목록 항목")
    public static class EventListItem {

        @Schema(description = "이벤트 ID", example = "201")
        private final Long eventId;

        @Schema(description = "이벤트 제목", example = "수원 문화 행사")
        private final String eventTitle;

        @Schema(description = "이벤트 내용", nullable = true, example = "지역 이벤트 안내")
        private final String eventContent;

        @Schema(description = "이벤트 이미지 URL", example = "https://example.com/event-main.png")
        private final String eventImageUrl;

        @Schema(description = "이벤트 시작 일시(yyyy-MM-dd'T'HH:mm:ss)", nullable = true,
                example = "2026-07-01T10:00:00")
        private final String eventStart;

        @Schema(description = "이벤트 종료 일시(yyyy-MM-dd'T'HH:mm:ss)", nullable = true,
                example = "2026-07-07T18:00:00")
        private final String eventEnd;

        public EventListItem(
                Long eventId,
                String eventTitle,
                String eventContent,
                String eventImageUrl,
                String eventStart,
                String eventEnd
        ) {
            this.eventId = eventId;
            this.eventTitle = eventTitle;
            this.eventContent = eventContent;
            this.eventImageUrl = eventImageUrl;
            this.eventStart = eventStart;
            this.eventEnd = eventEnd;
        }

        /**
         * 이벤트 엔티티를 목록 항목으로 변환합니다.
         *
         * @param event 변환할 이벤트
         * @return 이벤트 목록 항목
         */
        public static EventListItem toDto(Event event) {
            TourismContent tourismContent = event.getTourismContent();
            return new EventListItem(
                    event.getEventId(),
                    tourismContent.getTitle(),
                    tourismContent.getOverview(),
                    tourismContent.getPrimaryImageUrl(),
                    toDateTimeText(event.getEventStartDate()),
                    toDateTimeText(event.getEventEndDate())
            );
        }
    }

    private static String toDateTimeText(LocalDate date) {
        return date == null
                ? null
                : date.atStartOfDay().format(EVENT_DATE_TIME_FORMATTER);
    }
}
