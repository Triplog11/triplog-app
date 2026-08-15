package triplog.backend.region.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.region.dto.response.RegionResponse.NationwideMapResponse;
import triplog.backend.region.dto.response.RegionResponse.ProvinceMapResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionDetailResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionListResponse;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.entity.UsersRegion;
import triplog.backend.region.exception.RegionErrorCode;
import triplog.backend.region.exception.RegionException;
import triplog.backend.region.exception.RegionNotFoundException;
import triplog.backend.region.repository.RegionRepository;
import triplog.backend.region.repository.UsersRegionRepository;
import triplog.backend.regionvisitlog.service.RegionVisitLogService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link RegionService}의 기본 구현체입니다.
 * 법정동 코드를 기준으로 지역을 조회하고 동기화합니다.
 */
@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;
    private final UsersRegionRepository usersRegionRepository;
    private final RegionVisitLogService regionVisitLogService;
    private final LandmarkService landmarkService;

    /**
     * 법정동 시도·시군구 코드 조합으로 Region을 조회합니다.
     *
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @return 코드와 일치하는 Region
     * @throws RegionNotFoundException 일치하는 Region이 없는 경우
     */
    @Override
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
    @Override
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

    /**
     * 해당 ID의 Region이 존재하는지 확인합니다.
     *
     * @param regionId Region 식별자
     * @return 존재하면 true
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long regionId) {
        return regionRepository.existsById(regionId);
    }

    /**
     * 전국 지도 현황을 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 전국 지도 현황 응답
     */
    @Override
    @Transactional(readOnly = true)
    public NationwideMapResponse getNationwideMap(String usersId) {
        List<Region> allRegions = regionRepository.findAll();
        if (allRegions.isEmpty()) {
            throw new RegionException(RegionErrorCode.NATIONWIDE_MAP_NOT_FOUND);
        }

        List<UsersRegion> userRegions = usersRegionRepository.findByUsersId(usersId);
        Set<Long> visitedRegionIds = userRegions.stream()
                .map(ur -> ur.getRegion().getRegionId())
                .collect(Collectors.toSet());

        Map<Long, Long> landmarkCountMap = landmarkService.countLandmarksByRegion();

        Map<Long, Long> visitedLandmarkMap = landmarkService.countVisitedLandmarksByRegionAndUser(usersId);

        return NationwideMapResponse.toDto(allRegions, visitedRegionIds, landmarkCountMap, visitedLandmarkMap);
    }

    /**
     * 광역 지도 현황을 조회합니다.
     *
     * @param usersId      사용자 식별자
     * @param provinceCode 광역 코드
     * @return 광역 지도 현황 응답
     */
    @Override
    @Transactional(readOnly = true)
    public ProvinceMapResponse getProvinceMap(String usersId, String provinceCode) {
        List<Region> provinceRegions = regionRepository.findByLegalRegionCode(provinceCode);
        if (provinceRegions.isEmpty()) {
            throw new RegionException(RegionErrorCode.PROVINCE_MAP_NOT_FOUND);
        }

        List<UsersRegion> userRegions = usersRegionRepository.findByUsersId(usersId);
        Set<Long> visitedRegionIds = userRegions.stream()
                .map(ur -> ur.getRegion().getRegionId())
                .collect(Collectors.toSet());

        Map<Long, Long> landmarkCountMap = landmarkService.countLandmarksByRegion();

        Map<Long, Long> visitedLandmarkMap = landmarkService.countVisitedLandmarksByRegionAndUser(usersId);

        return ProvinceMapResponse.toDto(provinceRegions, visitedRegionIds, landmarkCountMap, visitedLandmarkMap);
    }

    /**
     * 지역 상세 정보를 조회합니다.
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 ID
     * @return 지역 상세 응답
     */
    @Override
    @Transactional(readOnly = true)
    public RegionDetailResponse getRegionDetail(String usersId, Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new RegionException(RegionErrorCode.REGION_DETAIL_NOT_FOUND));

        UsersRegion usersRegion = usersRegionRepository.findByUsersIdAndRegionRegionId(usersId, regionId)
                .orElse(null);

        List<Landmark> landmarks = landmarkService.findByRegionId(regionId);

        Set<Long> acquiredLandmarkIds = landmarkService.findAcquiredLandmarkIdsByUsersId(usersId);

        return RegionDetailResponse.toDto(region, usersRegion, landmarks, acquiredLandmarkIds);
    }

    /**
     * 지역 목록을 페이징하여 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param page    페이지 번호
     * @param size    페이지 크기
     * @return 지역 목록 응답
     */
    @Override
    @Transactional(readOnly = true)
    public RegionListResponse getRegionList(String usersId, int page, int size) {
        Page<Region> regionPage = regionRepository.findAll(PageRequest.of(page, size));
        if (regionPage.isEmpty()) {
            throw new RegionException(RegionErrorCode.REGION_LIST_NOT_FOUND);
        }

        List<UsersRegion> userRegions = usersRegionRepository.findByUsersId(usersId);
        Set<Long> visitedRegionIds = userRegions.stream()
                .map(ur -> ur.getRegion().getRegionId())
                .collect(Collectors.toSet());

        return RegionListResponse.toDto(regionPage, visitedRegionIds);
    }

    /**
     * 지역 방문을 기록합니다. (없으면 생성, 있으면 count+1)
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 식별자
     */
    @Override
    @Transactional
    public void recordRegionVisit(String usersId, Long regionId) {

        Optional<UsersRegion> existingVisit = usersRegionRepository.findByUsersIdAndRegionRegionId(usersId, regionId);

        if (existingVisit.isPresent()) {
            usersRegionRepository.incrementVisitCount(existingVisit.get().getUsersRegionId());
        } else {
            Region region = regionRepository.findById(regionId)
                    .orElseThrow(() -> new RegionException(RegionErrorCode.REGION_DETAIL_NOT_FOUND));
            usersRegionRepository.save(new UsersRegion(region, usersId));
        }

        regionVisitLogService.createLog(usersId, regionId);
    }

    /**
     * 사용자의 특정 지역 방문 이력 존재 여부를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param regionId 지역 식별자
     * @return 방문 이력이 있으면 {@code true}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasVisited(String usersId, Long regionId) {
        return usersRegionRepository.findByUsersIdAndRegionRegionId(usersId, regionId).isPresent();
    }
}
