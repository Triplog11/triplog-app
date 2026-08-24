package triplog.backend.region.service;

import triplog.backend.region.dto.response.RegionResponse.NationwideMapResponse;
import triplog.backend.region.dto.response.RegionResponse.ProvinceMapResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionDetailResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionListResponse;
import triplog.backend.region.entity.Region;
import triplog.backend.region.exception.RegionNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Region 조회와 법정동 코드 동기화 기능을 정의하는 도메인 서비스입니다.
 */
public interface RegionService {

    /**
     * 홈 화면에 노출할 최근 방문 지역 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param limit 최대 조회 수
     * @return 최근 방문 지역 목록
     */
    List<RegionHomeInfo> getRecentVisitedRegionInfo(String usersId, int limit);

    /**
     * 사용자가 방문한 서로 다른 지역 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 방문 지역 수
     */
    int countVisitedRegions(String usersId);

    /**
     * 사용자가 정복한 서로 다른 지역 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 정복 지역 수
     */
    int countConqueredRegions(String usersId);

    /** 서비스에 등록된 서로 다른 시·도 수를 조회합니다. */
    int countProvinces();

    /** 최근 방문이 연속으로 새로운 지역이었던 횟수를 조회합니다. */
    int countConsecutiveNewRegionVisits(String usersId);

    /**
     * 법정동 시도·시군구 코드 조합으로 Region을 조회합니다.
     *
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @return 코드와 일치하는 Region
     * @throws RegionNotFoundException 일치하는 Region이 없는 경우
     */
    Region findByLegalCode(String legalRegionCode, String legalDistrictCode);

    /**
     * 법정동 코드가 있으면 지역명을 갱신하고 없으면 새 Region을 저장합니다.
     *
     * @param syncData Region 동기화 입력값
     * @return 생성하거나 갱신한 Region
     */
    Region upsert(RegionSyncData syncData);

    /**
     * 해당 ID의 Region이 존재하는지 확인합니다.
     *
     * @param regionId Region 식별자
     * @return 존재하면 true
     */
    boolean existsById(Long regionId);

    /**
     * 식별자로 지역을 조회합니다.
     *
     * @param regionId 지역 식별자
     * @return 지역, 존재하지 않으면 빈 값
     */
    Optional<Region> findOptionalById(Long regionId);

    /**
     * 전국 지도 현황을 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 전국 지도 현황 응답
     */
    NationwideMapResponse getNationwideMap(String usersId);

    /**
     * 광역 지도 현황을 조회합니다.
     *
     * @param usersId      사용자 식별자
     * @param provinceCode 광역 코드
     * @return 광역 지도 현황 응답
     */
    ProvinceMapResponse getProvinceMap(String usersId, String provinceCode);

    /**
     * 지역 상세 정보를 조회합니다.
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 ID
     * @return 지역 상세 응답
     */
    RegionDetailResponse getRegionDetail(String usersId, Long regionId);

    /**
     * 지역 목록을 페이징하여 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param page    페이지 번호
     * @param size    페이지 크기
     * @return 지역 목록 응답
     */
    RegionListResponse getRegionList(String usersId, int page, int size);

    /**
     * 지역 방문을 기록합니다. (없으면 생성, 있으면 count+1)
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 식별자
     */
    void recordRegionVisit(String usersId, Long regionId);

    /**
     * 정복 기준을 충족한 지역을 최초 정복 상태로 변경합니다.
     *
     * @param usersId 사용자 식별자
     * @param regionId 지역 식별자
     * @param totalLandmarkCount 지역의 전체 랜드마크 수
     * @param visitedLandmarkCount 사용자가 방문한 고유 랜드마크 수
     * @return 이번 호출에서 최초 정복 상태로 변경했으면 {@code true}
     */
    boolean conquerIfEligible(
            String usersId,
            Long regionId,
            long totalLandmarkCount,
            long visitedLandmarkCount
    );

    /**
     * 사용자가 해당 지역을 방문한 적 있는지 확인합니다.
     *
     * @param usersId 사용자 식별자
     * @param regionId 지역 식별자
     * @return 방문 이력이 있으면 {@code true}
     */
    boolean hasVisited(String usersId, Long regionId);
}
