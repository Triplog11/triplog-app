package triplog.backend.landmark.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.UsersCardLandmark;
import triplog.backend.region.entity.Region;

import java.util.List;

/**
 * 랜드마크(Landmark) 관련 응답 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "랜드마크 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LandmarkResponse {

    /**
     * 로그인 사용자가 획득한 카드 목록 응답 DTO입니다.
     */
    @Getter
    @Schema(description = "내가 획득한 카드 전체 조회 응답")
    public static class ObtainedCardListResponse {

        @Schema(description = "현재 페이지 번호", example = "0")
        private final int page;

        @Schema(description = "페이지 크기", example = "20")
        private final int size;

        @Schema(description = "전체 획득 카드 수", example = "24")
        private final long totalElements;

        @Schema(description = "전체 페이지 수", example = "3")
        private final int totalPages;

        @Schema(description = "획득 카드 목록")
        private final List<ObtainedCardItem> items;

        public ObtainedCardListResponse(
                int page,
                int size,
                long totalElements,
                int totalPages,
                List<ObtainedCardItem> items
        ) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.items = items;
        }

        /**
         * 획득 카드 엔티티 페이지를 API 응답으로 변환합니다.
         *
         * @param result 획득 카드 조회 결과
         * @return 획득 카드 목록 응답
         */
        public static ObtainedCardListResponse toDto(Page<UsersCardLandmark> result) {
            List<ObtainedCardItem> items = result.getContent().stream()
                    .map(ObtainedCardItem::toDto)
                    .toList();
            return new ObtainedCardListResponse(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    items
            );
        }
    }

    /**
     * 획득 카드 목록의 개별 항목 DTO입니다.
     */
    @Getter
    @Schema(description = "획득 카드 항목")
    public static class ObtainedCardItem {

        @Schema(description = "카드 ID", example = "501")
        private final Long cardId;

        @Schema(description = "랜드마크 ID", example = "301")
        private final Long landmarkId;

        @Schema(description = "랜드마크명", example = "수원화성")
        private final String landmarkName;

        @Schema(description = "카드명", example = "수원 화성")
        private final String cardName;

        @Schema(description = "카드 등급", example = "RARE")
        private final String cardTier;

        @Schema(description = "카드 이미지 URL")
        private final String cardUrl;

        @Schema(description = "카드 획득 일시", example = "2026-06-20T14:30:00")
        private final String acquiredAt;

        public ObtainedCardItem(
                Long cardId,
                Long landmarkId,
                String landmarkName,
                String cardName,
                String cardTier,
                String cardUrl,
                String acquiredAt
        ) {
            this.cardId = cardId;
            this.landmarkId = landmarkId;
            this.landmarkName = landmarkName;
            this.cardName = cardName;
            this.cardTier = cardTier;
            this.cardUrl = cardUrl;
            this.acquiredAt = acquiredAt;
        }

        private static ObtainedCardItem toDto(UsersCardLandmark obtainedCard) {
            Card card = obtainedCard.getCard();
            Landmark landmark = obtainedCard.getLandmark();
            return new ObtainedCardItem(
                    card.getCardId(),
                    landmark.getLandmarkId(),
                    landmark.getLandmarkName(),
                    card.getCardName(),
                    card.getCardTier().name(),
                    card.getCardUrl(),
                    obtainedCard.getUsersCardLandmarkVisitedAt().toString()
            );
        }
    }

    /**
     * 랜드마크 상세 조회 응답 DTO입니다.
     */
    @Getter
    @Schema(description = "랜드마크 상세 조회 응답")
    public static class LandmarkDetailResponse {

        @Schema(description = "랜드마크 ID", example = "301")
        private final Long landmarkId;

        @Schema(description = "랜드마크명", example = "수원화성")
        private final String landmarkName;

        @Schema(description = "지역 ID", example = "101")
        private final Long regionId;

        @Schema(description = "지역명", example = "수원시")
        private final String regionName;

        @Schema(description = "Tour API 식별자", example = "TOUR-10001")
        private final String contentId;

        @Schema(description = "법정동 시도 코드", example = "41")
        private final String legalRegionCode;

        @Schema(description = "법정동 시군구 코드", example = "110")
        private final String legalDistrictCode;

        @Schema(description = "카드 이름", example = "수원 화성", nullable = true)
        private final String cardName;

        @Schema(description = "카드 등급", example = "RARE", nullable = true)
        private final String cardTier;

        @Schema(description = "Cloudinary 카드 이미지 URL", nullable = true)
        private final String cardUrl;

        @Schema(description = "카드 획득 여부", example = "true")
        private final Boolean acquired;

        @Schema(description = "카드 획득 일시", example = "2026-06-20T14:30:00", nullable = true)
        private final String acquiredAt;

        @Schema(description = "방문 횟수", example = "2", nullable = true)
        private final Integer visitCount;

        /**
         * 랜드마크 상세 조회 응답을 생성합니다.
         *
         * @param landmarkId        랜드마크 식별자
         * @param landmarkName      랜드마크명
         * @param regionId          지역 식별자
         * @param regionName        지역명
         * @param contentId         TourAPI 콘텐츠 식별자
         * @param legalRegionCode   법정동 시도 코드
         * @param legalDistrictCode 법정동 시군구 코드
         * @param cardName          카드 이름
         * @param cardTier          카드 등급
         * @param cardUrl           카드 이미지 URL
         * @param acquired          카드 획득 여부
         * @param acquiredAt        카드 획득 일시
         * @param visitCount        방문 횟수
         */
        public LandmarkDetailResponse(
                Long landmarkId,
                String landmarkName,
                Long regionId,
                String regionName,
                String contentId,
                String legalRegionCode,
                String legalDistrictCode,
                String cardName,
                String cardTier,
                String cardUrl,
                Boolean acquired,
                String acquiredAt,
                Integer visitCount
        ) {
            this.landmarkId = landmarkId;
            this.landmarkName = landmarkName;
            this.regionId = regionId;
            this.regionName = regionName;
            this.contentId = contentId;
            this.legalRegionCode = legalRegionCode;
            this.legalDistrictCode = legalDistrictCode;
            this.cardName = cardName;
            this.cardTier = cardTier;
            this.cardUrl = cardUrl;
            this.acquired = acquired;
            this.acquiredAt = acquiredAt;
            this.visitCount = visitCount;
        }

        /**
         * Landmark 엔티티와 사용자 획득 정보를 기반으로 랜드마크 상세 응답을 생성합니다.
         *
         * @param landmark          랜드마크 엔티티
         * @param card              랜드마크 카드 정보 (없으면 null)
         * @param usersCardLandmark 사용자 카드 랜드마크 획득 정보 (없으면 null)
         * @return 랜드마크 상세 응답 DTO
         */
        public static LandmarkDetailResponse toDto(Landmark landmark,
                                                   Card card,
                                                   UsersCardLandmark usersCardLandmark) {
            Region region = landmark.getTourismContent().getRegion();

            String cardName = card == null ? null : card.getCardName();
            String cardTier = card == null ? null : card.getCardTier().name();
            String cardUrl = card == null ? null : card.getCardUrl();

            if (usersCardLandmark == null) {
                return new LandmarkDetailResponse(
                        landmark.getLandmarkId(),
                        landmark.getLandmarkName(),
                        region.getRegionId(),
                        region.getRegionName(),
                        landmark.getTourismContent().getExternalContentId(),
                        region.getLegalRegionCode(),
                        region.getLegalDistrictCode(),
                        cardName,
                        cardTier,
                        cardUrl,
                        false,
                        null,
                        null);
            }

            return new LandmarkDetailResponse(
                    landmark.getLandmarkId(),
                    landmark.getLandmarkName(),
                    region.getRegionId(),
                    region.getRegionName(),
                    landmark.getTourismContent().getExternalContentId(),
                    region.getLegalRegionCode(),
                    region.getLegalDistrictCode(),
                    cardName,
                    cardTier,
                    cardUrl,
                    true,
                    usersCardLandmark.getUsersCardLandmarkVisitedAt().toString(),
                    usersCardLandmark.getUsersCardLandmarkCount());
        }
    }
}
