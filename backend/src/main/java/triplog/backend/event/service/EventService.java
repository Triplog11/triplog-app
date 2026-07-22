package triplog.backend.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.event.entity.Event;
import triplog.backend.event.exception.InvalidEventContentTypeException;
import triplog.backend.event.repository.EventRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * Event 생성과 축제 상세정보 갱신을 담당하는 도메인 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class EventService {

    public static final String EVENT_CONTENT_TYPE_ID = "15";

    private final EventRepository eventRepository;

    /**
     * TourismContent 기준으로 Event를 생성하거나 최신 상세정보를 반영합니다.
     *
     * @param tourismContent contentTypeId가 15인 관광 콘텐츠
     * @param syncData 축제 상세정보 동기화 입력값
     * @return 생성하거나 갱신한 Event
     * @throws InvalidEventContentTypeException 콘텐츠 타입이 15가 아닌 경우
     */
    @Transactional
    public Event upsert(TourismContent tourismContent, EventSyncData syncData) {
        if (!EVENT_CONTENT_TYPE_ID.equals(tourismContent.getContentTypeId())) {
            throw new InvalidEventContentTypeException(tourismContent.getContentTypeId());
        }

        return eventRepository.findByTourismContentTourismContentId(
                        tourismContent.getTourismContentId()
                )
                .map(event -> {
                    event.update(syncData);
                    return event;
                })
                .orElseGet(() -> eventRepository.save(new Event(tourismContent, syncData)));
    }
}
