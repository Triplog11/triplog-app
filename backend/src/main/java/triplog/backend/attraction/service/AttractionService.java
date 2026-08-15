package triplog.backend.attraction.service;

import triplog.backend.attraction.entity.Attraction;
import triplog.backend.attraction.exception.InvalidAttractionContentTypeException;
import triplog.backend.tourismcontent.entity.TourismContent;
import java.util.Optional;

/**
 * 일반 관광지 선정 정보를 관리하는 도메인 서비스입니다.
 */
public interface AttractionService {

    /**
     * 관광 콘텐츠 식별자로 일반 관광지를 선택 조회합니다.
     *
     * @param tourismContentId 관광 콘텐츠 식별자
     * @return 일반 관광지 또는 빈 Optional
     */
    Optional<Attraction> findByTourismContentId(Long tourismContentId);

    /**
     * 관광 콘텐츠를 일반 관광지로 등록합니다.
     *
     * @param tourismContent contentTypeId가 12, 14, 28 중 하나인 관광 콘텐츠
     * @return 기존 또는 새 관광지
     * @throws InvalidAttractionContentTypeException 콘텐츠 유형이 12, 14, 28 중 하나가 아닌 경우
     */
    Attraction upsert(TourismContent tourismContent);
}
