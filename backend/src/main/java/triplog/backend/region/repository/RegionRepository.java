package triplog.backend.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.region.entity.Region;

import java.util.List;
import java.util.Optional;

/**
 * Region 영속성 처리를 담당하는 Repository입니다.
 */
public interface RegionRepository extends JpaRepository<Region, Long> {

    /**
     * 법정동 시도·시군구 코드 조합으로 Region을 조회합니다.
     *
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @return 코드와 일치하는 Region
     */
    Optional<Region> findByLegalRegionCodeAndLegalDistrictCode(
            String legalRegionCode,
            String legalDistrictCode
    );

    /**
     * 광역 코드로 해당 광역에 속한 지역 목록을 조회합니다.
     *
     * @param legalRegionCode 광역 코드 (법정동 시도 코드)
     * @return 광역에 속한 지역 목록
     */
    List<Region> findByLegalRegionCode(String legalRegionCode);
}
