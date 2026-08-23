package triplog.backend.region.service;

import triplog.backend.region.entity.Region;
import triplog.backend.region.entity.UsersRegion;

import java.time.LocalDateTime;

/**
 * 홈 화면에 노출할 최근 방문 지역 정보입니다.
 */
public record RegionHomeInfo(
        Long regionId,
        String regionName,
        String regionOverview,
        String regionZipcode,
        LocalDateTime visitedAt,
        Integer visitedCount
) {

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
