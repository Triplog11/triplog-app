package triplog.backend.region.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.region.entity.Region;
import triplog.backend.region.exception.RegionNotFoundException;
import triplog.backend.region.repository.RegionRepository;

/**
 * Region 조회와 법정동 코드 동기화를 담당하는 도메인 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    /**
     * 법정동 시도·시군구 코드 조합으로 Region을 조회합니다.
     *
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @return 코드와 일치하는 Region
     * @throws RegionNotFoundException 일치하는 Region이 없는 경우
     */
    @Transactional(readOnly = true)
    public Region findByLegalCode(String legalRegionCode, String legalDistrictCode) {
        return regionRepository.findByLegalRegionCodeAndLegalDistrictCode(
                        legalRegionCode,
                        legalDistrictCode
                )
                .orElseThrow(() -> new RegionNotFoundException(
                        legalRegionCode,
                        legalDistrictCode
                ));
    }

    /**
     * 법정동 코드가 있으면 지역명을 갱신하고 없으면 새 Region을 저장합니다.
     *
     * @param syncData Region 동기화 입력값
     * @return 생성하거나 갱신한 Region
     */
    @Transactional
    public Region upsert(RegionSyncData syncData) {
        return regionRepository.findByLegalRegionCodeAndLegalDistrictCode(
                        syncData.legalRegionCode(),
                        syncData.legalDistrictCode()
                )
                .map(region -> {
                    region.updateSyncedName(syncData.regionName());
                    return region;
                })
                .orElseGet(() -> regionRepository.save(new Region(
                        syncData.regionName(),
                        syncData.legalRegionCode(),
                        syncData.legalDistrictCode()
                )));
    }
}
