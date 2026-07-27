package triplog.backend.event.service;

import triplog.backend.event.entity.Event;
import triplog.backend.event.exception.InvalidEventContentTypeException;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * Event 생성과 축제 상세정보 갱신 기능을 정의하는 도메인 서비스입니다.
 */
public interface EventService {

    /**
     * TourismContent 기준으로 Event를 생성하거나 최신 상세정보를 반영합니다.
     *
     * @param tourismContent contentTypeId가 15인 관광 콘텐츠
     * @param syncData 축제 상세정보 동기화 입력값
     * @return 생성하거나 갱신한 Event
     * @throws InvalidEventContentTypeException 콘텐츠 타입이 15가 아닌 경우
     */
    Event upsert(TourismContent tourismContent, EventSyncData syncData);

    /**
     * 해당 ID의 Event가 존재하는지 확인합니다.
     *
     * @param eventId Event 식별자
     * @return 존재하면 true
     */
    boolean existsById(Long eventId);
}
