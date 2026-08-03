package triplog.backend.batch.tourapi.seed;

import java.util.Set;

/**
 * 랜드마크와 일반 관광지로 선정된 TourAPI contentId 목록입니다.
 *
 * @param landmarkContentIds 랜드마크 선정 contentId
 * @param attractionContentIds 일반 관광지 선정 contentId
 */
public record SelectedContentSeeds(
        Set<String> landmarkContentIds,
        Set<String> attractionContentIds
) {

    /**
     * 전달받은 두 선정 목록을 변경할 수 없는 Set으로 복사합니다.
     */
    public SelectedContentSeeds {
        landmarkContentIds = Set.copyOf(landmarkContentIds);
        attractionContentIds = Set.copyOf(attractionContentIds);
    }

    /**
     * contentId가 랜드마크 선정 목록에 포함되는지 확인합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return 랜드마크로 선정됐으면 true
     */
    public boolean isLandmark(String contentId) {
        return landmarkContentIds.contains(contentId);
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
        java.util.LinkedHashSet<String> contentIds = new java.util.LinkedHashSet<>(landmarkContentIds);
        contentIds.addAll(attractionContentIds);
        return Set.copyOf(contentIds);
    }
}
