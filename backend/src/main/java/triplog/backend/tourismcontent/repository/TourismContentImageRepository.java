package triplog.backend.tourismcontent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.tourismcontent.entity.TourismContentImage;

import java.util.List;

/**
 * TourismContentImage 영속성 처리를 담당하는 Repository입니다.
 */
public interface TourismContentImageRepository extends JpaRepository<TourismContentImage, Long> {

    /**
     * 관광 콘텐츠에 연결된 모든 이미지를 조회합니다.
     *
     * @param tourismContentId TourismContent 내부 식별자
     * @return 활성·비활성 이미지를 모두 포함한 목록
     */
    List<TourismContentImage> findAllByTourismContentTourismContentId(Long tourismContentId);
}
