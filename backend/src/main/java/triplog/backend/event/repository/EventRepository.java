package triplog.backend.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.event.entity.Event;

import java.util.Optional;

/**
 * Event 영속성 처리를 담당하는 Repository입니다.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * TourismContent 내부 식별자로 Event를 조회합니다.
     *
     * @param tourismContentId TourismContent 내부 식별자
     * @return 연결된 Event
     */
    Optional<Event> findByTourismContentTourismContentId(Long tourismContentId);
}
