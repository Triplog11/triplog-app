package triplog.backend.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.event.entity.Event;
import triplog.backend.event.exception.InvalidEventContentTypeException;
import triplog.backend.event.repository.EventRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * {@link EventService}의 기본 구현체입니다.
 * 관광 콘텐츠를 기준으로 축제 상세정보를 생성하거나 갱신합니다.
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final String EVENT_CONTENT_TYPE_ID = "15";

    private final EventRepository eventRepository;

    /**
     * TourismContent 기준으로 Event를 생성하거나 최신 상세정보를 반영합니다.
     *
     * @param tourismContent contentTypeId가 15인 관광 콘텐츠
     * @param syncData 축제 상세정보 동기화 입력값
     * @return 생성하거나 갱신한 Event
     * @throws InvalidEventContentTypeException 콘텐츠 타입이 15가 아닌 경우
     */
    @Override
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

    /**
     * 해당 ID의 Event가 존재하는지 확인합니다.
     *
     * @param eventId Event 식별자
     * @return 존재하면 true
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long eventId) {
        return eventRepository.existsById(eventId);
    }
}
