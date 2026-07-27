package triplog.backend.landmark.service;

import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.exception.InvalidLandmarkContentTypeException;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * Landmark 생성과 표시명 갱신 기능을 정의하는 도메인 서비스입니다.
 */
public interface LandmarkService {

    /**
     * TourismContent 기준으로 Landmark를 생성하거나 표시명을 갱신합니다.
     *
     * @param tourismContent contentTypeId가 12인 관광 콘텐츠
     * @param displayName CSV에서 관리하는 표시명 오버라이드
     * @return 생성하거나 갱신한 Landmark
     * @throws InvalidLandmarkContentTypeException 콘텐츠 타입이 12가 아닌 경우
     */
    Landmark upsert(TourismContent tourismContent, String displayName);

    /**
     * 해당 ID의 Landmark가 존재하는지 확인합니다.
     *
     * @param landmarkId Landmark 식별자
     * @return 존재하면 true
     */
    boolean existsById(Long landmarkId);
}
