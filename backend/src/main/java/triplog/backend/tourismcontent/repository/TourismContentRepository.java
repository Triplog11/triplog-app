package triplog.backend.tourismcontent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.util.Optional;

/**
 * TourismContent 영속성 처리를 담당하는 Repository입니다.
 */
public interface TourismContentRepository extends JpaRepository<TourismContent, Long> {

    /**
     * TourAPI contentId로 관광 콘텐츠를 조회합니다.
     *
     * @param externalContentId TourAPI contentId
     * @return 일치하는 관광 콘텐츠
     */
    Optional<TourismContent> findByExternalContentId(String externalContentId);
}
