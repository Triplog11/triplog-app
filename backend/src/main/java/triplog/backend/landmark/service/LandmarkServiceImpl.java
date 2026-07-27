package triplog.backend.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.exception.InvalidLandmarkContentTypeException;
import triplog.backend.landmark.repository.LandmarkRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * {@link LandmarkService}의 기본 구현체입니다.
 * 관광 콘텐츠를 기준으로 랜드마크를 생성하거나 표시명을 갱신합니다.
 */
@Service
@RequiredArgsConstructor
public class LandmarkServiceImpl implements LandmarkService {

    private static final String LANDMARK_CONTENT_TYPE_ID = "12";

    private final LandmarkRepository landmarkRepository;

    /**
     * TourismContent 기준으로 Landmark를 생성하거나 표시명을 갱신합니다.
     *
     * @param tourismContent contentTypeId가 12인 관광 콘텐츠
     * @param displayName CSV에서 관리하는 표시명 오버라이드
     * @return 생성하거나 갱신한 Landmark
     * @throws InvalidLandmarkContentTypeException 콘텐츠 타입이 12가 아닌 경우
     */
    @Override
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

    /**
     * 해당 ID의 Landmark가 존재하는지 확인합니다.
     *
     * @param landmarkId Landmark 식별자
     * @return 존재하면 true
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long landmarkId) {
        return landmarkRepository.existsById(landmarkId);
    }
}
