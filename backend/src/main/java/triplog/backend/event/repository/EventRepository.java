package triplog.backend.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import triplog.backend.event.entity.Event;

import java.util.Optional;

/**
 * Event 영속성 처리를 담당하는 Repository입니다.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * 이벤트 상세 응답에 필요한 관광 콘텐츠를 함께 조회합니다.
     *
     * @param eventId Event 식별자
     * @return 관광 콘텐츠가 함께 로딩된 Event
     */
    @EntityGraph(attributePaths = "tourismContent")
    Optional<Event> findByEventId(Long eventId);

    /**
     * 이벤트와 관광 콘텐츠를 페이지 단위로 조회합니다.
     *
     * @param pageable 페이지 및 정렬 정보
     * @return 관광 콘텐츠가 함께 로딩된 이벤트 페이지
     */
    @Override
    @EntityGraph(attributePaths = "tourismContent")
    Page<Event> findAll(Pageable pageable);

    /**
     * TourismContent 내부 식별자로 Event를 조회합니다.
     *
     * @param tourismContentId TourismContent 내부 식별자
     * @return 연결된 Event
     */
    Optional<Event> findByTourismContentTourismContentId(Long tourismContentId);
}
