package triplog.backend.landmark.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.UsersCardLandmark;
import triplog.backend.region.entity.Region;

/**
 * 랜드마크(Landmark) 관련 응답 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "랜드마크 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LandmarkResponse {

    /**
     * 랜드마크 상세 조회 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
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

        @Schema(description = "카드 획득 여부", example = "true")
        private final Boolean acquired;

        @Schema(description = "카드 획득 일시", example = "2026-06-20T14:30:00", nullable = true)
        private final String acquiredAt;

        @Schema(description = "방문 횟수", example = "2", nullable = true)
        private final Integer visitCount;

        /**
         * Landmark 엔티티와 사용자 획득 정보를 기반으로 랜드마크 상세 응답을 생성합니다.
         *
         * @param landmark          랜드마크 엔티티
         * @param usersCardLandmark 사용자 카드 랜드마크 획득 정보 (없으면 null)
         * @return 랜드마크 상세 응답 DTO
         */
        public static LandmarkDetailResponse toDto(Landmark landmark,
                                                   UsersCardLandmark usersCardLandmark) {
            Region region = landmark.getTourismContent().getRegion();

            if (usersCardLandmark == null) {
                return new LandmarkDetailResponse(
                        landmark.getLandmarkId(),
                        landmark.getLandmarkName(),
                        region.getRegionId(),
                        region.getRegionName(),
                        landmark.getTourismContent().getExternalContentId(),
                        region.getLegalRegionCode(),
                        region.getLegalDistrictCode(),
                        false,
                        null,
                        null
                );
            }

            return new LandmarkDetailResponse(
                    landmark.getLandmarkId(),
                    landmark.getLandmarkName(),
                    region.getRegionId(),
                    region.getRegionName(),
                    landmark.getTourismContent().getExternalContentId(),
                    region.getLegalRegionCode(),
                    region.getLegalDistrictCode(),
                    true,
                    usersCardLandmark.getUsersCardLandmarkVisitedAt().toString(),
                    usersCardLandmark.getUsersCardLandmarkCount()
            );
        }
    }
}
