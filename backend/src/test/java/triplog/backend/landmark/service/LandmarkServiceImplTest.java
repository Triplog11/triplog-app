package triplog.backend.landmark.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.landmark.dto.response.LandmarkResponse.LandmarkDetailResponse;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.UsersCardLandmark;
import triplog.backend.landmark.exception.InvalidLandmarkContentTypeException;
import triplog.backend.landmark.exception.LandmarkException;
import triplog.backend.landmark.repository.LandmarkRepository;
import triplog.backend.landmark.repository.UsersCardLandmarkRepository;
import triplog.backend.region.entity.Region;
import triplog.backend.tourismcontent.entity.TourismContent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static triplog.backend.landmark.exception.LandmarkErrorCode.LANDMARK_DETAIL_NOT_FOUND;

/**
 * {@link LandmarkServiceImpl}의 랜드마크 조회 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class LandmarkServiceImplTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock
    private LandmarkRepository landmarkRepository;

    @Mock
    private UsersCardLandmarkRepository usersCardLandmarkRepository;

    @Mock
    private triplog.backend.landmarkvisitlog.service.LandmarkVisitLogService landmarkVisitLogService;

    private LandmarkServiceImpl landmarkService;

    @BeforeEach
    void setUp() {
        landmarkService = new LandmarkServiceImpl(landmarkRepository, usersCardLandmarkRepository, landmarkVisitLogService);
    }

    /** 허용한 콘텐츠 타입을 랜드마크로 저장하는지 검증합니다. */
    @ParameterizedTest
    @ValueSource(strings = {"12", "14", "28"})
    @DisplayName("허용한 contentTypeId의 관광 콘텐츠를 랜드마크로 저장한다")
    void 허용한_관광_콘텐츠를_랜드마크로_저장한다(String contentTypeId) {
        // Given
        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getContentTypeId()).thenReturn(contentTypeId);
        when(tourismContent.getTourismContentId()).thenReturn(1L);
        given(landmarkRepository.findByTourismContentTourismContentId(1L))
                .willReturn(Optional.empty());
        given(landmarkRepository.save(any(Landmark.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Landmark landmark = landmarkService.upsert(tourismContent, "선정 랜드마크");

        // Then
        assertThat(landmark.getTourismContent()).isSameAs(tourismContent);
        verify(landmarkRepository).save(any(Landmark.class));
    }

    /** 허용하지 않은 콘텐츠 타입의 랜드마크 저장을 거부하는지 검증합니다. */
    @Test
    @DisplayName("허용하지 않은 contentTypeId는 랜드마크로 저장하지 않는다")
    void 허용하지_않은_콘텐츠는_랜드마크로_저장하지_않는다() {
        // Given
        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getContentTypeId()).thenReturn("15");

        // When
        // Then
        assertThatThrownBy(() -> landmarkService.upsert(tourismContent, "축제"))
                .isInstanceOf(InvalidLandmarkContentTypeException.class);
        verify(landmarkRepository, never()).save(any());
    }

    /**
     * 랜드마크 상세 조회 시 획득한 랜드마크의 정보가 정확히 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("랜드마크 상세 정보를 조회한다 - 획득 상태")
    void getLandmarkDetail_Acquired() {
        // given
        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(1L);
        when(region.getRegionName()).thenReturn("수원시");
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getRegion()).thenReturn(region);
        when(tourismContent.getExternalContentId()).thenReturn("EXT-101");

        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(1L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        UsersCardLandmark usersCardLandmark = mock(UsersCardLandmark.class);
        when(usersCardLandmark.getUsersCardLandmarkVisitedAt())
                .thenReturn(LocalDateTime.of(2026, 6, 20, 14, 30, 0));
        when(usersCardLandmark.getUsersCardLandmarkCount()).thenReturn(2);

        given(landmarkRepository.findByIdWithTourismContentAndRegion(1L))
                .willReturn(Optional.of(landmark));
        given(usersCardLandmarkRepository.findByUsersIdAndLandmarkLandmarkId(USERS_ID, 1L))
                .willReturn(Optional.of(usersCardLandmark));

        // when
        LandmarkDetailResponse response = landmarkService.getLandmarkDetail(USERS_ID, 1L);

        // then
        assertThat(response.getLandmarkId()).isEqualTo(1L);
        assertThat(response.getLandmarkName()).isEqualTo("수원 화성");
        assertThat(response.getRegionId()).isEqualTo(1L);
        assertThat(response.getRegionName()).isEqualTo("수원시");
        assertThat(response.getContentId()).isEqualTo("EXT-101");
        assertThat(response.getLegalRegionCode()).isEqualTo("41");
        assertThat(response.getLegalDistrictCode()).isEqualTo("110");
        assertThat(response.getAcquired()).isTrue();
        assertThat(response.getAcquiredAt()).isEqualTo("2026-06-20T14:30");
        assertThat(response.getVisitCount()).isEqualTo(2);
    }

    /**
     * 랜드마크 상세 조회 시 미획득 상태의 정보가 정확히 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("랜드마크 상세 정보를 조회한다 - 미획득 상태")
    void getLandmarkDetail_NotAcquired() {
        // given
        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(1L);
        when(region.getRegionName()).thenReturn("수원시");
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getRegion()).thenReturn(region);
        when(tourismContent.getExternalContentId()).thenReturn("EXT-101");

        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(1L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        given(landmarkRepository.findByIdWithTourismContentAndRegion(1L))
                .willReturn(Optional.of(landmark));
        given(usersCardLandmarkRepository.findByUsersIdAndLandmarkLandmarkId(USERS_ID, 1L))
                .willReturn(Optional.empty());

        // when
        LandmarkDetailResponse response = landmarkService.getLandmarkDetail(USERS_ID, 1L);

        // then
        assertThat(response.getAcquired()).isFalse();
        assertThat(response.getAcquiredAt()).isNull();
        assertThat(response.getVisitCount()).isNull();
    }

    /**
     * 랜드마크 상세 조회 시 랜드마크가 존재하지 않으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("랜드마크 상세 조회 시 랜드마크가 없으면 예외가 발생한다")
    void getLandmarkDetail_NotFound() {
        // given
        given(landmarkRepository.findByIdWithTourismContentAndRegion(999L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> landmarkService.getLandmarkDetail(USERS_ID, 999L))
                .isInstanceOf(LandmarkException.class)
                .extracting("errorCode")
                .isEqualTo(LANDMARK_DETAIL_NOT_FOUND);
    }

    /**
     * 지역별 전체 랜드마크 수를 정확히 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("지역별 전체 랜드마크 수를 조회한다")
    void countLandmarksByRegion() {
        // given
        given(landmarkRepository.countLandmarksByRegion())
                .willReturn(List.<Object[]>of(new Object[]{1L, 4L}, new Object[]{2L, 3L}));

        // when
        Map<Long, Long> result = landmarkService.countLandmarksByRegion();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(1L)).isEqualTo(4L);
        assertThat(result.get(2L)).isEqualTo(3L);
    }

    /**
     * 지역별 사용자 방문 랜드마크 수를 정확히 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("지역별 사용자 방문 랜드마크 수를 조회한다")
    void countVisitedLandmarksByRegionAndUser() {
        // given
        given(landmarkRepository.countVisitedLandmarksByRegionAndUser(USERS_ID))
                .willReturn(List.<Object[]>of(new Object[]{1L, 2L}));

        // when
        Map<Long, Long> result = landmarkService.countVisitedLandmarksByRegionAndUser(USERS_ID);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(1L)).isEqualTo(2L);
    }

    /**
     * 사용자가 획득한 랜드마크 ID 집합을 정확히 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("사용자가 획득한 랜드마크 ID 집합을 조회한다")
    void findAcquiredLandmarkIdsByUsersId() {
        // given
        given(usersCardLandmarkRepository.findAcquiredLandmarkIdsByUsersId(USERS_ID))
                .willReturn(Set.of(1L, 3L, 5L));

        // when
        Set<Long> result = landmarkService.findAcquiredLandmarkIdsByUsersId(USERS_ID);

        // then
        assertThat(result).containsExactlyInAnyOrder(1L, 3L, 5L);
    }
}
