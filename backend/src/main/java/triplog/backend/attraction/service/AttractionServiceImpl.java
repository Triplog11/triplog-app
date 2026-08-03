package triplog.backend.attraction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.attraction.entity.Attraction;
import triplog.backend.attraction.exception.InvalidAttractionContentTypeException;
import triplog.backend.attraction.repository.AttractionRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.util.Set;

/**
 * 일반 관광지 선정 정보를 생성하는 기본 구현체입니다.
 */
@Service
public class AttractionServiceImpl implements AttractionService {

    private static final Set<String> ATTRACTION_CONTENT_TYPE_IDS = Set.of("12", "14", "28");

    private final AttractionRepository attractionRepository;

    /**
     * 일반 관광지 저장소를 주입받습니다.
     *
     * @param attractionRepository 일반 관광지 영속성 저장소
     */
    public AttractionServiceImpl(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    /**
     * 관광 콘텐츠를 일반 관광지로 생성하거나 기존 관광지를 반환합니다.
     *
     * @param tourismContent contentTypeId가 12, 14, 28 중 하나인 관광 콘텐츠
     * @return 기존 또는 새 일반 관광지
     * @throws InvalidAttractionContentTypeException 콘텐츠 유형이 12, 14, 28 중 하나가 아닌 경우
     */
    @Override
    @Transactional
    public Attraction upsert(TourismContent tourismContent) {
        if (!ATTRACTION_CONTENT_TYPE_IDS.contains(tourismContent.getContentTypeId())) {
            throw new InvalidAttractionContentTypeException(tourismContent.getContentTypeId());
        }
        return attractionRepository.findByTourismContentTourismContentId(
                        tourismContent.getTourismContentId()
                )
                .orElseGet(() -> attractionRepository.save(new Attraction(tourismContent)));
    }
}
