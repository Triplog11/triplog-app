package triplog.backend.landmark.service;

import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.UsersCardLandmark;
import triplog.backend.region.entity.Region;

/**
 * 홈 화면에 노출할 최근 획득 카드 정보입니다.
 */
public record LandmarkHomeCardInfo(
        Long landmarkId,
        String landmarkName,
        String landmarkZipcode,
        String cardTier,
        String cardName,
        String cardUrl
) {

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
