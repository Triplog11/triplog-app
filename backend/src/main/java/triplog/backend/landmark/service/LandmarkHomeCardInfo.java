package triplog.backend.landmark.service;

import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.UsersCardLandmark;
import triplog.backend.region.entity.Region;

/**
 * 홈 화면에 노출할 최근 획득 카드 정보입니다.
 *
 * @param landmarkId 랜드마크 식별자
 * @param landmarkName 랜드마크 표시명
 * @param landmarkZipcode 법정동 시도·시군구 결합 코드
 * @param cardTier 카드 희귀도
 * @param cardName 카드 표시명
 * @param cardUrl 카드 이미지 URL
 */
public record LandmarkHomeCardInfo(
        Long landmarkId,
        String landmarkName,
        String landmarkZipcode,
        String cardTier,
        String cardName,
        String cardUrl
) {

    /**
     * 사용자 카드 획득 정보를 홈 카드 조회 모델로 변환합니다.
     *
     * @param obtainedCard 사용자 카드 획득 정보
     * @return 홈 화면 카드 정보
     */
    public static LandmarkHomeCardInfo from(UsersCardLandmark obtainedCard) {
        Landmark landmark = obtainedCard.getLandmark();
        Region region = landmark.getTourismContent().getRegion();
        Card card = obtainedCard.getCard();
        return new LandmarkHomeCardInfo(
                landmark.getLandmarkId(),
                landmark.getLandmarkName(),
                region.getLegalRegionCode() + region.getLegalDistrictCode(),
                card.getCardTier().name(),
                card.getCardName(),
                card.getCardUrl()
        );
    }
}
