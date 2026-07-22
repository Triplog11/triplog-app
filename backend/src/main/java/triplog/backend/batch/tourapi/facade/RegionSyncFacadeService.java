package triplog.backend.batch.tourapi.facade;

import org.springframework.stereotype.Service;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.dto.TourApiLegalDistrictItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.region.service.RegionService;
import triplog.backend.region.service.RegionSyncData;

/**
 * TourAPI 법정동 시도·시군구 페이지 조회와 Region 저장 순서를 조합합니다.
 */
@Service
public class RegionSyncFacadeService {

    private static final int PAGE_SIZE = 100;
    private final TourApiClient tourApiClient;
    private final RegionService regionService;

    /**
     * TourAPI Client와 Region 도메인 서비스를 주입받습니다.
     *
     * @param tourApiClient 법정동 코드 조회 Client
     * @param regionService Region 저장 서비스
     */
    public RegionSyncFacadeService(TourApiClient tourApiClient, RegionService regionService) {
        this.tourApiClient = tourApiClient;
        this.regionService = regionService;
    }

    /**
     * 전체 법정동 시도와 하위 시군구를 조회하여 Region을 생성하거나 갱신합니다.
     *
     * @return 저장한 시군구 수
     */
    public int synchronizeAll() {
        int savedCount = 0;
        int regionPageNumber = 1;
        TourApiPage<TourApiLegalDistrictItem> regionPage;
        do {
            regionPage = tourApiClient.getLegalRegions(regionPageNumber, PAGE_SIZE);
            for (TourApiLegalDistrictItem region : regionPage.items()) {
                savedCount += synchronizeDistricts(region);
            }
            regionPageNumber++;
        } while (!regionPage.isLastPage());
        return savedCount;
    }

    /**
     * 지정한 시도의 시군구를 모두 조회해 지역 정보로 저장합니다.
     *
     * @param region 시군구를 조회할 시도 항목
     * @return 저장하거나 갱신한 시군구 수
     */
    private int synchronizeDistricts(TourApiLegalDistrictItem region) {
        int savedCount = 0;
        int districtPageNumber = 1;
        TourApiPage<TourApiLegalDistrictItem> districtPage;
        do {
            districtPage = tourApiClient.getLegalDistricts(
                    region.code(),
                    districtPageNumber,
                    PAGE_SIZE
            );
            for (TourApiLegalDistrictItem district : districtPage.items()) {
                regionService.upsert(new RegionSyncData(
                        region.name() + " " + district.name(),
                        region.code(),
                        district.code()
                ));
                savedCount++;
            }
            districtPageNumber++;
        } while (!districtPage.isLastPage());
        return savedCount;
    }
}
