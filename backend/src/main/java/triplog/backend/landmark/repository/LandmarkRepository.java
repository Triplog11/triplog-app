package triplog.backend.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.landmark.entity.Landmark;

import java.util.Optional;

/**
 * Landmark 영속성 처리를 담당하는 Repository입니다.
 */
public interface LandmarkRepository extends JpaRepository<Landmark, Long> {

    /**
     * TourismContent 내부 식별자로 Landmark를 조회합니다.
     *
     * @param tourismContentId TourismContent 내부 식별자
     * @return 연결된 Landmark
     */
    Optional<Landmark> findByTourismContentTourismContentId(Long tourismContentId);
}
