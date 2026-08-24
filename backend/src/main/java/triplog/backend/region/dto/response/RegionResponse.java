package triplog.backend.region.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.region.entity.Region;
import triplog.backend.region.entity.UsersRegion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

/**
 * 지역(Region) 관련 응답 데이터를 전달하기 위한 DTO입니다.
 * <p>
 * 서비스 계층에서 처리된 지역 정보를 클라이언트에 반환할 때 사용됩니다.
 */
@Schema(description = "지역 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionResponse {

    /**
     * 전국 지도 현황 조회 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "전국 지도 현황 응답")
    public static class NationwideMapResponse {

        @Schema(description = "전체 지역 수", example = "229")
        private final Integer totalRegionCount;

        @Schema(description = "완료 지역 수", example = "12")
        private final Integer completedRegionCount;

        @Schema(description = "방문 지역 수", example = "18")
        private final Integer visitedRegionCount;

        @Schema(description = "전체 완료율 (0~100)", example = "5.24")
        private final Double overallCompletionRate;

        @Schema(description = "지역 지도 상태 목록")
        private final List<RegionMapItem> regions;

        public static NationwideMapResponse toDto(List<Region> allRegions,
                                                  Set<Long> visitedRegionIds,
                                                  Set<Long> conqueredRegionIds,
                                                  Map<Long, Long> landmarkCountMap,
                                                  Map<Long, Long> visitedLandmarkMap) {
            int totalRegionCount = allRegions.size();
            int visitedRegionCount = 0;
            int completedRegionCount = 0;

            List<RegionMapItem> regionItems = new java.util.ArrayList<>();

            for (Region region : allRegions) {
                Long regionId = region.getRegionId();
                boolean visited = visitedRegionIds.contains(regionId);

                long totalLandmarks = landmarkCountMap.getOrDefault(regionId, 0L);
                long visitedLandmarks = visitedLandmarkMap.getOrDefault(regionId, 0L);

                double completionRate = totalLandmarks > 0
                        ? Math.round(visitedLandmarks * 10000.0 / totalLandmarks) / 100.0
                        : 0.0;
                boolean completed = conqueredRegionIds.contains(regionId);

                if (visited) {
                    visitedRegionCount++;
                }
                if (completed) {
                    completedRegionCount++;
                }

                regionItems.add(RegionMapItem.toDto(region, visited, completed, completionRate));
            }

            double overallCompletionRate = totalRegionCount > 0
                    ? Math.round((double) completedRegionCount / totalRegionCount * 10000.0) / 100.0
                    : 0.0;

            return new NationwideMapResponse(
                    totalRegionCount,
                    completedRegionCount,
                    visitedRegionCount,
                    overallCompletionRate,
                    regionItems
            );
        }
    }

    /**
     * 광역 지도 현황 조회 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "광역 지도 현황 응답")
    public static class ProvinceMapResponse {

        @Schema(description = "광역 내 전체 지역 수", example = "31")
        private final Integer totalRegionCount;

        @Schema(description = "완료 지역 수", example = "4")
        private final Integer completedRegionCount;

        @Schema(description = "방문 지역 수", example = "7")
        private final Integer visitedRegionCount;

        @Schema(description = "광역 완료율 (0~100)", example = "12.9")
        private final Double provinceCompletionRate;

        @Schema(description = "시군구 지도 상태 목록")
        private final List<ProvinceRegionItem> regions;

        public static ProvinceMapResponse toDto(List<Region> regions,
                                                Set<Long> visitedRegionIds,
                                                Set<Long> conqueredRegionIds,
                                                Map<Long, Long> landmarkCountMap,
                                                Map<Long, Long> visitedLandmarkMap) {
            int totalRegionCount = regions.size();
            int visitedRegionCount = 0;
            int completedRegionCount = 0;

            List<ProvinceRegionItem> regionItems = new java.util.ArrayList<>();

            for (Region region : regions) {
                Long regionId = region.getRegionId();
                boolean visited = visitedRegionIds.contains(regionId);

                long totalLandmarks = landmarkCountMap.getOrDefault(regionId, 0L);
                long visitedLandmarks = visitedLandmarkMap.getOrDefault(regionId, 0L);

                double completionRate = totalLandmarks > 0
                        ? Math.round(visitedLandmarks * 10000.0 / totalLandmarks) / 100.0
                        : 0.0;
                boolean completed = conqueredRegionIds.contains(regionId);

                if (visited) {
                    visitedRegionCount++;
                }
                if (completed) {
                    completedRegionCount++;
                }

                regionItems.add(ProvinceRegionItem.toDto(region, visited, completed, completionRate));
            }

            double provinceCompletionRate = totalRegionCount > 0
                    ? Math.round((double) completedRegionCount / totalRegionCount * 10000.0) / 100.0
                    : 0.0;

            return new ProvinceMapResponse(
                    totalRegionCount,
                    completedRegionCount,
                    visitedRegionCount,
                    provinceCompletionRate,
                    regionItems
            );
        }
    }

    /**
     * 광역 내 개별 시군구의 지도 상태 정보입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "광역 내 시군구 지도 상태")
    public static class ProvinceRegionItem {

        @Schema(description = "지역 ID", example = "101")
        private final Long regionId;

        @Schema(description = "지역명", example = "수원시")
        private final String regionName;

        @Schema(description = "법정동 시도 코드", example = "41")
        private final String legalRegionCode;

        @Schema(description = "법정동 시군구 코드", example = "110")
        private final String legalDistrictCode;

        @Schema(description = "방문 여부", example = "true")
        private final Boolean visited;

        @Schema(description = "완료 여부", example = "false")
        private final Boolean completed;

        @Schema(description = "지역 완료율 (0~100)", example = "70.0")
        private final Double completionRate;

        public static ProvinceRegionItem toDto(Region region, boolean visited,
                                               boolean completed, double completionRate) {
            return new ProvinceRegionItem(
                    region.getRegionId(),
                    region.getRegionName(),
                    region.getLegalRegionCode(),
                    region.getLegalDistrictCode(),
                    visited,
                    completed,
                    completionRate
            );
        }
    }

    /**
     * 개별 지역의 지도 상태 정보입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "지역 지도 상태")
    public static class RegionMapItem {

        @Schema(description = "지역 ID", example = "101")
        private final Long regionId;

        @Schema(description = "지역명", example = "수원시")
        private final String regionName;

        @Schema(description = "법정동 시도 코드", example = "41")
        private final String legalRegionCode;

        @Schema(description = "법정동 시군구 코드", example = "110")
        private final String legalDistrictCode;

        @Schema(description = "방문 여부", example = "true")
        private final Boolean visited;

        @Schema(description = "완료 여부", example = "true")
        private final Boolean completed;

        @Schema(description = "지역 완료율 (0~100)", example = "100.0")
        private final Double completionRate;

        public static RegionMapItem toDto(Region region, boolean visited,
                                          boolean completed, double completionRate) {
            return new RegionMapItem(
                    region.getRegionId(),
                    region.getRegionName(),
                    region.getLegalRegionCode(),
                    region.getLegalDistrictCode(),
                    visited,
                    completed,
                    completionRate
            );
        }
    }

    /**
     * 지역 상세 조회 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "지역 상세 조회 응답")
    public static class RegionDetailResponse {

        @Schema(description = "지역 ID", example = "101")
        private final Long regionId;

        @Schema(description = "지역명", example = "수원시")
        private final String regionName;

        @Schema(description = "지역 설명", example = "역사와 문화가 있는 지역")
        private final String regionOverview;

        @Schema(description = "법정동 시도 코드", example = "41")
        private final String legalRegionCode;

        @Schema(description = "법정동 시군구 코드", example = "110")
        private final String legalDistrictCode;

        @Schema(description = "방문 여부", example = "true")
        private final Boolean visited;

        @Schema(description = "방문 횟수", example = "3")
        private final Integer visitedCount;

        @Schema(description = "지역 랜드마크 목록")
        private final LandmarkListDto landmarks;

        public static RegionDetailResponse toDto(Region region,
                                                 UsersRegion usersRegion,
                                                 List<Landmark> landmarks,
                                                 Set<Long> acquiredLandmarkIds) {
            LandmarkListDto landmarkListDto = LandmarkListDto.toDto(landmarks, region, acquiredLandmarkIds);

            if (usersRegion == null) {
                return new RegionDetailResponse(
                        region.getRegionId(),
                        region.getRegionName(),
                        region.getRegionOverview(),
                        region.getLegalRegionCode(),
                        region.getLegalDistrictCode(),
                        false,
                        0,
                        landmarkListDto
                );
            }

            return new RegionDetailResponse(
                    region.getRegionId(),
                    region.getRegionName(),
                    region.getRegionOverview(),
                    region.getLegalRegionCode(),
                    region.getLegalDistrictCode(),
                    true,
                    usersRegion.getUsersRegionVisitedCount(),
                    landmarkListDto
            );
        }
    }

    /**
     * 랜드마크 목록을 감싸는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "랜드마크 목록")
    public static class LandmarkListDto {

        @Schema(description = "랜드마크 항목 목록")
        private final List<LandmarkItem> items;

        /**
         * 랜드마크 목록 DTO를 생성합니다.
         *
         * @param landmarks           랜드마크 엔티티 목록
         * @param region              지역 엔티티
         * @param acquiredLandmarkIds 사용자가 획득한 랜드마크 ID 집합
         * @return 랜드마크 목록 DTO
         */
        public static LandmarkListDto toDto(List<Landmark> landmarks, Region region,
                                            Set<Long> acquiredLandmarkIds) {
            List<LandmarkItem> items = landmarks.stream()
                    .map(landmark -> LandmarkItem.toDto(landmark, region, acquiredLandmarkIds))
                    .toList();
            return new LandmarkListDto(items);
        }
    }

    /**
     * 지역 상세 내 랜드마크 항목입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "랜드마크 항목")
    public static class LandmarkItem {

        @Schema(description = "랜드마크 ID", example = "301")
        private final Long landmarkId;

        @Schema(description = "랜드마크명", example = "수원화성")
        private final String landmarkName;

        @Schema(description = "Tour API 식별자", example = "TOUR-10001")
        private final String contentId;

        @Schema(description = "법정동 시도 코드", example = "41")
        private final String legalRegionCode;

        @Schema(description = "법정동 시군구 코드", example = "110")
        private final String legalDistrictCode;

        @Schema(description = "카드 획득 여부", example = "false")
        private final Boolean acquired;

        public static LandmarkItem toDto(Landmark landmark, Region region,
                                         Set<Long> acquiredLandmarkIds) {
            return new LandmarkItem(
                    landmark.getLandmarkId(),
                    landmark.getLandmarkName(),
                    landmark.getTourismContent().getExternalContentId(),
                    region.getLegalRegionCode(),
                    region.getLegalDistrictCode(),
                    acquiredLandmarkIds.contains(landmark.getLandmarkId())
            );
        }
    }

    /**
     * 지역 목록 조회 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "지역 목록 조회 응답")
    public static class RegionListResponse {

        @Schema(description = "현재 페이지", example = "0")
        private final Integer page;

        @Schema(description = "페이지 크기", example = "10")
        private final Integer size;

        @Schema(description = "전체 지역 수", example = "229")
        private final Long totalElements;

        @Schema(description = "전체 페이지 수", example = "23")
        private final Integer totalPages;

        @Schema(description = "지역 목록")
        private final List<RegionListItem> regions;

        public static RegionListResponse toDto(Page<Region> regionPage, Set<Long> visitedRegionIds) {
            List<RegionListItem> regions = regionPage.getContent().stream()
                    .map(region -> RegionListItem.toDto(region, visitedRegionIds))
                    .toList();

            return new RegionListResponse(
                    regionPage.getNumber(),
                    regionPage.getSize(),
                    regionPage.getTotalElements(),
                    regionPage.getTotalPages(),
                    regions
            );
        }
    }

    /**
     * 지역 목록 내 개별 지역 항목입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "지역 목록 항목")
    public static class RegionListItem {

        @Schema(description = "지역 ID", example = "101")
        private final Long regionId;

        @Schema(description = "지역명", example = "수원시")
        private final String regionName;

        @Schema(description = "지역 설명", example = "역사와 문화가 있는 지역", nullable = true)
        private final String regionOverview;

        @Schema(description = "법정동 시도 코드", example = "41")
        private final String legalRegionCode;

        @Schema(description = "법정동 시군구 코드", example = "110")
        private final String legalDistrictCode;

        @Schema(description = "방문 여부", example = "false")
        private final Boolean visited;

        public static RegionListItem toDto(Region region, Set<Long> visitedRegionIds) {
            return new RegionListItem(
                    region.getRegionId(),
                    region.getRegionName(),
                    region.getRegionOverview(),
                    region.getLegalRegionCode(),
                    region.getLegalDistrictCode(),
                    visitedRegionIds.contains(region.getRegionId())
            );
        }
    }
}
