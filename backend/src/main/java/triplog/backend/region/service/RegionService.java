package triplog.backend.region.service;

import triplog.backend.region.entity.Region;
import triplog.backend.region.exception.RegionNotFoundException;

/**
 * Region 조회와 법정동 코드 동기화 기능을 정의하는 도메인 서비스입니다.
 */
public interface RegionService {

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
}
