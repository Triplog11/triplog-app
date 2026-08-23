package triplog.backend.batch.tourapi.seed;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 랜드마크와 일반 관광지로 선정된 TourAPI contentId 목록입니다.
 *
 * @param landmarkSeeds contentId별 랜드마크 카드 선정 정보
 * @param attractionContentIds 일반 관광지 선정 contentId
 */
public record SelectedContentSeeds(
        Map<String, LandmarkSeed> landmarkSeeds,
        Set<String> attractionContentIds
) {

    /**
     * 전달받은 선정 정보를 입력 순서를 유지하는 불변 컬렉션으로 복사합니다.
     */
    public SelectedContentSeeds {
        landmarkSeeds = Collections.unmodifiableMap(new LinkedHashMap<>(landmarkSeeds));
        attractionContentIds = Collections.unmodifiableSet(
                new LinkedHashSet<>(attractionContentIds)
        );
    }

    /**
     * 선정 랜드마크 contentId 집합을 반환합니다.
     *
     * @return 입력 순서를 유지하는 contentId 집합
     */
    public Set<String> landmarkContentIds() {
        return landmarkSeeds.keySet();
    }

    /**
     * contentId가 랜드마크 선정 목록에 포함되는지 확인합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return 랜드마크로 선정됐으면 true
     */
    public boolean isLandmark(String contentId) {
        return landmarkSeeds.containsKey(contentId);
    }

    /**
     * contentId에 고정된 랜드마크 카드 정보를 반환합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return 랜드마크 카드 선정 정보
     * @throws IllegalArgumentException 랜드마크 선정 목록에 없는 경우
     */
    public LandmarkSeed getLandmarkSeed(String contentId) {
        LandmarkSeed landmarkSeed = landmarkSeeds.get(contentId);
        if (landmarkSeed == null) {
            throw new IllegalArgumentException("랜드마크 CSV에 contentId가 없습니다: " + contentId);
        }
        return landmarkSeed;
    }

    /**
     * contentId가 일반 관광지 선정 목록에 포함되는지 확인합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return 일반 관광지로 선정됐으면 true
     */
    public boolean isAttraction(String contentId) {
        return attractionContentIds.contains(contentId);
    }

    /**
     * 랜드마크와 일반 관광지 선정 contentId의 합집합을 반환합니다.
     *
     * @return 중복이 없는 전체 선정 contentId
     */
    public Set<String> allContentIds() {
        LinkedHashSet<String> contentIds = new LinkedHashSet<>(landmarkSeeds.keySet());
        contentIds.addAll(attractionContentIds);
        return Collections.unmodifiableSet(contentIds);
    }
}
