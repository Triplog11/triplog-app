package triplog.backend.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.landmark.entity.CardTier;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.exception.InvalidLandmarkContentTypeException;
import triplog.backend.landmark.exception.LandmarkErrorCode;
import triplog.backend.landmark.exception.LandmarkException;
import triplog.backend.landmark.repository.LandmarkRepository;
import triplog.backend.landmarkvisitlog.service.LandmarkVisitLogService;
import triplog.backend.tourismcontent.entity.TourismContent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link LandmarkService}의 기본 구현체입니다.
 * 관광 콘텐츠를 기준으로 랜드마크를 생성하거나 표시명을 갱신합니다.
 */
@Service
@RequiredArgsConstructor
public class LandmarkServiceImpl implements LandmarkService {

    private static final Set<String> LANDMARK_CONTENT_TYPE_IDS = Set.of("12", "14", "28");

    private final LandmarkRepository landmarkRepository;
    private final CardService cardService;
    private final LandmarkVisitLogService landmarkVisitLogService;

    /**
     * 서비스에 등록된 전체 랜드마크 수를 조회합니다.
     *
     * @return 전체 랜드마크 수
     */
    @Override
    @Transactional(readOnly = true)
    public int countLandmarks() {
        return Math.toIntExact(landmarkRepository.count());
    }

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
    @Override
    @Transactional
    public Landmark upsert(
            TourismContent tourismContent,
            String displayName,
            CardTier cardTier,
            String cardUrl
    ) {
        if (!LANDMARK_CONTENT_TYPE_IDS.contains(tourismContent.getContentTypeId())) {
            throw new InvalidLandmarkContentTypeException(tourismContent.getContentTypeId());
        }

        Landmark landmark = landmarkRepository.findByTourismContentTourismContentId(
                        tourismContent.getTourismContentId()
                )
                .map(existingLandmark -> {
                    existingLandmark.updateName(displayName);
                    return existingLandmark;
                })
                .orElseGet(() -> landmarkRepository.save(
                        new Landmark(tourismContent, displayName)
                ));

        cardService.upsert(landmark, tourismContent.getTitle(), cardTier, cardUrl);
        return landmark;
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

    /**
     * 특정 지역에 속한 랜드마크 목록을 조회합니다.
     *
     * @param regionId 지역 식별자
     * @return 해당 지역의 랜드마크 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Landmark> findByRegionId(Long regionId) {
        return landmarkRepository.findByRegionId(regionId);
    }

    /**
     * 지역별 전체 랜드마크 수를 조회합니다.
     *
     * @return 지역 ID와 랜드마크 수 맵
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countLandmarksByRegion() {
        return landmarkRepository.countLandmarksByRegion().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * 특정 사용자가 방문한 랜드마크 수를 지역별로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 지역 ID와 방문 랜드마크 수 맵
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countVisitedLandmarksByRegionAndUser(String usersId) {
        return landmarkRepository.countVisitedLandmarksByRegionAndUser(usersId).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * 특정 지역의 전체 랜드마크 수를 조회합니다.
     *
     * @param regionId 지역 식별자
     * @return 전체 랜드마크 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countLandmarksByRegion(Long regionId) {
        return landmarkRepository.countByTourismContentRegionRegionId(regionId);
    }

    /**
     * 특정 사용자가 특정 지역에서 방문한 고유 랜드마크 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param regionId 지역 식별자
     * @return 방문한 고유 랜드마크 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countVisitedLandmarksByRegionAndUser(String usersId, Long regionId) {
        return landmarkRepository.countVisitedLandmarksByRegionAndUser(usersId, regionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countLandmarksByProvince() {
        return landmarkRepository.countLandmarksByProvince().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countVisitedLandmarksByProvinceAndUser(String usersId) {
        return landmarkRepository.countVisitedLandmarksByProvinceAndUser(usersId).stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));
    }

    @Override
    @Transactional(readOnly = true)
    public long countDistinctVisitDates(String usersId, Long landmarkId) {
        return landmarkVisitLogService.countDistinctVisitDates(usersId, landmarkId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countWeekendVisits(String usersId) {
        return landmarkVisitLogService.countWeekendVisits(usersId);
    }

    /**
     * 랜드마크를 TourismContent 및 Region과 함께 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 랜드마크 엔티티
     */
    @Override
    @Transactional(readOnly = true)
    public Landmark findByIdWithContent(Long landmarkId) {
        return landmarkRepository.findByIdWithTourismContentAndRegion(landmarkId)
                .orElseThrow(() -> new LandmarkException(LandmarkErrorCode.LANDMARK_DETAIL_NOT_FOUND));
    }

    /**
     * 랜드마크를 TourismContent 및 Region과 함께 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 랜드마크, 존재하지 않으면 빈 값
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Landmark> findOptionalByIdWithContent(Long landmarkId) {
        return landmarkRepository.findByIdWithTourismContentAndRegion(landmarkId);
    }

    /**
     * 관광 콘텐츠 식별자로 랜드마크를 조회합니다.
     *
     * @param tourismContentId 관광 콘텐츠 식별자
     * @return 연결된 랜드마크, 존재하지 않으면 빈 값
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Landmark> findByTourismContentId(Long tourismContentId) {
        return landmarkRepository.findByTourismContentTourismContentId(tourismContentId);
    }

    /**
     * 사용자의 랜드마크 방문 기록 존재 여부를 확인합니다.
     *
     * @param usersId   사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 방문 기록이 존재하면 true
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasVisited(String usersId, Long landmarkId) {
        return landmarkVisitLogService.hasVisited(usersId, landmarkId);
    }

    /**
     * 랜드마크 방문 로그를 저장합니다.
     *
     * @param usersId    사용자 식별자
     * @param landmarkId 랜드마크 식별자
     */
    @Override
    @Transactional
    public void saveVisitLog(String usersId, Long landmarkId) {
        landmarkVisitLogService.createLog(usersId, landmarkId);
    }

}
