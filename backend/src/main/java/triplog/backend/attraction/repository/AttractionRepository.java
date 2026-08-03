package triplog.backend.attraction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.attraction.entity.Attraction;

import java.util.Optional;

/**
 * 일반 관광지 엔티티의 영속성 기능을 제공합니다.
 */
public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    /**
     * 공통 관광 콘텐츠 식별자로 일반 관광지를 조회합니다.
     *
     * @param tourismContentId 공통 관광 콘텐츠 식별자
     * @return 해당 콘텐츠와 연결된 일반 관광지
     */
    Optional<Attraction> findByTourismContentTourismContentId(Long tourismContentId);
}
