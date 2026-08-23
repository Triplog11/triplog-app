package triplog.backend.region.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.dto.response.RegionResponse.NationwideMapResponse;
import triplog.backend.region.dto.response.RegionResponse.ProvinceMapResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionDetailResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionListResponse;
import triplog.backend.region.entity.Region;
import triplog.backend.region.entity.UsersRegion;
import triplog.backend.region.exception.RegionException;
import triplog.backend.region.repository.RegionRepository;
import triplog.backend.region.repository.UsersRegionRepository;
import triplog.backend.tourismcontent.entity.TourismContent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static triplog.backend.region.exception.RegionErrorCode.*;

/**
 * {@link RegionServiceImpl}의 지역 조회 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private UsersRegionRepository usersRegionRepository;

    @Mock
    private LandmarkService landmarkService;

    @Mock
    private triplog.backend.regionvisitlog.service.RegionVisitLogService regionVisitLogService;

    private RegionServiceImpl regionService;

    @BeforeEach
    void setUp() {
        regionService = new RegionServiceImpl(regionRepository, usersRegionRepository, regionVisitLogService, landmarkService);
    }

    @Test
    @DisplayName("사용자가 방문한 서로 다른 지역 수를 조회한다")
    void countVisitedRegions() {
        // given
        given(usersRegionRepository.countByUsersId(USERS_ID)).willReturn(5L);

        // when
        int result = regionService.countVisitedRegions(USERS_ID);

        // then
        assertThat(result).isEqualTo(5);
    }

    /**
     * 전국 지도 현황 조회 시 전체 지역과 완료율이 정확히 계산되는지 검증합니다.
     */
    @Test
    @DisplayName("전국 지도 현황을 조회한다")
    void getNationwideMap() {
        // given
        Region region1 = createRegion(1L, "수원시", "41", "110");
        Region region2 = createRegion(2L, "성남시", "41", "130");

        UsersRegion usersRegion = mock(UsersRegion.class);
        when(usersRegion.getRegion()).thenReturn(region1);

        given(regionRepository.findAll()).willReturn(List.of(region1, region2));
        given(usersRegionRepository.findByUsersId(USERS_ID)).willReturn(List.of(usersRegion));
        given(landmarkService.countLandmarksByRegion()).willReturn(Map.of(1L, 4L, 2L, 3L));
        given(landmarkService.countVisitedLandmarksByRegionAndUser(USERS_ID)).willReturn(Map.of(1L, 4L));

        // when
        NationwideMapResponse response = regionService.getNationwideMap(USERS_ID);

        // then
        assertThat(response.getTotalRegionCount()).isEqualTo(2);
        assertThat(response.getVisitedRegionCount()).isEqualTo(1);
        assertThat(response.getCompletedRegionCount()).isEqualTo(1);
        assertThat(response.getOverallCompletionRate()).isEqualTo(50.0);
        assertThat(response.getRegions()).hasSize(2);
        assertThat(response.getRegions().get(0).getLegalRegionCode()).isEqualTo("41");
        assertThat(response.getRegions().get(0).getLegalDistrictCode()).isEqualTo("110");
        assertThat(response.getRegions().get(0).getVisited()).isTrue();
        assertThat(response.getRegions().get(0).getCompleted()).isTrue();
        assertThat(response.getRegions().get(0).getCompletionRate()).isEqualTo(100.0);
        assertThat(response.getRegions().get(1).getVisited()).isFalse();
        assertThat(response.getRegions().get(1).getCompletionRate()).isEqualTo(0.0);
    }

    /**
     * 전국 지도 현황 조회 시 지역이 없으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("전국 지도 현황 조회 시 지역이 없으면 예외가 발생한다")
    void getNationwideMap_NotFound() {
        // given
        given(regionRepository.findAll()).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> regionService.getNationwideMap(USERS_ID))
                .isInstanceOf(RegionException.class)
                .extracting("errorCode")
                .isEqualTo(NATIONWIDE_MAP_NOT_FOUND);
    }

    /**
     * 광역 지도 현황 조회 시 해당 광역의 지역만 필터링되는지 검증합니다.
     */
    @Test
    @DisplayName("광역 지도 현황을 조회한다")
    void getProvinceMap() {
        // given
        Region region1 = createRegion(1L, "수원시", "41", "110");
        Region region2 = createRegion(2L, "성남시", "41", "130");

        given(regionRepository.findByLegalRegionCode("41")).willReturn(List.of(region1, region2));
        given(usersRegionRepository.findByUsersId(USERS_ID)).willReturn(List.of());
        given(landmarkService.countLandmarksByRegion()).willReturn(Map.of(1L, 4L, 2L, 3L));
        given(landmarkService.countVisitedLandmarksByRegionAndUser(USERS_ID)).willReturn(Map.of(1L, 2L));

        // when
        ProvinceMapResponse response = regionService.getProvinceMap(USERS_ID, "41");

        // then
        assertThat(response.getTotalRegionCount()).isEqualTo(2);
        assertThat(response.getVisitedRegionCount()).isEqualTo(0);
        assertThat(response.getCompletedRegionCount()).isEqualTo(0);
        assertThat(response.getRegions()).hasSize(2);
        assertThat(response.getRegions().get(0).getCompletionRate()).isEqualTo(50.0);
    }

    /**
     * 광역 지도 현황 조회 시 해당 광역에 지역이 없으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("광역 지도 현황 조회 시 지역이 없으면 예외가 발생한다")
    void getProvinceMap_NotFound() {
        // given
        given(regionRepository.findByLegalRegionCode("99")).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> regionService.getProvinceMap(USERS_ID, "99"))
                .isInstanceOf(RegionException.class)
                .extracting("errorCode")
                .isEqualTo(PROVINCE_MAP_NOT_FOUND);
    }

    /**
     * 지역 상세 조회 시 방문 정보와 랜드마크 목록이 정확히 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("지역 상세 정보를 조회한다")
    void getRegionDetail() {
        // given
        Region region = createRegion(1L, "수원시", "41", "110");

        UsersRegion usersRegion = mock(UsersRegion.class);
        when(usersRegion.getUsersRegionVisitedCount()).thenReturn(3);

        Landmark landmark = mock(Landmark.class);
        TourismContent tourismContent = mock(TourismContent.class);
        when(landmark.getLandmarkId()).thenReturn(101L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);
        when(tourismContent.getExternalContentId()).thenReturn("EXT-101");

        given(regionRepository.findById(1L)).willReturn(Optional.of(region));
        given(usersRegionRepository.findByUsersIdAndRegionRegionId(USERS_ID, 1L))
                .willReturn(Optional.of(usersRegion));
        given(landmarkService.findByRegionId(1L)).willReturn(List.of(landmark));
        given(landmarkService.findAcquiredLandmarkIdsByUsersId(USERS_ID)).willReturn(Set.of(101L));

        // when
        RegionDetailResponse response = regionService.getRegionDetail(USERS_ID, 1L);

        // then
        assertThat(response.getRegionId()).isEqualTo(1L);
        assertThat(response.getRegionName()).isEqualTo("수원시");
        assertThat(response.getLegalRegionCode()).isEqualTo("41");
        assertThat(response.getLegalDistrictCode()).isEqualTo("110");
        assertThat(response.getVisited()).isTrue();
        assertThat(response.getVisitedCount()).isEqualTo(3);
        assertThat(response.getLandmarks().getItems()).hasSize(1);
        assertThat(response.getLandmarks().getItems().get(0).getLandmarkId()).isEqualTo(101L);
        assertThat(response.getLandmarks().getItems().get(0).getContentId()).isEqualTo("EXT-101");
        assertThat(response.getLandmarks().getItems().get(0).getAcquired()).isTrue();
    }

    /**
     * 지역 상세 조회 시 미방문 상태를 정확히 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("지역 상세 조회 시 미방문이면 visited가 false이고 visitedCount가 0이다")
    void getRegionDetail_NotVisited() {
        // given
        Region region = createRegion(1L, "수원시", "41", "110");

        given(regionRepository.findById(1L)).willReturn(Optional.of(region));
        given(usersRegionRepository.findByUsersIdAndRegionRegionId(USERS_ID, 1L))
                .willReturn(Optional.empty());
        given(landmarkService.findByRegionId(1L)).willReturn(List.of());
        given(landmarkService.findAcquiredLandmarkIdsByUsersId(USERS_ID)).willReturn(Set.of());

        // when
        RegionDetailResponse response = regionService.getRegionDetail(USERS_ID, 1L);

        // then
        assertThat(response.getVisited()).isFalse();
        assertThat(response.getVisitedCount()).isEqualTo(0);
    }

    /**
     * 지역 상세 조회 시 지역이 존재하지 않으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("지역 상세 조회 시 지역이 없으면 예외가 발생한다")
    void getRegionDetail_NotFound() {
        // given
        given(regionRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> regionService.getRegionDetail(USERS_ID, 999L))
                .isInstanceOf(RegionException.class)
                .extracting("errorCode")
                .isEqualTo(REGION_DETAIL_NOT_FOUND);
    }

    /**
     * 지역 목록 조회 시 페이징과 방문 여부가 정확히 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("지역 목록을 페이징하여 조회한다")
    void getRegionList() {
        // given
        Region region1 = createRegion(1L, "수원시", "41", "110");
        Region region2 = createRegion(2L, "성남시", "41", "130");

        Page<Region> regionPage = new PageImpl<>(List.of(region1, region2), PageRequest.of(0, 10), 2);

        UsersRegion usersRegion = mock(UsersRegion.class);
        when(usersRegion.getRegion()).thenReturn(region1);

        given(regionRepository.findAll(any(Pageable.class))).willReturn(regionPage);
        given(usersRegionRepository.findByUsersId(USERS_ID)).willReturn(List.of(usersRegion));

        // when
        RegionListResponse response = regionService.getRegionList(USERS_ID, 0, 10);

        // then
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getRegions()).hasSize(2);
        assertThat(response.getRegions().get(0).getVisited()).isTrue();
        assertThat(response.getRegions().get(1).getVisited()).isFalse();
    }

    @Test
    @DisplayName("홈 화면용 최근 방문 지역 정보를 조회한다")
    void getRecentVisitedRegionInfo() {
        // given
        Region region = createRegion(1L, "수원시", "41", "110");
        UsersRegion usersRegion = mock(UsersRegion.class);
        when(usersRegion.getRegion()).thenReturn(region);
        when(usersRegion.getUsersRegionVisitedAt())
                .thenReturn(java.time.LocalDateTime.of(2026, 7, 1, 10, 0));
        when(usersRegion.getUsersRegionVisitedCount()).thenReturn(2);
        PageRequest pageable = PageRequest.of(0, 3);
        given(usersRegionRepository
                .findByUsersIdOrderByUsersRegionVisitedAtDescUsersRegionIdDesc(USERS_ID, pageable))
                .willReturn(List.of(usersRegion));

        // when
        List<RegionHomeInfo> result = regionService.getRecentVisitedRegionInfo(USERS_ID, 3);

        // then
        assertThat(result).singleElement().satisfies(item -> {
            assertThat(item.regionId()).isEqualTo(1L);
            assertThat(item.regionZipcode()).isEqualTo("41110");
            assertThat(item.visitedCount()).isEqualTo(2);
        });
    }

    /**
     * 지역 목록 조회 시 결과가 비어있으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("지역 목록 조회 시 결과가 비어있으면 예외가 발생한다")
    void getRegionList_NotFound() {
        // given
        Page<Region> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        given(regionRepository.findAll(any(Pageable.class))).willReturn(emptyPage);

        // when & then
        assertThatThrownBy(() -> regionService.getRegionList(USERS_ID, 0, 10))
                .isInstanceOf(RegionException.class)
                .extracting("errorCode")
                .isEqualTo(REGION_LIST_NOT_FOUND);
    }

    /**
     * 테스트용 Region 엔티티를 생성합니다.
     */
    private Region createRegion(Long regionId, String regionName,
                                String legalRegionCode, String legalDistrictCode) {
        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(regionId);
        when(region.getRegionName()).thenReturn(regionName);
        when(region.getLegalRegionCode()).thenReturn(legalRegionCode);
        when(region.getLegalDistrictCode()).thenReturn(legalDistrictCode);
        return region;
    }
}
