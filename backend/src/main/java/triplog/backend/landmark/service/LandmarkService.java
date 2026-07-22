package triplog.backend.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.exception.InvalidLandmarkContentTypeException;
import triplog.backend.landmark.repository.LandmarkRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * Landmark 생성과 표시명 갱신을 담당하는 도메인 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class LandmarkService {

    public static final String LANDMARK_CONTENT_TYPE_ID = "12";

    private final LandmarkRepository landmarkRepository;

    /**
     * TourismContent 기준으로 Landmark를 생성하거나 표시명을 갱신합니다.
     *
     * @param tourismContent contentTypeId가 12인 관광 콘텐츠
     * @param displayName CSV에서 관리하는 표시명 오버라이드
     * @return 생성하거나 갱신한 Landmark
     * @throws InvalidLandmarkContentTypeException 콘텐츠 타입이 12가 아닌 경우
     */
    @Transactional
    public Landmark upsert(TourismContent tourismContent, String displayName) {
        if (!LANDMARK_CONTENT_TYPE_ID.equals(tourismContent.getContentTypeId())) {
            throw new InvalidLandmarkContentTypeException(tourismContent.getContentTypeId());
        }

        return landmarkRepository.findByTourismContentTourismContentId(
                        tourismContent.getTourismContentId()
                )
                .map(landmark -> {
                    landmark.updateName(displayName);
                    return landmark;
                })
                .orElseGet(() -> landmarkRepository.save(
                        new Landmark(tourismContent, displayName)
                ));
    }
}
