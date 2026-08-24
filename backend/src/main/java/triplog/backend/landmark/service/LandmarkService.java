package triplog.backend.landmark.service;

import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.CardTier;
import triplog.backend.landmark.exception.InvalidLandmarkContentTypeException;
import triplog.backend.tourismcontent.entity.TourismContent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Landmark 생성과 표시명 갱신 기능을 정의하는 도메인 서비스입니다.
 */
public interface LandmarkService {

    /**
     * 서비스에 등록된 전체 랜드마크 수를 조회합니다.
     *
     * @return 전체 랜드마크 수
     */
    int countLandmarks();

    /**
     * TourismContent 기준으로 Landmark를 생성하거나 표시명을 갱신합니다.
     *
     * @param tourismContent contentTypeId가 12, 14, 28 중 하나인 관광 콘텐츠
     * @param displayName CSV에서 관리하는 표시명 오버라이드
     * @param cardTier CSV에서 관리하는 카드 희귀도
     * @param cardUrl 카드 이미지 URL, 비어 있으면 설정된 기본 이미지 사용
     * @return 생성하거나 갱신한 Landmark
     * @throws InvalidLandmarkContentTypeException 콘텐츠 타입이 12, 14, 28 중 하나가 아닌 경우
     */
    Landmark upsert(
            TourismContent tourismContent,
            String displayName,
            CardTier cardTier,
            String cardUrl
    );

    /**
     * 해당 ID의 Landmark가 존재하는지 확인합니다.
     *
     * @param landmarkId Landmark 식별자
     * @return 존재하면 true
     */
    boolean existsById(Long landmarkId);

    /**
     * 특정 지역에 속한 랜드마크 목록을 조회합니다.
     *
     * @param regionId 지역 식별자
     * @return 해당 지역의 랜드마크 목록
     */
    List<Landmark> findByRegionId(Long regionId);

    /**
     * 지역별 전체 랜드마크 수를 조회합니다.
     *
     * @return 지역 ID와 랜드마크 수 맵
     */
    Map<Long, Long> countLandmarksByRegion();

    /**
     * 특정 사용자가 방문한 랜드마크 수를 지역별로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 지역 ID와 방문 랜드마크 수 맵
     */
    Map<Long, Long> countVisitedLandmarksByRegionAndUser(String usersId);

    /**
     * 특정 지역의 전체 랜드마크 수를 조회합니다.
     *
     * @param regionId 지역 식별자
     * @return 전체 랜드마크 수
     */
    long countLandmarksByRegion(Long regionId);

    /**
     * 특정 사용자가 특정 지역에서 방문한 고유 랜드마크 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param regionId 지역 식별자
     * @return 방문한 고유 랜드마크 수
     */
    long countVisitedLandmarksByRegionAndUser(String usersId, Long regionId);

    /** 시·도별 전체 랜드마크 수를 조회합니다. */
    Map<String, Long> countLandmarksByProvince();

    /** 사용자가 방문한 고유 랜드마크 수를 시·도별로 조회합니다. */
    Map<String, Long> countVisitedLandmarksByProvinceAndUser(String usersId);

    /** 사용자가 특정 랜드마크를 방문한 서로 다른 날짜 수를 조회합니다. */
    long countDistinctVisitDates(String usersId, Long landmarkId);

    /** 사용자의 주말 랜드마크 방문 인증 수를 조회합니다. */
    long countWeekendVisits(String usersId);

    /**
     * 랜드마크를 TourismContent 및 Region과 함께 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 랜드마크 엔티티
     */
    Landmark findByIdWithContent(Long landmarkId);

    /**
     * 랜드마크를 TourismContent 및 Region과 함께 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 랜드마크, 존재하지 않으면 빈 값
     */
    Optional<Landmark> findOptionalByIdWithContent(Long landmarkId);

    /**
     * 관광 콘텐츠 식별자로 랜드마크를 조회합니다.
     *
     * @param tourismContentId 관광 콘텐츠 식별자
     * @return 연결된 랜드마크, 존재하지 않으면 빈 값
     */
    Optional<Landmark> findByTourismContentId(Long tourismContentId);

    /**
     * 사용자의 랜드마크 방문 기록 존재 여부를 확인합니다.
     *
     * @param usersId   사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 방문 기록이 존재하면 true
     */
    boolean hasVisited(String usersId, Long landmarkId);

    /**
     * 랜드마크 방문 로그를 저장합니다.
     *
     * @param usersId    사용자 식별자
     * @param landmarkId 랜드마크 식별자
     */
    void saveVisitLog(String usersId, Long landmarkId);

}
