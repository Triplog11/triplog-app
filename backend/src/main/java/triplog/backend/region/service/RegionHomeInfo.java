package triplog.backend.region.service;

import triplog.backend.region.entity.Region;
import triplog.backend.region.entity.UsersRegion;

import java.time.LocalDateTime;

/**
 * 홈 화면에 노출할 최근 방문 지역 정보입니다.
 *
 * @param regionId 지역 식별자
 * @param regionName 지역명
 * @param regionOverview 지역 소개
 * @param regionZipcode 법정동 시도·시군구 결합 코드
 * @param visitedAt 최근 방문 일시
 * @param visitedCount 누적 방문 횟수
 */
public record RegionHomeInfo(
        Long regionId,
        String regionName,
        String regionOverview,
        String regionZipcode,
        LocalDateTime visitedAt,
        Integer visitedCount
) {

    /**
     * 사용자 지역 방문 정보를 홈 지역 조회 모델로 변환합니다.
     *
     * @param usersRegion 사용자 지역 방문 정보
     * @return 홈 화면 지역 정보
     */
    public static RegionHomeInfo from(UsersRegion usersRegion) {
        Region region = usersRegion.getRegion();
        return new RegionHomeInfo(
                region.getRegionId(),
                region.getRegionName(),
                region.getRegionOverview(),
                region.getLegalRegionCode() + region.getLegalDistrictCode(),
                usersRegion.getUsersRegionVisitedAt(),
                usersRegion.getUsersRegionVisitedCount()
        );
    }
}
