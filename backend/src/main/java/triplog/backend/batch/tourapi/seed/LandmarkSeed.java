package triplog.backend.batch.tourapi.seed;

import triplog.backend.landmark.entity.CardTier;

/**
 * 선정 랜드마크의 카드 메타데이터입니다.
 *
 * @param contentId TourAPI 콘텐츠 식별자
 * @param cardTier 랜드마크에 고정된 카드 희귀도
 * @param cardUrl Cloudinary 카드 이미지 URL, 비어 있으면 기본 이미지 사용
 */
public record LandmarkSeed(
        String contentId,
        CardTier cardTier,
        String cardUrl
) {
}
